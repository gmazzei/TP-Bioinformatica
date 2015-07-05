package com.bioinformatica;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

public class Exercise3 {
	
	private static final String BLAST_REPORT_FILE_PATH = "src/main/resources/exercise3/BlastReport.txt";
	
	public static void main(String[] args) {

		File blastReport = new File(BLAST_REPORT_FILE_PATH);
		if (!blastReport.exists()) {
			throw new RuntimeException("Blast report does not exist");
		}

		try {
			String blastOutput;
			String lineSeparator = System.getProperty("line.separator");
			StringBuilder fileContents = new StringBuilder((int) blastReport.length());
			Scanner scanner = new Scanner((Readable) new BufferedReader(new FileReader(blastReport)));

			try {
				while (scanner.hasNextLine()) {
					fileContents.append(scanner.nextLine() + lineSeparator);
				}
				blastOutput = fileContents.toString();
			} finally {
				scanner.close();
			}

			String[] hits = blastOutput.split(">");
			String inputSequenceSeparator = StringUtils.repeat(lineSeparator, 4);


			for (int i = 0; i < hits.length; i++) {
				if (hits[i].indexOf(inputSequenceSeparator) >= 0) {
					hits[i] = hits[i].substring(0,	hits[i].indexOf(inputSequenceSeparator));
				}
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String pattern = "";
			boolean resultsFound;

			while (true) {
				System.out.println("Ingrese un patron para la busqueda: ");

				resultsFound = false;
				try {
					pattern = br.readLine();
					for (int i = 1; i < hits.length; i++) {
						if (StringUtils.containsIgnoreCase(hits[i], pattern)) {
							System.out.println(hits[i]);
							resultsFound = true;
						}
					}
					if (!resultsFound) {
						System.out.println("No se encontro ningun resultado");
					}
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		
	}
}