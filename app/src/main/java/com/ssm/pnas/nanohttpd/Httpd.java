package com.ssm.pnas.nanohttpd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.ssm.pnas.C;
import com.ssm.pnas.Dbg;

public class Httpd
{
    private WebServer server;
    private volatile static Httpd instance = null; 
    private Context mContext;
    private static String TAG = "Httpd";
    public static Httpd getInstance(Context mContext) {
		synchronized (Httpd.class) {
		if (null == instance) {
				instance = new Httpd(mContext);
			}
		}
		
		instance.mContext = mContext;
		return instance;
	}
    
    private Httpd(Context mContext){
    	this.mContext = mContext;
    	server = new WebServer();
    }
    
    public void start(){
        try {
        	if(server==null)
        		server = new WebServer();
            server.start();
        } catch(IOException ioe) {
            Log.i("Httpd", "The server could not start.");
        }
        Log.i("Httpd", "Web server initialized.");
    }

	public void stop(){
		if (server != null)
			server.stop();
	}

    private class WebServer extends NanoHTTPD {

        public WebServer(){
            super(C.port);
        }
        public String mCode;

        public boolean haveDot(String uri){
        	return uri.indexOf(".") == -1 ? false : true;
        }

		public boolean isInBoundary(String strCode){
			try{
				int code = Integer.parseInt(strCode);
				if(0 < code && HashIndex.MAX_NUM > code){
					this.mCode = strCode;
					return true;
				}
				else{
					this.mCode = null;
					return false;
				}
			}
			catch(NumberFormatException e) {
				Log.d("httpd",e.getMessage());
				return false;
			}
		}

		/**
		 * hash code를 사용해서 서브 파일 경로를 가져온다.
		 * 루트 경로는 sd card의 루트이다.
		 * */
		public String getSubFilePath(String code){
			return HashIndex.getInstance().getPathFromHash(code);
		}

		/**
		 * file의 전체 경로를 가져온다.
		 * */
		public String getFilePath(String code){
			String ext = Environment.getExternalStorageState();
			String filePath = null;
			if (ext.equals(Environment.MEDIA_MOUNTED)) {
				filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
				filePath += getSubFilePath(code);
			}
			return filePath;
		}

        @Override
        public Response serve(IHTTPSession session) {
        	StringBuilder response = new StringBuilder();

			Response.IStatus status = Response.Status.BAD_REQUEST;
        	String mimeType = MIME_PLAINTEXT;
			String line;

            try {
                Method method = session.getMethod();
                String uri = session.getUri();
                String[] splitUri = uri.split("/");
                Map<String, String> parms = session.getParms();

				if (Method.GET.equals(method) && uri != null && !uri.equals("/favicon.ico")){
					//splitUri[0] -> '' 빈칸이 앞에 있음 ''/'api'

                 	if(splitUri[1].equals("api")){
                		response.append(WebManager.getInstance(mContext).executeAPI(splitUri, parms, Method.GET));
                		status = Response.Status.OK;
                		mimeType = APP_JSON;
                	}
					else if(splitUri.length == 2 && isInBoundary(splitUri[1])){
						//File Transfer Part
						String fullPath = getFilePath(this.mCode);
						String[] pathArr = fullPath.split("/");
						int lastIndex = pathArr.length-1;

						Log.d(TAG,fullPath);

						File file = new File(fullPath);
						FileInputStream fileInputStream = new FileInputStream(file);

						status = Response.Status.OK;
						mimeType = "application/force-download";

						NanoHTTPD.Response responsefile = new NanoHTTPD.Response(status, mimeType, fileInputStream);
						responsefile.addHeader("Content-Disposition","attachment; filename="+pathArr[lastIndex]);
						return responsefile;
					}
                 	else {
                		try {

	                		InputStream inputstream = mContext.getAssets().open("www"+uri);
	                		BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));
	                		while ((line = reader.readLine()) != null) {
	                			response.append(line);
	                		}
	                		reader.close();

	                		status = Response.Status.OK;
	                		
	                		String tmp = uri.substring(uri.length()-4).toLowerCase();

							Log.d(TAG,tmp);

	                		if(tmp.equals("html"))
	                			mimeType = MIME_HTML;
	                		else if(tmp.equals(".css"))
	                			mimeType = "text/css";
	                		else if(tmp.equals("woff"))
	                			mimeType = "application/x-font-woff";
	                  		else if(tmp.equals(".eot"))
	                			mimeType = "application/vnd.ms-fontobject";
	                  		else if(tmp.equals(".svg"))
	                			mimeType = "image/svg+xml";
	                		else
	                			mimeType = MIME_HTML;
                		}
                		catch (FileNotFoundException e) {
                			Log.i("Httpd", e.toString());
                			response.append("{\"status\":\"ERROR\",\"message\":\"URL ERROR\"}");
                			status = Response.Status.NOT_FOUND;
                    		mimeType = APP_JSON;
                		}
                 	}
                }
                else if (Method.POST.equals(method) && uri != null && !uri.equals("/favicon.ico")){
                	Map<String, String> files = new HashMap<String, String>();
                	if(splitUri[1].equals("api")){
                    	session.parseBody(files);
                		response.append(WebManager.getInstance(mContext).executeAPI(splitUri, parms, Method.POST));
                		status = Response.Status.OK;
                		mimeType = APP_JSON;
                	}
                }
            }catch(IOException ioe) {
                Log.i("Httpd", ioe.toString());
            	status = Response.Status.INTERNAL_ERROR;
        		mimeType = APP_JSON;
        		response.append(String.format("{\"status\":\"ERROR\",\"message\":\"IOException: %s\"}", ioe.getMessage()));
            }catch (ResponseException re) {
            	status = re.getStatus();
            	mimeType= APP_JSON;
            	response.append(String.format("{\"status\":\"ERROR\",\"message\":\"ResponseException: %s\"}", re.getMessage()));
            }catch (Exception e) {
            	Log.i("Httpd", e.toString());
            	status = Response.Status.INTERNAL_ERROR;
        		mimeType = APP_JSON;
        		response.append(String.format("{\"status\":\"ERROR\",\"message\":\"Exception: %s\"}", e.getMessage()));
			}
            return new NanoHTTPD.Response(status, mimeType, response.toString());
        }
    }
}