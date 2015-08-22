package com.ssm.pnas.tools.pbox;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.ssm.pnas.C;
import com.ssm.pnas.network.NetworkManager;
import com.ssm.pnas.network.protocol.PboxListRequest;
import com.ssm.pnas.network.protocol.PboxListResponse;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by glory on 15. 8. 22..
 */
public class PboxList {

    private Response.Listener<JSONObject> onPboxListResponse;
    private Response.ErrorListener onErrorListener;
    private volatile static PboxList instance = null;
    private static String TAG = "PboxList";
    private static int Success = 200;
    private List<String> pboxlist;

    private PboxList() {
        //Response Listener binding
        onPboxListResponse = new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(TAG, response.toString());
                    PboxListResponse pboxListResponse = new PboxListResponse(response);
                    if(pboxListResponse.status == Success){
                        pboxlist.add(pboxListResponse.ip);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        onErrorListener = new ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
            }
        };
    }

    public static PboxList getInstance() {
        if (null == instance) {
            synchronized (PboxList.class) {
                instance = new PboxList();
            }
        }
        return instance;
    }

    public void getPboxList(){
        String url = C.localIP;
        Log.d(TAG,url);
        String[] splitUrl = url.split("\\.");
        String tmpUrl = splitUrl[0]+"."+splitUrl[1]+"."+splitUrl[2];

        //PboxListRequest pboxListRequest = new PboxListRequest(tmpUrl+"."+i);
        //NetworkManager.getInstance().request(pboxListRequest, onPboxListResponse, onErrorListener);

    }
}
