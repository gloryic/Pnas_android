package com.ssm.pnas.userSetting;

/**
 * Created by kangSI on 2015-08-23.
 */
public class ListRow{

    public String fileName;
    public String fileFullPath;
    public String code;
    public boolean isDir;
    public boolean isSharing;
    public boolean isDuplic;

    /**
     * @param fileName fileName
     * @param fileFullPath fileFullPath
     * @param code code
     * @param isDir isDir
     */
    public ListRow(String fileName ,String fileFullPath, String code, boolean isDir)
    {
        this.fileName = fileName;
        this.fileFullPath = fileFullPath;
        this.code = code;
        this.isDir = isDir;
        this.isSharing = false;
        this.isDuplic = false;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileFullPath() {
        return fileFullPath;
    }

    public void setFileFullPath(String fileFullPath) {
        this.fileFullPath = fileFullPath;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public boolean isDir() {
        return isDir;
    }

    public void setIsDir(boolean isDir) {
        this.isDir = isDir;
    }


    public boolean isDuplic() {
        return isDuplic;
    }

    public void setIsDuplic(boolean isDuplic) {
        this.isDuplic = isDuplic;
    }
}
