package com.ssm.pnas.userSetting;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.ssm.pnas.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by glory on 15. 8. 22..
 */

public class SwipeRefresh extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    private static String TAG = "MainActivity";

    private Handler mTimerHandler;
    private TimerTask mTask;
    private Timer mTimer;

    //private ArrayAdapter mAdapter;
    private CustomList mAdapter;
    private SwipeMenuListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    String musicRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getName();
    String movieRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getName();
    String downLoadRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getName();
    String dcimLoadRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getName();

    String[] initList = {musicRoot,movieRoot,downLoadRoot,dcimLoadRoot};


    String root = "";
    String path = "";

//    ListView mListFile;
    ArrayList<String> mArFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipe_to_refresh);

        if (isSdCard() == false)
            finish();

        //파일 경로 text view
        //mTextMsg = (TextView) findViewById(R.id.textMessage);
        //mTextMsg.setText(mRoot);

        mListView = (SwipeMenuListView) findViewById(R.id.activity_main_swipemenulistview);

        root = Environment.getExternalStorageDirectory().toString();
        path = root;

        initFolder();
        initListView();
        fileList2Array(initList);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
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
                        getApplicationContext());
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
                Toast.makeText(getApplicationContext(), position + " long click", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }


    private void delete(ApplicationInfo item) {
        // delete app
        try {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(Uri.fromParts("package", item.packageName, null));
            startActivity(intent);
        } catch (Exception e) {
        }
    }

    private void open(ApplicationInfo item) {
        // open app
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(item.packageName);
        List<ResolveInfo> resolveInfoList = getPackageManager()
                .queryIntentActivities(resolveIntent, 0);
        if (resolveInfoList != null && resolveInfoList.size() > 0) {
            ResolveInfo resolveInfo = resolveInfoList.get(0);
            String activityPackageName = resolveInfo.activityInfo.packageName;
            String className = resolveInfo.activityInfo.name;

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName componentName = new ComponentName(
                    activityPackageName, className);

            intent.setComponent(componentName);
            startActivity(intent);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_good:
                Toast.makeText(this, "Good Button is clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_share:
                Toast.makeText(this, "Share Button is clicked", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void initFolder(){

        for(int i=0;i<initList.length;++i)
        {
            String tmp = initList[i];
            File file = new File(root+"/"+tmp);
            if(!file.isFile())
                file.mkdir();
        }


    }

    public void initListView() {
        mArFile = new ArrayList<String>();
        mAdapter = new CustomList(SwipeRefresh.this, mArFile, R.drawable.next);
        mListView=(SwipeMenuListView)findViewById(R.id.activity_main_swipemenulistview);
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

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        String strItem = mArFile.get(position);
        String strPath = getAbsolutePath(strItem);

        String[] fileList = getFileList(strPath);
        fileList2Array(fileList);
    }

    public String getAbsolutePath(String strFolder) {
        String strPath;
        if (strFolder == "..") {
            int pos = path.lastIndexOf("/");
            strPath = path.substring(0, pos);
        } else
            strPath = path + "/" + strFolder;
        return strPath;
    }

    public boolean isSdCard() {
        String ext = Environment.getExternalStorageState();
        if (ext.equals(Environment.MEDIA_MOUNTED) == false) {
            Toast.makeText(this, "SD Card does not exist", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }

    public String[] getFileList(String strPath) {
        File fileRoot = new File(strPath);

        if (fileRoot.isDirectory() == false)
        {
            /**
             *
             *
             * 실행부분
             */
            if(isMusicFile(fileRoot))
            {
                Intent intent = new Intent();
                intent.putExtra("MusicFilePath", fileRoot.toString());


                setResult(Activity.RESULT_OK,intent);
                finish();
            }
            else return null;

        }
        path = strPath;
        //mTextMsg.setText(mPath);
        String[] fileList = fileRoot.list();
        return fileList;
    }

    public boolean isMusicFile(File fileRoot)
    {
        String str = fileRoot.toString();
        int pos = str.lastIndexOf(".");
        String extensionName = str.substring(pos, str.length());

        return extensionName.equals(".mp3")||extensionName.equals(".wma") ? true : false;
    }


    public void fileList2Array(String[] fileList) {
        if (fileList == null)
            return;
        mArFile.clear();

        if (root.equals(path)) {
            for (int i = 0; i < initList.length; i++) {
                mArFile.add(initList[i]);
            }
        } else {
            if (root.length() < path.length())
                mArFile.add("..");

            for (int i = 0; i < fileList.length; i++) {
                mArFile.add(fileList[i]);
            }
        }

        mAdapter.notifyDataSetChanged();
    }

    // For Timer...
    @Override
    protected void onDestroy() {
        if (mTimer != null)
            mTimer.cancel();
        super.onDestroy();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.action_left) {
//            mListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
//            return true;
//        }
//        if (id == R.id.action_right) {
//            mListView.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
