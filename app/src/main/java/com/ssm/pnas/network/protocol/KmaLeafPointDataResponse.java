package com.ssm.pnas.network.protocol;

import org.json.JSONArray;
import org.json.JSONObject;

public class KmaLeafPointDataResponse {
	
	public int leafValue;
	public int gridx, gridy;
	
	public KmaLeafPointDataResponse(JSONArray jsonObj, String sublocality_level_2) {
		OnSuccess(jsonObj, sublocality_level_2);
	}

	protected void OnSuccess(JSONArray responseData, String sublocality_level_2) {
		try {
			
			int length = responseData.length();
			JSONObject row;
			String value;
			for(int i=0; i<length; i++){
				row = responseData.getJSONObject(i);
				value = new String(row.getString("value").getBytes("ISO-8859-1"), "UTF-8");
				if(value.equals(sublocality_level_2)){
					leafValue = row.getInt("code");
					gridx = row.getInt("x");
					gridy = row.getInt("y");
					break;
				}
				leafValue = -1;
			}
			
			if(leafValue==-1){
				row = responseData.getJSONObject(0);
				leafValue = row.getInt("code");
				gridx = row.getInt("x");
				gridy = row.getInt("y");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getLeafValue() {
		return leafValue;
	}
	
	public int getGridX() {
		return gridx;
	}
	
	public int getGridY() {
		return gridy;
	}
}
