package com.ljq.mydiary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.os.Handler;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.logging.LogRecord;


public class LoginActivity extends Activity {

    EditText usernameEdit;
    EditText pwdEdit;
    CheckBox rememberPwdCheck;
    Button loginBtn;
    static ProgressBar progressBar;
    SharedPreferences pref;
     Handler handler;
    //private final Handler mHandler = new MyHandler(this);
    static final int STOP=0X111;    //进度完成的标记
    static final int CONTINUE=0X112;    //继续显示进度条的标记
    static final int MAX=100;   //最大的进度数为100%
    static int progress;   //进度条的当前进度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        setListeners();
    }

    void init(){
        /*初始化控件变量*/
        initViews();
        /*初始化线程的手柄*/
        initHandler();
    }

    void initViews(){
        usernameEdit = (EditText)findViewById(R.id.edit_userName);
        pwdEdit = (EditText)findViewById(R.id.edit_passWord);
        rememberPwdCheck = (CheckBox)findViewById(R.id.rememberpsw_checkBox);
        loginBtn = (Button)findViewById(R.id.btn_login);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        pref=getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        usernameEdit.setText(pref.getString("USERNAME",""));
        if(pref.getBoolean("REMBERPWD",false)){
            pwdEdit.setText(pref.getString("PASSWORD",""));
        }
        rememberPwdCheck.setChecked(pref.getBoolean("REMBERPWD",false));
        progress = 0;
        progressBar.setProgress(progress);
        progressBar.setMax(MAX);
    }

    void initHandler(){
        handler = new Handler() {
        public void handleMessage(Message msg){
            switch (msg.what){
                case CONTINUE:
                    if(!Thread.currentThread().isInterrupted())
                        progressBar.setProgress(progress);
                    break;
                case STOP:
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, DiaryActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


    }

    void setListeners(){
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEdit.getText().toString();
                String pwd = pwdEdit.getText().toString();
                if((!username.equals("admin")) || (!pwd.equals("admin"))){
                    Toast.makeText(LoginActivity.this,"用户名或密码不正确", Toast.LENGTH_LONG).show();
                }
                else{
                    SharedPreferences.Editor editor = pref.edit();
                    if(rememberPwdCheck.isChecked()){
                        editor.putString("USERNAME",username);
                        editor.putString("PWD", pwd);
                        editor.putBoolean("REMBERPWD",true);
                        editor.commit();
                    }
                    else{
                        editor.putBoolean("REMBERPWD",false);
                        editor.commit();
                    }
                    usernameEdit.setEnabled(false);
                    pwdEdit.setEnabled(false);
                    loginBtn.setEnabled(false);

                    /*将显示进度条5秒钟*/
                    progressBar.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                for(int i=0; i<5;i++){
                                    progress = (i+1)*20;
                                    Thread.sleep(1000);
                                    if(i==4){
                                        Message msg = new Message();
                                        msg.what = STOP;
                                        handler.sendMessage(msg);
                                        break;
                                    }
                                    else{
                                        Message msg = new Message();
                                        msg.what = CONTINUE;
                                        handler.sendMessage(msg);
                                    }
                                }
                            }
                            catch(InterruptedException e){
                                e.printStackTrace();
                            }

                        }
                    }).start();
                }
            }
        });
    }

    /**private  class MyHandler extends Handler{
        private final WeakReference<LoginActivity> loginActivityWeakReference;
        public MyHandler(LoginActivity activity){
            loginActivityWeakReference = new WeakReference<LoginActivity>(activity);
        }

        public void handleMessage(Message msg){
            switch (msg.what){
                case CONTINUE:
                    if(!Thread.currentThread().isInterrupted())
                        progressBar.setProgress(progress);
                    break;
                case STOP:
                    Intent intent = new Intent();
                    intent.setClass(loginActivityWeakReference.get(), DiaryActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }**/


}


