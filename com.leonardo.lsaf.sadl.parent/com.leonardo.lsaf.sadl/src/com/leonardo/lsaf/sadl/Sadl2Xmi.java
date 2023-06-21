package com.leonardo.lsaf.sadl;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Injector;

public class Sadl2Xmi {

	public static void main(String[] args) throws IOException {
		Sadl2Xmi sadl2xmi = new Sadl2Xmi();
		sadl2xmi.toXmi("hello.sadl", "hello.sadl.xmi");
		System.out.println("Finished!");
	}

	public void toXmi(String sadlpath, String xmipath) throws IOException {
		Injector injector = new SADLStandaloneSetup().createInjectorAndDoEMFRegistration();
		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);

		File sadlFile = new File(sadlpath);
		File xmiFile = new File(xmipath);

		Resource omlResource = resourceSet.createResource(URI.createFileURI(sadlFile.getAbsolutePath()), null);

		omlResource.load(null);

		EcoreUtil.resolveAll(omlResource);

		Resource xmiResource = resourceSet.createResource(URI.createFileURI(xmiFile.getAbsolutePath()), null);
		xmiResource.getContents().addAll(EcoreUtil.copyAll(omlResource.getContents()));
		try {
			omlResource.save(null);
			xmiResource.save(null);

			omlResource.unload();
			xmiResource.unload();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
