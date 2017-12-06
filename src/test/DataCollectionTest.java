package test;


import io.DataCollection;
import org.junit.After;
import org.junit.jupiter.api.Test;


import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataCollectionTest {
	@Test
	public void testNoFilesExistBefore() {
		deleteAllFiles();
		boolean filesExist = checkFilesExist();
		DataCollection.prepareDataCollection();
		assertEquals(!filesExist,checkFilesExist());
	}

	@Test
	public void testFilesExistBefore() {
		boolean filesExist = checkFilesExist();
		DataCollection.prepareDataCollection();
		assertEquals(filesExist,checkFilesExist());
	}
	
	@After
	public void tearDown() throws Exception {
		deleteAllFiles();
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
	 * @return false if any file doesnt exist
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
