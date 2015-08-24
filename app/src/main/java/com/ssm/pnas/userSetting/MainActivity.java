package com.ssm.pnas.userSetting;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.ssm.pnas.C;
import com.ssm.pnas.R;
import com.ssm.pnas.nanohttpd.Httpd;
import com.ssm.pnas.network.NetworkManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;
    public static Context sContext;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation_layout drawer.
     */
    private DrawerLayout mDrawerLayout;
    private LinearLayout mNavigationDrawer;

    private LinearLayout mBtn0, mBtn1, mBtn2;

    private SwitchCompat switchCompat;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;

    private FragmentManager mFragmentManager;
    private SwipeRefresh mSwipeRefreshFragment;
    private MyPboxSwipeRefresh mMyPboxSwipeRefresh;
    private BackPressCloseHandler backPressCloseHandler;

    private String ipAddr;

    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        sContext = this;

        backPressCloseHandler = new BackPressCloseHandler(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationDrawer = (LinearLayout) findViewById(R.id.navigation_drawer);
        mBtn0 = (LinearLayout) findViewById(R.id.btn_setting0);
        mBtn1 = (LinearLayout) findViewById(R.id.btn_setting1);
        mBtn2 = (LinearLayout) findViewById(R.id.btn_setting2);

        mSwipeRefreshFragment = new SwipeRefresh();
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name);

        setActionbarTitle("Pbox");
        setSupportActionBar(mToolbar);

        mBtn0.setOnClickListener(this);
        mBtn1.setOnClickListener(this);
        mBtn2.setOnClickListener(this);

        mFragmentManager = getFragmentManager();
        mFragmentManager.beginTransaction()
                .replace(R.id.container, mSwipeRefreshFragment)
                .commit();

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.requestLayout();

        // Indicator는 기본적으로 Enable 되어있고, 버튼을 보여주는 설정을 해줘야한다.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NetworkManager.getInstance().initialize(this);
    }

    private void setActionbarTitle(String title) {
        mToolbar.setTitle(Html.fromHtml("<font color='#ffffff'>" + title + "</font>"));
        C.currentFrag = title;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);

        MenuItem item = menu.findItem(R.id.toggle);
        item.setActionView(R.layout.switch_layout);
        switchCompat = (SwitchCompat) item.getActionView().findViewById(R.id.switch_for_actionbar);

        if (switchCompat != null) {
            switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    mSwipeRefreshFragment.notifyToAdaptor();

                    mToolbar.getTitle().equals("Pbox");

                    if (!isChecked) {
                        C.isServerToggle = 0;

                        OnGoingNotification.getInstance(mContext).closeNotification();

                        findViewById(R.id.blind_block).setVisibility(View.VISIBLE);
                        findViewById(R.id.blind_block).bringToFront();

                        C.localIP = null;

                        Httpd.getInstance(mContext).stop();

                        Toast.makeText(mContext, getResources().getString(R.string.stopserver), Toast.LENGTH_SHORT).show();

                    } else {
                        ipAddr = getWifiIpAddress();
                        C.localIP = ipAddr;

                        if (ipAddr != null && !ipAddr.equals("0.0.0.0")) {
                            C.isServerToggle = 1;
                            findViewById(R.id.blind_block).setVisibility(View.GONE);
                            mSwipeRefreshFragment.notifyToAdaptor();

                            OnGoingNotification.getInstance(mContext).openNotification();


                            Httpd.getInstance(mContext).start();
                            Toast.makeText(mContext, getResources().getString(R.string.starserver), Toast.LENGTH_SHORT).show();

                        } else
                            Toast.makeText(mContext, getResources().getString(R.string.setwifi), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            checkOnOff();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if(mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        switch (item.getItemId()) {
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
            switchCompat.setChecked(false);

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
                            switchCompat.setChecked(false);
                            dialog.cancel();
                        }
                    });
            alertDialog.show();
            return false;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.btn_setting0:
                if (!switchCompat.isChecked()) {
                    Toast.makeText(this, "서비스를 활성화 해주세요.", Toast.LENGTH_SHORT).show();
                    mDrawerLayout.closeDrawer(mNavigationDrawer);
                    return;
                }
                else if (C.currentFrag.equals("Pbox")) {
                    mDrawerLayout.closeDrawer(mNavigationDrawer);
                    return;
                }

                mFragmentManager = getFragmentManager();
                mFragmentManager.beginTransaction()
                        .replace(R.id.container, mSwipeRefreshFragment)
                        .commit();
                mDrawerLayout.closeDrawer(mNavigationDrawer);

                setActionbarTitle("Pbox");

                break;
            case R.id.btn_setting1:
                if (!switchCompat.isChecked()) {
                    Toast.makeText(this, "서비스를 활성화 해주세요.", Toast.LENGTH_SHORT).show();
                    mDrawerLayout.closeDrawer(mNavigationDrawer);
                    return;
                }
                else if (C.currentFrag.equals("My box")) {
                    mDrawerLayout.closeDrawer(mNavigationDrawer);
                    return;
                }

                bundle = new Bundle();
                bundle.putString("ip", null);
                mMyPboxSwipeRefresh = new MyPboxSwipeRefresh();
                mMyPboxSwipeRefresh.setArguments(bundle);

                mFragmentManager = getFragmentManager();
                mFragmentManager.beginTransaction()
                        .replace(R.id.container, mMyPboxSwipeRefresh)
                        .commit();
                mDrawerLayout.closeDrawer(mNavigationDrawer);

                setActionbarTitle("My box");

                break;
            case R.id.btn_setting2:
                if (!switchCompat.isChecked()) {
                    Toast.makeText(this, "서비스를 활성화 해주세요.", Toast.LENGTH_SHORT).show();
                    mDrawerLayout.closeDrawer(mNavigationDrawer);
                    return;
                }

                EditText et = (EditText) findViewById(R.id.other_code);
                if (et.getText().length() == 0 || Integer.parseInt(et.getText().toString()) > 255)
                    return;

                bundle = new Bundle();
                bundle.putString("ip", et.getText().toString());
                mMyPboxSwipeRefresh = new MyPboxSwipeRefresh();
                mMyPboxSwipeRefresh.setArguments(bundle);

                mFragmentManager = getFragmentManager();
                mFragmentManager.beginTransaction()
                        .replace(R.id.container, mMyPboxSwipeRefresh)
                        .commit();
                mDrawerLayout.closeDrawer(mNavigationDrawer);

                setActionbarTitle("Other box");

                break;
        }
    }

    public void checkOnOff(){
        if(switchCompat != null && Httpd.getInstance(mContext).isAlive()){
            switchCompat.setChecked(true);
        }
        else{
            switchCompat.setChecked(false);
            C.localIP = null;
        }
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    @Override
    protected void onPause() {
        Log.i("log", "userSetting pause");
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        Log.i("log", "userSetting dest");
        // TODO Auto-generated method stub
        super.onDestroy();
        Httpd.getInstance(mContext).stop();
        OnGoingNotification.getInstance(mContext).closeNotification();
    }
    @Override
    protected void onRestart() {
        Log.i("log", "userSetting rest");
        // TODO Auto-generated method stub
        super.onRestart();
    }
    @Override
    protected void onStart() {
        Log.i("log", "userSetting stt");
        super.onStart();
    }
    @Override
    protected void onStop() {
        Log.i("log", "userSetting stop");
        super.onStop();
    }
    @Override
    protected void onResume() {
        Log.i("log", "userSetting resume");
        super.onResume();
        if(switchCompat!=null)
            checkOnOff();
    }

//    /**
//     * A placeholder fragment containing a simple view.
//     */
//    public static class PlaceholderFragment extends Fragment {
//        /**
//         * The fragment argument representing the section number for this
//         * fragment.
//         */
//        private static final String ARG_SECTION_NUMBER = "section_number";
//
//        /**
//         * Returns a new instance of this fragment for the given section
//         * number.
//         */
//        public static PlaceholderFragment newInstance(int sectionNumber) {
//            PlaceholderFragment fragment = new PlaceholderFragment();
//            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        public PlaceholderFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//            return rootView;
//        }
//
//        @Override
//        public void onAttach(Activity activity) {
//            super.onAttach(activity);
//            ((MainActivity) activity).onSectionAttached(
//                    getArguments().getInt(ARG_SECTION_NUMBER));
//        }
//    }

}
