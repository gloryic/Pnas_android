package com.ssm.pnas;


import com.ssm.pnas.nanohttpd.Httpd;
import com.ssm.pnas.network.NetworkManager;
import com.ssm.pnas.tools.device.Devices;
import com.ssm.pnas.tools.pbox.PboxList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Dbg extends Activity implements OnClickListener {
	
	private Button start, stop, serverStart, serverStop, viewGraph, 
					xmlTest, selectMusic, dspbtn, tts, Calendar, eHelper, setting, callLog, SchedulerEx, weather, conStop , next, showText;

	private static Context _context;
	private Httpd httpd;
	private String TAG = getClass().getSimpleName();
	//dbgWindow
	private static TextView dbgWindow;
	
	public static TextView tv;
	public static String musicPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ssm.pnas.R.layout.dbg_layout);

		// TODO: Context 부분 다시 논하기
        _context = this;
        //_context = this.getApplicationContext();
        //_context = C.mContext;
        
		// TODO: [상봉] Next Button Caller
		// Intent intent = new Intent(Dbg.this, NextButtonService.class);
		// startService(intent);
        
        //******************************
        
        start = (Button)findViewById(com.ssm.pnas.R.id.button1);
        stop = (Button)findViewById(com.ssm.pnas.R.id.button2);
        viewGraph = (Button)findViewById(com.ssm.pnas.R.id.button3);
        serverStart = (Button)findViewById(com.ssm.pnas.R.id.button4);
        serverStop = (Button)findViewById(com.ssm.pnas.R.id.button5);
        selectMusic = (Button)findViewById(com.ssm.pnas.R.id.button6);
        xmlTest = (Button)findViewById(com.ssm.pnas.R.id.button7);
        dspbtn = (Button)findViewById(com.ssm.pnas.R.id.button8);
        tts = (Button)findViewById(com.ssm.pnas.R.id.button9);
        
        Calendar = (Button)findViewById(com.ssm.pnas.R.id.button10);
        eHelper = (Button)findViewById(com.ssm.pnas.R.id.button11);
        callLog = (Button)findViewById(com.ssm.pnas.R.id.button12);
        setting = (Button)findViewById(com.ssm.pnas.R.id.button13);
        tv = (TextView) findViewById(com.ssm.pnas.R.id.selectMusic);
        
        SchedulerEx = (Button)findViewById(com.ssm.pnas.R.id.button14);
        weather = (Button)findViewById(com.ssm.pnas.R.id.button15);
        conStop = (Button)findViewById(com.ssm.pnas.R.id.button16);
        next = (Button)findViewById(com.ssm.pnas.R.id.button17);
        showText = (Button)findViewById(com.ssm.pnas.R.id.button18);
        
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        serverStart.setOnClickListener(this);
        serverStop.setOnClickListener(this);
        viewGraph.setOnClickListener(this);
        dspbtn.setOnClickListener(this);
        xmlTest.setOnClickListener(this);
        selectMusic.setOnClickListener(this);
        tts.setOnClickListener(this);
        Calendar.setOnClickListener(this);
        eHelper.setOnClickListener(this);
        callLog.setOnClickListener(this);
        setting.setOnClickListener(this);
        SchedulerEx.setOnClickListener(this);
        weather.setOnClickListener(this);
        conStop.setOnClickListener(this);
        next.setOnClickListener(this);
        showText.setOnClickListener(this);
        
        httpd = Httpd.getInstance(this);
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
            alertDialog.setTitle("Confirm...");
            alertDialog.setMessage("Do you want to go to wifi settings?");
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
			switch(v.getId()){
			case com.ssm.pnas.R.id.button1:
				Log.i("Noti", "Start Recording");

				NetworkManager.getInstance().initialize(_context);
				PboxList.getInstance().getPboxList();
				break;
			case com.ssm.pnas.R.id.button2:
				Log.i("Noti", "Start Recording");
				break;
			case com.ssm.pnas.R.id.button3:
				break;
			case com.ssm.pnas.R.id.button4:
				Log.i("Noti", "Server Start");
				if(getWifiIpAddress()!=null){
					Log(getWifiIpAddress()+":"+C.port);
					C.localIP = getWifiIpAddress();
					httpd.start();
				}
				break;
			case com.ssm.pnas.R.id.button5:
				httpd.stop();
				break;
			case com.ssm.pnas.R.id.button6:
				Log.i("Noti", "PlayMusic");
				break;
			case com.ssm.pnas.R.id.button7:
				Log.i("Noti", "News Parsing");
				break;
			case com.ssm.pnas.R.id.button8:
				Dbg.Log("DSP Start");
				break;
			case com.ssm.pnas.R.id.button9:
				Dbg.Log("TTS Start");
				break;
			case com.ssm.pnas.R.id.button10:
				break;
			case com.ssm.pnas.R.id.button11:
				break;
			case com.ssm.pnas.R.id.button12:
				TOAST(new Devices().checkPermission(this)+"");
				TOAST(C.DeviceName);
				break;
			case com.ssm.pnas.R.id.button13:
				Dbg.Log("Scheduler Setting Activity Start");
				break;
			case com.ssm.pnas.R.id.button14 :
				Dbg.Log("scheudler execute");
				break;
			case com.ssm.pnas.R.id.button15:
				Dbg.Log("Weather Start");
				break;
			case com.ssm.pnas.R.id.button16:
				Dbg.Log("contentsStop");
				break;
			case com.ssm.pnas.R.id.button17:
				break;
			case com.ssm.pnas.R.id.button18:
				break;
			}			
	}
	
	public static void TOAST(final String text){
		if(Dbg.getAppContext()!=null){
			Log.i("noticall",text);
			((Activity) _context).runOnUiThread(new Runnable() {
				 
	            @Override
	            public void run() {
	                try {
	                	Toast.makeText(_context, text, Toast.LENGTH_LONG).show();
	                } catch (Exception e) {
	                    Log.d("Activity_", "Fail >> " + e.toString());
	                }
	            }
	        });
		}
	}
	
	public static void Log(final String text){
		if(Dbg.getAppContext()!=null){
			Log.i("noticall",text);
			((Activity) _context).runOnUiThread(new Runnable() {
				 
	            @Override
	            public void run() {
	                try {
	                    dbgWindow = (TextView)((Activity) Dbg.getAppContext()).findViewById(com.ssm.pnas.R.id.dbgWindow);
	                    dbgWindow.append(text+"\n");
	                } catch (Exception e) {
	                    Log.d("Activity_", "Fail >> " + e.toString());
	                }
	            }
	        });
		}
	}
	
	private static Context getAppContext() {
		return _context;
	}
    @Override
	protected void onPause() {
		Log.i("log", "testacti pause");
		super.onPause();
	}
	@Override
	protected void onDestroy() {

		Log.i("log", "testacti dest");
		super.onDestroy();
	}
	@Override
	protected void onRestart() {

		Log.i("log", "testacti rest");
		super.onRestart();
	}
	@Override
	protected void onStart() {
		Log.i("log", "testacti stt");		
		super.onStart();
	}
	@Override
	protected void onStop() {
		Log.i("log", "testacti stop");
		super.onStop();
	}
	@Override
	protected void onResume() {

		Log.i("log", "testacti resume");
		super.onResume();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
}
