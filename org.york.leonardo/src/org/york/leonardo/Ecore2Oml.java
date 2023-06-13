package org.york.leonardo;

import java.io.File;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.epsilon.egl.EglFileGeneratingTemplateFactory;
import org.eclipse.epsilon.egl.EgxModule;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.emc.emf.EmfUtil;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Injector;
import com.leonardo.lsaf.sadl.SADLStandaloneSetup;

public class Ecore2Oml {

  public static void main(String[] args) throws Exception {

  }

  public void xmiToOml(File sourceModelFile, File sourceMetamodelFile) throws Exception {

    EmfModel metamodel = new EmfModel();
    metamodel.setModelFile(sourceMetamodelFile.getAbsolutePath());
    metamodel.setName("M1");
    metamodel.load();

    org.eclipse.emf.common.util.URI uri = org.eclipse.emf.common.util.URI
        .createFileURI(sourceMetamodelFile.getAbsolutePath());
    Registry registry = EPackage.Registry.INSTANCE;
    EPackage ePackage = (EPackage) metamodel.getResource().getContents().get(0);
    String nsURI = ePackage.getNsURI();
    if (!registry.containsKey(nsURI)) {
      EmfUtil.register(uri, EPackage.Registry.INSTANCE);
    }

    EmfModel model = new EmfModel();
    model.setModelFile(sourceModelFile.getAbsolutePath());
    model.setName("M2");
    model.load();

    File egxFile = new File("egx/ecore2oml.egx");
    EgxModule module = new EgxModule(new EglFileGeneratingTemplateFactory());
    module.parse(egxFile);

    module.getContext().getModelRepository().addModel(metamodel);
    module.getContext().getModelRepository().addModel(model);
    module.execute();
  }
  
  public void ecoreToOml(File sourceModelFile, File sourceMetamodelFile) throws Exception {

    EmfModel metamodel = new EmfModel();
    metamodel.setModelFile(sourceMetamodelFile.getAbsolutePath());
    metamodel.setName("M1");
    metamodel.load();

    org.eclipse.emf.common.util.URI uri = org.eclipse.emf.common.util.URI
        .createFileURI(sourceMetamodelFile.getAbsolutePath());
    Registry registry = EPackage.Registry.INSTANCE;
    EPackage ePackage = (EPackage) metamodel.getResource().getContents().get(0);
    String nsURI = ePackage.getNsURI();
    if (!registry.containsKey(nsURI)) {
      EmfUtil.register(uri, EPackage.Registry.INSTANCE);
    }

    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
    Resource resource = resourceSet
        .getResource(org.eclipse.emf.common.util.URI.createFileURI(sourceModelFile.getAbsolutePath()), true);
    resource.load(null);
    
    EmfModel model = new InMemoryEmfModel(resource);
    model.setModelFile(sourceModelFile.getAbsolutePath());
    model.setName("M2");
    model.load();

    File egxFile = new File("egx/ecore2oml.egx");
    EgxModule module = new EgxModule(new EglFileGeneratingTemplateFactory());
    module.parse(egxFile);

    module.getContext().getModelRepository().addModel(metamodel);
    module.getContext().getModelRepository().addModel(model);
    module.execute();
  }

  public void sadlToOml(File sourceModelFile, File sourceMetamodelFile) throws Exception {

    EmfModel metamodel = new EmfModel();
    metamodel.setModelFile(sourceMetamodelFile.getAbsolutePath());
    metamodel.setName("M1");
    metamodel.load();

    Injector injector = new SADLStandaloneSetup().createInjectorAndDoEMFRegistration();
    XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
    Resource resource = resourceSet.createResource(URI.createFileURI(sourceModelFile.getAbsolutePath()), null);
    resource.load(null);

    EmfModel model = new InMemoryEmfModel(resource);
    model.setName("M2");

    File egxFile = new File("egx/ecore2oml.egx");
    EgxModule module = new EgxModule(new EglFileGeneratingTemplateFactory());
    module.parse(egxFile);

    module.getContext().getModelRepository().addModel(metamodel);
    module.getContext().getModelRepository().addModel(model);
    module.execute();
  }

}
