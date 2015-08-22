package com.ssm.pnas.network.protocol;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class PboxListResponse{
	public JSONArray result;
	public int status;
	public String ip;

	public PboxListResponse(JSONObject jsonObj) {
		OnSuccess(jsonObj);
	}
	
	protected void OnSuccess(JSONObject responseData) {
		try {
			JSONObject jsonObj = responseData;
			this.status = jsonObj.getInt("status");
			this.ip = jsonObj.getJSONObject("responseData").getString("ip");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


