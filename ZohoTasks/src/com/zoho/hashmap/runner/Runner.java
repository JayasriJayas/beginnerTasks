package com.zoho.hashmap.runner;

import com.zoho.hashmap.task.HashMapTask;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.Scanner;
import com.zoho.hashmap.exception.InvalidException;
import com.zoho.hashmap.customclass.Employee;
import com.zoho.hashmap.util.Util;
class Runner{
	
	public static void main(String[] args){
		Scanner sc = new Scanner(System.in);
		int choice, count,values;
		String value,keys,search,defaultValue;
		String[][] stringArray;
		HashMapTask task = new HashMapTask();
		Runner runner = new Runner();
		
		try{
			do{
				System.out.println("1.Create HashMap, Print it and its size");
				System.out.println("2.Add string elements, Print it and its size");
				System.out.println("3.Add integer elements, Print it and its size");
				System.out.println("4.Add string key and int value , Print it and its size ");
				System.out.println("5.Add string key and object value, Print it and its size ");
				System.out.println("6.Add null value to a key, Print it and its size");
				System.out.println("7.Add null key and a non-null value, Print it and its size");
				System.out.println("8.Check key exists");
				System.out.println("9.Check value exists");
				System.out.println("10.Change values of keys ,Print it and its size");
				System.out.println("11.Get existing key and its value ");
				System.out.println("12.Get value of non-existing key");
				System.out.println("13.Get default value , Print hashmap and its size");
				System.out.println("14.Remove existing key, Print hashmap and its size before and after ");
				System.out.println("15.Remove key if value matches, Print hashmap and its size before and after");
				System.out.println("16.Replace existing key,Print hashmap and its size before and after ");
				System.out.println("17.Replace existing key if value exists, Print hashmap and its size before and after");
				System.out.println("18.Transfer keys and values from one map to other, Print both hashmap and its size before and after");
				System.out.println("19.Iterate over the hashmap ");
				System.out.println("20.Remove , Print hashmap and its size before and after");

				System.out.println("Enter your choice(enter -1 to end): ");
				
				choice = sc.nextInt();
			
					switch(choice){
					case 1:
						Map<Integer,String> mapOne = task.getHashMap();
						System.out.println("HashMap: ");
						runner.displayElements(mapOne);
						System.out.println("Size: " + task.getSize(mapOne));
						break;
		
					case 2:
						Map<String,String> mapTwo = task.getHashMap();
						System.out.println("Enter no of elements: ");
						count = sc.nextInt();
						for(int i=0;i<count;i++){
							System.out.println("Enter key and value:");
							String key = sc.next();
							value = sc.next();
							task.addElements(mapTwo, key, value);
						}	
						System.out.println("HashMap: ");
						runner.displayElements(mapTwo);
						System.out.println("Size: " + task.getSize(mapTwo));
						break;
					case 3:
						Map<Integer,Integer> mapThree = task.getHashMap();
						System.out.println("Enter no of elements: ");
						count = sc.nextInt();
						for(int i=0;i<count;i++){
							System.out.println("Enter key and value:");
							int key = sc.nextInt();
							values = sc.nextInt();
							task.addElements(mapThree, key, values);
						}	
						System.out.println("HashMap: ");
						runner.displayElements(mapThree);
						System.out.println("Size: " + task.getSize(mapThree));
						break;
					case 4:
						Map<String,Integer> mapFour = task.getHashMap();
						System.out.println("Enter no of elements: ");
						count = sc.nextInt();
						for(int i=0;i<count;i++){
							System.out.println("Enter key and value:");
							String key = sc.next();
							values = sc.nextInt();
							task.addElements(mapFour, key, values);
						}	
						System.out.println("HashMap: ");
						runner.displayElements(mapFour);
						System.out.println("Size: " + task.getSize(mapFour));
						break;
					case 5:
						Map<String, Employee> mapFive = task.getHashMap();
						System.out.println("Enter no of elements: ");
						count = sc.nextInt();
						for(int i=0;i<count;i++){
							System.out.println("Enter key and value:");
							String key = sc.next();
							System.out.println("Enter the empoyee id and name:");
							String id = sc.next();
							String name = sc.next();
							Employee employee = new Employee(id,name);
							task.addElements(mapFive, key, employee);
						}	
						System.out.println("HashMap: ");
						runner.displayElements(mapFive);
						System.out.println("Size: " + task.getSize(mapFive));
						break;
					case 6:
						Map<String,String> mapSix = task.getHashMap();
						System.out.println("Enter no of elements: ");
						count = sc.nextInt();
						for(int i=0;i<count;i++){
							System.out.println("Enter key and value:");
							String key = sc.next();
						        if(i==1){
								 value = null;
							}
							else{
							value = sc.next();
							}
							task.addElements(mapSix, key, value);
						}	
						System.out.println("HashMap: ");
						runner.displayElements(mapSix);
						System.out.println("Size: " + task.getSize(mapSix));
						break;
					case 7:
						Map<String,String> mapSeven = task.getHashMap();
						System.out.println("Enter no of elements: ");
						count = sc.nextInt();
						String keyElement;
						for(int i=0;i<count;i++){
							System.out.println("Enter key and value:");
						        if(i==1){
								 keyElement = null;
							}     
							else{
								keyElement = sc.next();
							}
							value = sc.next();
							task.addElements(mapSeven, keyElement, value);
						}	
						System.out.println("HashMap: ");
						runner.displayElements(mapSeven );
						System.out.println("Size: " + task.getSize(mapSeven));
						break;
					case 8: 
						Map<String,String> mapEight = task.getHashMap();
						System.out.println("Enter no of elements: ");
						count = sc.nextInt();
						for(int i=0;i<count;i++){
							System.out.println("Enter key and value:");
							String key = sc.next();
							value = sc.next();
							task.addElements(mapEight, key, value);
						}
						System.out.println("Enter the key to check");
						String key = sc.next();	
						System.out.println("Key exists: " + task.checkKey(mapEight,key));
						break;
		                     	case 9:
						Map<String,String> mapNine = task.getHashMap();
						System.out.println("Enter no of elements: ");
						count = sc.nextInt();
						for(int i=0;i<count;i++){
							System.out.println("Enter key and value:");
							keys = sc.next();
							value = sc.next();
							task.addElements(mapNine, keys, value);
						}
						System.out.println("Enter the value to check");
						value = sc.next();	
						System.out.println("Key exists: " + task.checkValue(mapNine,value));
						break;
					case 10:
						Map<String,String> mapTen = task.getHashMap();
						System.out.println("Enter no of elements: ");
						count = sc.nextInt();
						for(int i=0;i<count;i++){
							System.out.println("Enter key and value:");
							keys = sc.next();
							value = sc.next();
							task.addElements(mapTen, keys, value);
						}
						System.out.println("Before the change: ");
						runner.displayElements(mapTen);
						Set<String> keySet = task.getKeySet(mapTen); 
						for(String keyWord : keySet){
							System.out.println("Enter the value for key: " +keyWord);
							String newValue = sc.next();
							task.addElements(mapTen,keyWord, newValue);
						}
						System.out.println("After the change: ");
						runner.displayElements(mapTen);
						System.out.println("Size: " + task.getSize(mapTen));
						break;
					case 11:
						Map<String,String> mapEleven = task.getHashMap();
						System.out.println("Enter no of elements: ");
						count = sc.nextInt();
						for(int i=0;i<count;i++){
							System.out.println("Enter key and value:");
							keys= sc.next();
							value = sc.next();
							task.addElements(mapEleven, keys, value);
						}
						System.out.println("Enter a existing key value:");
						String searchKey = sc.next();
						System.out.println("Value: " + task.getKeyValue(mapEleven,searchKey));
						break;
					case 12:
						Map<String,String> mapTwelve = task.getHashMap();
						System.out.println("Enter no of elements: ");
						count = sc.nextInt();
						for(int i=0;i<count;i++){
							System.out.println("Enter key and value:");
							keys = sc.next();
							value = sc.next();
							task.addElements(mapTwelve, keys, value);
						}
						System.out.println("Enter a non-existing key value:");
						search = sc.next();
						System.out.println("Enter default value");
						defaultValue = sc.next();
						System.out.println("Value: " + task. getDefaultValue( mapTwelve,search,defaultValue));
						break;
					case 13:
						Map<String,String> mapThirteeen = task.getHashMap();
						System.out.println("Enter no of elements: ");
						count = sc.nextInt();
						for(int i=0;i<count;i++){
							System.out.println("Enter key and value:");
							keys = sc.next();
							value = sc.next();
							task.addElements( mapThirteeen, keys, value);
						}
						System.out.println("Enter a non-existing key value:");
						search = sc.next();
						defaultValue = "Zoho";
						System.out.println("Value: " + task. getDefaultValue( mapThirteeen,search,defaultValue));
						break;

					case 14:
						Map<String,String> mapFourteen = task.getHashMap();
						System.out.println("Enter no of elements: ");
						count = sc.nextInt();
						for(int i=0;i<count;i++){
							System.out.println("Enter key and value:");
							key = sc.next();
							value = sc.next();
							task.addElements(mapFourteen, key, value);
						}
						System.out.println("Hashmap size before removing: " + task.getSize(mapFourteen));
						System.out.println("Enter the key to remove");
						keys = sc.next();
						task.removeKey(mapFourteen, keys);
						System.out.println("Hashmap size after removing: " + task.getSize(mapFourteen));
						break;
					case 15:
						Map<String,String> mapFifteen = task.getHashMap();
						System.out.println("Enter no of elements: ");
						count = sc.nextInt();
						for(int i=0;i<count;i++){
							System.out.println("Enter key and value:");
							key = sc.next();
							value = sc.next();
							task.addElements(mapFifteen, key, value);
						}
						System.out.println("Hashmap size before removing: " + task.getSize(mapFifteen));
						System.out.println("Enter the key to remove and its value: ");
						keys = sc.next();
						String withValue = sc.next();
						task.removeIfVaue(mapFifteen, keys, withValue);
						System.out.println("Hashmap size after removing: " + task.getSize(mapFifteen));
						break;
					case 16:
						Map<String,String> mapSixteen = task.getHashMap();
						System.out.println("Enter no of elements: ");
						count = sc.nextInt();
						for(int i=0;i<count;i++){
							System.out.println("Enter key and value:");
							key = sc.next();
							value = sc.next();
							task.addElements(mapSixteen, key, value);
						}
						System.out.println("HashMap before replacing: ");
						runner.displayElements(mapSixteen);
						System.out.println("Hashmap size before replacing: " + task.getSize(mapSixteen));
						System.out.println("Enter the key to replace");
						keys = sc.next();
						System.out.println("Enter replace value");
						String replaceValue = sc.next();
						task.replaceValue(mapSixteen, keys, replaceValue);
						System.out.println("HashMap after replacing: ");
						runner.displayElements(mapSixteen);
						System.out.println("Hashmap size after removing: " + task.getSize(mapSixteen));
						break;
					case 17:
						Map<String,String> mapSeventeen = task.getHashMap();
						System.out.println("Enter no of elements: ");
						count = sc.nextInt();
						for(int i=0;i<count;i++){
							System.out.println("Enter key and value:");
							key = sc.next();
							value = sc.next();
							task.addElements(mapSeventeen, key, value);
						}
						System.out.println("HashMap before replacing: ");
						runner.displayElements(mapSeventeen);
						System.out.println("Hashmap size before replacing: " + task.getSize(mapSeventeen));
						System.out.println("Enter the key to replace");
						keys = sc.next();
						System.out.println("Enter old value");
						String oldValue = sc.next();
						System.out.println("Enter new value");
						String newValue = sc.next();
						task.replaceWithValue(mapSeventeen, keys, oldValue, newValue);
						System.out.println("HashMap after replacing: ");
						runner.displayElements(mapSeventeen);
						System.out.println("Hashmap size after removing: " + task.getSize(mapSeventeen));
						break;
					case 18:
						Map<String,String> mapEighteen = task.getHashMap();
						System.out.println("Enter no of elements: ");
						count = sc.nextInt();
						for(int i=0;i<count;i++){
							System.out.println("Enter key and value:");
							key = sc.next();
							value = sc.next();
							task.addElements(mapEighteen, key, value);
						}
						System.out.println("HashMap before : ");
						runner.displayElements(mapEighteen);
						System.out.println("Hashmap size before : " + task.getSize(mapEighteen));
						Map<String,String> secondMap = task.getHashMap();
						System.out.println("Enter no of elements: ");
						count = sc.nextInt();
						for(int i=0;i<count;i++){
							System.out.println("Enter key and value:");
							key = sc.next();
							value = sc.next();
							task.addElements(secondMap, key, value);
						}
						System.out.println("HashMap before : ");
						runner.displayElements(secondMap);
						System.out.println("Hashmap two size before : " + task.getSize(secondMap));
						task.addAllElements(mapEighteen,secondMap);
						System.out.println("HashMap after : ");
						runner.displayElements(mapEighteen);
						System.out.println("Hashmap size after : " + task.getSize(mapEighteen));
						System.out.println("HashMap after : ");
						runner.displayElements(secondMap);
						System.out.println("Hashmap two size after : " + task.getSize(secondMap));
						break;
					case 19:
						Map<String,String> mapNineteen = task.getHashMap();
						System.out.println("Enter no of elements: ");
						count = sc.nextInt();
						for(int i=0;i<count;i++){
							System.out.println("Enter key and value:");
							key = sc.next();
							value = sc.next();
							task.addElements(mapNineteen, key, value);
						}
						stringArray = task.iterateMap(mapNineteen);
						for(int i=0;i<count;i++){
							System.out.println("Key: " + stringArray[i][0] + "Value: " +stringArray[i][1]);
						}
						break;						
					case 20:
						Map<String,String> mapTwenty = task.getHashMap();
						System.out.println("Enter no of elements: ");
						count = sc.nextInt();
						for(int i=0;i<count;i++){
							System.out.println("Enter key and value:");
							key = sc.next();
							value = sc.next();
							task.addElements(mapTwenty, key, value);
						}
						System.out.println("HashMap before : ");
						runner.displayElements(mapTwenty);
						System.out.println("Hashmap size before : " + task.getSize(mapTwenty));
						task.clearAll(mapTwenty);
						System.out.println("HashMap after : ");
						runner.displayElements(mapTwenty);
						System.out.println("Hashmap size after : " + task.getSize(mapTwenty));
						break;
						
					case -1:
						System.out.println("Exiting the program");
						break;
				
					default:
						System.out.println("Invalid choice");
				
					}
			}while(choice != -1);
		}
		catch(InvalidException e){
			System.out.println(e.getMessage());
		}
		finally{
			sc.close();
		}
	}
	<K,V> void displayElements(Map<K,V> hashMap){
		for(Map.Entry<K,V> entry : hashMap.entrySet())
		{
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}
	
		
	}
	
}