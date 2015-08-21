package com.ssm.pnas.network.protocol;

import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.ssm.pnas.C;

public class AdCountUpRequest extends Request implements HeaderParameterizable {
	// 현 위치 정보 가져오기 프로토콜 요청
	
	private static final String REST_PROTOCOL = "/client/count/add"; 
	
	private String token;
	private String id;
	private String type;
	private String count;
	
	public AdCountUpRequest(String token, String id, String type, String count) {
		this.token = token;
		this.id = id;
		this.type = type;
		this.count = count;
	}
	
	@Override
	public JSONObject toJSonObject() {
		JSONObject json = new JSONObject();
		try {
			json.put(ad_countup_id, id);
			json.put(ad_countup_type, type);
			json.put(ad_countup_count, count);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	@Override
	public String toURI() {
		return C.baseURL + REST_PROTOCOL;
	}

	@Override
	public int getMethod() {
		return Method.POST;
	}

	@Override
	public String getToken() {
		return this.token;
	}
}
