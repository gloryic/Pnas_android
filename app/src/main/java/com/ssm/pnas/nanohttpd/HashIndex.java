package com.ssm.pnas.nanohttpd;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by glory on 15. 8. 22..
 */
public class HashIndex {

    private volatile static HashIndex instance = null;
    private static String TAG = "HashIndex";
    private Map<String,String> hashTable;
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

    public Map<String,String> getHashMap(){
        return hashTable;
    }

    /**
     * code를 키로 갖는 path가 없다면 null반환한다.
     * */
    public String getPathFromHash(String code){
        return hashTable.get(code);
    }

    /**
     * 10,000개가 꽉찼을때 -1반환한다.
     * */
    public String generateCode(String path){
        if(hashTable.size() == MAX_NUM) return null;
        int salt = 1;
        String code = getFormatedCode(hashCode(path, salt, MAX_NUM));

        if(hashTable.get(code) == path) {
            Log.d(TAG, "already exist file");
            return code;
        }
        else {
            while (hashTable.get(code) != null)
                code = getFormatedCode(hashCode(path, salt += 3, MAX_NUM));
            hashTable.put(code, path);
            return code;
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
