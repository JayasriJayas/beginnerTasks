package com.zoho.files.singleton;
import java.io.Serializable;

//public class SerializationSingleton implements Serializable{
//	private static final long serialVersion = 1L;
//	
//	private SerializationSingleton() {}
//	
//	 private static class InnerClass {
//	        private static final SerializationSingleton  instance = new SerializationSingleton ();
//	    }
//
//	    public static SerializationSingleton  getInstance() {
//	        return InnerClass.instance;
//	    }
//	
//	
//		
//}
public class SerializationSingleton implements Serializable {
    private static final long serialVersionUID = 1L; 

    private SerializationSingleton() { }

    private static class InnerClass {
        private static final SerializationSingleton instance = new SerializationSingleton();
    }

    public static SerializationSingleton getInstance() {
        return InnerClass.instance;
    }

    
    protected Object readResolve() { 
        return getInstance();
    }
}
