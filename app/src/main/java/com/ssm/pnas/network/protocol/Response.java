package com.ssm.pnas.network.protocol;

import org.json.JSONObject;

import android.util.Log;

import com.ssm.pnas.C;
import com.ssm.pnas.network.Status;

public abstract class Response extends Protocol {
	
	protected static final String STATUS_KEY = "status";	
	protected static final String RESPONSE_DATA_KEY = "responseData";
	protected static final String ERROR_DESC_KEY = "errorDesc";
	protected static final String UNKNOWN_ERROR_DESC = "Unknown Error";
	
	private Status status;
	
	public Response(JSONObject jsonObj) {
		Object result = null;
		OnInit();
		try {		
			status = Status.getStatus(jsonObj.getInt(STATUS_KEY));
			if (status == Status.SUCCESS) {
				result = jsonObj.get(RESPONSE_DATA_KEY);
				OnSuccess(result);
			}
			else {
				result = jsonObj.get(ERROR_DESC_KEY);
				OnFail(status, result);
			}
			
			if (C.networkLogging) {
				Log.e("test-network", "response: " + result.toString());
			}
		} catch (Exception e) {
			status = Status.UNKNOWN_ERROR;
			OnFail(status, UNKNOWN_ERROR_DESC);
		}
	}
	
	protected void OnInit() {
	}
	
	protected void OnSuccess(Object responseData) {	
	}
	
	protected void OnFail(Status status, Object errorDesc) {
	}
	
	public final Status getStatus() {
		return status;
	}

}
