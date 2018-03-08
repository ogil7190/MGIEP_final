package edu.mriu.kankhol;

import java.util.HashMap;

/**
 * Created by ogil on 01/12/17.
 */

/**
 * A class to store object on runtime to provide a facility passing data and storing object
 */
public class LocalDataStore {
    private static HashMap<String,Object> map = new HashMap<>();

    public static void putObject(String name, Object value){
        map.put(name, value);
    }

    public static Object getObject(String name){
        return map.get(name);
    }
}
