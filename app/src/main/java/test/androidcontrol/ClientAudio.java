package test.androidcontrol;

import android.media.AudioRecord;
import android.os.Handler;
import android.os.Message;

import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by ibm on 2017/9/25.
 */

public class ClientAudio extends Thread{
    Socket socket;
    Handler handler;
    String host;
    int port;
    ChildServiceAudio childService;
    static int h=1000;
    boolean aBoolean=false;
    int bufferSize;
    AudioRecord audioRecord;
    public ClientAudio(Handler handler, String host, int port,int bufferSize, AudioRecord audioRecord){
        this.host=host;
        this.port=port;
        this.handler=handler;
        this.bufferSize=bufferSize;
        this.audioRecord=audioRecord;
    }
    @Override
    public void run() {
        try {
            System.out.println("T1");
            socket = new Socket(host, port);
            aBoolean=true;
            handler.sendEmptyMessage(1);
            childService=new ChildServiceAudio(socket, bufferSize, audioRecord);
            childService.start();
            byte buffer[] = new byte[1024];
            int temp = 0;
            // 从InputStream当中读取客户端所发送的数据
            while ((temp = socket.getInputStream().read(buffer)) != -1&&aBoolean) {
                byte bytes[] = new byte[temp];
                System.arraycopy(buffer, 0, bytes,0, temp);
                send(0,bytes);
            }
            socket.close();
            handler.sendEmptyMessage(-1);
            aBoolean=false;
        }
        catch (ConnectException e1) {
            handler.sendEmptyMessage(-1);
            System.out.println("网络连接超时！！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void write(){
        System.out.println("write");
        if (aBoolean)
            System.out.println("write2");
            if (childService.isStart){
                System.out.println("write3");
                childService.send=true;
            }
    }
    void stopAudio(){
        if (childService!=null){
            childService.send=false;
        }
    }
    void setQuality(int h){
        this.h=h;
    }
    void send(byte[] bytes){
        Message message = new Message();
        message.obj = bytes;
        handler.sendMessage(message);
    }
    void send(int what,byte[] bytes){
        Message message = new Message();
        message.what = what;
        message.obj = bytes;
        handler.sendMessage(message);
    }
}