package com.zoho.arraylist.runner;
import com.zoho.arraylist.task.ArrayListTask;
import com.zoho.files.task.FileTask;
import java.util.Scanner;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import com.zoho.arraylist.exception.InvalidException;
import com.zoho.arraylist.exception.OutOfBoundException;
import com.zoho.arraylist.customclass.Employee;
import java.util.List;
import com.zoho.arraylist.util.Util;

public class Runner {

    public static void main(String[] args) {
        int choice;
        ArrayListTask task = new ArrayListTask();
       
	    Scanner sc = new Scanner(System.in);
	     

        try {
        	String className = System.getProperty("list");
        	
            do {
                System.out.println("1. Creation of ArrayList and find its length");
                System.out.println("2. Add strings and find its length");
                System.out.println("3. Add integers and find its length");
                System.out.println("4. Add custom objects and find its length");
                System.out.println("5. Add string, integer, custom object and find its length");
                System.out.println("6. Find index of a string in an ArrayList");
                System.out.println("7. Traverse using iterator method and find its length");
                System.out.println("8. Find string at a given index and find its length");
                System.out.println("9. Find first and last position of duplicate string and find its length");
                System.out.println("10. Add string at an index and find its length");
                System.out.println("11. Create a new list from an existing list and find its length");
                System.out.println("12. Create a new list from two lists and find its length");
                System.out.println("13. Add second list ahead of the first list and find its length");
                System.out.println("14. Remove value at index and find ArrayList length");
                System.out.println("15. Remove all elements and find its length");
                System.out.println("16. Retain values and find its length");
                System.out.println("17. Clear ArrayList and find its length");
                System.out.println("18. Check the string and find its length");
                System.out.println("Enter your choice (to end enter -1): ");
                choice = sc.nextInt();

                switch (choice) {
                  case 1:
                        List<String> arrayListOne = task.getArrayList(className);
                        System.out.println("Size: " + task.getLength(arrayListOne));
                        break;

                  case 2:
                        List<String> arrayListTwo = task.getArrayList(className);
                        System.out.println("Enter the number of strings:");
                        int count = sc.nextInt();
			            System.out.println("Enter the strings:");
                        System.out.println("ArrayList: " + task.addElements(arrayListTwo,Util.getStrings(count,sc)));
                        System.out.println("Length: " + task.getLength(arrayListTwo));
                        break;

                  case 3:
                        List<Integer> arrayListThree = task.getArrayList(className);
                        System.out.println("Enter the number of integers:");
                        int countOne = sc.nextInt();
                        Integer[] integers = new Integer[countOne];
                        System.out.println("Enter the integers:");
                        for (int i = 0; i < countOne; i++) {
                            integers[i] = sc.nextInt();
                        }
                        System.out.println("ArrayList: " + task.addElements(arrayListThree,integers));
                        System.out.println("Length: " + task.getLength(arrayListThree));
                        break;

                  case 4:
                        List<Object> arrayListFour = task.getArrayList(className);
                        System.out.println("Enter the number of employees:");
                        int countTwo = sc.nextInt();
                        Employee[] employees = new Employee[countTwo];
                        for (int i = 0; i < countTwo; i++) {
                            System.out.println("Enter Employee name and id:");
                            String name = sc.next();
                            String id = sc.next();
                            employees[i] = new Employee(name, id);
                        }
                        System.out.println("ArrayList: " + task.addElements(arrayListFour,employees));
                        System.out.println("Length: " + task.getLength(arrayListFour));
                        break;

                  case 5:
                        List<Object> arrayListFive = task.getArrayList(className);
                        System.out.println("Enter the number of integers:");
                        int countThree = sc.nextInt();
                        Integer[] integerArray = new Integer[countThree];
                        System.out.println("Enter the integers:");
                        for (int i = 0; i < countThree; i++) {
                            integerArray[i] = sc.nextInt();
                        }
                        task.addElements(arrayListFive, integerArray);

                        System.out.println("Enter the number of strings:");
                        int countFour = sc.nextInt();
                        task.addElements(arrayListFive, Util.getStrings(countFour,sc));

                        System.out.println("Enter the number of employees:");
                        int countFive = sc.nextInt();
                        Employee[] employeeArray = new Employee[countFive];
                        for (int i = 0; i < countFive; i++) {
                            System.out.println("Enter Employee name and id:");
                            String name = sc.next();
                            String id = sc.next();
                            employeeArray[i] = new Employee(name, id);
                        }
                        task.addElements(arrayListFive, employeeArray);
                        System.out.println("ArrayList: " + arrayListFive);
                        System.out.println("Length: " + task.getLength(arrayListFive));
                        break;

                  case 6:
                        List<String> arrayListSix = task.getArrayList(className);
                        System.out.println("Enter the number of strings:");
                        int countSix = sc.nextInt();
                        task.addElements(arrayListSix, Util.getStrings(countSix,sc));
                        System.out.println("Enter the string to find its index:");
                        String targetString = sc.next();
                        System.out.println("Index: " + task.findIndex(arrayListSix, targetString));
                        System.out.println("ArrayList: " + arrayListSix);
                        System.out.println("Length: " + task.getLength(arrayListSix));
                        break;

                  case 7:
                        List<String> arrayListSeven = task.getArrayList(className);
                        System.out.println("Enter the number of strings:");
                        int countSeven = sc.nextInt();
                        task.addElements(arrayListSeven, Util.getStrings(countSeven,sc));
                        Iterator<String> iterator = arrayListSeven.iterator();
                        while (iterator.hasNext()) {
                            System.out.println(iterator.next());
                        }
                        System.out.println("Length: " + task.getLength(arrayListSeven));
                        break;

                  case 8:
                        List<String> arrayListEight = task.getArrayList(className);
                        System.out.println("Enter the number of strings:");
                        int countEight = sc.nextInt();
                        task.addElements(arrayListEight, Util.getStrings(countEight,sc));
                        System.out.println("Enter the index:");
                        int index = sc.nextInt();
                        System.out.println("Element at index: " + task.getElement(arrayListEight, index));
                        System.out.println("Length: " + task.getLength(arrayListEight));
                        break;
		  case 9:
   			List<String> arrayListNine = task.getArrayList(className);
    			System.out.println("Enter the number of strings:");
   			int countNine = sc.nextInt();
    			task.addElements(arrayListNine, Util.getStrings(countNine,sc));
    			System.out.println("Enter the duplicate string:");
   	 		String duplicate = sc.next();
    			System.out.println("First Index: " + task.getFirstPosition(arrayListNine, duplicate));
    			System.out.println("Last Index: " + task.getLastPosition(arrayListNine, duplicate));
    			break;

		 case 10:
   			List<String> arrayListTen = task.getArrayList(className);
    			System.out.println("Enter the number of strings:");
    			int countTen = sc.nextInt();
   		 	task.addElements(arrayListTen,Util.getStrings(countTen	,sc));
    			System.out.println("Enter the string to be added:");
    			String stringOne = sc.next();
    			System.out.println("Enter the index to be added:");
    			int indexPosition = sc.nextInt();
    			System.out.println("ArrayList: " + task.addAtPosition(arrayListTen, stringOne, indexPosition));
    			System.out.println("Length: " + task.getLength(arrayListTen));
    			break;

	      	case 11:
    			List<String> arrayListEleven = task.getArrayList(className);
    			System.out.println("Enter the number of strings:");
    			int countEleven = sc.nextInt();
    			task.addElements(arrayListEleven,Util.getStrings(countEleven,sc));
    			System.out.println("Enter start and end index:");
    			int start = sc.nextInt();
    			int end = sc.nextInt();
    			List<String> arrayListSecond = task.getSubList(arrayListEleven, start, end);
    			System.out.println("Second ArrayList: " + arrayListSecond);
    			break;

	     	case 12:
    			List<String> arrayListTwelve = task.getArrayList(className);
    			System.out.println("Enter the number of strings:");
    			int countTwelve = sc.nextInt();
    			task.addElements(arrayListTwelve,Util.getStrings(countTwelve,sc));
    			List<String> second = task.getArrayList(className);
    			System.out.println("Enter the number of strings for the second list:");
    			int countSecond = sc.nextInt();
    			task.addElements(second, Util.getStrings(countSecond,sc));
   			    List<String> third = task.addList(arrayListTwelve, second);
    			System.out.println(third);
    			break;

	  	case 14:
    			List<Double> arrayListFourteen = task.getArrayList(className);
    			System.out.println("Enter the number of decimal values:");
    			int countFourteen = sc.nextInt();
    			Double[] doubleArray = new Double[countFourteen];
                        for (int i = 0; i < countFourteen; i++) {
        			doubleArray[i] = sc.nextDouble();
    			}
    			task.addElements(arrayListFourteen, doubleArray);
    			System.out.println("Enter the position:");
    			int value = sc.nextInt();
    			System.out.println("ArrayList after removal: " + task.removeElement(arrayListFourteen, value));
    			break;

		case 15:
    			List<String> arrayListFifteen = task.getArrayList(className);
    			System.out.println("Enter the number of strings:");
   			    int countFifteen = sc.nextInt();
    			task.addElements(arrayListFifteen,Util.getStrings(countFifteen,sc));
    			List<String> secondList = task.getArrayList(className);
    			System.out.println("Enter the number of strings for the second list:");
    			int countList = sc.nextInt();
    			task.addElements(secondList,Util.getStrings(countList,sc));
    			System.out.println("ArrayList: " + task.removeList(arrayListFifteen, secondList));
    			System.out.println("Length: " + task.getLength(secondList));
    			break;

		case 16:
    			List<String> arrayListSixteen = task.getArrayList(className);
    			System.out.println("Enter the number of strings:");
    			int countSixteen = sc.nextInt();
   		 	task.addElements(arrayListSixteen,Util.getStrings(countSixteen,sc));
   			List<String> listSecond = task.getArrayList(className);
    			System.out.println("Enter the number of strings for the second list:");
    			int countSecondSixteen = sc.nextInt();
    			task.addElements(listSecond,Util.getStrings(countSecondSixteen,sc));
    			System.out.println("ArrayList: " + task.retainList(arrayListSixteen, listSecond));
    			System.out.println("Length: " + task.getLength(listSecond));
    			break;

		case 17:
   	 		List<Long> arrayListSeventeen = task.getArrayList(className);
    			System.out.println("Enter the number of values:");
    			int countSeventeen = sc.nextInt();
    			Long[] longArray = new Long[countSeventeen];
    			for (int i = 0; i < countSeventeen; i++) {
        			longArray[i] = sc.nextLong();
    			}
    			task.addElements(arrayListSeventeen, longArray);
    			System.out.println("ArrayList: " + task.clearList(arrayListSeventeen));
    			System.out.println("Length: " + task.getLength(arrayListSeventeen));
    			break;

		case 18:
   			List<String> arrayListEighteen = task.getArrayList(className);
    			System.out.println("Enter the number of strings:");
    			int countEighteen = sc.nextInt();
    			task.addElements(arrayListEighteen,Util.getStrings(countEighteen,sc));
    			System.out.println("Enter the string:");
    			String checkString = sc.next();
    			System.out.println(task.checkElement(arrayListEighteen, checkString));
    			break;
			


                   default:
                        System.out.println("Invalid choice");
                }
            } while (choice != -1);
        } catch (InvalidException e) {
            System.out.println(e.getMessage());
        } catch (OutOfBoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        catch(ClassNotFoundException e) {
        	System.out.println(e.getMessage());
        }
        catch(InvocationTargetException e) {
        	System.out.println(e.getMessage());
        }
        catch(IllegalAccessException e) {
        	System.out.println(e.getMessage());
        }
        catch(NoSuchMethodException e) {
        	System.out.println(e.getMessage());
        }
        catch(InstantiationException e) {
        	System.out.println(e.getMessage());
        }
        finally{
        
		sc.close();
		}
    }

}