package com.zoho.jdbc.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.zoho.jdbc.dependent.DependentDetails;
import com.zoho.jdbc.employee.Employee;
import com.zoho.jdbc.task.JdbcConnection;

public class JdbcRunner {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
        Employee employee;
        List<Employee> list;
        List<DependentDetails> dependents;
        int noOfEmp,id,age;
        String name,relation;

        try {
            JdbcConnection dbConn = new JdbcConnection();
            int choice;

            do {
                System.out.println("Enter choice (1: Create Table, 2: Insert Data, -1: Exit)");
                choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        dbConn.createTable();
                        System.out.println("Table created successfully.");
                        break;

                    case 2:
                        System.out.println("Enter the number of employees to be added:");
                        int entries = sc.nextInt();
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
                            employee = new Employee(empId, empName, mobile, email, department);
                            if (employee == null) {
                                System.out.println("Error: Employee object is null.");
                            } else {
                                dbConn.insertuser(employee);
                            }
                            System.out.println("Data inserted successfully.");
                        }
                        break;
                        
                    case 3:
        				System.out.println("Enter the employee name to reterive the details");
        				String empName = sc.next();
        				System.out.println(dbConn.getByName(empName));
        				break;
        			case 4:
        				System.out.println("Enter the employee id to modify details");
        				id = sc.nextInt();
        				System.out.println("Enter the data to modify");
        				String mobile = sc.next();
        				dbConn.updateDetails(mobile, id);
        				System.out.println("Data has been updated successfully");
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
        				System.out.println("Data successfully deleted");
        			case 8:
        				dbConn.createDependentTable();
                        System.out.println("Table created successfully.");
                        break;
        			case 9:
        				System.out.println("Enter the number of employee dependents need to be added");
        				noOfEmp = sc.nextInt();
        				for (int i = 0; i < noOfEmp; i++) {
        					System.out.println("Enter the employee id");
        					id= sc.nextInt();
        					System.out.println("Enter the dependent name");
        					name=sc.next();
        					System.out.println("Enter the age");
        					age=sc.nextInt();
        					System.out.println("Enter the relationship");
        					relation =sc.next();
        					dbConn.insertDependent(id,name,age,relation);
        				}
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
                        System.out.println("Exiting program.");
                        break;

                    default:
                        System.out.println("Invalid choice! Try again.");
                        break;
                }

            } while (choice != -1);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sc.close();
        }
    


	



				
				

	}

}
