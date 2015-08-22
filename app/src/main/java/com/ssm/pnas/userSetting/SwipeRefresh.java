package com.ssm.pnas.userSetting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ssm.pnas.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by glory on 15. 8. 22..
 */

public class SwipeRefresh extends Activity implements AdapterView.OnItemClickListener{

    SwipeRefreshLayout mSwipeRefreshLayout;
    ArrayAdapter mAdapter;

    String musicRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getName();
    String movieRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getName();
    String downLoadRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getName();
    String dcimLoadRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getName();

    String[] initList = {musicRoot,movieRoot,downLoadRoot,dcimLoadRoot};


    String root = "";
    String path = "";

    TextView mTextMsg;
    ListView mListFile;
    ArrayList<String> mArFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipe_to_refresh);
        SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);

        if (isSdCard() == false)
            finish();

        //파일 경로 text view
        //mTextMsg = (TextView) findViewById(R.id.textMessage);
        //mTextMsg.setText(mRoot);

        root = Environment.getExternalStorageDirectory().toString();
        path = root;
        initFolder();

        initListView();
        fileList2Array(initList);

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
        CustomList adapter = new CustomList(SwipeRefresh.this, mArFile, R.drawable.next);
        mListFile=(ListView)findViewById(R.id.activity_main_listview);
        mListFile.setAdapter(adapter);
        mListFile.setOnItemClickListener((AdapterView.OnItemClickListener) this);

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
        String extensionName = str.substring(pos,str.length());

        return extensionName.equals(".mp3")||extensionName.equals(".wma") ? true : false;
    }


    public void fileList2Array(String[] fileList) {
        if (fileList == null)
            return;
        mArFile.clear();

        if(root.equals(path))
        {
            for (int i = 0; i < initList.length; i++) {
                mArFile.add(initList[i]);
            }
        }
        else {
            if (root.length() < path.length())
                mArFile.add("..");

            for (int i = 0; i < fileList.length; i++) {
                mArFile.add(fileList[i]);
            }
        }

        ArrayAdapter<?> adapter = (ArrayAdapter<?>) mListFile.getAdapter();
        adapter.notifyDataSetChanged();
    }
}
