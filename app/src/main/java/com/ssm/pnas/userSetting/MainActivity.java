package com.ssm.pnas.userSetting;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.ssm.pnas.C;
import com.ssm.pnas.R;
import com.ssm.pnas.nanohttpd.Httpd;


public class MainActivity extends AppCompatActivity {
    private Context mContext;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private DrawerLayout mDrawerLayout;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private SwitchCompat switchCompat;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;

    private SwipeRefresh mSwipeRefreshFragment;

    private String ipAddr;

    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(Html.fromHtml("<font color='#ffffff'>Pnas</font>"));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mTitle = getTitle();

        setSupportActionBar(mToolbar);


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.setDrawerIndicatorEnabled(true);


        mSwipeRefreshFragment = new SwipeRefresh();
//        Bundle args = new Bundle();
//        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                //.replace(R.id.container, new MyPboxSwipeRefresh())
                .replace(R.id.container, mSwipeRefreshFragment)
                .commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
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
                    // do something, the isChecked will be
                    // true if the switch is in the On position
                    mSwipeRefreshFragment.notifyToAdaptor();

                    if (!isChecked) {
                        C.isServerToggle = 0;
                        C.localIP = null;

                        Httpd.getInstance(mContext).stop();
                        Toast.makeText(mContext, getResources().getString(R.string.stopserver), Toast.LENGTH_SHORT).show();
                    } else {

                        ipAddr = getWifiIpAddress();
                        C.localIP = ipAddr;

                        if (ipAddr != null) {
                            C.isServerToggle = 1;

                            String uri = ipAddr + ":" + C.port;

                            // btn_server_summary.setText(Html.fromHtml(String.format("<a href=\"http://%s\">%s</a> ", uri, uri)));
                            // btn_server_summary.setMovementMethod(LinkMovementMethod.getInstance());

                            Httpd.getInstance(mContext).start();
                            Toast.makeText(mContext, uri, Toast.LENGTH_SHORT).show();
                            Toast.makeText(mContext, getResources().getString(R.string.starserver), Toast.LENGTH_SHORT).show();

                        } else
                            Toast.makeText(mContext, getResources().getString(R.string.setwifi), Toast.LENGTH_SHORT).show();
                    }
                }
            });
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

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        switch (item.getItemId()) {

            case 0:
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new MyPboxSwipeRefresh());
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
