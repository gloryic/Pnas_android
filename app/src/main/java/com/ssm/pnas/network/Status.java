package com.ssm.pnas.network;

public enum Status {

	SUCCESS(200),
	UNKNOWN_ERROR(0),
	ALREADY_EXIST_ERROR(303),
	NOT_FOUND_ERROR(404),
	INTERNAL_SERVER_ERROR(500),
	;
	
	private int code;
	
	private Status(int code) {
		this.code = code;
	}
	
	public static Status getStatus(int code) {
		for (Status status : values()) {
			if (status.code == code)
				return status;
		}
		return UNKNOWN_ERROR;
	}
}
