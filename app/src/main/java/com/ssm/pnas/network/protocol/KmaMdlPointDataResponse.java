package com.ssm.pnas.network.protocol;

import org.json.JSONArray;
import org.json.JSONObject;

public class KmaMdlPointDataResponse {
	
	/**
	 * ex) 종로구
	 * */
	public int mdlValue;
	
	public KmaMdlPointDataResponse(JSONArray jsonObj, String sublocality_level_1) {
		OnSuccess(jsonObj, sublocality_level_1);
	}

	protected void OnSuccess(JSONArray responseData, String sublocality_level_1) {
		try {
			
			int length = responseData.length();
			JSONObject row;
			String value;
			for(int i=0; i<length; i++){
				row = responseData.getJSONObject(i);
				value = new String(row.getString("value").getBytes("ISO-8859-1"), "UTF-8");
				if(value.equals(sublocality_level_1)){
					mdlValue = row.getInt("code");
					break;
				}
				mdlValue = -1;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getMdlValue() {
		return mdlValue;
	}
}
