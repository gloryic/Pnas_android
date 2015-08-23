package com.ssm.pnas.userSetting;

import android.app.Fragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
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

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.ssm.pnas.C;
import com.ssm.pnas.R;
import com.ssm.pnas.tools.file.FileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by glory on 15. 8. 22..
 */

public class SwipeRefresh extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    private static String TAG = "MainActivity";

    private Handler mTimerHandler;
    private TimerTask mTask;
    private Timer mTimer;
    private Context mContext;
    private ShareDialog shareDialog;
    private SwipeMenuListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RelativeLayout mBlurBlock;

    private int isServerToggle;
    private String ipAddr;

    private CustomList mAdapter ;

    static private ArrayList<ListRow> mArFile;
    private String root = "";
    private String path = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.swipe_to_refresh, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBlurBlock = (RelativeLayout) getActivity().findViewById(R.id.blur_block);
        if (C.isServerToggle == 0) {
            mBlurBlock.bringToFront();
        }
        else {
            mBlurBlock.setVisibility(View.GONE);
        }

        if (FileManager.getInstance().isSdCard(getActivity()) == false)
            Toast.makeText(getActivity(), "Error isSdCard", Toast.LENGTH_SHORT).show();
//            finish();

        root = Environment.getExternalStorageDirectory().toString();
        path = root;
        mListView = (SwipeMenuListView) getActivity().findViewById(R.id.activity_main_swipemenulistview);

        mSwipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setEnabled(true);

        initFolder();
        initListView();
        FileManager.getInstance().fileList2Array(FileManager.getInstance().initList,mAdapter,mArFile,root,path);

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0x33,0x66,0x99)));
                // set item width
                openItem.setWidth(dp2px(180));
                // set item title
                openItem.setTitle("Share");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);
            }
        };

        // set creator
        mListView.setMenuCreator(creator);

        // step 2. listener item click event
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // open
                        break;
                    case 1:
                        // delete
//					delete(item);
                        break;
                }
                return false;
            }
        });

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

    private void initListView() {

        mArFile = new ArrayList<ListRow>();
        mAdapter = new CustomList(getActivity(), mArFile);
        mListView=(SwipeMenuListView)getActivity().findViewById(R.id.activity_main_swipemenulistview);

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
                Log.d(TAG, "SwipeStart");
                mSwipeRefreshLayout.setEnabled(false);
            }

            @Override
            public void onSwipeEndWithDx(int position, float dx) {
                // swipe end
                if(position < 0) return;
                Log.d(TAG, "SwipeEnd");
                // show dialog
                if(dx > 400){
                    mListView.closeMenu();
                    shareDialog = new ShareDialog(getActivity(), getActivity(), mArFile.get(position), position);
                    shareDialog.show();
                }else{
                    mListView.smoothCloseMenu();
                }
                mSwipeRefreshLayout.setEnabled(true);
            }

            @Override
            public boolean checkPosition(int position){
                ListRow listRow = mAdapter.getItem(position);
                Log.d(TAG, listRow.fileName);
                if(listRow.fileName.equals("..")) return false;
                else return true;
            }
            @Override
            public boolean checkAbleMove(){
                if(C.isServerToggle == 1) return true;
                else return false;
            }
        });
    }

    private void initFolder(){

        for(int i=0;i<FileManager.getInstance().initList.length;++i)
        {
            String tmp = FileManager.getInstance().initList[i];
            File file = new File(root+"/"+tmp);
            if(!file.isFile())
                file.mkdir();
        }
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
        if (position == 0) {
            return;
        }

        String strItem = mArFile.get(position).fileName;
        String strPath = FileManager.getInstance().getAbsolutePath(strItem, path);
        String[] fileList = FileManager.getInstance().getFileList(strPath);
        if(fileList!=null && fileList.length>=0) path = strPath;
        else
        {
            //is not directory
            int pos = strPath.lastIndexOf(".");
            String extensionName = strPath.substring(pos, strPath.length());
            if(extensionName.equals(".jpg")) {
                getThumbnail(strPath);
            }
        }
        FileManager.getInstance().fileList2Array(fileList, mAdapter,mArFile,root,strPath);
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

    void changeTvtoHash()
    {

    }
}
