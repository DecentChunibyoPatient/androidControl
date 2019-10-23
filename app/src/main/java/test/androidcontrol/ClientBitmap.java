package test.androidcontrol;

import android.os.Handler;
import android.os.Message;

import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by ibm on 2017/9/25.
 */

public class ClientBitmap extends Thread{
    Socket socket;
    Handler handler;
    String host;
    int port;
    ChildServiceBitmap childService;
    CameraPreview cameraPreview;
    static int quality=60,h=60;
    boolean aBoolean=false;
    public ClientBitmap(Handler handler,String host, int port, CameraPreview cameraPreview){
        this.host=host;
        this.port=port;
        this.handler=handler;
        this.cameraPreview=cameraPreview;
        childService=new ChildServiceBitmap(socket,cameraPreview);
    }
    @Override
    public void run() {
        try {

            System.out.println("T1");
            socket = new Socket(host, port);
            aBoolean=true;
            handler.sendEmptyMessage(1);
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
        if (aBoolean)
            if (!childService.isStart) {
                MainActivity.preview.addView(cameraPreview);
                childService.send=true;
            }
    }
    void stopBitmap(){
        if (childService!=null){
            MainActivity.preview.removeAllViews();
            childService.send=false;
        }
    }
    void setQuality(int quality,int h){
        this.quality=quality;
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