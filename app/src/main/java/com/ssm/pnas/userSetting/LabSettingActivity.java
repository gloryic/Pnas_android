package com.ssm.pnas.userSetting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.format.Formatter;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ssm.pnas.C;
import com.ssm.pnas.R;
import com.ssm.pnas.nanohttpd.HashIndex;
import com.ssm.pnas.nanohttpd.Httpd;

public class LabSettingActivity extends Activity implements OnClickListener {

	private static Context context;
	private String TAG = getClass().getSimpleName();
	private ImageView  btn_server_toggle;
	private TextView btn_server_summary;
	private int isServerToggle;
	// music textView
	private SharedPreferences pref, default_pref;
	private String ipAddr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lab_setting_layout);
		context = this;
		
		default_pref = PreferenceManager.getDefaultSharedPreferences(this);
		pref = getSharedPreferences("NoticallService", Context.MODE_PRIVATE);

		btn_server_toggle = (ImageView)findViewById(R.id.btn_server_toggle);
		btn_server_summary = (TextView)findViewById(R.id.btn_server_summary);
		
		btn_server_toggle.setOnClickListener(this);
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
					dialog.cancel();
				}
			});
			alertDialog.show();
			return false;
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

			case R.id.btn_server_toggle:
				//Server isServerToggle
				if (isServerToggle == 1) {
					btn_server_toggle.setBackgroundResource(R.drawable.toggle_off);
					isServerToggle = 0;
					C.localIP = null;

					Httpd.getInstance(context).stop();
					btn_server_summary.setText(getResources().getString(R.string.server_summary));
					Toast.makeText(this, getResources().getString(R.string.stopserver), Toast.LENGTH_SHORT).show();
				} else if (isServerToggle == 0) {

					ipAddr = getWifiIpAddress();
					C.localIP = ipAddr;

					if (ipAddr != null) {

						btn_server_toggle.setBackgroundResource(R.drawable.toggle_on);
						isServerToggle = 1;

						String uri = ipAddr + ":" + C.port + "/views/Dashboard.html";
						btn_server_summary.setText(Html.fromHtml(String.format("<a href=\"http://%s\">%s</a> ", uri, uri)));
						btn_server_summary.setMovementMethod(LinkMovementMethod.getInstance());

						Httpd.getInstance(context).start();
						Toast.makeText(this, getResources().getString(R.string.starserver), Toast.LENGTH_SHORT).show();


					} else
						Toast.makeText(this, getResources().getString(R.string.setwifi), Toast.LENGTH_SHORT).show();
				}
				break;
		}
	}
    
	private void showToast(String msg){
	    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	
	public static Context getAppContext() {
		return context;
	}
	
	@Override
	protected void onPause() {
		Log.i("log", "userSetting pause");
		super.onPause();
	}
	@Override
	protected void onDestroy() {
		Log.i("log", "userSetting dest");
		super.onDestroy();
	}
	@Override
	protected void onRestart() {
		Log.i("log", "userSetting rest");
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

		//TODO test
		String code = HashIndex.getInstance().generateCode(Environment.getExternalStorageState()+"/Music");
		Toast.makeText(this, "code : "+code, Toast.LENGTH_SHORT).show();

		super.onResume();
	}	
}