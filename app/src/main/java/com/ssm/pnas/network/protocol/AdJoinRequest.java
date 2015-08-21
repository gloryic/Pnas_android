package com.ssm.pnas.network.protocol;

import org.json.JSONObject;

import android.util.Base64;

import com.android.volley.Request.Method;
import com.ssm.pnas.C;

public class AdJoinRequest extends Request implements HeaderParameterizable {
	// 현 위치 정보 가져오기 프로토콜 요청
	
	private static final String REST_PROTOCOL = "/client/signup"; 
	
	private String mail;
	private String password;
	private String nickName;
	
	public AdJoinRequest(String nickName, String mail, String password) {
		this.mail = mail;
		this.password = password;
		this.nickName = nickName;
	}
	
	@Override
	public JSONObject toJSonObject() {
		JSONObject json = new JSONObject();
		try {
			json.put(ad_login_nickname, nickName);
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
		return Base64.encodeToString(new String(mail+":"+password).getBytes(), Base64.DEFAULT);
	}
}
