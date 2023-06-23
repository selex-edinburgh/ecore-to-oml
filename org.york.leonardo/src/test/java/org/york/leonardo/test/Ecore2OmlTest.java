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

import io.opencaesar.oml.Description;
import io.opencaesar.oml.Vocabulary;
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
   * Test transforming Modisco Java model/metamodel to OML
   * 
   * @throws Exception
   */
  @Test
  void testXmi2OmlUsingETL() throws Exception {

    File model = new File("model/java.xmi");
    File metamodel = new File("model/java.ecore");

    Ecore2Oml ecore2oml = new Ecore2Oml();
    ecore2oml.getDependencies().put("rdf", "http://www.w3.org/2000/01/rdf-schema#");
    ecore2oml.getDependencies().put("xsd", "http://www.w3.org/2001/XMLSchema#");
    ecore2oml.getDependencies().put("dc", "http://purl.org/dc/elements/1.1/");
    ecore2oml.xmiToOmlUsingETL(model, metamodel);

    // assert
    XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
    Resource omlResource = resourceSet.createResource(URI.createFileURI(new File(ecore2oml.getTargetOmlDirectory() + "www.eclipse.org/MoDisco/Java/0.2.incubation/java/vocabulary/java.oml").getAbsolutePath()), null);
    omlResource.load(null);
    Vocabulary vocabulary = (Vocabulary)  omlResource.getContents().get(0);
    assertThat(vocabulary.getIri()).isEqualTo("http://www.eclipse.org/MoDisco/Java/0.2.incubation/java/vocabulary/java");
    
    omlResource = resourceSet.createResource(URI.createFileURI(new File(ecore2oml.getTargetOmlDirectory() + "www.eclipse.org/MoDisco/Java/0.2.incubation/java/description/java.oml").getAbsolutePath()), null);
    omlResource.load(null);
    Description description = (Description)  omlResource.getContents().get(0);
    assertThat(description.getIri()).isEqualTo("http://www.eclipse.org/MoDisco/Java/0.2.incubation/java/description/java");
  }
  
  /***
   * Test sADL model to OML using ETL
   * 
   * @throws Exception
   */
  @Test
  void testSADLXmi2OmlUsingETL() throws Exception {

    File model = new File("model/first.sadl");
    File metamodel = new File("model/SADL.ecore");

    Ecore2Oml ecore2oml = new Ecore2Oml();
    ecore2oml.getDependencies().put("rdf", "http://www.w3.org/2000/01/rdf-schema#");
    ecore2oml.getDependencies().put("xsd", "http://www.w3.org/2001/XMLSchema#");
    ecore2oml.getDependencies().put("dc", "http://purl.org/dc/elements/1.1/");
    ecore2oml.sadlToOmlUsingETL(model, metamodel);

    // assert
    XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
    Resource omlResource = resourceSet.createResource(URI.createFileURI(new File(ecore2oml.getTargetOmlDirectory() + "www.leonardo.com/lsaf/sadl/SADL/vocabulary/sADL.oml").getAbsolutePath()), null);
    omlResource.load(null);
    Vocabulary vocabulary = (Vocabulary)  omlResource.getContents().get(0);
    assertThat(vocabulary.getIri()).isEqualTo("http://www.leonardo.com/lsaf/sadl/SADL/vocabulary/sADL");
    
    omlResource = resourceSet.createResource(URI.createFileURI(new File(ecore2oml.getTargetOmlDirectory() + "www.leonardo.com/lsaf/sadl/SADL/description/first.oml").getAbsolutePath()), null);
    omlResource.load(null);
    Description description = (Description)  omlResource.getContents().get(0);
    assertThat(description.getIri()).isEqualTo("http://www.leonardo.com/lsaf/sadl/SADL/description/first");
  }
  
  /***
   * Test sADL model from XMI to OML
   * 
   * @throws Exception
   */
  @Test
  void testSADLXmi2Oml() throws Exception {

    File model = new File("model/first.sadl.xmi");
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
        "../targetoml/src/oml/www.eclipse.org/emf/2002/Ecore/vocabulary/ecore.oml");
    String output = Files.readString(Path.of(omlVocabulary.getAbsolutePath()), StandardCharsets.UTF_8);
    assertThat(output).contains("Ecore");

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
    Vocabulary vocabulary = (Vocabulary) omlResource.getContents().get(0);
    assertThat(vocabulary.getIri()).contains("www.leonardo.com/lsaf/sadl/SADL/vocabulary/sADL");
  }

//  /***
//   * Test OML code to OML XMI
//   * 
//   * @throws Exception
//   */
//  @Test
//  void testOmlCode2OmlXmi() throws Exception {
//
//    File model = new File("model/java.oml");
//
//    Ecore2Oml ecore2oml = new Ecore2Oml();
//    ecore2oml.omlCodeToOmlXmi(model);
//
//    // assert
//    XMIResource resource = new XMIResourceImpl(URI.createFileURI("model/sADL.xmi"));
//    resource.load(null);
//    assertThat(resource.getContents().size()).isGreaterThan(0);
//  }
//  
//  /***
//   * Test OML XMI to OML code
//   * 
//   * @throws Exception
//   */
//  @Test
//  void testOmlXmi2OmlCode() throws Exception {
//
//    File model = new File("model/sADL.xmi");
//
//    Ecore2Oml ecore2oml = new Ecore2Oml();
//    ecore2oml.omlXmiToOmlCode(model);
//
//    
//  }
}
