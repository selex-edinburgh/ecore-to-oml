package org.york.leonardo.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.york.leonardo.Ecore2Oml;

import com.google.inject.Injector;

import io.opencaesar.oml.dsl.OmlStandaloneSetup;

class Ecore2OmlTest {

  private static Injector injector;
  private static OmlStandaloneSetup omlSetup;

  @BeforeAll
  public static void initialisation() {
    omlSetup = new OmlStandaloneSetup();
    injector = omlSetup.createInjectorAndDoEMFRegistration();
  }

  /***
   * Test sADL model from XMI to OML
   * 
   * @throws Exception
   */
  @Test
  void testSADLXmi2Oml() throws Exception {

    File model = new File("model/first.xmi");
    File metamodel = new File("model/SADL.ecore");

    Ecore2Oml ecore2oml = new Ecore2Oml();
    ecore2oml.xmiToOml(model, metamodel);

    // assert
    File omlVocabulary = new File("../targetoml/src/oml/www.leonardo.com/lsaf/sadl/SADL/vocabulary/sADL.oml");
    String output = Files.readString(Path.of(omlVocabulary.getAbsolutePath()), StandardCharsets.UTF_8);
    assertThat(output).contains("sADL");

    XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
    Resource omlResource = resourceSet.createResource(URI.createFileURI(omlVocabulary.getAbsolutePath()), null);
    omlResource.load(null);
    assertThat(omlResource.getContents().size()).isGreaterThan(0);
  }

  /***
   * Test Modisco Java model to OML
   * 
   * @throws Exception
   */
  @Test
  void testJava2Oml() throws Exception {

    File model = new File("model/java.xmi");
    File metamodel = new File("model/java.ecore");

    Ecore2Oml ecore2oml = new Ecore2Oml();
    ecore2oml.xmiToOml(model, metamodel);

    // assert
    File omlVocabulary = new File(
        "../targetoml/src/oml/www.eclipse.org/MoDisco/Java/0.2.incubation/java/vocabulary/java.oml");
    String output = Files.readString(Path.of(omlVocabulary.getAbsolutePath()), StandardCharsets.UTF_8);
    assertThat(output).contains("java");

    XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
    Resource omlResource = resourceSet.createResource(URI.createFileURI(omlVocabulary.getAbsolutePath()), null);
    omlResource.load(null);
    assertThat(omlResource.getContents().size()).isGreaterThan(0);
  }
  
  /***
   * Test Ecore metamodel (and as a model) to OML
   * 
   * @throws Exception
   */
  @Test
  void testEcore2Oml() throws Exception {

    Ecore2Oml ecore2oml = new Ecore2Oml();
    ecore2oml.builtInEcoreToOml();

    // assert
    File omlVocabulary = new File(
        "../targetoml/src/oml/www.eclipse.org/MoDisco/Java/0.2.incubation/java/vocabulary/java.oml");
    String output = Files.readString(Path.of(omlVocabulary.getAbsolutePath()), StandardCharsets.UTF_8);
    assertThat(output).contains("java");

    XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
    Resource omlResource = resourceSet.createResource(URI.createFileURI(omlVocabulary.getAbsolutePath()), null);
    omlResource.load(null);
    assertThat(omlResource.getContents().size()).isGreaterThan(0);
  }

  /***
   * Test sADL model from Source Code to OML
   * 
   * @throws Exception
   */
  @Test
  void testSADLCode2Oml() throws Exception {

    File model = new File("model/first.sadl");
    File metamodel = new File("model/SADL.ecore");

    Ecore2Oml ecore2oml = new Ecore2Oml();
    ecore2oml.sadlToOml(model, metamodel);

    // assert
    File omlVocabulary = new File("../targetoml/src/oml/www.leonardo.com/lsaf/sadl/SADL/vocabulary/sADL.oml");
    String output = Files.readString(Path.of(omlVocabulary.getAbsolutePath()), StandardCharsets.UTF_8);
    assertThat(output).contains("sADL");

    XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
    Resource omlResource = resourceSet.createResource(URI.createFileURI(omlVocabulary.getAbsolutePath()), null);
    omlResource.load(null);
    assertThat(omlResource.getContents().size()).isGreaterThan(0);
  }

  /***
   * Test OML code to OML XMI
   * 
   * @throws Exception
   */
  @Test
  void testOmlCode2OmlXmi() throws Exception {

    File model = new File("model/sADL.oml");

    Ecore2Oml ecore2oml = new Ecore2Oml();
    ecore2oml.omlCodeToOmlXmi(model);

    // assert
    XMIResource resource = new XMIResourceImpl(URI.createFileURI("model/sADL.xmi"));
    resource.load(null);
    assertThat(resource.getContents().size()).isGreaterThan(0);
  }
  
  /***
   * Test OML XMI to OML code
   * 
   * @throws Exception
   */
  @Test
  void testOmlXmi2OmlCode() throws Exception {

    File model = new File("model/sADL.xmi");

    Ecore2Oml ecore2oml = new Ecore2Oml();
    ecore2oml.omlXmiToOmlCode(model);

    
  }
}