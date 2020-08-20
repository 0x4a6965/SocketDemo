package com.jie.socketdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private EditText mEtIp, mEtData;
    private OutputStream mOutputStream = null;
    private Socket mSocket = null;
    private boolean mIsConnected = false; // 服务器是否连接
    private int mPort = 8000; // 服务端开启的端口

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEtIp = findViewById(R.id.et_ip);
        mEtData = findViewById(R.id.et_data);

        // 连接服务器
        findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String ip = mEtIp.getText().toString();

                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        if (!mIsConnected) {
                            try {
                                // 连接服务端
                                mSocket = new Socket(ip, mPort);
                                if(mSocket.isConnected()){
                                    mIsConnected = true;
                                    mOutputStream = mSocket.getOutputStream();

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                thread.start();
            }
        });

        // 向服务器发送数据
        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String data = mEtData.getText().toString();

                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        if(mIsConnected && mOutputStream != null){
                            try {
                                // 将String转换成byte[]传输数据，使用UTF-8编码，服务端也使用UTF-8转换，支持中文
                                mOutputStream.write(data.getBytes(StandardCharsets.UTF_8));

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (IOException e) {
                                mIsConnected = false;
                                e.printStackTrace();
                            }
                        }
                    }
                };
                thread.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mOutputStream != null) mOutputStream.close();
            if (mSocket != null) mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
