package com.ssm.pnas.network.protocol;

import org.json.JSONObject;

public interface Parameterizable {

	abstract JSONObject toJSonObject();
	abstract String toURI();
	abstract int getMethod();
}
