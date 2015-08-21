package com.ssm.pnas.network.protocol;

import org.json.JSONObject;

import com.android.volley.Request.Method;

public class KmaLeafPointDataRequest extends Request {
	private static final String REST_PROTOCOL = "http://www.kma.go.kr/DFSROOT/POINT/DATA/leaf.%s.json.txt";
	
	/**
	 * ex)금천구
	 * */
	public int mdlValue;
	
	public KmaLeafPointDataRequest(int mdlValue) {
		this.mdlValue = mdlValue;
	}
	
	@Override
	public JSONObject toJSonObject() {
		return null;
	}
	
	@Override
	public String toURI() {
		String result = String.format(REST_PROTOCOL, mdlValue);
		return result;
	}
	
	@Override
	public int getMethod() {
		return Method.GET;
	}
}
