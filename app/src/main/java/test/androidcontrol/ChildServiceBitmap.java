package test.androidcontrol;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;

import java.io.ByteArrayOutputStream;
import java.net.Socket;

/**
 * Created by ibm on 2017/9/25.
 */

public class ChildServiceBitmap extends Thread{
    boolean isStart = false,send = false;
    private Socket socket;
    CameraPreview mPreview;
    public ChildServiceBitmap(final Socket socket, CameraPreview mPreview) {
        this.socket = socket;
        this.mPreview = mPreview;
    }
    public void run() {
        isStart=true;
        while(isStart) {
            try {
                if (send){
                    byte[] bytess=mPreview.getImageBuffer();
                    YuvImage image = new YuvImage(bytess, ImageFormat.NV21, mPreview.getPreviewWidth(), mPreview.getPreviewHeight(), null);
                    ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
                    image.compressToJpeg(new Rect(0, 0,  mPreview.getPreviewWidth(), mPreview.getPreviewHeight()), ClientBitmap2.quality, outputstream);
                    byte[] bytes = outputstream.toByteArray();
                    socket.getOutputStream().write(bytes);
                }


            } catch (Exception e) {
                System.out.println("close");
                isStart=false;
                send=false;
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000/ClientBitmap2.BitmapH);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isStart=false;
        send=false;
    }
    public void stopRecord() {
        isStart=false;
        send=false;
    }
}
