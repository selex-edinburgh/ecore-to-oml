package org.york.leonardo;

import java.io.File;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.egl.EglFileGeneratingTemplateFactory;
import org.eclipse.epsilon.egl.EgxModule;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.emc.emf.EmfUtil;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Injector;
import com.leonardo.lsaf.sadl.SADLStandaloneSetup;

import io.opencaesar.oml.dsl.OmlStandaloneSetup;

public class Ecore2Oml {

  public static void main(String[] args) throws Exception {

  }

  /***
   * Transform XMI model and its metammodel to OML
   * 
   * @param sourceModelFile
   * @param sourceMetamodelFile
   * @throws Exception
   */

  public void xmiToOml(File sourceModelFile, File sourceMetamodelFile) throws Exception {

    EmfModel metamodel = new EmfModel();
    metamodel.setModelFile(sourceMetamodelFile.getAbsolutePath());
    metamodel.setName("M2");
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
    model.setName("M1");
    model.load();

    File egxFile = new File("egx/ecore2oml.egx");
    EgxModule module = new EgxModule(new EglFileGeneratingTemplateFactory());
    module.parse(egxFile);

    module.getContext().getModelRepository().addModel(metamodel);
    module.getContext().getModelRepository().addModel(model);
    module.execute();
  }

  /***
   * Transform Ecore metamodel and as a model to OML. The built-in Ecore metamodel
   * is used as the model and metamodel.
   * 
   * @throws Exception
   */
  public void builtInEcoreToOml() throws Exception {

    XMIResource resource = new XMIResourceImpl();

    EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage("http://www.eclipse.org/emf/2002/Ecore");
    Resource internalEcoreResource = ePackage.eResource();
    resource.getContents().addAll(EcoreUtil.copyAll(internalEcoreResource.getContents()));

    EmfModel metamodel = new InMemoryEmfModel(resource);
    metamodel.setName("M2");

    EmfModel model = new InMemoryEmfModel(resource);
    model.setName("M1");

    File egxFile = new File("egx/ecore2oml.egx");
    EgxModule module = new EgxModule(new EglFileGeneratingTemplateFactory());
    module.parse(egxFile);

    module.getContext().getModelRepository().addModel(metamodel);
    module.getContext().getModelRepository().addModel(model);
    module.execute();
  }

  /***
   * Transform sADL model to OML.
   * 
   * @param sourceModelFile
   * @param sourceMetamodelFile
   * @throws Exception
   */
  public void sadlToOml(File sourceModelFile, File sourceMetamodelFile) throws Exception {

    EmfModel metamodel = new EmfModel();
    metamodel.setModelFile(sourceMetamodelFile.getAbsolutePath());
    metamodel.setName("M2");
    metamodel.load();

    Injector injector = new SADLStandaloneSetup().createInjectorAndDoEMFRegistration();
    XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
    Resource resource = resourceSet.createResource(URI.createFileURI(sourceModelFile.getAbsolutePath()), null);
    resource.load(null);

    EmfModel model = new InMemoryEmfModel(resource);
    model.setName("M1");

    File egxFile = new File("egx/ecore2oml.egx");
    EgxModule module = new EgxModule(new EglFileGeneratingTemplateFactory());
    module.parse(egxFile);

    module.getContext().getModelRepository().addModel(metamodel);
    module.getContext().getModelRepository().addModel(model);
    module.execute();
  }
  
  /***
   * Oml code to XMI model
   * 
   * @param sourceModelFile
   * @param sourceMetamodelFile
   * @throws Exception
   */
  public void omlCodeToOmlXmi(File sourceModelFile) throws Exception {

    Injector injector = new OmlStandaloneSetup().createInjectorAndDoEMFRegistration();
    XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
    Resource omlResource = resourceSet.createResource(URI.createFileURI(sourceModelFile.getAbsolutePath()), null);
    omlResource.load(null);
    
    String path = omlResource.getURI().toFileString();
    path = path.replace(".oml", ".xmi");
    XMIResource xmiResouce = new XMIResourceImpl(URI.createFileURI(path));
    xmiResouce.getContents().addAll(EcoreUtil.copyAll(omlResource.getContents()));
    xmiResouce.save(null);
    
  }
  
  /***
   * Oml XMI to Oml code
   * 
   * @param sourceModelFile
   * @param sourceMetamodelFile
   * @throws Exception
   */
  public void omlXmiToOmlCode(File sourceModelFile) throws Exception {

    Injector injector = new OmlStandaloneSetup().createInjectorAndDoEMFRegistration();
    XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
    
    XMIResource xmiResource = new XMIResourceImpl(URI.createFileURI(sourceModelFile.getAbsolutePath()));
    xmiResource.load(null);
    String path = xmiResource.getURI().toFileString();
    path = path.replace(".xmi", ".oml");
    
    Resource omlResource = resourceSet.createResource(URI.createFileURI(path), null);
    omlResource.getContents().addAll(EcoreUtil.copyAll(xmiResource.getContents()));
    
    omlResource.save(null);
    
  }

}
