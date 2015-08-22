package com.ssm.pnas.userSetting;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(web.get(position));
        imageView.setImageResource(imageId);

        return rowView;
    }

}