///*
// * generated by Xtext 2.30.0
// */
//package com.leonardo.lsaf.sadl.scoping;
//
//
///**
// * This class contains custom scoping description.
// * 
// * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#scoping
// * on how and when to use it.
// */
//public class SADLScopeProvider extends AbstractSADLScopeProvider {
//
//}
package com.leonardo.lsaf.sadl.scoping;

import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.impl.AliasedEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;
import com.google.common.base.Function;
import com.google.inject.Inject;
import com.leonardo.lsaf.sadl.sadl.Application;
import com.leonardo.lsaf.sadl.sadl.Component;
import com.leonardo.lsaf.sadl.sadl.Connection;
import com.leonardo.lsaf.sadl.sadl.Container;
import com.leonardo.lsaf.sadl.sadl.Instance;
import com.leonardo.lsaf.sadl.sadl.InterfacePort;
import com.leonardo.lsaf.sadl.sadl.Link;
import com.leonardo.lsaf.sadl.sadl.Port;
import com.leonardo.lsaf.sadl.sadl.PropertyValue;
import com.leonardo.lsaf.sadl.sadl.SadlPackage;
import com.leonardo.lsaf.sadl.sadl.TopicPort;

/**
 * 
 * This class contains custom scoping description.
 *
 * 
 * 
 * See
 * https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#scoping
 * 
 * on how and when to use it.
 * 
 */
public class SADLScopeProvider extends AbstractSADLScopeProvider {
	@Inject
	private IQualifiedNameConverter qualifiedNameConverter;
	@Inject
	private IQualifiedNameProvider qualifiedNameProvider;

	@Override
	public IScope getScope(EObject context, EReference reference) {
		if (context instanceof Connection) {
			if (reference.getFeatureID() == SadlPackage.CONNECTION__FROM
					|| reference.getFeatureID() == SadlPackage.CONNECTION__TO) {
				if (context.eContainer() instanceof Container) {
					Container parent = (Container) context.eContainer();
					Map<QualifiedName, IEObjectDescription> map = new LinkedHashMap<QualifiedName, IEObjectDescription>(
							4);
					populateInstanceMap(qualifiedNameProvider.getFullyQualifiedName(parent), parent, map);
					BasicEList<Instance> instances = new BasicEList<Instance>();
					for (IEObjectDescription descr : map.values()) {
						instances.add((Instance) descr.getEObjectOrProxy());
					}
					return Scopes.scopeFor(instances, IScope.NULLSCOPE);
					// return new MapBasedScope(IScope.NULLSCOPE, map, false);
				} else {
					return IScope.NULLSCOPE;
				}
			} else if (reference.getFeatureID() == SadlPackage.CONNECTION__SOURCE) {
				if (context.eContainer() instanceof Container) {
					Container parent = (Container) context.eContainer();
					return getPortScope(parent, ((Connection) context).getSource(), ((Connection) context).getFrom());
				} else if (context.eContainer() instanceof Component) {
					Component parent = (Component) context.eContainer();
					return getPortScope(parent, ((Connection) context).getSource(), ((Connection) context).getFrom());
				} else {
					return IScope.NULLSCOPE;
				}
			} else if (reference.getFeatureID() == SadlPackage.CONNECTION__DESTINATION) {
				if (context.eContainer() instanceof Container) {
					Container parent = (Container) context.eContainer();
					return getPortScope(parent, ((Connection) context).getDestination(),
							((Connection) context).getTo());
				} else if (context.eContainer() instanceof Component) {
					Component parent = (Component) context.eContainer();
					return getPortScope(parent, ((Connection) context).getDestination(),
							((Connection) context).getTo());
				} else {
					return IScope.NULLSCOPE;
				}
			}
		} else if (context instanceof PropertyValue) {
			if (reference.getFeatureID() == SadlPackage.PROPERTY_VALUE__PROPERTY) {
				if (context.eContainer() instanceof Link) {
					Link parent = (Link) context.eContainer();
					if (parent.getTransport() != null) {
						return Scopes.scopeFor(parent.getTransport().getProperties());
					} else {
						return IScope.NULLSCOPE;
					}
				}
			}
		}
		return super.getScope(context, reference);
	}

	IScope getPortScope(EObject parent, EList<Port> contextPorts, Instance root) {
		EList<Port> candidates = getResolvedPorts(contextPorts);
		if (candidates.isEmpty()) {
			if (root != null && root.getType() != null) {
				candidates.addAll(root.getType().getPorts());
			} else if (parent instanceof Component) {
				candidates.addAll(((Component) parent).getPorts()); // TODO Handle Component (as opposed to Container)
																	// connections
			}
		} else {
			Port lastPort = candidates.get(candidates.size() - 1);
			if (lastPort instanceof InterfacePort) {
				if (((InterfacePort) lastPort).getType() != null) {
					candidates.addAll(((InterfacePort) lastPort).getType().getPorts());
				}
			} else if (lastPort instanceof TopicPort) {
				candidates.add(lastPort);
			}
		}
		return Scopes.scopeFor(candidates, new Function<Port, QualifiedName>() {
			@Override
			public QualifiedName apply(Port port) {
				QualifiedName qnResult = qualifiedNameConverter.toQualifiedName(port.getName());
				return qnResult;
			}
		}, IScope.NULLSCOPE);
	}

	private EList<Port> getResolvedPorts(EList<Port> ports) {
		if (ports instanceof EObjectResolvingEList<?>) {
			EObjectResolvingEList<Port> resolvingPorts = (EObjectResolvingEList<Port>) ports;
			EList<Port> result = new BasicEList<Port>();
			for (int i = 0; i < resolvingPorts.size(); i++) {
				Port testPort = resolvingPorts.basicGet(i);
				if (testPort.eIsProxy()) {
					break;
				}
				result.add(ports.get(i));
			}
			return result;
		}
		return ports;
	}

	private void populateInstanceMap(QualifiedName rootQN, EObject parent,
			Map<QualifiedName, IEObjectDescription> map) {
		EList<Instance> insts = getAllInstances(parent);
		EList<Instance> alreadyMapped = new BasicEList<Instance>();
		for (IEObjectDescription eOD : map.values()) {
			alreadyMapped.add((Instance) eOD.getEObjectOrProxy());
		}
		for (Instance inst : insts) {
			QualifiedName instQN = qualifiedNameProvider.getFullyQualifiedName(inst);
			QualifiedName aliasQN = instQN.skipFirst(rootQN.getSegmentCount());
			if (!map.containsKey(aliasQN) && !alreadyMapped.contains(inst)) {
				IEObjectDescription od = EObjectDescription.create(instQN, inst);
				map.put(instQN.skipFirst(rootQN.getSegmentCount()), new AliasedEObjectDescription(aliasQN, od));
			}
		}
		if (parent.eContainer() instanceof Container || parent.eContainer() instanceof Application) {
			populateInstanceMap(rootQN.skipLast(1), parent.eContainer(), map);
		}
	}

	private EList<Instance> getAllInstances(EObject parent) {
		EList<Instance> result = new BasicEList<Instance>();
		if (parent instanceof Application) {
			for (Container cont : ((Application) parent).getContainers()) {
				result.addAll(getAllInstances(cont));
			}
		} else if (parent instanceof Container) {
			for (Container cont : ((Container) parent).getChildren()) {
				result.addAll(getAllInstances(cont));
			}
			result.addAll(((Container) parent).getInstances());
		} else {
			throw new IllegalArgumentException("Unexpected type.");
		}
		return result;
	}
}