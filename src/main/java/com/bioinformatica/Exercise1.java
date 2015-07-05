package com.bioinformatica;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.ProteinSequence;
import org.biojava3.core.sequence.RNASequence;
import org.biojava3.core.sequence.io.FastaWriterHelper;
import org.biojava3.core.sequence.io.GenbankReaderHelper;
import org.biojava3.core.sequence.transcription.Frame;

public class Exercise1 {
	
	private final static String GENEBANK_FILE_PATH = "src/main/resources/exercise1/ABCA12.gb";
	private final static String FASTA_FILE_PATH = "src/main/resources/exercise1/ABCA12.fasta";

	public static void main(String[] args) {

		//Get genebank file
		Map<String, DNASequence> dnaSequences = null;
		File geneBankFile = new File(GENEBANK_FILE_PATH);
		
		try {
			dnaSequences = GenbankReaderHelper.readGenbankDNASequence(geneBankFile);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		List<RNASequence> orfTranlationList = new LinkedList<RNASequence>();
		List<ProteinSequence> aaSequenceList = new LinkedList<ProteinSequence>();
		for (DNASequence dnaSequence : dnaSequences.values()) {
			orfTranlationList.add(dnaSequence.getRNASequence(Frame.ONE));
			orfTranlationList.add(dnaSequence.getRNASequence(Frame.TWO));
			orfTranlationList.add(dnaSequence.getRNASequence(Frame.THREE));

			orfTranlationList.add(dnaSequence.getRNASequence(Frame.REVERSED_ONE));
			orfTranlationList.add(dnaSequence.getRNASequence(Frame.REVERSED_TWO));
			orfTranlationList.add(dnaSequence.getRNASequence(Frame.REVERSED_THREE));

			for (RNASequence arnSequence : orfTranlationList) {
				aaSequenceList.add(arnSequence.getProteinSequence());
			}

			Integer index = 1;
			for (ProteinSequence aaSequence : aaSequenceList) {
				aaSequence.setOriginalHeader(index.toString());
				index++;
			}
			
			
			//Write result to Fasta file
			File outputFASTA = new File(FASTA_FILE_PATH);

			try {
				outputFASTA.createNewFile();
				FastaWriterHelper.writeProteinSequence(outputFASTA, aaSequenceList);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}

			System.out.println("Task Finished");
		}
	}
}