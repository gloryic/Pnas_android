package com.ssm.pnas.nanohttpd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.ssm.pnas.C;
import com.ssm.pnas.Dbg;

public class WebManager {
	
	private Context mContext;
	private volatile static WebManager instance;
	private Gson gson;
    private static String TAG = "WebManager";
	
	private WebManager(Context context){
		this.mContext = context;
		gson = new Gson();
	}
	
	public static WebManager getInstance(Context context) {
		synchronized (WebManager.class) {
		if (null == instance) {
				instance = new WebManager(context);
			}
		}
		instance.mContext = context;
		return instance;
	}
	
	private long[] convertTime(long seconds){
		long day = (int)TimeUnit.SECONDS.toDays(seconds);
		long hours = TimeUnit.SECONDS.toHours(seconds) - (day *24);
		long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
		long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
		return new long[] {day,hours,minute,second};
	}

	public String executeAPI(String[] splitUri, Map<String,String> parms, NanoHTTPD.Method method) throws Exception{
		if (NanoHTTPD.Method.GET.equals(method))
			return getAPI(splitUri, parms, method);
		else if(NanoHTTPD.Method.POST.equals(method))
			return postAPI(splitUri, parms, method);
		else
			return "{\"status\":\"ERROR\",\"message\":\"NOT SUPPORT METHOD\"}";
	}

	public String getParams(Map<String,String> parms, String key, String defaultValue){
		String value = parms.get(key);
		return value != null ? value : defaultValue;
	}
	
	public String postAPI(String[] splitUri, Map<String,String> parms, NanoHTTPD.Method method) throws Exception{
        return "";
	}
	
	public String getAPI(String[] splitUri, Map<String,String> parms, NanoHTTPD.Method method) throws Exception{
		JSONObject result = new JSONObject();
		//splitUri[1]은 api

	    if(splitUri[2].equals("ping")) {
            result.put("status", "200");
            JSONObject subResult = new JSONObject();
            subResult.put("ip", C.localIP);
            result.put("responseData",subResult);
            return result.toString().replaceAll("\\\\", "");
        }
        else if(splitUri[2].equals("viewall")){
            result.put("status", "200");
            JSONObject subResult = new JSONObject();
            Map<String,String> selects = HashIndex.getInstance().getHashMap();

            for(Map.Entry<String, String> entry : selects.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                subResult.put(key,value);
            }

            result.put("responseData",subResult);
        	return result.toString().replaceAll("\\\\","");
        }
        else{
        	Log.d(TAG, "ERROR : Wrong Method");
        	for(String oneMethod : splitUri){
        		Log.d(TAG,oneMethod);
        	}
        	throw new Exception("METHOD ERROR");
        }
	}
}
