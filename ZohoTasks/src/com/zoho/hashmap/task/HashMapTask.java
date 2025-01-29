package com.zoho.hashmap.task;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.zoho.hashmap.util.Util;
import com.zoho.hashmap.exception.InvalidException;


public class HashMapTask{

	public <K,V> Map<K,V> getHashMap(){
		return new HashMap<>();
	} 
	
	public <K,V> int getSize(Map<K,V> hashMap)throws InvalidException{
		Util.validate(hashMap);
		return hashMap.size();
	}
	public <K,V> void addElements(Map<K,V> hashMap, K key, V value )throws InvalidException
	{
		Util.validate(hashMap);
		hashMap.put(key,value);
	}
	public <K,V> boolean checkKey(Map<K,V> hashMap, K key)throws InvalidException
	{
		Util.validate(hashMap);
		return hashMap.containsKey(key);
	}
	public <K,V> boolean checkValue(Map<K,V> hashMap, V value)throws InvalidException
	{
		Util.validate(hashMap);
		return hashMap.containsValue(value);
	}
	public <K,V> Set<K> getKeySet(Map<K,V> hashMap)throws InvalidException 
	{
		Util.validate(hashMap);
		return hashMap.keySet();
	}
	public <K,V> V getKeyValue(Map<K,V> hashMap,K key)throws InvalidException
	{
		Util.validate(hashMap);
		return hashMap.get(key);
	}
	public <K,V> V getDefaultValue(Map<K,V> hashMap,K key, V value)throws InvalidException
	{
		Util.validate(hashMap);
		return hashMap.getOrDefault(key,value);
	}
	public <K,V> void removeKey(Map<K,V> hashMap, K key)throws InvalidException
	{
		Util.validate(hashMap);
		hashMap.remove(key);
	}
	public <K,V> void removeIfVaue(Map<K,V> hashMap,K key, V value)throws InvalidException
	{
		Util.validate(hashMap);
		hashMap.remove(key, value);
	}
	public <K,V> void replaceValue(Map<K,V> hashMap,K key, V value)throws InvalidException
	{	
		Util.validate(hashMap);
		hashMap.replace(key, value);
	} 
	public <K,V> void replaceWithValue(Map<K,V> hashMap,K key, V oldValue, V newValue)throws InvalidException
	{
		Util.validate(hashMap);
		hashMap.replace(key, oldValue, newValue);
	}
	public <K,V> void addAllElements(Map<K,V> hashMap, Map<K,V> map)throws InvalidException
	{
		Util.validate(map);
		hashMap.putAll(map);
	}
	public <K,V> void clearAll(Map<K,V> hashMap)throws InvalidException
	{
		Util.validate(hashMap);
		hashMap.clear();
	
	}
	public <K,V> String[][] iterateMap(Map<K,V> hashMap)throws InvalidException
	{
		int count=getSize(hashMap);
		String[][] setArray = new String[count][2]; 
		int i=0;
		for(Map.Entry<K,V> entry : hashMap.entrySet()){
			setArray[i][0]=String.valueOf(entry.getKey());
			setArray[i][1]=String.valueOf(entry.getValue());
			i++;	
		}
		return setArray;
	}
		
}
