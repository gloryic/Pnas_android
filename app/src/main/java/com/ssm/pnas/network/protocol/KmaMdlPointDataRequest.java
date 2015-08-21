package com.ssm.pnas.network.protocol;

import org.json.JSONObject;

import com.android.volley.Request.Method;

public class KmaMdlPointDataRequest extends Request {
	private static final String REST_PROTOCOL = "http://www.kma.go.kr/DFSROOT/POINT/DATA/mdl.%s.json.txt";
	
	/**
	 * ex)서울특별시
	 * */
	public int topValue;
	
	public KmaMdlPointDataRequest(int topValue) {
		this.topValue = topValue;
	}
	
	@Override
	public JSONObject toJSonObject() {
		return null;
	}
	
	@Override
	public String toURI() {
		String result = String.format(REST_PROTOCOL, topValue);
		return result;
	}
	
	@Override
	public int getMethod() {
		return Method.GET;
	}
}
