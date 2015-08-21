package com.ssm.pnas.tools.device;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

public class Devices {
	
	public String getDeviceName() {
	    String manufacturer = Build.MANUFACTURER;
	    String model = Build.MODEL;
	    if (model.startsWith(manufacturer)) {
	        return capitalize(model);
	    } else {
	        return capitalize(manufacturer) + " " + model;
	    }
	}

	private String capitalize(String s) {
	    if (s == null || s.length() == 0) {
	        return "";
	    }
	    char first = s.charAt(0);
	    if (Character.isUpperCase(first)) {
	        return s;
	    } else {
	        return Character.toUpperCase(first) + s.substring(1);
	    }
	}
	
	public boolean checkPermission(Context mContext){
	    String permission = "android.permission.READ_LOGS";
	    int res = mContext.checkCallingOrSelfPermission(permission);
	    return (res == PackageManager.PERMISSION_GRANTED);            
	}
}
