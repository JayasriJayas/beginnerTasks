package com.zoho.files.runner;
import com.zoho.files.task.FileTask;
import com.zoho.files.singleton.SerializationSingleton;
import com.zoho.files.singleton.BillPugh;
import com.zoho.files.time.Time;
import com.zoho.files.applicant.Applicant;
import com.zoho.files.customer.Customer;
import com.zoho.files.rainbow.RainbowColors;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;

class Runner {
	static final Logger logger = Logger.getLogger(Runner.class.getName());
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int count,lines;
       
        String key,value,applicantName,DOB,address,directory;
        int choice,applicantNo,age;
        File sample,propertiesFile,fileObj;
        String fileName;
        FileTask task = new FileTask(); 
        Time time = new Time();
        Customer customer;
       
        
            
        try {
        	do {
                logger.info("1. Create a file");
                logger.info("2.Write properties and load properties in file");
                logger.info("3.Store back properties from file and print it");
                logger.info("4.In given directory crete file and properties ");
                logger.info("5.Use Instance to print the string");
                logger.info("6.Create a Pojo class and print its instance");
                logger.info("7.Invoke getter and setter methods");
                logger.info("8.Using reflection in invoking constructors,methods");
                logger.info("9.Create enum and print its oridinal and constants");
                logger.info("10.Singleton class pattern");
                logger.info("11.Time Calculations");
                logger.info("Enter your choice (Enter -1 to exit)");
                choice = sc.nextInt();
            switch(choice) {
                case 1:
                	logger.info("Enter directory");
                	directory = sc.next();
                    logger.info("Enter the filename");
                    fileName=sc.next();
                    logger.info("Enter the numbe of lines to write");
                	lines = sc.nextInt();
                	sc.nextLine();
                	String[] text = new String[lines];
                	logger.info("Enter the text");
                	for(int i=0;i<lines;i++) {
                		text[i] = sc.nextLine();
                	}
                	task.FileOperation(directory, fileName, text);
                	logger.info("Data written to file ");

                	logger.info("Enter the file name with .properties");
                	fileName=sc.next();
                    Properties prop = task.getNewProperties();
                    logger.info("No of properties ");
                	count = sc.nextInt();
                	for(int i=0;i<count;i++) {
                		logger.info("Enter the keys and values");
                		key = sc.next();
                		value= sc.next();  
                		task.setPropertyValue(prop, key, value);
            		}
                	fileObj = task.getFileObject(directory,fileName);
                    task.storeProperty(prop, fileObj, "File Properties");
                    
                    logger.info("Data written to file");
                    
                    Properties readProps = new Properties();
                    fileObj = task.getFileObject(directory,fileName);
                    task.loadProperty(readProps, fileObj);
                    for (Map.Entry<Object, Object> entry : readProps.entrySet()) {
                    	logger.info(entry.getKey() + " : " + entry.getValue());
                    }
                    
                    break;
                    
                case 2:
                	logger.info("Enter the Customer name");
                	String name=sc.next();
                	customer = new Customer(name);
                	logger.info("Customer Details"+ customer);
                	break;
                case 3:
              
                	logger.info("Enter the applicant name,no,age,DOB,address");
                    applicantName = sc.next();  
                	applicantNo = sc.nextInt();
                	age = sc.nextInt();
                	DOB = sc.next();
                	address = sc.next();
                	Applicant applicant = new Applicant(applicantName,applicantNo,age,DOB,address);
                	logger.info("Applicant details"+applicant);
                	break;
                	
                case 4:
                	Applicant applicantTwo = new Applicant();
                	logger.info("Enter the applicant name,no,age,DOB,address");
                	applicantName = sc.next();  
                	applicantTwo.setName(applicantName);
                	applicantNo = sc.nextInt();
                	applicantTwo.setApplicantNo(applicantNo);
                	age = sc.nextInt();
                	applicantTwo.setAge(age);
                	DOB = sc.next();
                	applicantTwo.setDOB(DOB);
                	address = sc.next();
                	applicantTwo.setAddress(address);
                	logger.info("Name: " + applicantTwo.getName());
                	logger.info("ApplicantNo: " + applicantTwo.getApplicantNo());
                	logger.info("Age: " + applicantTwo.getAge());
                	logger.info("DOB: " + applicantTwo.getDOB());
                	logger.info("Address: " + applicantTwo.getAddress());
                	break;
                	
                case 5:
                	Class<?> pojo = Class.forName("com.zoho.files.student.Student");
                	Object objectOne = pojo.getDeclaredConstructor().newInstance();
                	logger.info("Default Constructor"+ objectOne);
                	Constructor<?> constructor = pojo.getDeclaredConstructor(String.class, int.class);
                	logger.info("Enter the student name and age");
                	String studentName = sc.next();
                	int studentAge = sc.nextInt();
                	Object objectTwo = constructor.newInstance(studentName,studentAge);
                	logger.info("Constructor"+objectTwo);
                	Method nameMethod = pojo.getMethod("getName");
                	String stuName = (String) nameMethod.invoke(objectTwo);
                	logger.info("Get Method"+ stuName);
                	Method ageMethod = pojo.getMethod("setAge",int.class);
                	logger.info("Enter the age");
                	int setAge=sc.nextInt();
                	ageMethod.invoke(objectTwo, setAge);
                	Method ageGet = pojo.getMethod("getAge");
                	int ageStored = (int) ageGet.invoke(objectTwo);
                	logger.info("Set Method: " + ageStored);
                	break;
                	
                case 6:
                	RainbowColors[] rainbow = RainbowColors.values();
                	for(RainbowColors code : rainbow) {
                		logger.info("code for " + code.name() + " is " + code.getCode() + " its ordinal value is "+ code.ordinal());
                	}
                	break;
                
                case 7:
                	BillPugh billinstanceOne = BillPugh.getInstance();
                	BillPugh billinstanceTwo = BillPugh.getInstance();
                	if(billinstanceOne == billinstanceTwo) {
                		logger.info("Both are same instances");
                	}
                	break;

                	
                	
                case 8:
                	logger.info("Current Time:" + time.getCurrentTime());
                	logger.info("Current time in millis:" + time.getInMillis());
                	logger.info("Enter the Zones");
                	String zoneOne = sc.next();
                	logger.info("The Current Time at" + zoneOne +"is"+ time.getZoneTime(zoneOne) );
                	String zoneTwo =sc.next();
                	logger.info("The Current Time at" + zoneTwo +"is"+ time.getZoneTime(zoneTwo) );
                	logger.info("Week Day:" + time.getWeekDay());
                	logger.info("Current Month" + time.getCurrentMonth());
                	logger.info("CurrentYear" + time.getCurrentYear());
                	break;   	
                	
                	
                case -1:
                	logger.info("Exiting");
                    break;
                default:
                    logger.warning("Invalid choice.");
            }
        }while(choice != -1);
        }
        catch(IOException e) {
        	logger.log(Level.SEVERE,e.getMessage());
        	e.printStackTrace();
        	
        }
        catch(ClassNotFoundException e) {
        	logger.log(Level.SEVERE,e.getMessage());
        	e.printStackTrace();
        }
        catch(InvocationTargetException e){
        	logger.log(Level.SEVERE,e.getMessage());
        	e.printStackTrace();
        	
        }
        catch(NoSuchMethodException e) {
        	logger.log(Level.SEVERE,e.getMessage());
        	e.printStackTrace();
        	
        }
        catch(InstantiationException e) {
        	logger.log(Level.SEVERE,e.getMessage());
        	e.printStackTrace();
        	
        }
        catch(IllegalAccessException e) {
        	logger.log(Level.SEVERE,e.getMessage());
        	e.printStackTrace();
        	
        }
        finally { 
        	sc.close();
      
        
        	
        }
        


}
}




//case 1:
//logger.info("Enter the filename");
//fileName = sc.next();
//sample = task.getFileObject(fileName); 
//if(task.createFile(sample)) {
//	logger.info("File created");
//}
//else {
//	logger.info("File exists");
//}
//writer =  new BufferedWriter(new FileWriter(sample));
//logger.info("Enter the number of lines to write");
//lines = sc.nextInt();
//sc.nextLine();
//logger.info("Enter the text");
//for(int i=0;i<lines;i++) {
//	String text = sc.nextLine();
//	task.writeFile(sample,writer,text);
//	writer.newLine();
//}
//writer.close();
//break;
//
//case 2:
//Properties property = task.getNewProperties();
//logger.info("No of properties ");
//count = sc.nextInt();
//for(int i=0;i<count;i++) {
//	logger.info("Enter the keys and values");
//	key = sc.next();
//	value= sc.next();  
//	task.setPropertyValue(property, key, value);
//}
//writer = new BufferedWriter(new FileWriter("myprops.properties"));   
//task.storeProperty(property, writer, "Properties of the file");
//writer.close();
//break;
//case 3:
//Properties properties = new Properties();
//BufferedReader reader = new BufferedReader(new FileReader("myprops.properties"));
//task.loadProperty(properties, reader);
//for(Map.Entry<Object,Object> entry : properties.entrySet()) {
//	logger.info(entry.getKey()+" : "+ entry.getValue());
//}
//reader.close();
//


//SerializationSingleton instanceOne = SerializationSingleton.getInstance();
//ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("test.ser"));
//out.writeObject(instanceOne);
//out.close();
//
//ObjectInputStream in = new ObjectInputStream(new FileInputStream("test.ser"));
//SerializationSingleton instanceTwo = (SerializationSingleton) in.readObject();
//in.close();
//System.out.println("Instance One HashCode: " + instanceOne.hashCode());
//System.out.println("Instance Two HashCode: " + instanceTwo.hashCode());
