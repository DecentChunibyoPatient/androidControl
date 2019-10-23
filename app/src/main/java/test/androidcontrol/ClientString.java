package test.androidcontrol;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.widget.SeekBar;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by ibm on 2017/9/25.
 */

public class ClientString extends Thread{
    Socket socket;
    Handler handler;
    String host;
    int port;
    boolean aBoolean=false;
    ChildServiceString childService;
    int l=0;
    byte[] bytes=bytes(1000);
    public ClientString(Handler handler,String host, int port){
        this.host=host;
        this.port=port;
        this.handler=handler;

    }

    @Override
    public void run() {
        try {
            System.out.println("T1");
            socket = new Socket(host, port);
            aBoolean=true;
            handler.sendEmptyMessage(1);
            byte buffer[] = new byte[1024];
            int temp = 0;
            // 从InputStream当中读取客户端所发送的数据
            while ((temp = socket.getInputStream().read(buffer)) != -1&&aBoolean) {
              String string=new String(buffer,0,temp);
               // System.out.println(string);
               for (String string1: string.split("[&]")){
                   String[] strings=string1.split("[=]");
                   if (strings[0].equals(MainActivity.spinner.getItemAtPosition(MainActivity.spinner.getSelectedItemPosition()))){
                       try {
                           //System.out.println( strings[1].substring(0,6));
                           float f=Float.parseFloat(strings[1].substring(0,6));
                           int i=(int)(255*(f+10)/20);

                           System.out.println( f+" "+(f+10));
                           if (l>=bytes.length){
                               displacement(bytes,(byte)(i&0xFF));
                           }else {
                               bytes[l]=(byte)(i&0xFF);
                               l++;
                           }
                           try {
                               send(2,draw(bytes));break;
                           }catch (Exception e){e.printStackTrace();}
                       }catch (Exception e){
                           e.printStackTrace();
                       }
                   }
                   if (false)
                   switch (strings[0]){
                       case "x":break;
                       case "y":break;
                       case "z":
                        try {
                           //System.out.println( strings[1].substring(0,6));
                            float f=Float.parseFloat(strings[1].substring(0,6));
                            int i=(int)(255*(f+10)/20);

                            System.out.println( f+" "+(f+10));
                            if (l>=bytes.length){
                                displacement(bytes,(byte)(i&0xFF));
                            }else {
                                bytes[l]=(byte)(i&0xFF);
                                l++;
                            }
                          try {
                              send(2,draw(bytes));break;
                          }catch (Exception e){e.printStackTrace();}
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                   }
               }
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
    void displacement(byte[] bytes,byte b){
        for(int i=0;i<bytes.length-1;i++){
            bytes[i]= bytes[i+1];
        }
        bytes[bytes.length-1]=b;
    }
    void write(String string) throws IOException {
        socket.getOutputStream().write(string.getBytes());
    }
    void send(byte[] bytes){
        Message message = new Message();
        message.obj = bytes;
        handler.sendMessage(message);
    }
    void send(int what,Object object){
        Message message = new Message();
        message.what = what;
        message.obj = object;
        handler.sendMessage(message);
    }
    public Bitmap draw(byte[] bytes){
        Bitmap bitmap=Bitmap.createBitmap(1000, 255, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStrokeWidth(1);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.BLACK);
        int spacing=1;
        canvas.drawLine(0,255/2+255%2,bytes.length*spacing,255/2+255%2,p);
        p.setColor(Color.GREEN);

        for (int i=0,s=0;i<bytes.length;i++,s+=spacing){
            if (i+spacing>=bytes.length){
                canvas.drawLine(s,bytes[i]&0xFF,s+spacing,255/2+255%2,p);
            }else {
                canvas.drawLine(s,bytes[i]&0xFF,s+spacing,bytes[i+1]&0xFF,p);
            }
        }

        //canvas.drawCircle(eyeLeft.x, eyeLeft.y, 20, p);
        //canvas.drawCircle(eyeRight.x, eyeRight.y, 20, p);
        //canvas.drawRect(faceRect, p);
        //将绘制完成后的faceBitmap返回
        return bitmap;
    }
    byte[]bytes(int length){
        byte[]bytes=new byte[length];
        for (int i=0;i<bytes.length;i++){
            bytes[i]=(byte) (255/2+255%2& 0xFF);
        }
        return bytes;
    }
}