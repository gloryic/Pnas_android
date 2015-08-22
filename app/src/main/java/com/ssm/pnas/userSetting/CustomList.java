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
    private final Integer imageId;

    public CustomList(Activity context,ArrayList<String> web, Integer imageId) {
        super(context, R.layout.list_single, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;

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
            holder.iv_icon.setImageResource(imageId);
            holder.tv_name.setText(web.get(position));
        }
        else{
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

        public ViewHolder(View view) {
            iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_summary = (TextView) view.findViewById(R.id.tv_summary);
            view.setTag(this);
        }
    }

}
