package com.ssm.pnas.userSetting;

/**
 * Created by glory on 15. 8. 23..
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.ssm.pnas.C;
import com.ssm.pnas.R;
import com.ssm.pnas.nanohttpd.HashIndex;
import com.ssm.pnas.tools.downloader.FileDownloader;

import java.util.HashSet;
import java.util.Set;

public class ShareDialog extends AlertDialog.Builder {

    private SharedPreferences pref;
    private Context mContext;
    private final ListRow listRow;
    private TextView file_name, file_storage, file_full_path, file_share;
    private final TextView tvStatus;

    public ShareDialog(Context context, Activity activity, ListRow item, int position, String currentFrag) {
        super(context);
        mContext = context;
        listRow = item;

        SwipeMenuListView mListView = (SwipeMenuListView)activity.findViewById(R.id.activity_main_swipemenulistview);
        View itemView = mListView.getChildAt(position-mListView.getFirstVisiblePosition());

        tvStatus = (TextView)itemView.findViewById(R.id.tv_status);

        View dialogShareInnerView = activity.getLayoutInflater().inflate(R.layout.share_dialog_layout, null);
        this.setView(dialogShareInnerView);

        pref = this.getContext().getSharedPreferences("pboxShareList", Context.MODE_PRIVATE);
        file_name = (TextView) dialogShareInnerView.findViewById(R.id.file_name);
        file_storage = (TextView) dialogShareInnerView.findViewById(R.id.file_storage);
        file_full_path = (TextView) dialogShareInnerView.findViewById(R.id.file_full_path);

        file_name.setText(item.fileName);
        file_full_path.setText(item.fileFullPath);
        file_storage.setText("12kb");


        if (currentFrag != null && currentFrag.equals("Other box")) {
            //btn register
            this.setPositiveButton("다운로드",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String shareUrl = "http://" + C.remoteIP + ":" + C.port + "/" + listRow.getCode();
                            FileDownloader.getInstance(mContext).downloadFile(shareUrl);
                            Toast.makeText(mContext, "다운로드를 시작합니다.", Toast.LENGTH_SHORT).show();
                        }
                    });

            this.setNegativeButton("취소",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
        }
        else if (currentFrag != null && currentFrag.equals("My box")) {
            //btn register
            this.setPositiveButton("복사",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {


                            ListRow fileItem = HashIndex.getInstance().generateCode(listRow.fileFullPath);
                            String code = fileItem.getCode();

//                        //TODO
//                        tvStatus.setText(code);
//                        SharedPreferences.Editor editor = pref.edit();
//                        editor.putStringSet("MyPbox", oneItem);
//                        editor.commit();

                            //TODO - 쉐얼드로 처리
                            String[] pathArr = fileItem.getFileFullPath().split("/");
                            String fileName = pathArr[pathArr.length - 1];

                            if (!fileItem.isDuplic())
                                C.myPboxList.add(new ListRow(fileName, fileItem.getFileFullPath(), fileItem.getCode(), fileItem.isDir()));

                            String shareUrl = "http://" + C.localIP + ":" + C.port + "/" + code;
                            Toast.makeText(mContext, "공유코드 : " + code, Toast.LENGTH_SHORT).show();

                            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                clipboard.setText(shareUrl);
                            } else {
                                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", shareUrl);
                                clipboard.setPrimaryClip(clip);
                            }

                        }
                    });

            this.setNegativeButton("취소",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            //TODO
            this.setNeutralButton("공유중지",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            HashIndex.getInstance().dismissCode(listRow);
                            //TODO
                            tvStatus.setText("공유가능");
                            dialog.cancel();
                        }
                    });
        }
        else {
            this.setPositiveButton("공유",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {


                            ListRow fileItem = HashIndex.getInstance().generateCode(listRow.fileFullPath);
                            String code = fileItem.getCode();


                        //TODO
                        if(FullPathHashMap.getInstance().mss.get(listRow.fileFullPath)==(null))
                        {
                            tvStatus.setText(code);
                            FullPathHashMap.getInstance().mss.put(listRow.fileFullPath,code);
                        }

//                        SharedPreferences.Editor editor = pref.edit();
//                        editor.putStringSet("MyPbox", oneItem);
//                        editor.commit();

                            //TODO - 쉐얼드로 처리
                            String[] pathArr = fileItem.getFileFullPath().split("/");
                            String fileName = pathArr[pathArr.length - 1];

                            if (!fileItem.isDuplic())
                                C.myPboxList.add(new ListRow(fileName, fileItem.getFileFullPath(), fileItem.getCode(), fileItem.isDir()));

                            String shareUrl = "http://" + C.localIP + ":" + C.port + "/" + code;
                            Toast.makeText(mContext, "공유코드 : " + code, Toast.LENGTH_SHORT).show();

                            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                clipboard.setText(shareUrl);
                            } else {
                                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", shareUrl);
                                clipboard.setPrimaryClip(clip);
                            }

                        }
                    });

            this.setNegativeButton("취소",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            //TODO
            this.setNeutralButton("공유중지",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            HashIndex.getInstance().dismissCode(listRow);
                            //TODO

                            
                            FullPathHashMap.getInstance().mss.remove(listRow.fileFullPath);

                            tvStatus.setText("공유가능");
                            dialog.cancel();
                        }
                    });
        }
        this.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //Canceled.
                dialog.dismiss();
            }
        });
    }

}

