package com.zoho.files.task;
import java.io.BufferedReader;
import java.util.Properties;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class FileTask extends Exception{
		public void FileOperation(String directory,String fileName,String[] content)throws IOException {
			if(directory == null || directory.isEmpty()) {
				directory = System.getProperty("user.dir");
			}
			File directoryObj = new File(directory);
			makeDirectory(directoryObj);
			File file = new File(fileName);
			createFile(file);
			for(String text : content) {
				writeFile(file,text);
			}
		}
		public File getFileObject (String directory,String fileName)throws IOException {
			if(directory == null || directory.isEmpty()) {
				directory = System.getProperty("user.dir");
			}
			File directoryObj = new File(directory);
			makeDirectory(directoryObj);
			File file = new File(fileName);
			createFile(file);
			return file;
		}
		public boolean createFile(File file)throws IOException{
			return file.createNewFile();
		}
		public void writeFile(File file,String text)throws IOException{
			try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
				writer.write(text);
			}
		}
		public Properties getNewProperties() {
			return new Properties();
		}
		public void setPropertyValue(Properties properties, String key, String value) throws IOException{
			properties.setProperty(key, value);
		}
		public void storeProperty(Properties properties,File file,String comment)throws IOException {
			try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
			properties.store(writer, comment);
			}
		}
	    public void loadProperty(Properties properties,File file) throws IOException{
	    	try(BufferedReader reader = new BufferedReader(new FileReader(file))){
	    	properties.load(reader);
	    	}
	    }
	    public boolean makeDirectory(File file) {
	    	return file.mkdirs();
	    }
}
