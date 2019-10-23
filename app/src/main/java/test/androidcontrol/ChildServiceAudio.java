package test.androidcontrol;

import android.media.AudioRecord;
import java.net.Socket;

/**
 * Created by ibm on 2017/9/25.
 */

public class ChildServiceAudio extends Thread{
    AudioRecord audioRecord;
    int bufferSize;
    Socket socket;
    boolean isStart = false,send = false;
    public ChildServiceAudio(final Socket socket,int bufferSize, AudioRecord audioRecord) {
        this.socket = socket;
        this.bufferSize = bufferSize;
        this.audioRecord = audioRecord;
    }
    public void run() {
        isStart = true;
        try {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
            int bytesRecord;
            //int bufferSize = 320;
            byte[] tempBuffer = new byte[bufferSize];

            if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
                stopRecord();
                return;
            }
            audioRecord.startRecording();
            //writeToFileHead();
            while (isStart) {
                if (null != audioRecord) {
                    bytesRecord = audioRecord.read(tempBuffer, 0, bufferSize);
                    if (bytesRecord == AudioRecord.ERROR_INVALID_OPERATION || bytesRecord == AudioRecord.ERROR_BAD_VALUE) {
                        continue;
                    }
                    if (bytesRecord != 0 && bytesRecord != -1) {
                        //在此可以对录制音频的数据进行二次处理 比如变声，压缩，降噪，增益等操作
                        //我们这里直接将pcm音频原数据写入文件 这里可以直接发送至服务器 对方采用AudioTrack进行播放原数据
                        calc1(tempBuffer,0,bytesRecord);
                        if (send){
                          try {
                              socket.getOutputStream().write(tempBuffer);
                          }catch (Exception e){
                              e.printStackTrace();
                          }
                        }

                    } else {
                        break;
                    }
                }
                Thread.sleep(1000/ClientAudio.h);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void calc1(byte[] lin,int off,int len) {
        int i,j;
        for (i = 0; i < len; i++) {
            j = lin[i+off];
            lin[i+off] = (byte)(j>>2);
        }
    }
    /**
     * 停止录音
     */
    public void stopRecord() {
        try {
            destroyThread();
            if (audioRecord != null) {
                if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                    audioRecord.stop();
                }
                if (audioRecord != null) {
                    audioRecord.release();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 销毁线程方法
     */
    void destroyThread() {
        isStart = false;
        send = false;
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
