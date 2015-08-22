package com.ssm.pnas.nanohttpd;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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

    public boolean isInBoundary(String strCode){
        try{
            int code = Integer.parseInt(strCode);
            if(0 < code && HashIndex.MAX_NUM > code) return true;
            else return false;
        }
        catch(NumberFormatException e) {
            Log.d("httpd",e.getMessage());
            return false;
        }
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
        else if(splitUri.length > 3 && splitUri[2].equals("filelist") && isInBoundary(splitUri[3])){
            /**
             * URI : /api/filelist/0012
             * DESC : 0012를 키로 갖는 값의 하위 디레토리 or 파일 리스트를 jsonArr로 반환한다.
             * */

            String code = splitUri[3];

            //TODO
            Httpd httpd = Httpd.getInstance(mContext);

            String fullPath = httpd.getFilePath(code);
            String[] pathArr = fullPath.split("/");
            String curPath = pathArr[pathArr.length-1];

            Log.d(TAG, fullPath);

            result.put("status", "200");
            JSONObject subResult = new JSONObject();
            JSONObject fileListJson = new JSONObject();
            JSONObject curfile = new JSONObject();

            String [] list = {"1"}; //TODO

            curfile.put(code,curPath);

            for(String one : list){
                Log.d(TAG, one);
                fileListJson.put(HashIndex.getInstance().generateCode(one), one); //"emulater/0/test.txt" 완전 full path
            }
            subResult.put("curfile",fileListJson);
            subResult.put("filelist",fileListJson);

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
