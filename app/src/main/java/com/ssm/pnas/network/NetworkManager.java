package com.ssm.pnas.network;


import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ssm.pnas.C;
import com.ssm.pnas.network.protocol.HeaderParameterizable;
import com.ssm.pnas.network.protocol.Parameterizable;

public class NetworkManager {
	
	private volatile static NetworkManager instance = null;
	
	 
	private NetworkManager() {
	}
	
	public static NetworkManager getInstance() {
		if (null == instance) {
			synchronized (NetworkManager.class) {
				instance = new NetworkManager();
			}
		}
		
		return instance;
	}
	
	private RequestQueue requestQueue = null;
	private Context context = null;
	
    public void initialize(Context _context) {
    	if (context == null) {
    		context = _context;
    		requestQueue = Volley.newRequestQueue(context);
    	}
    }
	
	public void request(Parameterizable param, Listener<JSONObject> listener, ErrorListener errorListener) {
		if (requestQueue == null) {
            throw new IllegalStateException("Volley Request Queue is not initialized.");
        }
		if (C.networkLogging) {
			Log.e("network", "request: " + param.toURI());
		}
				
		String url="";
		JSONObject jsonRequest = null;
		int method = param.getMethod();

		if (method == Method.GET) {
			url += param.toURI();
		}
		else {
			jsonRequest = param.toJSonObject();
		}
		
		Log.e("test", url);
		
		JsonObjectRequest req = new JsonObjectRequest(method, url, jsonRequest, listener, errorListener);
		requestQueue.add(req);
	}
	
	public void requestWithHeader(HeaderParameterizable param, Listener<JSONObject> listener, ErrorListener errorListener) {
		if (requestQueue == null) {
            throw new IllegalStateException("Volley Request Queue is not initialized.");
        }
		if (C.networkLogging) {
			Log.e("network", "request: " + param.toURI());
		}
				
		String url = param.toURI();
		JSONObject jsonRequest = null;
		
		int method = param.getMethod();
	
		jsonRequest = param.toJSonObject();
		
		
		final String token = param.getToken();
		
		//String query = URLEncoder.encode("apples oranges", "utf-8");
		
		Log.e("test", url);
		
		JsonObjectRequest req = new JsonObjectRequest(method, url, jsonRequest, listener, errorListener){
			/**Setup Header**/
			@Override
            public Map getHeaders() throws AuthFailureError {
                Map  params = new HashMap();
                params.put("Authorization", token);
		        params.put("Content-Type", "application/json");
                return params;
            }
		};
		requestQueue.add(req);
	}
	
	public void requestJSONArray(Parameterizable param, Listener<JSONArray> listener, ErrorListener errorListener) {
		if (requestQueue == null) {
            throw new IllegalStateException("Volley Request Queue is not initialized.");
        }
		if (C.networkLogging) {
			Log.e("network", "request: " + param.toURI());
		}
				
		String url="";
		JSONObject jsonRequest = null;
		int method = param.getMethod();
		
		if (method == Method.GET) {
			url += param.toURI();
		}
		else {
			jsonRequest = param.toJSonObject();
		}
		
		Log.e("test", url);
		
		JsonArrayRequest req = new JsonArrayRequest(url, listener, errorListener);
		requestQueue.add(req);
	}
	
	public void requestXML(Parameterizable param, Listener<String> listener, ErrorListener errorListener){
		if (requestQueue == null) {
            throw new IllegalStateException("Volley Request Queue is not initialized.");
        }
		if (C.networkLogging) {
			Log.e("network", "request: " + param.toURI());
		}
		
		Log.e("test", param.toURI());
		//xml로 올때
		StringRequest  req = new StringRequest(Method.GET, param.toURI(), listener, errorListener);
		requestQueue.add(req);		
	}
	
	public String getString(int resId) {
		try {
			return context.getString(resId);
		} catch (Exception e) {
			return "";
		}
	}
	
	public static String getEncodedStr(String str) {
		try {
			str = URLEncoder.encode(str, "UTF-8");
			String result = new String(str.getBytes("UTF8"));
			return result; 
		} catch (Exception e) {
			e.printStackTrace();
			return str;
		}
	}
	
	public static String getDecodedStr(String str) {
		try {
			return URLDecoder.decode(str, "utf-8");
		} catch (Exception ee) {
			return str;
		}
	}
}
