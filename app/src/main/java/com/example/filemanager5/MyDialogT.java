package com.example.filemanager5;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public abstract class MyDialogT extends Dialog {

    private Handler mHandler;
    protected Object result;

    public MyDialogT(Context context){
        super(context);
        onCreate();
    }

    public abstract void onCreate();

    /**
     * 结束对话框，将触发返回result对象
     */
    public void finishDialog(){
        dismiss();
        mHandler.sendEmptyMessage(0);
    }

    static class SynHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            throw new RuntimeException();
        }
    }

    /**
     * 显示同步对话框
     * @return 返回result对象
     */
    public Object showDialog() {
        super.show();
        try {
            Looper.getMainLooper();
            mHandler = new SynHandler();
            Looper.loop();
        } catch (Exception e) {
        }
        return result;
    }
}