package com.ssm.pnas.userSetting;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;
import com.ssm.pnas.R;

/**
 * Created by glory on 15. 8. 22..
 */

public class SwipeRefresh extends Activity {

    ListView mListView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ArrayAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipe_to_refresh);
        SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mListView = (ListView) findViewById(R.id.activity_main_listview);

        String[] fakeTweets = {"A","B"};
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.simple_list_item_1, fakeTweets);

        mListView.setAdapter(adapter);

    }
}
