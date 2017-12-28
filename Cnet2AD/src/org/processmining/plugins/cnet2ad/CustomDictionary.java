package org.processmining.plugins.cnet2ad;

import java.util.ArrayList;

/**
 * 
 * @author Utente
 * @param <T>
 * @param <V> 
 */
public class CustomDictionary<T, V> {

    /**
     *  public
     */
    public ArrayList<T> keys;
    /**
     * public 
     */
    public ArrayList<V> values;

    public CustomDictionary(){
        this.keys = new ArrayList<T>();
        this.values = new ArrayList<V>();
    }

    public void add(T key, V value){
        this.keys.add(key);
        this.values.add(value);
    }

    public T getKey(int index){
        if( index < this.keys.size())
            return this.keys.get(index);
        return null;
    }

    public T getKeyByValue(V value){
        int index = this.values.indexOf(value);
        return this.keys.get(index);
    }

    public V getValueByKey(T key){
        int index = this.keys.indexOf(key);
        return this.values.get(index);
    }

    public V getValue(int index){
        if( index < this.values.size())
            return this.values.get(index);
        return null;
    }

    public int size(){
        return this.keys.size();
    }

}
