package com.ssm.pnas.userSetting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.Formatter;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.ssm.pnas.C;
import com.ssm.pnas.R;
import com.ssm.pnas.nanohttpd.HashIndex;
import com.ssm.pnas.nanohttpd.Httpd;
import com.ssm.pnas.tools.file.FileManager;

import java.io.
        File;
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
    private Context mContext;
    private ShareDialog shareDialog;
    private SwipeMenuListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private int isServerToggle;
    private String ipAddr;
    private CustomList mAdapter ;

    private ArrayList<String> mArFile, mArFullPath;
    private String root = "";
    private String path = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipe_to_refresh);
        mContext = this;

        if (FileManager.getInstance().isSdCard(this) == false)
            finish();

        root = Environment.getExternalStorageDirectory().toString();
        path = root;
        mListView = (SwipeMenuListView) findViewById(R.id.activity_main_swipemenulistview);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        initFolder();
        initListView();
        FileManager.getInstance().fileList2Array(FileManager.getInstance().initList,mAdapter,mArFile,mArFullPath,root,path);

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
    private void initListView() {

        mArFile = new ArrayList<String>();
        mArFullPath = new ArrayList<String>();
        mAdapter = new CustomList(SwipeRefresh.this, mArFile,mArFullPath);
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
            public void onSwipeEndWithDx(int position, float dx) {
                // swipe end
                Log.d(TAG, "SwipeEnd");

                // show dialog
                if(dx > 500){
                    shareDialog = new ShareDialog(mContext, (SwipeRefresh)mContext);
                    shareDialog.show();
                }

                mListView.closeMenu();
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

            case R.id.toggle:
                //Server isServerToggle
                mAdapter.notifyDataSetChanged();

                if (isServerToggle == 1) {
                    item.setIcon(R.drawable.toggle_off);
                    isServerToggle = 0;
                    C.localIP = null;

                    Httpd.getInstance(this).stop();
                    Toast.makeText(this, getResources().getString(R.string.stopserver), Toast.LENGTH_SHORT).show();
                } else if (isServerToggle == 0) {

                    ipAddr = getWifiIpAddress();
                    C.localIP = ipAddr;

                    if (ipAddr != null) {
                        item.setIcon(R.drawable.toggle_on);
                        isServerToggle = 1;

                        String uri = ipAddr + ":" + C.port;

                        // btn_server_summary.setText(Html.fromHtml(String.format("<a href=\"http://%s\">%s</a> ", uri, uri)));
                        // btn_server_summary.setMovementMethod(LinkMovementMethod.getInstance());

                        Httpd.getInstance(this).start();
                        Toast.makeText(this, uri, Toast.LENGTH_SHORT).show();
                        Toast.makeText(this, getResources().getString(R.string.starserver), Toast.LENGTH_SHORT).show();

                    } else
                        Toast.makeText(this, getResources().getString(R.string.setwifi), Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String getWifiIpAddress() {
        if(chkWifi()){
            WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
            @SuppressWarnings("deprecation")
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            return ip;
        }
        else
            return null;
    }

    public boolean chkWifi(){

        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled()){
            return true;
        }
        else{
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Confirm");
            alertDialog.setMessage(getResources().getString(R.string.donotsetwifi));
            alertDialog.setPositiveButton("yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                        }
                    });
            alertDialog.setNegativeButton("no",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alertDialog.show();
            return false;
        }
    }


    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        if (position == 0) {
            return;
        }
        String strItem = mArFile.get(position);
        String strPath = FileManager.getInstance().getAbsolutePath(strItem,path);
        String[] fileList = FileManager.getInstance().getFileList(strPath);
        if(fileList!=null && fileList.length>=0) path = strPath;
        FileManager.getInstance().fileList2Array(fileList, mAdapter,mArFile,mArFullPath,root,strPath);
    }


    // For Timer...
    @Override
    protected void onDestroy() {
        if (mTimer != null)
            mTimer.cancel();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Log.i("log", "userSetting resume");
        super.onResume();
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
