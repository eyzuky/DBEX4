package edu.hebrew.db.merge;

import java.io.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
public class ExternalMergeSort implements IMergeSort {

	int counter = 0; 
	int counterForRead = 0;
	int counterForWrite = 0;
	int iteration = 0;
    BufferedReader buffer;
    int chunkSize = 1000;
    boolean didFinishReading = false;
    String tmpPath;
    String outPath;
    ArrayList<File> arrayOfFiles;
    
    @Override
    public void sortFile(String in, String out, String tmpPath) {
    		long a = System.currentTimeMillis();
    		arrayOfFiles = new ArrayList<File>();
    		//save the temporary path
    		this.tmpPath = tmpPath;
    		this.outPath = out;
    		
    		
    		
    		// create first folder with smallest files
    		createFirstFolder(in); 
    		

    		
    		createDirectoryForIteration();
    		//merge all the small files that were created to a big sorted one recursively.
    		//this function will write the sorted data to the out file.
    		mergeSort(out); 
    		System.out.println( (System.currentTimeMillis() - a) / 60000.0);
    }
    

    /*
     * function that creates a folder with the smallest files
     */
    public void createFirstFolder(String in) {
		File file = new File(in);
		createDirectoryForIteration();
		FileReader reader;
		try {
			reader = new FileReader(file);
    			buffer = new BufferedReader(reader);
    			ArrayList<String> currentChunk = readNextChunk();
    			while (!didFinishReading) {		
	    			File tmpFile = new File(getPath(0, counter)); // iteration is 0 
	    			System.out.println(counter);
    				createOneSortedFile(tmpFile, currentChunk);
    				counter++;
    				currentChunk = readNextChunk();
    			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		iteration++; //procceed to the next iteration
    }
    
    /*
     * 
     * TODO - implement this function. 
     * 
     * Assumption - 
     * the first folder already exists, with all the input file broken into sorted small files
     * 
     * when function finishes running, the path out will contain the sorted 1 last file
     */
    public void mergeSort(String out) { 
    		File file = new File(out); // write the end result to this file
    		while(counter > 0) {
    			
    			MergeTwoFiles files = getNextFilesToMerge(); //MOR - THIS HAS TO RETURN AN ARRAY OF FILES INSTEAD OF CLASS WITH 2 FILES, IF YOU WANT TO INCREASE TO MORE FILES
    			String pathToOutMerge = getPath(iteration, counterForWrite);
    			counterForWrite++;
    			if (files.file1 == null && files.file2 == null) {
    				System.out.println("-"+counter);
    				counter /= 2; //MOR - CHANGE THIS IF YOU INCREASE FROM 2 FILES TO MORE!
    				counterForRead = 0;
    				counterForWrite = 0;
    				if (counter == 0) {
    					File finalFile = new File(getPath(iteration, 0));
    					emitOutResult(finalFile, new File(out));
    					break;
    				}

    				iteration++;
    				
    				createDirectoryForIteration();
    				
    			} else if (files.file1 != null && files.file2 == null) {
    				//do something with 1 file (happens on the last file when we have odd amount of files)
    				counterForWrite--;
    				transfer(files.file1);
    			} else if (files.file1 != null && files.file2 != null) {
    				merge(files.file1, files.file2, new File(pathToOutMerge));
    			}
    		}
    		
    }
    
    public void merge(File file1, File file2, File outFile) { //MOR - THIS HAS TO CHANGE TO MAYBE GET AN ARRAY OF FILES IF YOU WANT TO INCREASE TO MORE THAN 2 FILES
    		try {
    			//read the files into array line by line lexicographically 
				FileReader reader1 = new FileReader(file1);
				FileReader reader2 = new FileReader(file2);
				BufferedReader br1 = new BufferedReader(reader1);
				BufferedReader br2 = new BufferedReader(reader2);
				String line1;
				String line2;
				try {
					line1 = br1.readLine();
					line2 = br2.readLine();
					ArrayList<String> tempArray = new ArrayList<String>();
					BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));

					while(line1 != null || line2 != null) {
						if (line1 != null && line2 != null) {
							int answer = line1.compareTo(line2); //MOR - FOR EXAMPLE, HERE YOU SHOULD COMPARE BETWEEN MULTIPLE LINES AND NOT ONLY 2, MAYBE YOU CAN SORT THEM AND TAKE THE FIRST OR SOMETHING
							if (answer < 0) {
								bw.write(line1);
								bw.newLine();
								line1 = br1.readLine(); // read next line
							} else if (answer >= 0){
								bw.write(line2);
								bw.newLine();
								line2 = br2.readLine(); //read next line 
							}
						} else if (line1 == null && line2 != null){ //MOR - MAKE SURE YOU CHECK ALL SITUATIONS IF YOU INCREASE FILES AMOUNT
							bw.write(line2);
							bw.newLine();
							line2 = br2.readLine(); //read only next in file 2!3499105\\\///
						} else if (line2 == null && line1 != null) {
							bw.write(line1);
							bw.newLine();
							line1 = br1.readLine(); //read only next in file 1
						}
					}
						
				bw.close();					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
    public MergeTwoFiles getNextFilesToMerge() { //MOR - THIS SHOULD RETURN AN ARRAY OF FILES
    		MergeTwoFiles files = new MergeTwoFiles();
    		
    		String path1 = getPath(iteration - 1, counterForRead);//go to previous iteration and get the next files
    		
    		counterForRead++;
    		String path2 = getPath(iteration - 1, counterForRead);
    		counterForRead++;
    		File file1 = new File(path1);
    		File file2 = new File(path2);
        	files.file1 = file1;
        	files.file2 = file2;
    		if (file1.length() == 0) {
    			files.file1 = null;
    		}
    		if (file2.length() == 0) {
    			files.file2 = null;
    		}
    		return files;
    }
    public void emitOutResult(File in, File out) {
			try {
				int countera = 0;
				FileWriter writer = new FileWriter(out);
				BufferedWriter bw1 = new BufferedWriter(writer);
		    		FileReader reader = new FileReader(in);
			
				BufferedReader br1 = new BufferedReader(reader);
				String line = br1.readLine();
				while(line != null) {
					countera++;
					bw1.write(line);
					line = br1.readLine();
					if (line != null) {
						bw1.newLine();
					}
				}
				System.out.println(countera);
				br1.close();
				bw1.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
	}
    
    public void transfer(File file) { //MOR - THIS FUNCTION TRANSFERS A FILE TO THE NEXT ITERATION IF IT HAD NO PARTNER TO MERGE (E.G ODD AMOUNT OF FILES), MAKE SURE YOU UPDATE IT
    		
    		FileReader reader;
			String dirPath = this.tmpPath + String.valueOf(iteration);
			File dir = new File(dirPath);
			if (dir.isDirectory()) {
				String path = getPath(iteration, counterForWrite);
				File toWrite = new File(path);
				try {
					FileWriter writer = new FileWriter(toWrite);
					BufferedWriter bw1 = new BufferedWriter(writer);
					reader = new FileReader(file);
					BufferedReader br1 = new BufferedReader(reader);
					String line = br1.readLine();
					while(line != null) {
						bw1.write(line);
						bw1.newLine();
						line = br1.readLine();
					}
					bw1.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
    }

    private class MergeTwoFiles {
    		File file1;
    		File file2;
    		
    }
    
    
    
    //====================================================
    // FILE SYSTEM HELPER FUNCTIONS
    //====================================================
    public boolean createDirectoryForIteration() {
    	
    		File dir = new File(this.tmpPath + String.valueOf(iteration));
    		if (dir.isDirectory()) {
    			return false; // already exists, we don't create a new one
    		} else {
    			dir.mkdir();
    			return true;
    		}
    }
    public String getPath(int iteration, int fileNumber) {
    		return this.tmpPath + String.valueOf(iteration) + "/" + String.valueOf(fileNumber);
    }
    /*
     * delete previous directory since we will not use it anymore
     */
    public void flushPreviousDir () {
    		if (iteration > 0) {
    			String iterationToDelete = String.valueOf(iteration - 1);
    			String pathToDelete = this.tmpPath + "/" + String.valueOf(iterationToDelete);
    			File dir = new File(pathToDelete);
    			if (dir.isDirectory()) {
    				File[] filesInDir = dir.listFiles();
    				for (int i = 0; i < filesInDir.length; i++) {
    					filesInDir[i].delete();
    				}
    				dir.delete();
    			}
    		}
    }
    
//====================================================
//HELPER FUNCTIONS FOR CREATING THE FIRST ITERATION
//====================================================

    public void createOneSortedFile(File tmpFile, ArrayList<String> chunk) {
    	try {
    			arrayOfFiles.add(tmpFile);
			BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile));
			ArrayList<String> sorted = sortChunk(chunk);
			for (int i = 0; i < sorted.size(); i++) {
				bw.write(sorted.get(i));
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public ArrayList<String> sortChunk(ArrayList<String> chunk) {
    		ArrayList<String> toBeSorted = chunk;
    	    Collections.sort(toBeSorted);
    	    return toBeSorted;
    }

    public ArrayList<String> readNextChunk() {
    		if (didFinishReading) {
    			return null;
    		}
    		ArrayList<String> array = new ArrayList<String>();
    		try {
	    			//StringBuffer stringBuffer = new StringBuffer();
	    			String line;
	    			for (int i = 0; i < chunkSize; i++) {
	    				line = buffer.readLine();
	    				if (line != null) {
	    					array.add(line);
	    				} else {
	    					didFinishReading = true;
	    					return array;
	    				}
	    			}
	    			return array;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return array;
    }
    
}
//====================================================
