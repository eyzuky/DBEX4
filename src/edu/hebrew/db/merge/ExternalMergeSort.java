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
    int chunkSize = 100;
    boolean didFinishReading = false;
    String tmpPath;
    
    
    @Override
    public void sortFile(String in, String out, String tmpPath) {
    		
    		//save the temporary path
    		this.tmpPath = tmpPath;
    		
    		// create first folder with smallest files
    		createFirstFolder(in); 
    		
    		//merge all the small files that were created to a big sorted one recursively.
    		//this function will write the sorted data to the out file.
    		mergeSort(out); 
    		
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
    		
    		while(true) {
    			MergeTwoFiles files = getNextFilesToMerge();
    			if (files.file1 == null && files.file2 == null) {
    				break;
    			} else if (files.file1 != null && files.file2 == null) {
    				//do something with 1 file (happens on the last file when we have odd ammount of files)
    			} else if (files.file1 != null & files.file2 != null) {
    				merge(files.file1, files.file2);
    			}
    		}
    }
    
    public void merge(File file1, File file2) {
    	
    }
    public MergeTwoFiles getNextFilesToMerge() {
    	
    		MergeTwoFiles files = new MergeTwoFiles();
    		
    		String path1 = getPath(iteration - 1, counter); //go to previous iteration and get the next files
    		String path2 = getPath(iteration - 1, counter + 1);
    		
    		files.file1 = new File(path1);
    		files.file2 = new File(path2);
    		
    		return files;
    }
    private class MergeTwoFiles {
    		File file1;
    		File file2;
    		
    }
    
    
    
    //====================================================
    // FILE SYSTEM HELPER FUNCTIONS
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
//HELPER FUNCTIONS FOR CREATING THE FIRST ITERATION
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
