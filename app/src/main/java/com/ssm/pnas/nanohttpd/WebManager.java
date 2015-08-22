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
import com.ssm.pnas.tools.file.FileManager;

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
            JSONArray subResult = new JSONArray();
            Map<String,FileItem> selects = HashIndex.getInstance().getHashMap();

            for(Map.Entry<String, FileItem> entry : selects.entrySet()) {
                JSONObject tmp = new JSONObject();
                tmp.put("code", entry.getKey());
                tmp.put("path",entry.getValue().getPath());
                tmp.put("isDir",entry.getValue().isDir());
                subResult.put(tmp);
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

            FileItem curItem = httpd.getFilePath(code);

            if(curItem == null)
                throw new Exception("WRONG CODE");

            String fullPath = curItem.getPath();
//            String[] pathArr = fullPath.split("/");
//            String curPath = pathArr[pathArr.length-1];

            Log.d(TAG, fullPath);

            result.put("status", "200");
            JSONArray fileListJson = new JSONArray();
            JSONObject subResult = new JSONObject();

            String [] list = FileManager.getInstance().getFileList(fullPath);

            JSONObject tmp = new JSONObject();
            tmp.put("code", curItem.getCode());
            tmp.put("path", curItem.getPath());
            tmp.put("isDir", curItem.isDir());

            subResult.put("curfile",tmp);

            for(String one : list){
                one = fullPath.concat("/").concat(one);
                FileItem item = HashIndex.getInstance().generateCode(one);
                tmp = new JSONObject();
                tmp.put("code", item.getCode());
                tmp.put("path", item.getPath());
                tmp.put("isDir", item.isDir());
                fileListJson.put(tmp);
            }

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
