package com.ssm.pnas.tools.file;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.ssm.pnas.R;
import com.ssm.pnas.userSetting.CustomList;
import com.ssm.pnas.userSetting.ListRow;
import com.ssm.pnas.userSetting.SwipeRefresh;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by kangSI on 2015-08-22.
 */
public class FileManager {

    private String musicRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getName();
    private String movieRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getName();
    private String downLoadRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getName();
    private String dcimLoadRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getName();
    private String kakaoTalkDown = "KakaoTalkDownload";
    private String pictureRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getName();
    public String[] initList = {musicRoot, movieRoot, downLoadRoot, dcimLoadRoot,pictureRoot,kakaoTalkDown};

    private FileManager()
    {
    }

    private volatile static FileManager instance = null;

    public static FileManager getInstance(){
        synchronized (FileManager.class){
            if(instance == null){
                instance =  new FileManager();
            }
        }
        return instance;
    }


    public String getAbsolutePath(String strFolder,String path) {
        String strPath;
        if (strFolder.equals("..")) {
            int pos = path.lastIndexOf("/");
            strPath = path.substring(0, pos);
        } else
            strPath = path + "/" + strFolder;
        return strPath;
    }

    public boolean isSdCard(Context context) {
        String ext = Environment.getExternalStorageState();
        if (ext.equals(Environment.MEDIA_MOUNTED) == false) {
            Toast.makeText(context, "SD Card does not exist", Toast.LENGTH_SHORT)
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
             * 실행부분
             */
            /*if(isMusicFile(fileRoot))
            {
                Intent intent = new Intent();
                intent.putExtra("MusicFilePath", fileRoot.toString());


                setResult(Activity.RESULT_OK,intent);
                finish();
            }
            else*/

            //Toast toast = Toast.makeText(this,"메롱", Toast.LENGTH_SHORT);
            //toast.show();

            return null;

        }

        //path = strPath;
        //mTextMsg.setText(mPath);
        String[] fileList = fileRoot.list();
        return fileList;
    }



    public void  fileList2Array(String[] fileList, CustomList mAdapter, ArrayList<ListRow> mArFile,
                                 String root, String path) {
        if (fileList == null)
            return;

        mArFile.clear();
        mArFile.add(new ListRow("","","",true));


        if (root.equals(path)) {
            for (int i = 0; i < initList.length; i++) {
                mArFile.add(new ListRow(initList[i],root+"/"+initList[i],"",false));
            }
        } else {
            if (root.length() < path.length()) {
                mArFile.add(new ListRow("..",path,"",false));
            }

            for (int i = 0; i < fileList.length; i++) {
                mArFile.add(new ListRow(fileList[i],path+"/"+fileList[i],"",false));
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    public void fileList2Array(ArrayList<ListRow> adapterList, CustomList mAdapter, ArrayList<ListRow> mArFile) {

        if (mArFile == null)
            return;

        adapterList.clear();
        adapterList.add(new ListRow("","","",false));

        for (int i = 0; i < mArFile.size(); i++) {
            adapterList.add(mArFile.get(i));
        }
        mAdapter.notifyDataSetChanged();
    }
}
