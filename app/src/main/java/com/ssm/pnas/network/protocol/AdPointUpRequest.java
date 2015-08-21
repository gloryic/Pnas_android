package com.ssm.pnas.network.protocol;

import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.ssm.pnas.C;

public class AdPointUpRequest extends Request implements HeaderParameterizable {
	// 현 위치 정보 가져오기 프로토콜 요청
	
	private static final String REST_PROTOCOL = "/client/point/add"; 
	
	private String token;
	
	public AdPointUpRequest(String token) {
		this.token = token;
	}
	
	@Override
	public JSONObject toJSonObject() {
		JSONObject json = new JSONObject();
		try {
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
