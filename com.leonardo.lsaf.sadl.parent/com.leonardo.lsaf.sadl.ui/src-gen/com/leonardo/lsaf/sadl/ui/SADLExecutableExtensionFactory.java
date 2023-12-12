/*
 * generated by Xtext 2.31.0
 */
package com.leonardo.lsaf.sadl.ui;

import com.google.inject.Injector;
import com.leonardo.lsaf.sadl.ui.internal.SadlActivator;
import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * This class was generated. Customizations should only happen in a newly
 * introduced subclass. 
 */
public class SADLExecutableExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

	@Override
	protected Bundle getBundle() {
		return FrameworkUtil.getBundle(SadlActivator.class);
	}
	
	@Override
	protected Injector getInjector() {
		SadlActivator activator = SadlActivator.getInstance();
		return activator != null ? activator.getInjector(SadlActivator.COM_LEONARDO_LSAF_SADL_SADL) : null;
	}

}
