package com.example.framgia.natuallanguageprocessing.service;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.framgia.natuallanguageprocessing.Data.model.MyViewGroup;
import com.example.framgia.natuallanguageprocessing.R;

/**
 * Created by framgia on 10/11/2016.
 */

public class MyService extends Service implements View.OnTouchListener,View.OnClickListener{
    static final String BROADCAST_ACTION="message";
    private WindowManager windowManager;
    private MyViewGroup myViewGroup;
    private WindowManager.LayoutParams mParams;
    private View subView;
    private ImageButton mMovableButton;
    private int DOWN_X, DOWN_Y,MOVE_X,MOVE_Y,xparam,yparam;
    private AlertDialog mAlertDialog;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initView();

    }

    private void initView() {
        windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        myViewGroup = new MyViewGroup(this);
        LayoutInflater minflater = LayoutInflater.from(this);
        subView = minflater.inflate(R.layout.element_home,myViewGroup);// nhet cai main vao cai viewGroup,
        // de anh xa ra subView
        //dinh nghia param
        mParams = new WindowManager.LayoutParams();
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.gravity = Gravity.CENTER;
        mParams.format = PixelFormat.TRANSLUCENT;//trong suot
        mParams.type = WindowManager.LayoutParams.TYPE_PHONE;// noi tren all be mat
        mParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// khong bi gioi han boi man hinh|Su duoc nut home
        windowManager.addView(myViewGroup,mParams);
        subView.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                xparam = mParams.x;
                yparam = mParams.y;
                DOWN_X = (int)event.getRawX();
                DOWN_Y = (int)event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                MOVE_X = (int) event.getRawX()- DOWN_X ;
                MOVE_Y = (int)event.getRawY()- DOWN_Y ;
                updateView(MOVE_X, MOVE_Y);
                break;
        }
        return true;
    }

    private void updateView(int x, int y) {
        mParams.x = x+xparam;
        mParams.y=y+yparam;
        windowManager.updateViewLayout(myViewGroup,mParams);
    }
    private void sendMessage(){
        Intent broastCast= new Intent();
        broastCast.setAction(BROADCAST_ACTION);
        sendBroadcast(broastCast);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }
    public void removeFocusInView(){
        mParams.flags = mParams.flags|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    }
}