package com.ssm.pnas.network.protocol;

import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.ssm.pnas.C;

public class PboxListRequest extends Request implements Parameterizable {
	// 같은 라우터 안의 Pbox를 요청하는 프로토콜
	
	private static final String REST_PROTOCOL = "/api/ping";
	private String url;

	public PboxListRequest(String url) {
		this.url = url;
	}
	
	@Override
	public JSONObject toJSonObject() {
		JSONObject json = new JSONObject();
		try {
			json.put(url, url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	@Override
	public String toURI() {
		return "http://" + this.url + ":" + C.port + REST_PROTOCOL;
	}

	@Override
	public int getMethod() {
		return Method.GET;
	}

}
