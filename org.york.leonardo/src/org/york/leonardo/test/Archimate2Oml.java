package org.york.leonardo.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.york.leonardo.Ecore2Oml;

import com.archimatetool.model.impl.ArchimatePackage;
import com.archimatetool.model.util.ArchimateResourceFactory;
import com.google.inject.Injector;

import io.opencaesar.oml.Description;
import io.opencaesar.oml.Vocabulary;
import io.opencaesar.oml.dsl.OmlStandaloneSetup;

class Archimate2Oml {

  private static Injector injector;
  private static OmlStandaloneSetup omlSetup;

  @BeforeAll
  public static void initialisation() {
    omlSetup = new OmlStandaloneSetup();
    injector = omlSetup.createInjectorAndDoEMFRegistration();
  }

  /***
   * Test Archimate model to OML
   * 
   * @throws Exception
   */
  @Test
  void testArchimate2Oml() throws Exception {

    File modelFile = new File("model/Archisurance.archimate");

    Resource metamodel = ArchimatePackage.eINSTANCE.eResource();
    Resource model = ArchimateResourceFactory.createNewResource(modelFile);
    model.load(null);

    Ecore2Oml ecore2oml = new Ecore2Oml();
    ecore2oml.xmiToOml(model, metamodel);

    // assert
    XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
    Resource omlResource = resourceSet.createResource(URI.createFileURI(
        new File(ecore2oml.getTargetOmlDirectory() + "www.archimatetool.com/archimate.oml")
            .getAbsolutePath()),
        null);
    omlResource.load(null);
    Vocabulary vocabulary = (Vocabulary) omlResource.getContents().get(0);
    assertThat(vocabulary.getIri()).isEqualTo("http://www.archimatetool.com/archimate");

    omlResource = resourceSet.createResource(URI.createFileURI(
        new File(ecore2oml.getTargetOmlDirectory() + "www.archimatetool.com/description/Archisurance.oml")
            .getAbsolutePath()),
        null);
    omlResource.load(null);
    Description description = (Description) omlResource.getContents().get(0);
    assertThat(description.getIri()).isEqualTo("http://www.archimatetool.com/description/Archisurance");
  }

}
