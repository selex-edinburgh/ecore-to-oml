package org.york.leonardo.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.eclipse.bpmn2.util.Bpmn2ResourceFactoryImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.york.leonardo.Ecore2Oml;

import com.google.inject.Injector;

import io.opencaesar.oml.dsl.OmlStandaloneSetup;

class Bpmn2OmlTest {

	private static Injector injector;
	private static OmlStandaloneSetup omlSetup;

	@BeforeAll
	public static void initialisation() {
		omlSetup = new OmlStandaloneSetup();
		injector = omlSetup.createInjectorAndDoEMFRegistration();
	}

	/***
	 * Test BPMN2 model to OML
	 * 
	 * @throws Exception
	 */
	@Test
	void testBpmn2Oml() throws Exception {

		File model = new File("model/bpmn.bpmn");
		File metamodel = new File("model/BPMN20.ecore");

		// BPMN-specific initialisation
		Registry registry = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> extensionToFactoryMap = registry.getExtensionToFactoryMap();
		extensionToFactoryMap.put("bpmn", new Bpmn2ResourceFactoryImpl());

		// create ecore to oml
		Ecore2Oml ecore2oml = new Ecore2Oml();
		ecore2oml.xmiToOml(model, metamodel);

		// assert
		File omlVocabulary = new File("../targetoml/src/oml/www.omg.org/spec/BPMN/20100524/MODEL-XMI/vocabulary/bpmn2.oml");
		String output = Files.readString(Path.of(omlVocabulary.getAbsolutePath()), StandardCharsets.UTF_8);
		assertThat(output).contains("BPMN");

		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
		Resource omlResource = resourceSet.createResource(URI.createFileURI(omlVocabulary.getAbsolutePath()), null);
		omlResource.load(null);
		assertThat(omlResource.getContents().size()).isGreaterThan(0);
	}

}
