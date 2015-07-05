package com.bioinformatica;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Exercise4 {
	
	private static final String TEMPORARY_FOLDER_OUTPUT_GETORF = "src/main/resources/exercise4/Ex4_temp_getorf/";
	private static final String TEMPORARY_FOLDER_OUTPUT_PATMATMOTIFS = "src/main/resources/exercise4/Ex4_temp_patmatmotifs/";
	private static final String OUTPUT_FILE = "src/main/resources/exercise4/Ex4_result.txt";
	private static final String FASTA_FILE = "src/main/resources/exercise4/ABCA12.fasta";
	
	public static void main(String[] args) {
		
		createTempDirectory(TEMPORARY_FOLDER_OUTPUT_GETORF);
		createTempDirectory(TEMPORARY_FOLDER_OUTPUT_PATMATMOTIFS);
		
		Path inputPath = Paths.get(FASTA_FILE);
		Path tempOutputGetorfPath = Paths.get(TEMPORARY_FOLDER_OUTPUT_GETORF);
		
		String getorfCommand = "getorf -sequence " + inputPath.toString() + " -table 0 -minsize 30 -maxsize 1000000 -find 0 -methionine -nocircular -reverse -flanking 100 -ossingle2 -osdirectory2 " + tempOutputGetorfPath.toString() + " -auto";
		executeSystemCommand(getorfCommand);
		
		String patmatmotifsCommand = null;

		Path tempEachSequencePath;
		Path tempOutputMotifPath;
		
		List<String> filenameList = getDirectoryFilenameList(TEMPORARY_FOLDER_OUTPUT_GETORF);
		int totalFiles = filenameList.size();
		int i = 1;
		
		for (String filename : getDirectoryFilenameList(TEMPORARY_FOLDER_OUTPUT_GETORF)) {
			
			System.out.println("Analizando archivo " + i + " de " + totalFiles);
			tempEachSequencePath = Paths.get(TEMPORARY_FOLDER_OUTPUT_GETORF + filename);
			tempOutputMotifPath = Paths.get(TEMPORARY_FOLDER_OUTPUT_PATMATMOTIFS + filename);
			
			patmatmotifsCommand = "patmatmotifs -sequence " + tempEachSequencePath.toString() + " -outfile " + tempOutputMotifPath.toString() + ".patmatmotifs -nofull -prune -rformat dbmotif -auto";
			executeSystemCommand(patmatmotifsCommand);
			i++;
		}
		
		List<Path> inputs = new ArrayList<Path>();

		for (String filename : getDirectoryFilenameList(TEMPORARY_FOLDER_OUTPUT_PATMATMOTIFS))
			inputs.add(Paths.get(TEMPORARY_FOLDER_OUTPUT_PATMATMOTIFS + filename));

		Path output = Paths.get(OUTPUT_FILE);

		Charset charset = StandardCharsets.UTF_8;

		for (Path path : inputs) {
			List<String> lines;
			
			try {
				lines = Files.readAllLines(path, charset);
				Files.write(output, lines, charset, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
		
		try {
			FileUtils.deleteDirectory(new File(TEMPORARY_FOLDER_OUTPUT_GETORF));
			FileUtils.deleteDirectory(new File(TEMPORARY_FOLDER_OUTPUT_PATMATMOTIFS));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		
		System.out.println("Task Finished");
		
	}

	private static void createTempDirectory(String path) {
		File file = null;

		try {
			file = new File(path);
			FileUtils.forceMkdir(file);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static void executeSystemCommand(String strCommand) {
		
		StringBuffer output = new StringBuffer();

		Process p;

		try {
			p = Runtime.getRuntime().exec(strCommand);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				output.append(line + System.getProperty("line.separator"));
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private static List<String> getDirectoryFilenameList(String directoryPath) {
		File folder = new File(directoryPath);
		File[] listOfFiles = folder.listFiles();

		List<String> fileNameList = new ArrayList<String>();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				fileNameList.add(listOfFiles[i].getName());
			}
		}

		return fileNameList;
	}
	
}