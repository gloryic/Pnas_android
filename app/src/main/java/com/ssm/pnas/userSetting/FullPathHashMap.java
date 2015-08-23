package com.ssm.pnas.userSetting;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kangSI on 2015-08-23.
 */
public class FullPathHashMap {
    public HashMap<String,String> mss = new HashMap<String,String>();

    private volatile static FullPathHashMap instance = null;

    public static FullPathHashMap getInstance(){
        synchronized (FullPathHashMap.class){
            if(instance == null){
                instance =  new FullPathHashMap();
            }
        }
        return instance;
    }

    private FullPathHashMap()
    {
    }
}
