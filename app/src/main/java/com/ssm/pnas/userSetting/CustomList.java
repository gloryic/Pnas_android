package com.ssm.pnas.userSetting;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ssm.pnas.C;
import com.ssm.pnas.R;

import java.util.ArrayList;

/**
 * Created by kangSI on 2015-08-22.
 */
public class CustomList extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> web;

    enum imgType {dotdot,folder,music,movie,img,pic,doc}
    Integer imgArr[] = new Integer[10];

    void setImgArr()
    {
        imgArr[imgType.dotdot.ordinal()] = R.drawable.android_arrow_back_pnas;
        imgArr[imgType.folder.ordinal()] = R.drawable.ic_folder_black_48dp_pnas;
        imgArr[imgType.music.ordinal()] = R.drawable.ic_folder_black_48dp_pnas;
    }


    int selectImg(String fileName)
    {

        if(fileName.equals("..")){
            return imgArr[imgType.dotdot.ordinal()];
        }

        int pos = fileName.lastIndexOf(".");
        String extensionName = fileName.substring(pos, fileName.length());

        if(extensionName.equals(".mp3")||extensionName.equals(".wma"))
        {
            return imgArr[imgType.music.ordinal()];
        }
        //else if()
        return imgArr[imgType.folder.ordinal()];

        //return extensionName.equals(".mp3")||extensionName.equals(".wma") ? true : false;
    }

    public CustomList(Activity context,ArrayList<String> web) {
        super(context, R.layout.list_single, web);
        setImgArr();
        this.context = context;
        this.web = web;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_list_app, null, true);
            new ViewHolder(convertView);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();

        if(position != 0){
            //holder.iv_icon.setImageResource(imageId);



            holder.tv_name.setText(web.get(position));


        }
        else{
            //holder.iv_icon.setImageResource(imageId);
            holder.tv_name.setText(C.localIP);
        }
        return convertView;
    }

    class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;

        public ViewHolder(View view) {
            iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            view.setTag(this);
        }
    }

}
