package com.ssm.pnas.userSetting;

import android.app.Fragment;
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
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
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


    private SwipeMenuListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private int isServerToggle;
    private String ipAddr;

    private CustomList mAdapter ;
    private ArrayList<String> mArFile;
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

        if (FileManager.getInstance().isSdCard(getActivity()) == false)
            Toast.makeText(getActivity(), "Error isSdCard", Toast.LENGTH_SHORT).show();
//            finish();

        root = Environment.getExternalStorageDirectory().toString();
        path = root;
        mListView = (SwipeMenuListView) getActivity().findViewById(R.id.activity_main_swipemenulistview);

        mSwipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

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
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("Open");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
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

        mArFile = new ArrayList<String>();
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
            public void onSwipeEnd(int position) {
                // swipe end
                Log.d(TAG, "SwipeEnd");
                mSwipeRefreshLayout.setEnabled(true);
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
        String strItem = mArFile.get(position);
        String strPath = FileManager.getInstance().getAbsolutePath(strItem, path);
        String[] fileList = FileManager.getInstance().getFileList(strPath);
        if(fileList!=null && fileList.length>=0) path = strPath;
        FileManager.getInstance().fileList2Array(fileList, mAdapter,mArFile,root,strPath);
    }
}
