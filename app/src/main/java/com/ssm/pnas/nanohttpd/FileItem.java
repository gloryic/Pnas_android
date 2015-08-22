package com.ssm.pnas.nanohttpd;

/**
 * Created by glory on 15. 8. 23..
 */

public class FileItem{

    String code;
    String path;
    boolean isDir;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public FileItem(String path, boolean isDir, String code){
        this.path = path;
        this.isDir = isDir;
        this.code = code;
    }

    public boolean isDir() {
        return isDir;
    }

    public void setIsDir(boolean isDir) {
        this.isDir = isDir;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}