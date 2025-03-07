package com.zoho.files.runner;
import com.zoho.files.task.FileTask;

import com.zoho.files.singleton.SerializationSingleton;
import com.zoho.files.singleton.SingleCheckSingleton;
import com.zoho.files.singleton.StaticBlockInitialization;
import com.zoho.files.singleton.ThreadSafe;
import com.zoho.files.singleton.BillPugh;
import com.zoho.files.singleton.DoubleCheckLocking;
import com.zoho.files.singleton.EagarInitialization;
import com.zoho.files.singleton.EnumSingleton;
import com.zoho.files.singleton.LazyInitialization;
import com.zoho.files.time.Time;
import com.zoho.files.applicant.Applicant;
import com.zoho.files.customer.Customer;
import com.zoho.files.rainbow.RainbowColors;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.ZoneId;
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
import java.util.HashMap;
import java.util.Map;

class Runner {
	static final Logger logger = Logger.getLogger(Runner.class.getName());
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int count,lines;
        SerializationSingleton instanceTwo =null;
        String key,value,applicantName,DOB,address,directory;
        int choice,applicantNo,age;
        File sample,propertiesFile,fileObj;
        String fileName;
        FileTask task = new FileTask(); 
        Time time = new Time();
        Customer customer;
//        HashMap<Integer,String> zoneId = new HashMap<>();
//        zoneId.put(, value);	
//        
        
            
        try {
        	do {
                logger.info("1. In given directory crete file and properties");
                logger.info("2.Use Instance to print the string");
                logger.info("3.Create a Pojo class and print its instance");
                logger.info("4.Invoke getter and setter methods");
                logger.info("5.Using reflection in invoking constructors,methods");
                logger.info("6.Create enum and print its oridinal and constants");
                logger.info("7.Singleton class pattern");
                logger.info("8.Time Calculations");
                logger.info("Enter your choice (Enter -1 to exit)");
                choice = sc.nextInt();
            switch(choice) {
                case 1:
                	logger.info("Enter directory");
                	directory = sc.next();
                	if(directory == null || directory.isEmpty()) {
                		directory = System.getProperty("user.dir");
                	}
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
                	EagarInitialization eagarOne = EagarInitialization.getInstance();
                	EagarInitialization eagarTwo = EagarInitialization.getInstance();
                	if(eagarOne == eagarTwo) {
                		logger.info("One instance of EagarInitialization is created");
                	}
                	try{
                		EagarInitialization eagarThree = (EagarInitialization)eagarOne.clone();
                		if(eagarOne !=eagarThree) {
                			logger.info("Cloning violates singleton pattern");
                		}
                	}catch(CloneNotSupportedException e) {
                		logger.log(Level.SEVERE,e.getMessage());
                    	e.printStackTrace();
                	}
                	StaticBlockInitialization staticOne = StaticBlockInitialization.getInstance();
                	StaticBlockInitialization staticTwo = StaticBlockInitialization.getInstance();
                	if(staticOne == staticTwo) {
                		logger.info("One instance of StaticBlockInitialization is created");
                	}
                	LazyInitialization lazyOne = LazyInitialization.getInstance();
                	LazyInitialization lazyTwo = LazyInitialization.getInstance();
                	if(lazyOne == lazyTwo) {
                		logger.info("One instance of LazyInitialization is created");
                	}
                	ThreadSafe threadOne =ThreadSafe.getInstance();
                	ThreadSafe threadTwo =ThreadSafe.getInstance();
                	if(threadOne == threadTwo) {
                		logger.info("One instance of ThreadSafe is created");
                	}
                	SingleCheckSingleton singleOne =SingleCheckSingleton.getInstance();
                	SingleCheckSingleton singleTwo =SingleCheckSingleton.getInstance();
                	if(singleOne == singleTwo) {
                		logger.info("One instance of SingleCheckSingleton is created");
                	}
                	DoubleCheckLocking doubleOne =DoubleCheckLocking.getInstance();
                	DoubleCheckLocking doubleTwo =DoubleCheckLocking.getInstance();
                	if(doubleOne == doubleTwo) {
                		logger.info("One instance of DoublecheckLocking is created");
                	}
                	BillPugh billinstanceOne = BillPugh.getInstance();
                	BillPugh billinstanceTwo = BillPugh.getInstance();
                	if(billinstanceOne == billinstanceTwo) {
                		logger.info("One instance of BillPugh is created");
                	}
                	SerializationSingleton instanceOne = SerializationSingleton.getInstance();
                	try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("test.ser"))){
                	out.writeObject(instanceOne);
                	}
                	
                	try(ObjectInputStream in = new ObjectInputStream(new FileInputStream("test.ser"))){
                	instanceTwo = (SerializationSingleton) in.readObject();
                	}
                	if(instanceOne== instanceTwo) {
                		logger.info("One instance for serialization is created");
                	}
                	LazyInitialization reflectOne = LazyInitialization.getInstance();
                	LazyInitialization reflectTwo = null;
                	try{
                		Constructor[] construct = LazyInitialization.class.getConstructors();
                		for(Constructor cons: construct) {
                			cons.setAccessible(true);
                			reflectTwo = (LazyInitialization)cons.newInstance();
                			break;
                		}
                	}
                	catch(Exception e) {
                		logger.log(Level.SEVERE,e.getMessage());
                    	e.printStackTrace();
                		
                		
                	}
                	if(reflectOne != reflectTwo) {
                		logger.info("Singleton pattern is violated using reflection");
                	}
                	EnumSingleton enumOne = EnumSingleton.Instance;
                	EnumSingleton enumTwo = EnumSingleton.Instance;
                	if(enumOne == enumTwo ) {
                		logger.info("Single instance of enumSingleton is created");
                	}
                	
                	
                	break;

                	
                	
                case 8:
                	logger.info("Current Time:" + time.getCurrentTime());
                	logger.info("Current time in millis:" + time.getInMillis());
                    for(String zone:time.getZoneIds()) {
                    	System.out.println(zone);
                    }
                    logger.info("Enter the ZoneId");
                	String zoneOne = sc.next();
                	logger.info("The Current Time at" + zoneOne +"is"+ time.getZoneTime(zoneOne) );
                	logger.info("Enter the ZoneId");
                	String zoneTwo =sc.next();
                	logger.info("The Current Time at" + zoneTwo +"is"+ time.getZoneTime(zoneTwo) );
                	logger.info("Enter the ZoneId to get current day");
                	String zoneThree =sc.next();
                	logger.info("Week Day:" + time.getWeekDay(zoneThree));
                	logger.info("Enter the ZoneId to get current month");
                	String zoneFour =sc.next();
                	logger.info("Current Month" + time.getCurrentMonth(zoneFour));
                	logger.info("Enter the ZoneId to get current year");
                	String zoneFive =sc.next();
                	logger.info("CurrentYear" + time.getCurrentYear(zoneFive));
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

