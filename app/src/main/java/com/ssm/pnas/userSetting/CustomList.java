package com.ssm.pnas.userSetting;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ssm.pnas.C;
import com.ssm.pnas.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by kangSI on 2015-08-22.
 */
public class CustomList extends ArrayAdapter<ListRow> {

    private final Activity context;
    private final ArrayList<ListRow> listRow;

    enum imgType {dotdot,folder,music,movie,img,doc,hwp,none}
    Integer imgArr[] = new Integer[10];

    void setImgArr()
    {
        imgArr[imgType.dotdot.ordinal()] = R.drawable.android_arrow_back_pnas;
        imgArr[imgType.folder.ordinal()] = R.drawable.ic_folder_black_48dp_pnas;
        imgArr[imgType.music.ordinal()] = R.drawable.mp3_pnas;
        imgArr[imgType.none.ordinal()] = R.drawable.ic_insert_drive_file_black_48dp_pnas;
        imgArr[imgType.doc.ordinal()] = R.drawable.google_docs_pnas;
        imgArr[imgType.hwp.ordinal()]= R.drawable.unnamed_pnas;
    }


    int selectImg(String fileName,String fullPath)
    {

        File file = new File(fullPath);
        if(fileName.equals(".."))
        {
            return imgArr[imgType.dotdot.ordinal()];
        }
        else if(file.isDirectory()){
            return imgArr[imgType.folder.ordinal()];
        }
        else {
            int pos = fullPath.lastIndexOf(".");
            String extensionName = fullPath.substring(pos, fullPath.length());

            if(extensionName.equals(".mp3")||extensionName.equals(".wma"))
            {
                return imgArr[imgType.music.ordinal()];
            }
            else if(extensionName.equals(".doc")||extensionName.equals(".docx")){
                return imgArr[imgType.doc.ordinal()];
            }else if(extensionName.equals(".hwp")){
                return imgArr[imgType.hwp.ordinal()];
            }
            else
                return imgArr[imgType.none.ordinal()];
        }

    }

    public CustomList(Activity context,ArrayList<ListRow> lr) {
        super(context, R.layout.list_single, lr);
        setImgArr();
        this.context = context;
        this.listRow = lr;
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
            holder.iv_icon.setImageResource(selectImg(listRow.get(position).fileName,listRow.get(position).fileFullPath));
            holder.tv_name.setText(listRow.get(position).fileName);
            holder.tv_summary.setText(listRow.get(position).fileFullPath);
        }
        else{
            holder.iv_root.setBackground(context.getResources().getDrawable(R.color.status_background));
            holder.iv_icon.setImageResource(R.drawable.ic_launcher);
            if(C.localIP != null) holder.tv_name.setText(C.localIP);
            else holder.tv_name.setText(context.getResources().getString(R.string.ip_is_null));
            holder.tv_summary.setVisibility(View.GONE);

        }
        return convertView;
    }

    class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_summary;
        RelativeLayout iv_root;

        public ViewHolder(View view) {
            iv_root = (RelativeLayout) view.findViewById(R.id.iv_root);
            iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_summary = (TextView) view.findViewById(R.id.tv_summary);
            view.setTag(this);
        }
    }

}
