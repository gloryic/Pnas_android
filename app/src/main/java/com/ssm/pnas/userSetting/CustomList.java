package com.ssm.pnas.userSetting;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    enum imgType {dotdot,folder,music,movie,img,doc,hwp,pdf,zip,none}
    Integer imgArr[] = new Integer[20];

    void setImgArr()
    {
        imgArr[imgType.dotdot.ordinal()] = R.drawable.android_arrow_back_pnas;
        imgArr[imgType.folder.ordinal()] = R.drawable.ic_folder_black_48dp_pnas;
        imgArr[imgType.music.ordinal()] = R.drawable.mp3_pnas;
        imgArr[imgType.img.ordinal()] = R.drawable.ic_photo_size_select_actual_black_48dp_pnas;
        imgArr[imgType.none.ordinal()] = R.drawable.ic_insert_drive_file_black_48dp_pnas;
        imgArr[imgType.doc.ordinal()] = R.drawable.google_docs_pnas;
        imgArr[imgType.hwp.ordinal()]= R.drawable.unnamed_pnas;
        imgArr[imgType.pdf.ordinal()] = R.drawable.pdf_icon_pnas;
        imgArr[imgType.movie.ordinal()] = R.drawable.film_icon_pnas;
        imgArr[imgType.zip.ordinal()] = R.drawable.zip_silver_pnas;
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
            if(pos==-1) return imgArr[imgType.none.ordinal()];

            String extensionName = fullPath.substring(pos, fullPath.length());

            if(extensionName.equals(".mp3")||extensionName.equals(".wma"))
            {
                return imgArr[imgType.music.ordinal()];
            }
            else if(extensionName.equals(".jpg")||extensionName.equals(".png")){
                return 77;
            }
            else if(extensionName.equals(".pdf")){
                return imgArr[imgType.pdf.ordinal()];
            }
            else if(extensionName.equals(".mp4")||extensionName.equals(".avi")){
                return imgArr[imgType.movie.ordinal()];
            }
            else if(extensionName.equals(".doc")||extensionName.equals(".docx")){
                return imgArr[imgType.doc.ordinal()];
            }
            else if(extensionName.equals(".hwp")){
                return imgArr[imgType.hwp.ordinal()];
            }
            else if(extensionName.equals(".zip")){
                return imgArr[imgType.zip.ordinal()];
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
            int rid = selectImg(listRow.get(position).fileName, listRow.get(position).fileFullPath);
            if(rid==77)
            {
                //holder.iv_icon.setImageResource(imgArr[imgType.img.ordinal()]);
                //holder.iv_root.setBackground(context.getResources().getDrawable(R.color.white));
                Bitmap myBitmap = BitmapFactory.decodeFile(listRow.get(position).fileFullPath);
                holder.iv_icon.setImageBitmap(myBitmap);
            }
            else
            {
                holder.iv_icon.setImageResource(rid);
            }
            holder.tv_name.setText(listRow.get(position).fileName);
            holder.tv_summary.setText(listRow.get(position).fileFullPath);


            if(listRow.get(position).fileName.equals(".."))
            {
                holder.tv_status.setText(context.getResources().getString(R.string.tv_status1));
            }
            else if(FullPathHashMap.getInstance().mss.get(listRow.get(position).fileFullPath)==null)
            {
                holder.tv_status.setText(context.getResources().getString(R.string.tv_status2));
            }
            else
            {
                holder.tv_status.setText(FullPathHashMap.getInstance().mss.get(listRow.get(position).fileFullPath));
            }
        }
        else{
            //holder.iv_root.setBackground(context.getResources().getDrawable(R.color.status_background));
            holder.iv_icon.setImageResource(R.drawable.ic_launcher);
            if(C.localIP != null) {
                holder.tv_name.setText(C.localIP+":"+C.port);
                holder.tv_summary.setText(context.getResources().getString(R.string.ip_is_not_null_m));
                holder.tv_status.setText(context.getResources().getString(R.string.tv_status1));
            }
            else {
                holder.tv_name.setText(context.getResources().getString(R.string.ip_is_null));
                holder.tv_summary.setText(context.getResources().getString(R.string.ip_is_null_m));
                holder.tv_status.setText(context.getResources().getString(R.string.tv_status1));
            }

        }
        return convertView;
    }



    class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_summary;
        TextView tv_status;
        RelativeLayout iv_root;


        public ViewHolder(View view) {
            iv_root = (RelativeLayout) view.findViewById(R.id.iv_root);
            iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_summary = (TextView) view.findViewById(R.id.tv_summary);
            tv_status = (TextView) view.findViewById(R.id.tv_status);

            view.setTag(this);
        }
    }



}
