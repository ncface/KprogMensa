package test;


import io.DataCollection;
import org.junit.After;

import java.io.File;
import java.util.ArrayList;

//für IntelliJ
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

//für Eclipse
//import org.junit.*;
//import static org.junit.Assert.*;

/**
 * Test class for the testing class DataCollection
 * @author Clauss, Hanselmann, Herzog, Rietzler
 * @version 2017-11-29
 */

public class DataCollectionTest {

	/**
	 * method that gets executed after all test methods have been completed
	 */
	@After
	public void tearDown() {
		deleteAllFiles();
	}

	/**
	 * Test to test if all files in the DataOutput directory are deleted correctly
	 */
	@Test
	public void testNoFilesExistBefore() {
		deleteAllFiles();
		boolean filesExist = checkFilesExist();
		DataCollection.prepareDataCollection();
		assertEquals(!filesExist,checkFilesExist());
	}

	/**
	 * Test to test if the data files are prepared correctly
	 */
	@Test
	public void testFilesExistBefore() {
		boolean filesExist = checkFilesExist();
		DataCollection.prepareDataCollection();
		assertEquals(filesExist,checkFilesExist());
	}

	@SuppressWarnings("Duplicates")
	private void deleteAllFiles(){
		//delete all files
		File outPutFolder = new File(DataCollection.getOutFolderPath() );
		if (outPutFolder.exists()) {
            File[] files = outPutFolder.listFiles();
            for (File file : files) {
                file.delete();//delete the files
            }
        }else{
            outPutFolder.mkdir();//create folder if not existing
        }
	}
	/**
	 * checks if all relevant files exist
	 * @return false if any file does`nt exist
	 */
	private boolean checkFilesExist() {
		//check if files exist
		ArrayList<File> outputFiles = new ArrayList<File>();
		//add all to checking files
		outputFiles.add(new File(DataCollection.getFilePathLeftEarly()));
		outputFiles.add(new File(DataCollection.getFilePathAdditionalStation()));
		outputFiles.add(new File(DataCollection.getFilePathMoneyLoss()));
		outputFiles.add(new File(DataCollection.getFilePathNumberCustomers()));
		outputFiles.add(new File(DataCollection.getFilePathOperatingCosts()));
		
		//check if they exist
		for(File outputFile: outputFiles)
			if(!outputFile.exists() || outputFile.isDirectory()) { 
			    return false;
			}
		return true;
	}

}
