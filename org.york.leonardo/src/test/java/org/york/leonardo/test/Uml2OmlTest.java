package org.york.leonardo.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.york.leonardo.Ecore2Oml;

import com.google.inject.Injector;

import io.opencaesar.oml.dsl.OmlStandaloneSetup;

class Uml2OmlTest {

  private static Injector injector;
  private static OmlStandaloneSetup omlSetup;

  @BeforeAll
  public static void initialisation() {
    omlSetup = new OmlStandaloneSetup();
    injector = omlSetup.createInjectorAndDoEMFRegistration();
  }

 

  /***
   * Test Modisco Java model to OML
   * 
   * @throws Exception
   */
  @Test
  void testUml2Oml() throws Exception {

    File model = new File("model/uml.uml");
    File metamodel = new File("model/uml.ecore");
    
    // UML-specific initialisation
    ResourceSet resources = new ResourceSetImpl();
    UMLResourcesUtil.init(resources);
    
    //create ecore to oml
    Ecore2Oml ecore2oml = new Ecore2Oml();
    ecore2oml.xmiToOml(model, metamodel);

    // assert
    File omlVocabulary = new File(
        "../targetoml/src/oml/www.eclipse.org/uml2/5.0.0/UML.oml");
    String output = Files.readString(Path.of(omlVocabulary.getAbsolutePath()), StandardCharsets.UTF_8);
    assertThat(output).contains("UML");

    XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
    Resource omlResource = resourceSet.createResource(URI.createFileURI(omlVocabulary.getAbsolutePath()), null);
    omlResource.load(null);
    assertThat(omlResource.getContents().size()).isGreaterThan(0);
  }
  
 }
