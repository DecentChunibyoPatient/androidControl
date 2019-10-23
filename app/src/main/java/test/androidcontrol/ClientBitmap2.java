package test.androidcontrol;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.FaceDetector;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by ibm on 2017/9/25.
 */

public class ClientBitmap2 extends Thread{
    String tag="ClientBitmap2";
    Socket socket;
    Handler handler;
    String host;
    int port;
    ChildServiceBitmap childService;
    CameraPreview cameraPreview;
    static int quality=60,BitmapH=60;
    boolean aBoolean=false;
    FaceDetector.Face[] face;
    int nFace;
    int f=0;

    public ClientBitmap2(Handler handler,String host, int port, CameraPreview cameraPreview){
        this.host=host;
        this.port=port;
        this.handler=handler;
        this.cameraPreview=cameraPreview;

    }
    @Override
    public void run() {
        try {
            System.out.println("T1");
            socket = new Socket(host, port);
            aBoolean=true;
            handler.sendEmptyMessage(1);
            childService=new ChildServiceBitmap(socket,cameraPreview);
            childService.start();
            byte bytes[] = new byte[1024*1024*2];
            int s=0;
            byte buffer[] = new byte[1024];
            int temp = 0;
            // 从InputStream当中读取客户端所发送的数据
            while ((temp = socket.getInputStream().read(buffer)) != -1&&aBoolean) {
                System.arraycopy(buffer, 0, bytes,s, temp);
                s=s+temp;
                if (bytes[s-2]==-1&&bytes[s-1]==-39){
                    Bitmap bitmap0 = BitmapFactory.decodeByteArray(bytes,0,s);
                    Matrix matrix = new Matrix();
                    matrix.setRotate(90,(float) bitmap0.getWidth() / 2, (float) bitmap0.getHeight() / 2);
                    Bitmap bitmap = Bitmap.createBitmap(bitmap0, 0, 0, bitmap0.getWidth(), bitmap0.getHeight(), matrix, true);
                    send(0,bitmap);
                    bytes = new byte[bytes.length];
                    s=0;
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
    void write(){
        if (aBoolean)
            if (childService.isStart) {
                childService.send=true;
            }
    }
    void stopBitmap(){
        if (childService!=null){
            childService.send=false;
        }
    }
    static void setQuality(int qualitys){
        quality=qualitys;
    }
    static void setBitmapH(int h){
       BitmapH=h;
    }
    void send(Bitmap bitmap){
        Message message = new Message();
        message.obj = bitmap;
        handler.sendMessage(message);
    }
    void send(int what,Bitmap bitmap){
            if((f++)>=0){
                f=0;
            detectFace(bitmap,1);
        }
        Message message = new Message();
        message.what = what;
        if (face!=null&&nFace>0){
            message.obj = draw(face,bitmap,nFace);
        }else {
            message.obj = bitmap;
        }
        handler.sendMessage(message);
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

    public void detectFace(Bitmap srcFace,int N_MAX){
        srcFace=srcFace.copy(Bitmap.Config.RGB_565, true);
        face = new FaceDetector.Face[N_MAX];
        nFace = new FaceDetector( srcFace.getWidth(), srcFace.getHeight(), N_MAX).findFaces(srcFace, face);
        Log.i(tag, "检测到人脸：n = " + nFace);
        //将绘制完成后的faceBitmap返回
    }

    public Bitmap draw(FaceDetector.Face[] face, Bitmap srcFace, int nFace){
        for(int i=0; i<nFace; i++){
            FaceDetector.Face f  = face[i];
            PointF midPoint = new PointF();
            float dis = f.eyesDistance();
            f.getMidPoint(midPoint);
            int dd = (int)(dis);
            Point eyeLeft = new Point((int)(midPoint.x - dis/2), (int)midPoint.y);
            Point eyeRight = new Point((int)(midPoint.x + dis/2), (int)midPoint.y);
            Rect faceRect = new Rect((int)(midPoint.x - dd), (int)(midPoint.y - dd), (int)(midPoint.x + dd), (int)(midPoint.y + dd));
            Log.i(tag, "左眼坐标 x = " + eyeLeft.x + "y = " + eyeLeft.y);
            if(checkFace(faceRect)){
                Canvas canvas = new Canvas(srcFace);
                Paint p = new Paint();
                p.setAntiAlias(true);
                p.setStrokeWidth(8);
                p.setStyle(Paint.Style.STROKE);
                p.setColor(Color.GREEN);

                canvas.drawCircle(eyeLeft.x, eyeLeft.y, 20, p);
                canvas.drawCircle(eyeRight.x, eyeRight.y, 20, p);
                canvas.drawRect(faceRect, p);
            }

        }
        //将绘制完成后的faceBitmap返回
        return srcFace;
    }
    public boolean checkFace(Rect rect){
        int w = rect.width();
        int h = rect.height();
        int s = w*h;
        Log.i(tag, "人脸 宽w = " + w + "高h = " + h + "人脸面积 s = " + s);
        if(s < 10000){
            Log.i(tag, "无效人脸，舍弃.");
            return false;
        }
        else{
            Log.i(tag, "有效人脸，保存.");
            return true;
        }
    }
}