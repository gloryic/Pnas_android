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

import com.ssm.pnas.C;
import com.ssm.pnas.R;
import com.ssm.pnas.nanohttpd.HashIndex;

public class ShareDialog extends AlertDialog.Builder {

    private SharedPreferences pref;
    private Context mContext;
    private ListRow listRow;
    private TextView file_name, file_storage, file_full_path;

    public ShareDialog(Context context, Activity activity, ListRow item) {
        super(context);
        mContext = context;
        listRow = item;

        View dialogShareInnerView = activity.getLayoutInflater().inflate(R.layout.share_dialog_layout, null);
        this.setView(dialogShareInnerView);

        pref = this.getContext().getSharedPreferences("pbox", Context.MODE_PRIVATE);
        file_name = (TextView) dialogShareInnerView.findViewById(R.id.file_name);
        file_storage = (TextView) dialogShareInnerView.findViewById(R.id.file_storage);
        file_full_path = (TextView) dialogShareInnerView.findViewById(R.id.file_full_path);

        file_name.setText(item.fileName);
        file_full_path.setText(item.fileFullPath);
        file_storage.setText("12kb");

//        btn_announce_test = (Button) dialogSoundInnerView.findViewById(R.id.btn_announce_test);
//        btn_announce_test.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                switch (v.getId()) {
//                    case R.id.btn_announce_test:
//                        break;
//                }
//            }
//        });

        //btn register
        this.setPositiveButton("공유",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        //TODO test
                        String code = HashIndex.getInstance().generateCode(listRow.fileFullPath);
                        String shareUrl = "http://"+C.localIP+":"+C.port+"/"+code;
                        Toast.makeText(mContext, "공유코드 : " + code , Toast.LENGTH_SHORT).show();

                        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
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

        this.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //Canceled.
                dialog.dismiss();
            }
        });
    }

}

