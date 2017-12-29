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
	int iteration = 0;
    BufferedReader buffer;
    int chunkSize = 2;
    boolean didFinishReading = false;
    String tmpPath;
    
    
    @Override
    public void sortFile(String in, String out, String tmpPath) {
    		this.tmpPath = tmpPath;
    		createFirstFolder(in); // create first folder with smallest files
    		mergeSort(); //merge all the small files that were created to a big sorted one recursively.
    		flushPreviousDir();
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
    				createOneSortedFile(tmpFile, currentChunk);
    				counter++;
    				currentChunk = readNextChunk();
    			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		iteration++; //procceed to the next iteration
		counter = 0; //I think this makes sense here
    }
    /*
     * TODO - implement this function. 
     * Assumption - 
     * the first folder already exists, with all the input file broken into sorted small files
     * 
     */
    public void mergeSort() { 
    		
    }

    public void merge() {
    	
    }
    //====================================================
    // FILE UTILS
    //====================================================
    public void createDirectoryForIteration() {
    		File dir = new File(this.tmpPath + "/" + String.valueOf(iteration));
    		dir.mkdir();
    }
    public String getPath(int iteration, int fileNumber) {
    		return this.tmpPath + "/" + String.valueOf(iteration) + "/" + String.valueOf(fileNumber);
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

    
    public void createOneSortedFile(File tmpFile, ArrayList<String> chunk) {
    	try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile));
			ArrayList<String> sorted = sortChunk(chunk);
			for (int i = 0; i < sorted.size(); i++) {
				System.out.print(sorted.get(i));
				bw.write(sorted.get(i));
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void mergeToFiles(String first, String second) {
    		if (second == null) {
    			return;
    		} else {
    			
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
