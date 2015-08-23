package com.ssm.pnas;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import com.ssm.pnas.tools.device.Devices;
import com.ssm.pnas.userSetting.ListRow;

import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.location.Location;

public class C {

	static public boolean networkLogging = true;
	static public int port = 8206;
	static public String baseURL = "http://210.118.74.97:3000";
	static public String localIP;

	//static public String baseURL = "http://210.118.74.107:5050";

	/*Using SQLite by Constructor*/
	
	//static public Context mContext;
	
	final static public String  DbName = "noticallDB";
	static public CursorFactory cursorFactgory = null;
	static public int DbVersion = 1;
	
	//static public int catchCall=0;
	static public boolean isCatchCall = false;

	static public Location lastLoc = null;
	
	//전화 상태가 offhook에 들어갔나?
	static public boolean isInOffhook = false;
	//전화 상태가 offhook에 들어가지 않았나?
	static public boolean isNotInOffhook = false;
	//전화가 종료 되었나?
	static public boolean isOffCall = false;
	
	static public int headLineCount = 2;
	static public String plusTime = "+9";
	
	//기쁨수정
	static public int call_state=0;
	static public long offhookTime=0;
	//static public String phoneNum="";

	// newsfd 관련
	static public int newsFd=1;
	static public boolean exInNews=false;
	static public final Semaphore available = new Semaphore(1, true);

	// 사용자TTS 관련
	static public int userTTSOffset=0;
	static public boolean exInUserTTS=false;
	static public final Semaphore UserTTSSemaphore = new Semaphore(1, true);

	// music 관련
	static public boolean exInMusic=false;
	static public final Semaphore MusicSemaphore = new Semaphore(1, true);
	
	//스케줄러 관련
	static public boolean hasNoContents = false; 
	static public boolean nextButtonOn = true;
	
	static public double defaultLat = 37.558040;
	static public double defaultLng = 126.988364;

	static final public String province = "한국 서울특별시 중구 예장동";
	
	static public boolean firstStartContents=false;
	
	//광고 관련
	static public int AdvOffset=0;
	static public boolean exInAdv=false;
	static public boolean usedAdv=false;
	static public boolean usedAdvComplete = false;
	static public final Semaphore AdvSemaphore = new Semaphore(1, true);
	
	static public boolean logTestStart = false;
	
	static public String DeviceName = new Devices().getDeviceName();

	static public ArrayList<ListRow> myPboxList = new ArrayList();
}
