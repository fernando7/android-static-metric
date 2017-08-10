package com.fernandoti.activityanalyser;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class MainTest {

	public static void main(String[] args) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
		Main.main(new String[]{"--source=/home/fernando/Documentos/Programação/Projetos Android/check-list/app/src/main"});
	}
}
