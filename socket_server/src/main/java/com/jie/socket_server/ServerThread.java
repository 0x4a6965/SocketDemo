package com.jie.socket_server;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerThread extends Thread {

    private static final String TAG = "ServerThread";

    private Socket mSocket;
    private Handler mHandler;
    private StringBuffer mStringBuffer;

    public ServerThread(Socket socket, Handler handler, StringBuffer stringBuffer) {
        this.mSocket = socket;
        this.mHandler = handler;
        this.mStringBuffer = stringBuffer;
    }

    @Override
    public void run() {
        byte[] bytes = new byte[1024];
        InputStream inputStream = null;
        try {
            if (mSocket != null) {
                inputStream = mSocket.getInputStream();
                while (inputStream.read(bytes) != -1) {
                    // 使用.trim()去掉空字符，不然byte[]里面会包含很多空字符
                    mStringBuffer.append(new String(bytes, StandardCharsets.UTF_8).trim());
                    Message msg = mHandler.obtainMessage();
                    msg.what = 2;
                    msg.obj = mStringBuffer;
                    Log.e(TAG, "run: msg = " + msg.obj.toString());
                    mHandler.sendMessage(msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (mSocket != null) mSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
