package com.ssm.pnas.nanohttpd;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.ssm.pnas.C;

public class Httpd
{
    private WebServer server;
    private volatile static Httpd instance = null; 
    private Context mContext;
    
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
        
        public boolean haveDot(String uri){
        	return uri.indexOf(".") == -1 ? false : true;
        }

        @Override
        public Response serve(IHTTPSession session) {
        	StringBuilder response = new StringBuilder();
        	Response.IStatus status = Response.Status.BAD_REQUEST;
        	String mimeType = MIME_PLAINTEXT;
        	
            try {
                Method method = session.getMethod();
                String uri = session.getUri();
                String[] splitUri = uri.split("/");
                Map<String, String> parms = session.getParms();
                
                if (Method.GET.equals(method) && uri != null && !uri.equals("/favicon.ico")){

                 	if(splitUri[1].equals("api")){
                		response.append(WebManager.getInstance(mContext).executeAPI(splitUri, parms, Method.GET));
                		status = Response.Status.OK;
                		mimeType = APP_JSON;
                	}
                 	else {
                		try {
                			String line="";
                			if(!haveDot(uri)) uri+=".html";
                			
	                		InputStream inputstream = mContext.getAssets().open("www"+uri);
	                		BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));
	                		while ((line = reader.readLine()) != null) {
	                			response.append(line);
	                		}
	                		reader.close();
	                		status = Response.Status.OK;
	                		
	                		String tmp = uri.substring(uri.length()-4).toLowerCase();
	                		Log.d("",tmp+"!!!");
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