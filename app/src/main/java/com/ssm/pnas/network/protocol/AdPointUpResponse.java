package com.ssm.pnas.network.protocol;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class AdPointUpResponse {
	public JSONObject first_result;
	public JSONArray result;
	public JSONArray address_components;
	public String locality, sublocality_level_1, sublocality_level_2, formatted_address;

	public AdPointUpResponse(JSONObject jsonObj) {
		OnSuccess(jsonObj);
	}
	
	protected void OnSuccess(JSONObject responseData) {
		try {
			
			JSONObject jsonObj = responseData;
			
			
			Log.d("",jsonObj.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getLocality() {
		return locality;
	}

	public String getSublocality_level_1() {
		return sublocality_level_1;
	}

	public String getSublocality_level_2() {
		return sublocality_level_2;
	}

	public String getFormatted_address() {
		return formatted_address;
	}
}


