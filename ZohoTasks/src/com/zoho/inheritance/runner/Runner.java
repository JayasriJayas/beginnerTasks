package com.zoho.inheritance.runner;

import com.zoho.inheritance.task.Car;
import com.zoho.inheritance.task.Swift;
import com.zoho.inheritance.task.SCross;
import com.zoho.inheritance.task.XUV;
import com.zoho.inheritance.birds.BirdAbstract;
import com.zoho.inheritance.birds.ParrotMod;
import com.zoho.inheritance.bird.Bird;
import com.zoho.inheritance.bird.Duck;
import java.util.Scanner;


class Runner{
	
	public static void main(String[] args){
		
		Scanner sc = new Scanner(System.in);
		Runner runObj = new Runner();
		int choice;
		
		try{
		do{
			System.out.println("1.To call getter and setter methods of Swift class alone");
			System.out.println("2.To call getter and setter methods os Scross and parent class methods");
			System.out.println("3.To ivoke a runner class method with ojects");
			System.out.println("4.Identifying actual underlying object"); 
			System.out.println("5.To invoke the method using swift , car ,xuv,scross objects");
			System.out.println("6.To invoke overriden method with the objects");
			System.out.println("7.Try to create instance of overloaded constructor");
			System.out.println("8.To create instance of abstract class");
			System.out.println("9.To create abstract method and overriding it");
			System.out.println("Enter your choice(to exit enter -1) :");
		        choice = sc.nextInt();
		
			
			
			switch(choice){
				case 1:
					Swift swift = new Swift();
					System.out.println("Enter Swift car seats,airbag,model,color");
					int seats=sc.nextInt();
					swift.setSeats(seats);
					int airbags=sc.nextInt();
				   	swift.setAirbags(airbags);
					String model = sc.next();
					swift.setModel(model);
					String color = sc.next();
					swift.setColor(color);
					System.out.println("The Swift seats :"+ swift.getSeats());
					System.out.println("The Swift airbags :"+ swift.getAirbags());
					System.out.println("The Swift model :" + swift.getModel());
					System.out.println("The Swift color :"+ swift.getColor());
					break;
					
					
				case 2:
					SCross scross = new SCross();
					System.out.println("Enter SCross car seats,airbag,model,color");
					int seat=sc.nextInt();
					scross.setSeats(seat);
					int airbag=sc.nextInt();
				   	scross.setAirbags(airbag);
					String modelScross = sc.next();
					scross.setModel(modelScross);
					String colorScross = sc.next();
					scross.setColor(colorScross);
					System.out.println("Enter SCross car yearOfMake,enginenumber,type");
					int yearOfMake=sc.nextInt();
					scross.setYearOfMake(yearOfMake);
					String engineNumber=sc.next();
					scross.setEngineNumber(engineNumber);
					String type=sc.next();
					scross.setType(type);
					System.out.println("The SCross seats :"+ scross.getSeats());
					System.out.println("The SCross airbags :"+ scross.getAirbags());
					System.out.println("The SCross model :" + scross.getModel());
					System.out.println("The SCross color :"+ scross.getColor());
					System.out.println("The SCross yearOfMake :"+ scross.getYearOfMake());
					System.out.println("The SCross engine number :" + scross.getEngineNumber());
					System.out.println("The SCross type :"+ scross.getType());
					break;
	
				case 3:
					
					Swift swifttObj =new Swift();
					runObj.identifyObj(swifttObj);
					break;
				case 4:
					
					Swift swiftObj = new Swift();
					runObj.identifyObj(swiftObj);
					SCross scrossObj = new SCross();
					runObj.identifyObj(scrossObj);
					XUV xuvObj = new XUV();
					runObj.identifyObj(xuvObj);
					break;
				case 5:

					Swift swiftObject = new Swift();
					runObj.swiftMethod(swiftObject);
					Car obj = new Swift();
			//		runObj.swiftMethod(obj);
					XUV xuvObject= new XUV();
			//		runObj.swiftMethod(xuvObject);
					SCross scrossObject =new SCross();
			//		runObj.swiftMethod(scrossObject);
					break;
				case 6:
					SCross scrossOb = new SCross();
					scrossOb.maintenance();
					Car carReference=new SCross();
					carReference.maintenance();
					Car car = new Car();
					car.maintenance();
					Swift swiftOb = new Swift();
					swiftOb.maintenance();
					break;
				case 7:
					XUV defaultConstructor = new XUV();
			//		XUV overloadedConstructor = new XUV("hello");
					break;
				case 8:
			//		BirdAbstract bird = new BirdAbstract();
				        ParrotMod parrot = new ParrotMod();
					parrot.fly();
					parrot.speak();	
					break;
				case 9:
					Duck duck = new Duck();
					duck.fly();
					duck.speak();
					break;
				default:
					System.out.println("Enter a valid choice");
					
			}
		        System.out.println("Enter your choice(to exit enter -1) :");
		        choice = sc.nextInt();
		}while(choice!=-1);
	}
	finally{
		sc.close();
	}
 }
	void identifyObj(Car obj){
		if(obj instanceof Swift){
			System.out.println("Hatch");
		}
		if(obj instanceof XUV){
			System.out.println("SUV");
		}
		if(obj instanceof SCross){
			System.out.println("Seden");
		}
	}
	void swiftMethod(Swift obj){
		System.out.println("Swift Method");
        }
 	
}	
		
		