package com.zoho.jdbc.runner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.zoho.jdbc.dependent.DependentDetails;
import com.zoho.jdbc.employee.Employee;
import com.zoho.jdbc.exception.DatabaseException;
import com.zoho.jdbc.task.JdbcConnection;
import com.zoho.threads.log.ThreadLog;

public class JdbcRunner {
	  private static final Logger logger = Logger.getLogger(JdbcRunner.class.getName());

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
        Employee employee;
        List<Employee> list;
        List<DependentDetails> dependents;
        int noOfEmp,id,age,modify;
        String name,relation,update="";
        DependentDetails dependent;
        try {
            JdbcConnection dbConn = new JdbcConnection();
            int choice;

            do {
            	System.out.println("1.Create employee table");
            	System.out.println("2.Add Employees");
            	System.out.println("3.Get Employee details ");
            	System.out.println("4.Modify the detail");
            	System.out.println("5.Print first n employee details");
            	System.out.println("6.Print first n employee details and order by name in descending order");
            	System.out.println("7.Delete Employee");
            	System.out.println("8.Create Dependent table");
            	System.out.println("9.Insert dependent employees");
            	System.out.println("10.List all dependent employees");
            	System.out.println("11.List all dependent employees with limit and order by name");
                System.out.println("Enter your choice (Enter -1 to Exit)");
                choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        dbConn.createTable();
                        logger.info("Table created successfully.");
                        break;

                    case 2:
                        System.out.println("Enter the number of employees to be added:");
                        int entries = sc.nextInt();
                        List<Employee> lists = new ArrayList<>();
                        for (int i = 0; i < entries; i++) {
                            System.out.println("Enter Employee ID:");
                            int empId = sc.nextInt();
                            sc.nextLine(); 
                            System.out.println("Enter Employee Name:");
                            String empName = sc.nextLine();
                            System.out.println("Enter Mobile:");
                            String mobile = sc.nextLine();
                            System.out.println("Enter Email:");
                            String email = sc.nextLine();
                            System.out.println("Enter Department:");
                            String department = sc.nextLine();
                            employee = new Employee();
                            employee.setEmpId(empId);
                            employee.setEmpName(empName);
                            employee.setEmail(email);
                            employee.setDepartment(department);
                            employee.setMobile(mobile);
                            lists.add(employee);
                            
                           
                        }
                        dbConn.insertUser(lists);
                        logger.info("Data inserted successfully.");
                        break;
                        
                    case 3:
        				System.out.println("Enter the employee name to reterive the details");
        				String empName = sc.next();
        				System.out.println(dbConn.getByName(empName));
        				break;
        			case 4:
        				System.out.println("Enter the employee id to modify details");
        				id = sc.nextInt();
        				do {
        				System.out.println("Enter the data to modify\n 1.Mobile\n 2.Depertment\n 3.Email\n");
        				modify = sc.nextInt();
        				
        				if(modify==1) {
        					update = "MOBILE";
        				}
        				else if(modify == 2) {
        					update = "DEPARTMENT";
        				}
        				else if(modify == 3) {
        					update = "EMAIL";
        				}
        				else {
        					modify = -1;
        				}
        				}while(modify==-1);
        				System.out.println("Enter the value to modify");
        				String value = sc.next();
        				dbConn.updateDetails(update,value, id);
        				logger.info("Data has been updated successfully");
        				break;
        			case 5:
        				System.out.println("Enter the no of employees to be displayed");
        				noOfEmp = sc.nextInt();
        				list = dbConn.getRows(noOfEmp);
        				for(int i=0;i<noOfEmp;i++) {
        					System.out.println(list.get(i));
        				}
        				break;
        			case 6:
        				System.out.println("Enter the no of employees to be displayed");
        				noOfEmp = sc.nextInt();
        				System.out.println("Enter the field to order");
        				String order = sc.next();
        				String upper =order.toUpperCase();
        				list = dbConn.orderBy(noOfEmp,upper);
        				for(int i=0;i<noOfEmp;i++) {
        					System.out.println(list.get(i));
        				}
        				break;
        			case 7:
        				System.out.println("Enter the employee id to delete");
        				id = sc.nextInt();
        				dbConn.deleteID(id);
        				logger.info("Data successfully deleted");
        			case 8:
        				dbConn.createDependentTable();
        				logger.info("Table created successfully.");
                        break;
        			case 9:
        				System.out.println("Enter the number of employee dependents need to be added");
        				noOfEmp = sc.nextInt();
        				List<DependentDetails> dependentList = new ArrayList<>();
        				for (int i = 0; i < noOfEmp; i++) {
        					dependent = new DependentDetails();
        					System.out.println("Enter the employee id");
        					id= sc.nextInt();
                            dependent.setDependentId(id);
        					System.out.println("Enter the dependent name");
        					name=sc.next();
        					dependent.setDependentName(name);
        					System.out.println("Enter the age");
        					age=sc.nextInt();
        					dependent.setAge(age);
        					System.out.println("Enter the relationship");
        					relation =sc.next();
        					dependent.setRelationship(relation);
        					dependentList.add(dependent);
        				}
        				dbConn.insertDependent(dependentList);
        				logger.info("Data inserted successfully.");
        				break;
        			case 10:
        				System.out.println("Enter the employee id");
        				id=sc.nextInt();
        				dependents = dbConn.getDependentDetails(id);
        				int len = dependents.size();
        				for(int i=0;i<len;i++) {
        					System.out.println(dependents.get(i));
        				}
        				break;
        				
        			case 11:
        				System.out.println("Enter the no of employees to be displayed");
        				noOfEmp = sc.nextInt();
        				dependents = dbConn.getAllDependent(noOfEmp);
        				int length = dependents.size();
        				for(int i=0;i<length;i++) {
        					System.out.println(dependents.get(i));
        				}
        				break;
        				
                    case -1:
                    	logger.info("Exiting program.");
                        break;

                    default:
                    	logger.info("Invalid choice! Try again.");
                        break;
                }

            } while (choice != -1);

        } catch (DatabaseException e) {
        	System.out.println("Error:" + e.getMessage());
        	if (e.getCause() != null) {
       			System.out.println("Cause: " + e.getCause().getMessage());
        	}
        } 
        finally {
            sc.close();
        }
    				

	}

}
