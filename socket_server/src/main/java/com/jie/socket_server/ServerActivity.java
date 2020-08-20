package com.jie.socket_server;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerActivity extends AppCompatActivity {

    private static final String TAG = "ServerActivity";

    private TextView mTvInfo;
    private TextView mTvGetData;

    private int mPort = 8000; // 指明服务端要使用的端口
    private ServerSocket mServerSocket = null; // 服务端Socket
    private Socket mSocket = null; // 客户端Socket
    private StringBuffer mStringBuffer = new StringBuffer();

    public Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    // Server的ip和port信息
                    mTvInfo.setText(message.obj.toString());
                    break;
                case 2:
                    // 从客户端获取到的消息
                    mTvGetData.setText(message.obj.toString());
                    mStringBuffer.setLength(0);
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        mTvInfo = findViewById(R.id.tv_info);
        mTvGetData = findViewById(R.id.tv_data);

        // 开启服务器并监听客户端发送的数据
        startServer();
    }

    /**
     * 服务器端接收数据
     * 需要注意以下一点：
     * 服务器端应该是多线程的，因为一个服务器可能会有多个客户端连接在服务器上；
     */
    public void startServer() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    if (mServerSocket == null) mServerSocket = new ServerSocket(mPort);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String ip = Utils.getIPAddress(ServerActivity.this);

                Message msg = mHandler.obtainMessage();
                msg.what = 1;
                msg.obj = "ip = " + ip + " , port = " + mPort;
                mHandler.sendMessage(msg);

                // 持续获取消息
                while (true) {
                    try {
                        if (mServerSocket != null && !mServerSocket.isClosed()) {
                            mSocket = mServerSocket.accept();
                        } else {
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (mSocket != null) new ServerThread(mSocket, mHandler, mStringBuffer).start();
                }
            }
        };
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mServerSocket != null) {
                mServerSocket.close();
                mServerSocket = null;
            }
            if (mSocket != null) {
                mSocket.close();
                mSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
