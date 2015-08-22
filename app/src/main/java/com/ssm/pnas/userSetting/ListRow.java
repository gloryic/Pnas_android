package com.ssm.pnas.userSetting;

/**
 * Created by kangSI on 2015-08-23.
 */
public class ListRow {

    public String fileName;
    public String fileFullPath;
    public String code;

    /**
     * @param p1 fileName
     * @param p2 fileFullPath
     * @param p3 code
     */
    public ListRow(String p1,String p2,String p3)
    {
        fileName = p1;
        fileFullPath = p2;
        code = p3;
    }
}
