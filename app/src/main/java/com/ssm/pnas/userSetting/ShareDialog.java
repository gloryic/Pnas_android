package com.ssm.pnas.userSetting;

/**
 * Created by glory on 15. 8. 23..
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.text.ClipboardManager;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.ssm.pnas.C;
import com.ssm.pnas.R;
import com.ssm.pnas.nanohttpd.HashIndex;

public class ShareDialog extends AlertDialog.Builder {

    private SharedPreferences pref;
    private Context mContext;

    public ShareDialog(Context context, Activity activity) {
        super(context);
        mContext = context;
        View dialogSoundInnerView = activity.getLayoutInflater().inflate(R.layout.share_dialog_layout, null);
        this.setView(dialogSoundInnerView);

        pref = this.getContext().getSharedPreferences("pbox", Context.MODE_PRIVATE);

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
        this.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        //TODO test
                        String code = HashIndex.getInstance().generateCode(Environment.getExternalStorageDirectory().toString()+"/Music");
                        String shareUrl = C.localIP+":"+C.port+"/"+code;
                        Toast.makeText(mContext, "code : '" + code + "'로 공유 완료!", Toast.LENGTH_SHORT).show();

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

