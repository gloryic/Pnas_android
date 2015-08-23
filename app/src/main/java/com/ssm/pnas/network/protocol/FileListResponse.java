package com.ssm.pnas.network.protocol;

import com.ssm.pnas.userSetting.ListRow;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FileListResponse {
	
	public ArrayList<ListRow> fileArrayList;

	public FileListResponse(JSONObject jsonObj) {
		OnSuccess(jsonObj);
	}

	protected void OnSuccess(JSONObject resultObj) {
		try {

			JSONObject responseData = resultObj.getJSONObject("responseData");
			JSONArray fileListArr = responseData.getJSONArray("filelist");
			JSONObject curFile = responseData.getJSONObject("curfile");

			int length = fileListArr.length();
			JSONObject row;

			String path;
			String code;
			boolean isDir;
			String[] pathArr;

			fileArrayList = new ArrayList();

			if(!curFile.getString("code").equals("0000")){
				path = "..";
				code = "-1";
				isDir = false;
				pathArr = path.split("/");
				fileArrayList.add(new ListRow(pathArr[pathArr.length-1],path,code,isDir));
			}

			for(int i=0; i<length; i++){
				row = fileListArr.getJSONObject(i);
				path = new String(row.getString("path"));
				code = new String(row.getString("code"));
				isDir = row.getBoolean("isDir");
				pathArr = path.split("/");
				fileArrayList.add(new ListRow(pathArr[pathArr.length-1],path,code,isDir));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<ListRow> getFileArrayList() {
		return fileArrayList;
	}
}
