package test.androidcontrol;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.AudioRecord;
import android.media.FaceDetector;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.SeekBar;

import java.net.Socket;

/**
 * Created by ibm on 2017/9/25.
 */

public class ChildServiceString extends Thread{
    SeekBar seekBarAudio,seekBarBitmap;
    VerticalSeekBar verticalSeekBarLeft,verticalSeekBarRight;
    Socket socket;
    boolean isStart = false;
    public ChildServiceString(final Socket socket, VerticalSeekBar verticalSeekBarLeft, VerticalSeekBar verticalSeekBarRight, SeekBar seekBarAudio, SeekBar seekBarBitmap) {
        this.socket = socket;
        this.verticalSeekBarLeft = verticalSeekBarLeft;
        this.verticalSeekBarRight = verticalSeekBarRight;
        this.seekBarAudio = seekBarAudio;
        this.seekBarBitmap = seekBarBitmap;
    }
    public void run() {
        isStart = true;
        int Left=0,Right=0;
        int audio=seekBarAudio.getProgress(),bitmap=seekBarBitmap.getProgress();
        try {
            while (isStart) {
                String string=null;

                String stringLeft=null;
                int verticalSeekBarLeftGetProgress=verticalSeekBarLeft.getProgress();
                if (Math.abs(verticalSeekBarLeftGetProgress-50)!=0){
                    Left++;
                    if (Left>=50-Math.abs(verticalSeekBarLeftGetProgress-50)){
                        stringLeft=""+(verticalSeekBarLeftGetProgress-50);
                        Left=0;
                    }
                }

                String stringRight=null;
                int verticalSeekBarRightGetProgress=verticalSeekBarRight.getProgress();
                if (Math.abs(verticalSeekBarRightGetProgress-50)!=0){
                    Right++;
                    if (Right>=50-Math.abs(verticalSeekBarRightGetProgress-50)){
                        stringRight=""+(verticalSeekBarRightGetProgress-50);
                        Right=0;
                    }
                }

                String ardunio=null;
                if (stringLeft!=null||stringRight!=null){
                    if (stringLeft==null){
                        stringLeft="0,0,";
                    }else {
                        stringLeft=stringLeft+",24,";
                    }
                    if (stringRight==null){
                        stringRight="0,0";
                    }else {
                        stringRight=stringRight+",24";
                    }
                    ardunio=stringLeft+stringRight;
                }

                if (ardunio!=null){
                    if (string==null){
                        string="ardunio="+ardunio;
                    }else {
                        string=string+";ardunio="+ardunio;
                    }
                }

                if (audio!=seekBarAudio.getProgress()){
                    audio=seekBarAudio.getProgress();
                    if (string==null){
                        string="seekBarAudio="+audio;
                    }else {
                        string=string+";seekBarAudio="+audio;
                    }
                }
                if (bitmap!=seekBarBitmap.getProgress()){
                    bitmap=seekBarBitmap.getProgress();
                    if (string==null){
                        string="seekBarBitmap="+bitmap;
                    }else {
                        string=string+";seekBarBitmap="+bitmap;
                    }
                }
                if (string!=null)
                    socket.getOutputStream().write(string.getBytes());
                Thread.sleep(1000/100);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 停止
     */
    public void stopRecord() {
        destroyThread();
    }
    /**
     * 销毁线程方法
     */
    void destroyThread() {
        isStart = false;
        try {
            if (null != this && Thread.State.RUNNABLE == this.getState()) {
                try {
                    Thread.sleep(500);
                    this.interrupt();
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}