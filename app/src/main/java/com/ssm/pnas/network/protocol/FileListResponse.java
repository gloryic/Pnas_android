package com.ssm.pnas.network.protocol;

import com.ssm.pnas.nanohttpd.FileItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FileListResponse {
	
	public ArrayList<FileItem> fileArrayList;

	public FileListResponse(JSONObject jsonObj) {
		OnSuccess(jsonObj);
	}

	protected void OnSuccess(JSONObject resultObj) {
		try {

			JSONObject responseData = resultObj.getJSONObject("responseData");
			JSONArray fileListArr = responseData.getJSONArray("filelist");

			int length = fileListArr.length();
			JSONObject row;

			String path;
			String code;
			boolean isDir;

			fileArrayList = new ArrayList();

			for(int i=0; i<length; i++){
				row = fileListArr.getJSONObject(i);
				path = new String(row.getString("path").getBytes("ISO-8859-1"), "UTF-8");
				code = new String(row.getString("code").getBytes("ISO-8859-1"), "UTF-8");
				isDir = row.getBoolean("isDir");
				fileArrayList.add(new FileItem(path,isDir,code));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<FileItem> getFileArrayList() {
		return fileArrayList;
	}
}
