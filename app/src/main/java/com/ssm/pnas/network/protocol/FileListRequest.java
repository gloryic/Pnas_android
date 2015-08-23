package com.ssm.pnas.network.protocol;

import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.ssm.pnas.C;

public class FileListRequest extends Request implements Parameterizable{
	private static final String REST_PROTOCOL = "/api/filelist";

	public String code;
	public String url;

	public FileListRequest(String url, String code) {
		this.code = code;
		this.url = url;
	}

	@Override
	public JSONObject toJSonObject() {
		JSONObject json = new JSONObject();
		try {
			json.put("url", url);
			json.put("code", code);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	@Override
	public String toURI() {
		return "http://" + this.url + ":" + C.port + REST_PROTOCOL + "/" + code;
	}
	
	@Override
	public int getMethod() {
		return Method.GET;
	}
}
