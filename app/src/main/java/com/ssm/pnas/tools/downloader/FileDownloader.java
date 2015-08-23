package com.ssm.pnas.tools.downloader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.ssm.noticall.C;

public class FileDownloader {
	private static AdFileDownloader instance;
	private SharedPreferences pref;
	private static AdFDState state;
	private Context mContext;

	public enum AdFDState {
		INIT, READY, ONGOING, END
	};

	String baseURL = C.baseURL; // URL
	String Save_Path;
	String Save_folder = "/Noticall-Data";

	DownloadListThread dlThread;
	DownloadQueueThread dqThread;

	private FileDownloader(Context context) {
		this.mContext = context;
		
		state = AdFDState.INIT;
		
		//Response Listener binding
        pref = mContext.getSharedPreferences("AdFileDownloader", Context.MODE_PRIVATE);

        Save_Path = pref.getString("save_path", "");

        if (Save_Path.equals("")) {
        	String ext = Environment.getExternalStorageState();

        	if (ext.equals(Environment.MEDIA_MOUNTED)) {
    			Save_Path = Environment.getExternalStorageDirectory()
    					.getAbsolutePath() + Save_folder;
    		}

    		File dir = new File(Save_Path);

			if (!dir.exists()) {
				dir.mkdir();
			}

			Save_Path += "/";
			pref.edit().putString("save_path", Save_Path).commit();
        }

        state = AdFDState.READY;
	}

	public static AdFileDownloader getInstance(Context context) {
		if (null == instance) {
			synchronized (AdFileDownloader.class) {
				instance = new AdFileDownloader(context);
			}
		}

		if (state == AdFDState.END) {
			state = AdFDState.READY;
		}
		
		instance.mContext = context;
		return instance;
	}

	private boolean checkNetwork() {
		ConnectivityManager connMgr = (ConnectivityManager)
				mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	public void startDownload(int loc) {
		if (checkNetwork()) {
			if (state == AdFDState.READY || state == AdFDState.END) {
				dlThread = new DownloadListThread(baseURL,	Save_Path, loc);
				dlThread.start();
				state = AdFDState.ONGOING;
			}
			else {
				Toast.makeText(mContext, "잠시후에 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private void resetDB() {
		String path = pref.getString("save_path", "");

		if (path.equals("")) {
			state = AdFDState.END;
			return;
		}

		final File folder = new File(path);
		deleteFilesForFolder(folder);

		AdFileDAO.getInstance(mContext).resetTable();
	}

	public void deleteFilesForFolder(final File folder) {
	    for (final File fileEntry : folder.listFiles()) {
	        if (!fileEntry.isDirectory()) {
	        	if (!safeDelete(fileEntry, 5)) {
	        		Log.d("TEST", "Cannot delete the file : " + fileEntry.getPath());
	        	}
	        }
	    }
	}

	private boolean safeDelete(File target, int numOfTrials) {
		int i;
		boolean isDeleted;

		for (i = 0; i < numOfTrials; i++) {
			isDeleted = target.delete();
			if (isDeleted) {
				break;
			}
		}

		if (i == numOfTrials) {
			return false;
		} else {
			return true;
		}
	}
	
	Handler mAfterDownList = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case 0:
				Log.d("TEST", "List file download finish.");

				String[] result = msg.obj.toString().split("\n");

				dqThread = new DownloadQueueThread(result,	Save_Path, msg.arg1);
				dqThread.start();

				break;
			case 1:
				Log.d("TEST", "Try again please.");
				state = AdFDState.END;
				break;
			case 2:
				resetDB();
		        pref.edit().putInt("length", 0).commit();
				break;
			}

		}

	};

	class DownloadListThread extends Thread {
		private static final int SUCCESS_THREAD = 0;
		private static final int FAIL_THREAD = 1;
		String mTargetUrl;
		String mLocalPath;
		String mExt = "nct";
		String mFileName = "list";
		int mLoc;

		DownloadListThread(String serverPath, String localPath, int loc) {
			mLocalPath = localPath;
			mTargetUrl = String.format("%s/files/downlist/%d", serverPath, loc);
			mLoc = loc;
			Log.d("TEST", "Start download list in loc : " + loc);
		}

		@Override
		public void run() {
			URL requestURL;
			int Read;
			String result = null;
			try {
				requestURL = new URL(mTargetUrl);
				HttpURLConnection conn = (HttpURLConnection) requestURL
						.openConnection();
				conn.setReadTimeout(10000 /* milliseconds */);
				conn.setConnectTimeout(15000 /* milliseconds */);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);

				// Starts the query
				conn.connect();
				int response = conn.getResponseCode();

				if (response == 200) {
					Log.d("TEST", "The response is: " + response);

					int len = conn.getContentLength();

					if (len == 1) {
						Log.d("TEST", "There is no list in loc");

						mAfterDownList.sendEmptyMessage(2);
						conn.disconnect();
						return;
					}
					else {
					    StringBuffer sb = new StringBuffer();
					    InputStream is = null;

					    try {
					        is = new BufferedInputStream(conn.getInputStream());
					        BufferedReader br = new BufferedReader(new InputStreamReader(is));
					        String inputLine = "";
					        while ((inputLine = br.readLine()) != null) {
					            sb.append(inputLine + "\n");
					        }
					        sb.append("\n");
					        result = sb.toString().replace("\n\n", "");
					        Log.i("TEST", "result is " + result);
					    }
					    catch (Exception e) {
					        Log.i("TEST", "Error reading InputStream");
					        result = null;
					    }
					    finally {
					        if (is != null) {
					            try {
					                is.close();
					            }
					            catch (IOException e) {
					                Log.i("TEST", "Error closing InputStream");
					            }
					        }
					    }
					}
				}
				conn.disconnect();
			} catch (MalformedURLException e) {
				Log.e("ERROR1", e.getMessage());
			} catch (IOException e) {
				Log.e("ERROR2", e.getMessage());
				e.printStackTrace();
			}


		    if (result != null) {
		    	Message msg = mAfterDownList.obtainMessage();
		    	msg.what = SUCCESS_THREAD;
		    	msg.arg1 = mLoc;
		    	msg.obj = result;

		    	mAfterDownList.sendMessage(msg);
		    }
		    else {
				mAfterDownList.sendEmptyMessage(FAIL_THREAD);
		    }
		}
	}



	class DownloadQueueThread extends Thread {
		String[] mfileURLs;
		String mLocalPath;
		Queue<String> mQueue;
		int mCount;
		DownloadThread dThread1;
		DownloadThread dThread2;
		int mLoc;

		DownloadQueueThread(String[] fileURLs, String localPath, int loc) {
			this.mfileURLs = fileURLs;
			mLocalPath = localPath;
			this.mCount = Integer.parseInt(fileURLs[0]);
			mLoc = loc;
			mQueue = new LinkedList<String>();

			initQueue();
			resetDB();
		}

		private void initQueue() {
			Log.d("TEST", "Queue Init");

	        pref.edit().putInt("length", mCount).commit();			

			int length = mCount * 3;
			for (int i = 1; i <= length; i++) {
				mQueue.add(this.mfileURLs[i]);
			}
		}

		@Override
		public void run() {
			if (!mQueue.isEmpty()) {
				Log.d("TEST", "Queue Thread Download START!!");
				downloadFile();
			}
			else {
				state = AdFDState.END;
			}
		}

		private void downloadNextFile() {
			synchronized (mQueue) {
				if (!mQueue.isEmpty()) {
					Log.d("TEST", String.format("Queue Thread Download #%d", mCount));
					downloadFile();
				}
				else {
					Log.d("TEST", "Queue is empty :: Download finish!");
					state = AdFDState.END;
				}
			}
		}

		private void downloadFile() {
			String list_id = mQueue.poll();
					
			String[] data = mQueue.poll().split(" ");


			dThread1 = new DownloadThread(data[0], mLocalPath, data[1], data[2], list_id);
			dThread1.start();

			data = mQueue.poll().split(" ");
			AdFileDAO.getInstance(mContext).insertFile(String.format("%s/%s/%s", data[0], data[1], data[2]), data[2], data[1], String.valueOf(mLoc), list_id);
		}

		Handler mAfterDown = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what) {
				case 0:
					Log.d("TEST", "Poster file download finish.");
					downloadNextFile();
					break;
				case 1:
					Log.d("TEST", "Server said the poster file is not valid.");
					downloadNextFile();
					break;
				case 2:
					Log.d("TEST", "Client side error.");
					downloadNextFile();
					break;
				}
			}
		};

		// 다운로드 쓰레드로 돌림..
		class DownloadThread extends Thread {
			private String mTargetUrl;
			private String mLocalPath;
			private String mExt;
			private String mFileName;
			private String mType;
			private String mList_id;

			DownloadThread(String serverPath, String localPath, String type, String fileName, String list_id) {
				mLocalPath = localPath;
				mType = type;
				mExt = "nca";
				mFileName = fileName;
				mTargetUrl = String.format("%s/%s/%s", serverPath, type, fileName);
				mList_id = list_id;
			}

			@Override
			public void run() {
				URL requestURL;
				int Read;
				try {
					requestURL = new URL(mTargetUrl);
					HttpURLConnection conn = (HttpURLConnection) requestURL
							.openConnection();
					conn.setReadTimeout(10000 /* milliseconds */);
					conn.setConnectTimeout(15000 /* milliseconds */);
					conn.setRequestMethod("GET");
					conn.setDoInput(true);

					// Starts the query
					conn.connect();
					int response = conn.getResponseCode();
					Log.d("TEST", "The response is: " + response);

					String disposition = conn.getHeaderField("Content-Disposition");
					if (disposition == null) {
						Log.d("TEST", "File is not existing in server : " + mTargetUrl);
						mAfterDown.sendEmptyMessage(1);
						conn.disconnect();
						return;
					}

					String uri = String.format("%s%s.%s", mLocalPath, mFileName, mExt);

					File file = new File(uri);
//					if (file.exists()) {
//						Log.d("TEST", "Already File exist. Don't download..." + mTargetUrl);
//						mAfterDown.sendEmptyMessage(2 + (mExt.equals("nca") ? 5 : 0));
//						conn.disconnect();
//						return;
//					}

					int len = conn.getContentLength();
					byte[] tmpByte = new byte[len];
					InputStream is = conn.getInputStream();

					FileOutputStream fos = new FileOutputStream(file);
					for (;;) {
						Read = is.read(tmpByte);
						if (Read <= 0) {
							break;
						}
						fos.write(tmpByte, 0, Read);
					}
					is.close();
					fos.close();

					AdFileDAO.getInstance(mContext).insertFile(uri, mFileName, mType, String.valueOf(mLoc), mList_id);

					mAfterDown.sendEmptyMessage(0);

					conn.disconnect();
				} catch (MalformedURLException e) {
					Log.e("ERROR1", e.getMessage());
					mAfterDown.sendEmptyMessage(2);
				} catch (IOException e) {
					Log.e("ERROR2", e.getMessage());
					e.printStackTrace();
					mAfterDown.sendEmptyMessage(2);
				}
			}
		}
	}
}
