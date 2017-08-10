package com.fernandoti.activityanalyser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Activity {

	private final String name;
	private List<String> imports;
	private List<String> classDependencies;
	private Map<String, Integer> importUse;
	
	public Activity(String name) {	
		this.name = name;
	}
	
	public void analyser(String dirBase) throws FileNotFoundException {
		String fileName = String.format("java/%s.java", name.replaceAll("\\.", "/"));
		File fileActivity = new File(dirBase, fileName);
		String builder = fileToString(fileActivity);
		listImports(builder);
		
		classDependencies = new ArrayList<>();
		
		for (String importName : imports) {
			int lastIndexOf = importName.lastIndexOf('.');
			classDependencies.add(importName.substring(lastIndexOf+1, importName.length()-1));
		}
		
		importUse = new HashMap<>();
		
		for (String classDependency : classDependencies) {
			Pattern pattern = Pattern.compile(Pattern.quote(classDependency));
			Matcher matcher = pattern.matcher(builder);
			
			while (matcher.find()) {
				String className = matcher.group();
				Integer total = importUse.get(className);
				if (total == null)
					importUse.put(className, 0);
				else
					importUse.put(className, total+1);
			}
		}
	}
	
	public Map<String, Integer> getImportUse() {
		return importUse;
	}

	private String fileToString(File fileActivity) throws FileNotFoundException {
		StringBuilder builder = new StringBuilder();
		
		try (Scanner scanner = new Scanner(fileActivity)) {
			
			while (scanner.hasNextLine()) {
				builder.append(scanner.nextLine())
				.append(String.format("%n"));
			}
		}
		return builder.toString();
	}

	private void listImports(String fileToString) {
		Pattern pattern = Pattern.compile("import [_a-zA-Z]+?\\..+");
		Matcher matcher = pattern.matcher(fileToString);
	
		imports = new ArrayList<>();
		
		while (matcher.find()) {
			imports.add(matcher.group());
		}
	}
	
	public List<String> getImports() {
		return imports != null ? imports : Collections.<String>emptyList();
	}
	
	public String getName() {
		return name;
	}
}
