package com.bioinformatica;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.biojava.nbio.ws.alignment.qblast.BlastOutputFormatEnum;
import org.biojava.nbio.ws.alignment.qblast.BlastProgramEnum;
import org.biojava.nbio.ws.alignment.qblast.NCBIQBlastAlignmentProperties;
import org.biojava.nbio.ws.alignment.qblast.NCBIQBlastOutputProperties;
import org.biojava.nbio.ws.alignment.qblast.NCBIQBlastService;
import org.biojava3.core.sequence.ProteinSequence;
import org.biojava3.core.sequence.io.FastaReaderHelper;
import org.biojava3.core.sequence.io.util.IOUtils;

public class Exercise2 {

	private final static String FASTA_FILE_PATH = "src/main/resources/exercise2/ABCA12.fasta";
	private final static String BLAST_REPORT_FILE_PATH = "src/main/resources/exercise2/BlastReport.txt";

	public static void main(String[] args) {

		Map<String, ProteinSequence> aaSequence = null;
		try {
			File fastaInputFile = new File(FASTA_FILE_PATH);
			aaSequence = FastaReaderHelper.readFastaProteinSequence(fastaInputFile);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		ProteinSequence correctProteinSequence = aaSequence.get(aaSequence.keySet().toArray()[0]);

		//Blast Services init
		NCBIQBlastService service = new NCBIQBlastService();
		NCBIQBlastAlignmentProperties props = new NCBIQBlastAlignmentProperties();
		props.setBlastProgram(BlastProgramEnum.blastp);
		props.setBlastDatabase("swissprot");
		NCBIQBlastOutputProperties outputProps = new NCBIQBlastOutputProperties();
		outputProps.setOutputFormat(BlastOutputFormatEnum.Text);
		
		
		FileWriter writer = null;
		BufferedReader reader = null;
		String requestId = null;
		
		try {
			requestId = service.sendAlignmentRequest(correctProteinSequence.getSequenceAsString(), props);

			System.out.println("Waiting for results");
			while (!service.isReady(requestId));
			System.out.println("Done");
			
			InputStream in = service.getAlignmentResults(requestId, outputProps);
			reader = new BufferedReader(new InputStreamReader(in));

			
			//Blast Report
			File blastReportFile = new File(BLAST_REPORT_FILE_PATH);
			writer = new FileWriter(blastReportFile);

			String line;
			while ((line = reader.readLine()) != null) {
				writer.write(line + System.getProperty("line.separator"));
			}

			System.out.println("Task Finished");

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			IOUtils.close(writer);
			IOUtils.close(reader);
			service.sendDeleteRequest(requestId);
		}
	}
}