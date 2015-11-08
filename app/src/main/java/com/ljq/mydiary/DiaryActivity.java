package com.ljq.mydiary;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class DiaryActivity extends Activity {
    EditText mydiaryEditText;
    Button saveButton;
    static final String FILENAME = "Mydiary.txt";
    FileOutputStream fOutputStream;
    FileInputStream fInputStream;
    private static long INTERVAL = 2000; //���η��ؼ�������ֵ����
    private long mFirstBackKeyPressTime = -1; //��һ�ΰ��·��ؼ���ʱ��
    private long mLastBackPressTime = -1; //���һ�ΰ��·��ؼ���ʱ��
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        init();
        setListeners();
    }

    void init(){
        mydiaryEditText = (EditText) findViewById(R.id.edit_diary);
        saveButton = (Button) findViewById(R.id.btn_save);
        /*���ϴα�����ռ��ļ���*/
        try{
            fInputStream = openFileInput(FILENAME);
            ByteArrayOutputStream bou = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            while((length = fInputStream.read(buffer)) != -1){
                bou.write(buffer,0,length);
            }
            mydiaryEditText.setText(new String(bou.toByteArray()));
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

        /*�ڴ˴�Ҫд����ռ�ǰ����������Ϣ*/
        Time time;
        time = new Time("GMT+8");
        time.setToNow();
        mydiaryEditText.append("\n" + time.year + "年-" + (time.month + 1) + "月-" + time.monthDay + "日\n");

    }

    void setListeners(){
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    fOutputStream = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                    fOutputStream.write(mydiaryEditText.getText().toString().getBytes());
                    Toast.makeText(DiaryActivity.this,"保存成功",Toast.LENGTH_LONG).show();
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
    }

    //�����ؼ����μ��˳�Ӧ��
    @Override
    public void onBackPressed() {
        if(mFirstBackKeyPressTime == -1){
            mFirstBackKeyPressTime = System.currentTimeMillis();
            Toast.makeText(DiaryActivity.this,"再按一下退出程序", Toast.LENGTH_LONG).show();
        }else{
            mLastBackPressTime = System.currentTimeMillis();
            if(mLastBackPressTime-mFirstBackKeyPressTime <= INTERVAL){
                finish();
                System.exit(0);
                super.onBackPressed();
            }else {
                mFirstBackKeyPressTime = System.currentTimeMillis();
                Toast.makeText(DiaryActivity.this,"再按一下退出程序", Toast.LENGTH_LONG).show();
            }
        }
    }
}
