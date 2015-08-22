package com.ssm.pnas.nanohttpd;

import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by glory on 15. 8. 22..
 */
public class HashIndex {

    private volatile static HashIndex instance = null;
    private static String TAG = "HashIndex";
    private Map<String,FileItem> hashTable;
    public static int MAX_NUM = 10000;
    public static int MAX_RADIX = 4;

    public static HashIndex getInstance() {
        synchronized (HashIndex.class) {
            if (null == instance) {
                instance = new HashIndex();
            }
        }
        return instance;
    }

    private HashIndex(){
        hashTable = new HashMap();
    }

    public Map<String,FileItem> getHashMap(){
        return hashTable;
    }

    /**
     * code를 키로 갖는 path가 없다면 null반환한다.
     * */
    public FileItem getPathFromHash(String code){
        return hashTable.get(code);
    }

    /**
     * 10,000개가 꽉찼을때 null반환한다.
     * */
    public FileItem generateCode(String path){
        if(hashTable.size() == MAX_NUM) return null;
        int salt = 1;
        String code = getFormatedCode(hashCode(path, salt, MAX_NUM));
        FileItem item = hashTable.get(code);

        if(item != null && item.getPath().equals(path)) {
            Log.d(TAG, "already exist file");
            return item;
        }
        else {
            while (hashTable.get(code) != null)
                code = getFormatedCode(hashCode(path, salt += 3, MAX_NUM));
            File file = new File(path);
            if(file.isDirectory()){
                item = new FileItem(path,true, code);
                hashTable.put(code, item);
            }
            else{
                item = new FileItem(path,false, code);
                hashTable.put(code, item);
            }
            return item;
        }
    }

    public String getFormatedCode(int code){
        String strCode = code+"";
        for(int i = 0; i < MAX_RADIX - strCode.length(); i++)
            strCode = "0" + strCode;
        return strCode;
    }

    public int hashCode(String path, int salt, int Max){
        int total = salt;
        for(char c : path.toCharArray())
            total += (int)c;
        return total%Max;
    }
}
