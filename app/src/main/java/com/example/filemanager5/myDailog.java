package com.example.filemanager5;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.AlertDialog;

import android.content.DialogInterface;

import android.os.Handler;

import android.os.Looper;

import android.os.Message;

import android.widget.EditText;

/**

 * Created by Andrew on 2015/7/21.

 */

class myDialog {
    String mInputString = "";

    Activity mContext;

    String mTitle;

    EditText mEditText;

    Handler mHandler;

    public myDialog(Activity context,String title){
        super();
        mContext = context;

        mTitle = title;

    }

    public String showDialog(){
//        Handler mHandler = new Handler(){
//
//            @Override
//
//            public void handleMessage(Message msg) {
//                //super.handleMessage(msg);
//
//                throw new RuntimeException();
//
//            }
//
//        };


        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(mTitle);

        builder.setCancelable(false);

        mEditText = new EditText(mContext);

        builder.setView(mEditText);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialogInterface, int i) {
                mInputString = mEditText.getText().toString();

                Message message = mHandler.obtainMessage();

                mHandler.sendMessage(message);

            }

        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialogInterface, int i) {
               dialogInterface.dismiss();

            }

        });

        builder.show();

        try {
            Looper.getMainLooper().loop();

        }

        catch(RuntimeException e2)

        {
        }

        return mInputString;

    }


}
