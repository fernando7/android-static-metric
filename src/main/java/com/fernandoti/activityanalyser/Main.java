package com.fernandoti.activityanalyser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Main {

	public static void main(String[] args) throws ParserConfigurationException, FileNotFoundException, SAXException, IOException {
		Map<String, String> params = new HashMap<>();
		
		for (String param : args) {
			if (param.startsWith("--")) {
				String[] nameAndValue = param.split("=");
				params.put(nameAndValue[0], nameAndValue[1]);
			}
		}
		
		if (params.get("--source") == null) 
			throw new IllegalArgumentException("Param --source not found");
		
		File source = new File(params.get("--source"));
		Element elementManifest = getElementRootManifest(source);
		String packageName = elementManifest.getAttribute("package");
		List<Activity> activities = listActivities(source, packageName, elementManifest);
		
		for (Activity activity : activities) {
			activity.analyser(source.getAbsolutePath());
			
			System.out.println("Activity: " + activity.getName());
			
			Map<String, Integer> importUse = activity.getImportUse();
			Set<String> classies = importUse.keySet();
			for (String className : classies) {
				System.out.printf("\tClass Name: %s, Total Use: %d%n", className, importUse.get(className));
			}
			
//			List<String> imports = activity.getImports();
//			for (String string : imports) {
//				System.out.println("\t" + string);
//			}
			
		}
	}

	private static List<Activity> listActivities(File source, String packageName, Element elementManifest)
			throws FileNotFoundException {
		
		NodeList nodesActivitys = elementManifest.getElementsByTagName("activity");
		System.out.println("List activities from manifest");
		
		List<Activity> activities = new ArrayList<>();
		
		for (int i = 0; i < nodesActivitys.getLength(); i++) {
			Element item = (Element) nodesActivitys.item(i);
			String activityName = item.getAttribute("android:name");
			
			if (activityName.startsWith("."))
				activities.add(new Activity(String.format("%s%s", packageName, activityName)));
			else
				activities.add(new Activity(activityName));
		}
		
		return activities;
	}

	private static Element getElementRootManifest(File source)
			throws ParserConfigurationException, SAXException, IOException, FileNotFoundException {
		File androidManifest = new File(source, "AndroidManifest.xml");
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(new FileInputStream(androidManifest));
		Element elementManifest = document.getDocumentElement();
		return elementManifest;
	}
}
