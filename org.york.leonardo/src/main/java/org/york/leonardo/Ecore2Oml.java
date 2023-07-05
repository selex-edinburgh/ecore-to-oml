package org.york.leonardo;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.egl.EglFileGeneratingTemplateFactory;
import org.eclipse.epsilon.egl.EgxModule;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.emc.emf.EmfUtil;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.etl.EtlModule;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.inject.Injector;
import com.leonardo.lsaf.sadl.SADLStandaloneSetup;

import io.opencaesar.oml.dsl.OmlStandaloneSetup;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "ecore2oml", mixinStandardHelpOptions = true, description = "A tool to generate OML descriptions and vocabularies from Ecore models/metamodels.")
public class Ecore2Oml implements Callable<Integer> {

	private Ecore2Oml ecore2Oml = this;

	private String targetOmlProjectDir = "../targetoml/";
	private String omlSourceDirInOmlProject = "src/oml/";
	private String targetOmlSourceDir = targetOmlProjectDir + omlSourceDirInOmlProject;

	private Map<String, String> dependencies = new HashMap<>();

	private static CommandLine commandLine;

	@Option(names = { "-m", "--model" }, description = "path to the source model (*.xmi, *.sadl)", required = true)
	private static String modelPath;

	@Option(names = { "-mm", "--metamodel" }, description = "path to the source metamodel (*.ecore)", required = true)
	private static String metamodelPath;

	@Option(names = { "-o",
			"--oml-project" }, description = "path to the target OML project, containing catalog.xml)", required = true)
	private static String omlProjectPath;

	@Option(names = { "-etl", "--etl-mode" }, description = "Use Epsilon Transformation Language (ETL) instead of "
			+ "Epsilon Generation Language (EGL)", required = false)

	private static boolean etlMode = false;

	@Override
	public Integer call() throws Exception { // your business logic goes here...

		System.out.println("Generating the OML files .. ");
		System.out.println("Source model: " + modelPath);
		System.out.println("Source metamodel: " + metamodelPath);
		System.out.println("Targt OML project: " + omlProjectPath);

		if (omlProjectPath.charAt(omlProjectPath.length() - 1) != '/') {
			omlProjectPath = omlProjectPath + File.separator;
		}
		ecore2Oml.setTargetOmlDirectory(omlProjectPath);

		String extension = modelPath.substring(modelPath.lastIndexOf("."), modelPath.length());

		if (etlMode) {
			if (extension.toLowerCase().equals(".xmi")) {
				ecore2Oml.xmiToOmlUsingETL(new File(modelPath), new File(metamodelPath));
			}
			if (extension.toLowerCase().equals(".sadl")) {
				ecore2Oml.sadlToOmlUsingETL(new File(modelPath), new File(metamodelPath));
			}
			return 1;
		}

		if (extension.toLowerCase().equals(".xmi")) {
			ecore2Oml.xmiToOml(new File(modelPath), new File(metamodelPath));
		}
		if (extension.toLowerCase().equals(".sadl")) {
			ecore2Oml.sadlToOml(new File(modelPath), new File(metamodelPath));
		}

		System.out.println("Done");

		return 0;
	}

	public static void main(String[] args) throws Exception {

		Ecore2Oml ecore2Oml = new Ecore2Oml();

		commandLine = new CommandLine(ecore2Oml);
		int systemExit = commandLine.execute(args);
		if (systemExit > 0) {
			System.exit(systemExit);
		}

	}

	public String getTargetOmlDirectory() {
		return targetOmlSourceDir;
	}

	public void setTargetOmlDirectory(String targetDirectory) {
		this.targetOmlSourceDir = targetDirectory;
		targetOmlSourceDir = targetOmlProjectDir + omlSourceDirInOmlProject;
	}

	public Map<String, String> getDependencies() {
		return dependencies;
	}

	/***
	 * Transform XMI model to OML using ETL.
	 * 
	 * @param sourceModelFile
	 * @param sourceMetamodelFile
	 * @throws Exception
	 */
	public void xmiToOmlUsingETL(File sourceModelFile, File sourceMetamodelFile) throws Exception {

		EmfModel xmiMetamodel = new EmfModel();
		xmiMetamodel.setModelFile(sourceMetamodelFile.getAbsolutePath());
		xmiMetamodel.setName("M2");
		xmiMetamodel.load();

		org.eclipse.emf.common.util.URI uri = org.eclipse.emf.common.util.URI
				.createFileURI(sourceMetamodelFile.getAbsolutePath());
		Registry registry = EPackage.Registry.INSTANCE;
		EPackage ePackage = (EPackage) xmiMetamodel.getResource().getContents().get(0);
		String nsPrefix = (ePackage.getNsPrefix() != null) ? ePackage.getNsPrefix() : ePackage.getName();
		String nsURI = ePackage.getNsURI();
		if (!registry.containsKey(nsURI)) {
			EmfUtil.register(uri, EPackage.Registry.INSTANCE);
		}

		String nsUri = ePackage.getNsURI();
		String baseNsUri = nsUri + "/vocabulary/base.oml";
		String vocabularyNsUri = nsUri + "/vocabulary/" + nsPrefix + ".oml";
		String descriptionName = sourceModelFile.getName().substring(0, sourceModelFile.getName().lastIndexOf("."));
		String descriptionNsUri = nsUri + "/description/" + descriptionName + ".oml";

		URI baseUri = URI.createURI(baseNsUri);
		URI vocabularyUri = URI.createURI(vocabularyNsUri);
		URI descriptionUri = URI.createURI(descriptionNsUri);

		File baseFile = createTargetFile(baseUri);
		File vocabularyFile = createTargetFile(vocabularyUri);
		File descriptionFile = createTargetFile(descriptionUri);

		EmfModel xmiModel = new EmfModel();
		xmiModel.setModelFile(sourceModelFile.getAbsolutePath());
		xmiModel.setName("M1");
		xmiModel.load();

		// initialise oml models
		Injector omlInjector = new OmlStandaloneSetup().createInjectorAndDoEMFRegistration();
		XtextResourceSet omfResourceSet = omlInjector.getInstance(XtextResourceSet.class);

		// target model
		Resource baseResource = omfResourceSet.createResource(URI.createFileURI(baseFile.getAbsolutePath()), null);
		EmfModel baseModel = new InMemoryEmfModel(baseResource);
		baseModel.setReadOnLoad(false);
		baseModel.setName("B");

		Resource vocabularyResource = omfResourceSet.createResource(URI.createFileURI(vocabularyFile.getAbsolutePath()),
				null);
		EmfModel vocabularyModel = new InMemoryEmfModel(vocabularyResource);
		baseModel.setReadOnLoad(false);
		vocabularyModel.setName("V");

		Resource descriptionResource = omfResourceSet
				.createResource(URI.createFileURI(descriptionFile.getAbsolutePath()), null);
		EmfModel descriptionModel = new InMemoryEmfModel(descriptionResource);
		descriptionModel.setReadOnLoad(false);
		descriptionModel.setName("D");

		File etlFile = new File("etl/ecore2oml.etl");
		EtlModule module = new EtlModule();
		module.parse(etlFile);

		module.getContext().getModelRepository().addModel(xmiMetamodel);
		module.getContext().getModelRepository().addModel(xmiModel);
		module.getContext().getModelRepository().addModel(baseModel);
		module.getContext().getModelRepository().addModel(vocabularyModel);
		module.getContext().getModelRepository().addModel(descriptionModel);

		// dependencies
		loadDependencies(omfResourceSet, module);

		// execute model
		module.execute();

		// save
		baseModel.store();
		vocabularyModel.store();
		descriptionModel.store();

	}

	@SuppressWarnings("unchecked")
	private void updateCatalogXml(Resource resource)
			throws IOException, SAXException, ParserConfigurationException, TransformerException {

		File catalogFile = new File(targetOmlProjectDir + "catalog.xml");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(catalogFile);
		Node catalogNode = doc.getFirstChild();
		NodeList nodes = catalogNode.getChildNodes();

		Set<String> iris = new HashSet<String>();
		Set<EPackage> ePackages = new HashSet<EPackage>();
		ePackages.addAll((Collection<? extends EPackage>) resource.getContents().stream().filter(e -> e instanceof EPackage).toList());
//		ePackages.addAll((Collection<? extends EPackage>) EPackage.Registry.INSTANCE.values());

		for (EPackage ePackage : ePackages) {
			for (EClassifier eClassifier : ePackage.getEClassifiers().stream().filter(c -> c instanceof EClass)
					.toList()) {
				EClass eClass = (EClass) eClassifier;
				for (EReference eReference : eClass.getEAllReferences()) {
					iris.add(eReference.getEType().getEPackage().getNsURI());
				}
			}
		}
//		System.out.println(iris.toString());

		A: for (String iri : iris) {
			int lastIndex = iri.lastIndexOf("/");
			if (lastIndex > -1)
				iri = iri.substring(0, lastIndex);

			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					String uriStartString = node.getAttributes().getNamedItem("uriStartString").getNodeValue();
					String iri2 = (iri.charAt(iri.length() - 1) != '/') ? iri + "/" : iri;
					if (iri2.equals(uriStartString)) {
						continue A;
					}
				}
			}

			Element rewriteURI = doc.createElement("rewriteURI");

			String uriStartString = (iri.charAt(iri.length() - 1) != '/') ? iri + "/" : iri;
			rewriteURI.setAttribute("uriStartString", uriStartString);

			String rewritePrefix = uriStartString;
			rewritePrefix = rewritePrefix.replace("http://", "");
			rewritePrefix = rewritePrefix.replace("https://", "");
			rewritePrefix = omlSourceDirInOmlProject + rewritePrefix;
			rewriteURI.setAttribute("rewritePrefix", rewritePrefix);

			catalogNode.appendChild(rewriteURI);

		}
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(catalogFile);

		transformer.transform(source, result);
	}

	/***
	 * Transform sADL model to OML using ETL.
	 * 
	 * @param sourceModelFile
	 * @param sourceMetamodelFile
	 * @throws Exception
	 */
	public void sadlToOmlUsingETL(File sourceModelFile, File sourceMetamodelFile) throws Exception {

		EmfModel sadlMetamodel = new EmfModel();
		sadlMetamodel.setModelFile(sourceMetamodelFile.getAbsolutePath());
		sadlMetamodel.setName("M2");
		sadlMetamodel.load();
		EPackage ePackage = (EPackage) sadlMetamodel.getResource().getContents().get(0);
		String nsPrefix = (ePackage.getNsPrefix() != null) ? ePackage.getNsPrefix() : ePackage.getName();

		String nsUri = ePackage.getNsURI();
		String baseNsUri = nsUri + "/vocabulary/base.oml";
		String vocabularyNsUri = nsUri + "/vocabulary/" + nsPrefix + ".oml";
		String descriptionName = sourceModelFile.getName().substring(0, sourceModelFile.getName().lastIndexOf("."));
		String descriptionNsUri = nsUri + "/description/" + descriptionName + ".oml";

		URI baseUri = URI.createURI(baseNsUri);
		URI vocabularyUri = URI.createURI(vocabularyNsUri);
		URI descriptionUri = URI.createURI(descriptionNsUri);

		File baseFile = createTargetFile(baseUri);
		File vocabularyFile = createTargetFile(vocabularyUri);
		File descriptionFile = createTargetFile(descriptionUri);

		Injector sadlInjector = new SADLStandaloneSetup().createInjectorAndDoEMFRegistration();
		XtextResourceSet sadlResourceSet = sadlInjector.getInstance(XtextResourceSet.class);
		Resource modelResource = sadlResourceSet.createResource(URI.createFileURI(sourceModelFile.getAbsolutePath()),
				null);
		modelResource.load(null);

		EmfModel sadlModel = new InMemoryEmfModel(modelResource);
		sadlModel.setName("M1");

		// initialise oml models
		Injector omlInjector = new OmlStandaloneSetup().createInjectorAndDoEMFRegistration();
		XtextResourceSet omfResourceSet = omlInjector.getInstance(XtextResourceSet.class);

		// target model
		Resource baseResource = omfResourceSet.createResource(URI.createFileURI(baseFile.getAbsolutePath()), null);
		EmfModel baseModel = new InMemoryEmfModel(baseResource);
		baseModel.setReadOnLoad(false);
		baseModel.setName("B");

		Resource vocabularyResource = omfResourceSet.createResource(URI.createFileURI(vocabularyFile.getAbsolutePath()),
				null);
		EmfModel vocabularyModel = new InMemoryEmfModel(vocabularyResource);
		baseModel.setReadOnLoad(false);
		vocabularyModel.setName("V");

		Resource descriptionResource = omfResourceSet
				.createResource(URI.createFileURI(descriptionFile.getAbsolutePath()), null);
		EmfModel descriptionModel = new InMemoryEmfModel(descriptionResource);
		descriptionModel.setReadOnLoad(false);
		descriptionModel.setName("D");

		File etlFile = new File("etl/ecore2oml.etl");
		EtlModule module = new EtlModule();
		module.parse(etlFile);

		module.getContext().getModelRepository().addModel(sadlMetamodel);
		module.getContext().getModelRepository().addModel(sadlModel);
		module.getContext().getModelRepository().addModel(baseModel);
		module.getContext().getModelRepository().addModel(vocabularyModel);
		module.getContext().getModelRepository().addModel(descriptionModel);

		// dependencies
		loadDependencies(omfResourceSet, module);

		// execute model
		module.execute();

		// save
		baseModel.store();
		vocabularyModel.store();
		descriptionModel.store();
	}

	private File createTargetFile(URI vocabularyUri) throws IOException {
		String targetPath = targetOmlSourceDir + vocabularyUri.authority();
		File targetDir = new File(targetPath);
		if (!targetDir.exists()) {
			targetDir.mkdir();
		}

		for (int i = 0; i < vocabularyUri.segments().length; i++) {
			String segment = vocabularyUri.segments()[i];
			targetPath = targetPath + "/" + segment;
			targetDir = new File(targetPath);
			if (i < vocabularyUri.segments().length - 1) {
				if (!targetDir.exists()) {
					targetDir.mkdir();
				}
			} else {
				if (!targetDir.exists()) {
					targetDir.createNewFile();
				}
			}

		}
		return targetDir;
	}

	private void loadDependencies(XtextResourceSet omfResourceSet, EtlModule module) throws IOException {
		for (Entry<String, String> entry : dependencies.entrySet()) {
			String prefix = entry.getKey();
			String iri = entry.getValue();
			String path = iri;
			path = path.replace("https://", "");
			path = path.replace("http://", "");
			while (path.charAt(path.length() - 1) == '/' || path.charAt(path.length() - 1) == '#') {
				path = path.substring(0, path.length() - 1);
			}
			path = "etl/lib/oml/" + path + ".oml";
			File libFile = new File(path);
			Resource libResource = omfResourceSet.createResource(URI.createFileURI(libFile.getAbsolutePath()), null);
			libResource.load(null);
			EmfModel libModel = new InMemoryEmfModel(libResource);
			libModel.setReadOnLoad(false);
			libModel.setName(prefix);

			module.getContext().getModelRepository().addModel(libModel);
		}
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

		getEPackages(metamodel);

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

		updateCatalogXml(metamodel.getResource());
	}

	private void getEPackages(EmfModel metamodel) {
		Map<String, EPackage> ePackages = new HashMap<>();
		TreeIterator<EObject> iterator = metamodel.getResource().getAllContents();
		while (iterator.hasNext()) {
			EObject eObject = iterator.next();
			if (eObject instanceof EClass) {
				EClass eClass = (EClass) eObject;
				for (EStructuralFeature eFeature : eClass.getEAllStructuralFeatures()) {
//					System.out.println(eFeature.getEType().getEPackage());
					ePackages.put(eFeature.getEType().getEPackage().getNsURI(), eFeature.getEType().getEPackage());
				}
			}
		}
		for (Entry<String, EPackage> entry : ePackages.entrySet()) {
			if (!metamodel.getResource().getContents().contains(entry.getValue())
					&& !EPackage.Registry.INSTANCE.containsKey(entry.getKey())) {
				metamodel.getResource().getContents().add(entry.getValue());
				this.getEPackages(metamodel);
			}
		}
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

		updateCatalogXml(internalEcoreResource);
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
