package com.ssm.pnas.userSetting;

import android.app.AlertDialog;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.ssm.pnas.C;
import com.ssm.pnas.R;
import com.ssm.pnas.network.NetworkManager;
import com.ssm.pnas.network.protocol.FileListRequest;
import com.ssm.pnas.network.protocol.FileListResponse;
import com.ssm.pnas.tools.file.FileManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by kangSI on 2015-08-23.
 */
public class MyPboxSwipeRefresh extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    private static String TAG = "MyPboxSwipeRefresh";

    private Handler mTimerHandler;
    private TimerTask mTask;
    private Timer mTimer;
    private ShareDialog shareDialog;
    private SwipeMenuListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Response.Listener<JSONObject> onFileListResponse;
    private Response.ErrorListener onErrorListener;
    private String prevCode;
    private CustomList mAdapter;
    private String ipAddress;

    private ArrayList<ListRow> mArFile, tempArrayList;
    private RelativeLayout mBlurBlock;
    private Stack<ListRow> fileStack;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.swipe_list_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (FileManager.getInstance().isSdCard(getActivity()) == false)
            Toast.makeText(getActivity(), "Error isSdCard", Toast.LENGTH_SHORT).show();
//            finish();

        mBlurBlock = (RelativeLayout) getActivity().findViewById(R.id.blind_block);
        if (C.isServerToggle == 0) {
            mBlurBlock.bringToFront();
        }
        else {
            mBlurBlock.setVisibility(View.GONE);
        }

        setIPAddress(this.getArguments().getString("ip"));

        mListView = (SwipeMenuListView) getActivity().findViewById(R.id.activity_main_swipemenulistview);

        mSwipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setEnabled(false);

        initListView();

        //Response Listener binding
        onFileListResponse = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(TAG, response.toString());
                    FileListResponse fileListResponse = new FileListResponse(response);
                    FileManager.getInstance().fileList2Array(mArFile, mAdapter, fileListResponse.getFileArrayList());//TODO

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

        initListArray();
        // step 1. create a MenuCreator
//        SwipeMenuCreator creator = new SwipeMenuCreator() {
//            @Override
//            public void create(SwipeMenu menu) {
//                // create "open" item
//                SwipeMenuItem openItem = new SwipeMenuItem(
//                        getActivity().getApplicationContext());
//                // set item background
//                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
//                        0xCE)));
//                // set item width
//                openItem.setWidth(dp2px(90));
//                // set item title
//                openItem.setTitle("Open");
//                // set item title fontsize
//                openItem.setTitleSize(18);
//                // set item title font color
//                openItem.setTitleColor(Color.WHITE);
//                // add to menu
//                menu.addMenuItem(openItem);
//
//                // create "delete" item
//                SwipeMenuItem deleteItem = new SwipeMenuItem(
//                        getActivity().getApplicationContext());
//                // set item background
//                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
//                        0x3F, 0x25)));
//                // set item width
//                deleteItem.setWidth(dp2px(90));
//                // set a icon
//                deleteItem.setIcon(R.drawable.ic_delete);
//                // add to menu
//                menu.addMenuItem(deleteItem);
//            }
//        };
//        // set creator
//        mListView.setMenuCreator(creator);

        // other setting
//		listView.setCloseInterpolator(new BounceInterpolator());

        // test item long click
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                Toast.makeText(getActivity(), position + " long click", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    public void initListArray(){
        FileListRequest fileListRequest = new FileListRequest(ipAddress,fileStack.peek().getCode());
        NetworkManager.getInstance().request(fileListRequest, onFileListResponse, onErrorListener);
    }

    private void initListView() {

        fileStack = new Stack();
        fileStack.add(new ListRow("..","..","0000",false));
        mArFile = new ArrayList<ListRow>();
        mAdapter = new CustomList(getActivity(), mArFile);
        mListView=(SwipeMenuListView)getActivity().findViewById(R.id.activity_main_swipemenulistview);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
            }

            @Override
            public void onSwipeEndWithDx(int position, float dx) {
            }

            @Override
            public boolean checkPosition(int position) {
                ListRow listRow = mAdapter.getItem(position);
                Log.d(TAG, listRow.fileName);
                return false;
                //if (listRow.fileName.equals("..")) return false;
                //else return true;
            }

            @Override
            public boolean checkAbleMove() {
                return false;
            }
        });
    }

    // Refresh Event
    @Override
    public void onRefresh() {
        mTimerHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case 0:
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;
                }
            }
        };

        mTask = new TimerTask() {
            @Override
            public void run() {
                mTimerHandler.sendEmptyMessage(0);
            }
        };

        mTimer = new Timer();

        mTimer.schedule(mTask, 2000);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    public void notifyToAdaptor() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        if (position == 0 && fileStack.size() == 1) return;
        ListRow listRow = mAdapter.getItem(position);

        if (position == 1 && listRow.getFileName().equals("..")){
            fileStack.pop();
            FileListRequest fileListRequest = new FileListRequest(ipAddress,fileStack.peek().getCode());
            NetworkManager.getInstance().request(fileListRequest, onFileListResponse, onErrorListener);
        }
        else if(listRow.isDir()){
            fileStack.add(listRow);
            FileListRequest fileListRequest = new FileListRequest(ipAddress,listRow.code);
            NetworkManager.getInstance().request(fileListRequest, onFileListResponse, onErrorListener);
        }
        else{
            //다이얼로그
            shareDialog = new ShareDialog(getActivity(), getActivity(), mArFile.get(position), position, C.currentFrag);
            shareDialog.show();
        }
    }

    public void setIPAddress(String num){
        if(num!=null){
            String[] ipArr = C.localIP.split("\\.");
            ipAddress =  ipArr[0]+"."+ipArr[1]+"."+ipArr[2]+"."+num;
            C.remoteIP = ipAddress;
        }
        else
            ipAddress = C.localIP;
    }

    private Bitmap getThumbnail(String path){

        Bitmap thumbBitmap = null;

        thumbBitmap = BitmapFactory.decodeFile(path);

        //Create a Dialog to display the thumbnail
        AlertDialog.Builder thumbDialog = new AlertDialog.Builder(getActivity());
        ImageView thumbView = new ImageView(getActivity());
        thumbView.setImageBitmap(thumbBitmap);
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(thumbView);
        thumbDialog.setView(layout);
        thumbDialog.show();


        return thumbBitmap;
    }


}
