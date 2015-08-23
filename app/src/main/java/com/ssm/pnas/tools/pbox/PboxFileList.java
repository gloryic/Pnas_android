package com.ssm.pnas.tools.pbox;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.ssm.pnas.C;
import com.ssm.pnas.network.NetworkManager;
import com.ssm.pnas.network.protocol.FileListResponse;
import com.ssm.pnas.network.protocol.FileListRequest;
import com.ssm.pnas.userSetting.ListRow;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by glory on 15. 8. 22..
 */
public class PboxFileList {

    private Response.Listener<JSONObject> onFileListResponse;
    private Response.ErrorListener onErrorListener;
    private volatile static PboxFileList instance = null;
    private static String TAG = "PboxFileList";
    public ArrayList<ListRow> fileItemArrayList;

    private PboxFileList() {
        //Response Listener binding
        onFileListResponse = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(TAG, response.toString());
                    FileListResponse fileListResponse = new FileListResponse(response);

                    //TODO
                    //fileItemArrayList = fileListResponse.getFileArrayList();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        onErrorListener = new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
            }
        };
    }

    public static PboxFileList getInstance() {
        if (null == instance) {
            synchronized (PboxFileList.class) {
                instance = new PboxFileList();
            }
        }
        return instance;
    }

    public void getPboxList(String url, String code){
        Log.d(TAG,url);
        FileListRequest fileListRequest = new FileListRequest(url,code);
        NetworkManager.getInstance().request(fileListRequest, onFileListResponse, onErrorListener);
    }
}
