package com.zoho.arraylist.task;


import com.zoho.arraylist.util.Util;
import com.zoho.arraylist.exception.InvalidException;
import com.zoho.arraylist.exception.OutOfBoundException;
import java.util.ArrayList;
import java.util.List;

public class ArrayListTask{

    
    public <T> List<T> getArrayList() {
        return new ArrayList<>();
    }

    public <T> int getLength(List<T> arraylist) throws InvalidException {
        Util.validate(arraylist);
        return arraylist.size();
    }

    public <T> List<T> addElements(List<T> arraylist, T[] elements) throws InvalidException {
        Util.validate(arraylist);
        Util.validate(elements);
        for (T value : elements) {
            arraylist.add(value);
        }
        return arraylist;
    }

    
    public <T> int findIndex(List<T> arraylist, T element) throws InvalidException {
        Util.validate(arraylist);
        Util.validate(element);
        return arraylist.indexOf(element);
    }
    public <T> T getElement(List<T> arraylist, int index) throws InvalidException, OutOfBoundException {
        int length = getLength(arraylist);
        Util.indexCheck(index, length);
        return arraylist.get(index);
    }

    
    public <T> int getFirstPosition(List<T> arraylist, T element) throws InvalidException {
        Util.validate(arraylist);
        Util.validate(element);
        return arraylist.indexOf(element);
    }

  
    public <T> int getLastPosition(List<T> arraylist, T element) throws InvalidException {
        Util.validate(arraylist);
        Util.validate(element);
        return arraylist.lastIndexOf(element);
    }

    
    public <T> List<T> addAtPosition(List<T> arraylist, T element, int index)throws InvalidException, OutOfBoundException {
        int length = getLength(arraylist);
        Util.indexCheck(index, length);
        Util.validate(arraylist);
        arraylist.add(index, element);
        return arraylist;
    }
    public <T> List<T> getSubList(List<T> arraylist, int fromIndex, int toIndex) throws InvalidException {
        Util.validate(arraylist);
        return new ArrayList<>(arraylist.subList(fromIndex, toIndex));
    }

   
    public <T> List<T> addList(List<T> arraylist,List<T> list) throws InvalidException {
        Util.validate(arraylist);
        Util.validate(list);
        arraylist.addAll(list);
        return arraylist;
    }

    public <T> List<T> removeElement(List<T> arraylist, int index)throws InvalidException, OutOfBoundException {
        int length = getLength(arraylist);
        Util.indexCheck(index, length);
        arraylist.remove(index);
        return arraylist;
    }

    
    public <T>List<T> removeList(List<T> arraylist, List<T> list) throws InvalidException {
        Util.validate(arraylist);
        Util.validate(list);
        arraylist.removeAll(list);
        return arraylist;
    }

    public <T>List<T> retainList(List<T> arraylist, List<T> list) throws InvalidException {
        Util.validate(arraylist);
        Util.validate(list);
        arraylist.retainAll(list);
        return arraylist;
    }

    
    public <T> boolean checkElement(List<T> arraylist, T element) throws InvalidException {
        Util.validate(arraylist);
        Util.validate(element);
        return arraylist.contains(element);
    }

       public <T>List<T> clearList(List<T> arraylist) throws InvalidException {
        Util.validate(arraylist);
        arraylist.clear();
        return arraylist;
    }
}