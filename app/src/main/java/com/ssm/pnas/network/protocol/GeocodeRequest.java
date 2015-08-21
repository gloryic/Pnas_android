package com.ssm.pnas.network.protocol;

import org.json.JSONObject;

import com.android.volley.Request.Method;

public class GeocodeRequest extends Request {
	private static final String REST_PROTOCOL = "http://maps.googleapis.com/maps/api/geocode/json";
	public double lat;
	public double lng;
	
	public GeocodeRequest(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}
	
	@Override
	public JSONObject toJSonObject() {
		return null;
	}
	
	@Override
	public String toURI() {
		String result = REST_PROTOCOL+String.format("?latlng=%s,%s&sensor=true&language=ko", lat, lng);
		return result;
	}
	
	@Override
	public int getMethod() {
		return Method.GET;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}
}
