package com.ssm.pnas.network.protocol;

import org.json.JSONArray;
import org.json.JSONObject;

public class GeocodeResponse {
		
	public JSONObject first_result;
	public JSONArray result;
	public JSONArray address_components;
	public String locality, sublocality_level_1, sublocality_level_2, formatted_address;

	public GeocodeResponse(JSONObject jsonObj) {
		OnSuccess(jsonObj);
	}
	
	protected void OnSuccess(JSONObject responseData) {
		try {
			JSONObject jsonObj = responseData;
			result = jsonObj.getJSONArray("results");
			String long_name="", type="";
			JSONObject row;
			
			if(result.length() > 0){
				first_result = (JSONObject) result.get(0);
				address_components = first_result.getJSONArray("address_components");
				for(int i=0; i<address_components.length(); i++){
					row = address_components.getJSONObject(i);
					long_name = row.getString("long_name");
					type = row.getJSONArray("types").get(0).toString();
					if(type.equals("locality"))
						locality = long_name;
					else if(type.equals("sublocality_level_1"))
						sublocality_level_1 = long_name;
					else if(type.equals("sublocality_level_2"))
						sublocality_level_2 = long_name;
				}
				formatted_address = first_result.getString("formatted_address");
			}
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
