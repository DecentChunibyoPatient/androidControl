package test.androidcontrol;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    AudioTrack audioTrack;
    CameraPreview cameraPreview;
    ImageView imageView;
    ImageView imageView3;
    EditText editText;
    ClientString clientString;
    ClientBitmap2 clientBitmap;
    ClientAudio clientAudio;
    AudioRecord audioRecord;
    int bufferSize;
    static  FrameLayout preview;
    SeekBar seekBarQuality,seekBarBitmap,seekBarThisQuality,seekBarThisBitmap;
    CheckBox checkBoxAudio,checkBoxBitmap;
    VerticalSeekBar verticalSeekBarLeft,verticalSeekBarRight;
    NumberPicker np5,np6;
    static Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner = (Spinner) findViewById(R.id.spinner);
        //数据
        ArrayList<String> data_list = new ArrayList<String>();
        data_list.add("x");
        data_list.add("y");
        data_list.add("z");
        data_list.add("x2");
        data_list.add("y2");
        data_list.add("z2");
        //适配器
        ArrayAdapter<String> arr_adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(arr_adapter);
        imageView3=(ImageView)findViewById(R.id.imageView3);
        imageView3.setImageBitmap(draw(bytes(1000)));
        System.out.println( (int) (((byte) (255 & 0xFF)) & 0xFF));
        TabHost host = (TabHost) findViewById(R.id.tabhost);
        host.setup();
        host.addTab(host
                .newTabSpec("t1")
                .setIndicator("t1", getResources().getDrawable(R.mipmap.ic_launcher))
                .setContent(R.id.sll01));
        host.addTab(host.newTabSpec("t2").setIndicator("t2")
                .setContent(R.id.sll02));
        host.addTab(host.newTabSpec("t3").setIndicator("t3")
                .setContent(R.id.sll03));
        NumberPicker.OnValueChangeListener onValueChangeListener=new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                try {
                    if (clientString!=null)
                        switch (numberPicker.getId()){
                            case R.id.numberPicker2: clientString.write("s="+numberPicker.getDisplayedValues()[numberPicker.getValue()]);break;
                            case R.id.numberPicker3: clientString.write("t="+numberPicker.getDisplayedValues()[numberPicker.getValue()]);break;
                            case R.id.numberPicker4: clientString.write("q="+numberPicker.getDisplayedValues()[numberPicker.getValue()]);break;
                            case R.id.numberPicker5: clientString.write("v="+np5.getDisplayedValues()[np5.getValue()]+"."+np6.getDisplayedValues()[np6.getValue()]);break;
                            case R.id.numberPicker6: clientString.write("v="+np5.getDisplayedValues()[np5.getValue()]+"."+np6.getDisplayedValues()[np6.getValue()]);break;
                        }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
        NumberPicker np2 = (NumberPicker) findViewById(R.id.numberPicker2);

        np2.setOnValueChangedListener(onValueChangeListener);
        setValue(np2,256,0,255);
        np2.setValue(np2.getMaxValue());
        NumberPicker np3 = (NumberPicker) findViewById(R.id.numberPicker3);
        np3.setOnValueChangedListener(onValueChangeListener);
        setValue(np3,255,1,255);
        NumberPicker np4 = (NumberPicker) findViewById(R.id.numberPicker4);
        np4.setOnValueChangedListener(onValueChangeListener);
        setValue(np4,150,1,150);
         np5 = (NumberPicker) findViewById(R.id.numberPicker5);
        np5.setOnValueChangedListener(onValueChangeListener);
        setValue(np5,19,-9,9);
        np5.setValue(np5.getMaxValue()/2);
         np6 = (NumberPicker) findViewById(R.id.numberPicker6);
        np6.setOnValueChangedListener(onValueChangeListener);
        setValue(np6,10,0,9);
        np6.setValue(np6.getMaxValue());

        editText=(EditText)findViewById(R.id.editText);
        imageView=(ImageView)findViewById(R.id.imageView);
        seekBarThisQuality=(SeekBar)findViewById(R.id.seekBarThisQuality);
        seekBarBitmap=(SeekBar)findViewById(R.id.seekBarBitmap);
        seekBarQuality=(SeekBar)findViewById(R.id.seekBarQuality);
        seekBarThisBitmap=(SeekBar)findViewById(R.id.seekBarThisBitmap);
        checkBoxAudio=(CheckBox)findViewById(R.id.checkBoxAudio);
        checkBoxBitmap=(CheckBox)findViewById(R.id.checkBoxBitmap);

        seekBarThisQuality.setMax(100);
        seekBarBitmap.setMax(200);
        seekBarQuality.setMax(100);
        seekBarThisBitmap.setMax(200);

        seekBarThisQuality.setProgress(ClientBitmap.h);
        seekBarBitmap.setProgress(ClientBitmap.h);
        seekBarQuality.setProgress(ClientBitmap.h);
        seekBarThisBitmap.setProgress(ClientBitmap.h);
        seekBarThisQuality.setOnSeekBarChangeListener(onSeekBarChangeListener);
        seekBarBitmap.setOnSeekBarChangeListener(onSeekBarChangeListener);
        seekBarQuality.setOnSeekBarChangeListener(onSeekBarChangeListener);
        seekBarThisBitmap.setOnSeekBarChangeListener(onSeekBarChangeListener);
        //录音类
        bufferSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize * 2);
        //播放类
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                android.media.AudioTrack.getMinBufferSize(8000,
                        AudioFormat.CHANNEL_CONFIGURATION_MONO,
                        AudioFormat.ENCODING_PCM_16BIT),
                AudioTrack.MODE_STREAM);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        cameraPreview=CameraInit(0);
        preview.addView(cameraPreview);
        //通讯按钮
        ((Button)findViewById(R.id.Call)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    if (clientAudio!=null)
                        clientAudio.stopAudio();
                    if (clientBitmap!=null)
                        clientBitmap.stopBitmap();

                }else if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    if (clientAudio!=null)
                        if (checkBoxAudio.isChecked()){
                            clientAudio.write();//发送语音
                        }
                    if (clientBitmap!=null)
                        if (checkBoxBitmap.isChecked()){
                            clientBitmap.write();//发送视频
                        }
                }

                return false;
            }
        });
        View.OnTouchListener onTouchListener=new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent motionEvent) {
                VerticalSeekBar verticalSeekBar=(VerticalSeekBar)view;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    System.out.println("ACTION_UP)");
                    verticalSeekBar.TouchEvent=false;
                    verticalSeekBar.setProgress(0x80);
                }else if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    verticalSeekBar.TouchEvent=true;
                    System.out.println("ACTION_DOWN)");
                }
                return false;
            }
        };

        //左按钮
        verticalSeekBarLeft=(VerticalSeekBar)findViewById(R.id.buttonLeft);
        verticalSeekBarLeft.setOnTouchListener(onTouchListener);
        verticalSeekBarLeft.setOnSeekBarChangeListener(onSeekBarChangeListener);

        //右按钮
        verticalSeekBarRight=(VerticalSeekBar)findViewById(R.id.buttonRight);
        verticalSeekBarRight.setOnTouchListener(onTouchListener);
        verticalSeekBarRight.setOnSeekBarChangeListener(onSeekBarChangeListener);

        //连接按钮
        ((Button)findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            boolean aBoolean=true;
            @Override
            public void onClick(View view) {
                String host=""+editText.getText();
                //串口通信
                if (aBoolean){
                    clientString=new ClientString(handlerString,host,8083);
                    clientString.start();
                }else {
                    clientString.aBoolean=false;
                    clientString=null;
                }
                //语音通讯
                if (aBoolean){
                    audioTrack.play();
                    clientAudio=new ClientAudio(handlerAudio,host,8081,bufferSize,audioRecord);
                    clientAudio.start();
                }else {
                    clientAudio.aBoolean=false;
                    clientAudio=null;

                }
                //视频通讯
                if (aBoolean){
                    clientBitmap=new ClientBitmap2(handlerBitmap2,host,8080,cameraPreview);
                    clientBitmap.start();
                }else {
                    clientBitmap.aBoolean=false;
                    clientBitmap=null;
                }
                aBoolean=!aBoolean;
                if (aBoolean){
                    ((Button)view).setText("Interrupt");
                }else {
                    ((Button)view).setText("Connect");
                }
            }
        });
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    void setValue(NumberPicker numberPicker,int lenth,int mix,int max){
        String[] strings=new String[lenth];
        for (int i=mix,s=strings.length-1;i<=max;i++,s--)
            strings[s]=i+"";
        numberPicker.setMaxValue(strings.length - 1);
        numberPicker.setMinValue(0);
        numberPicker.setDisplayedValues(strings);
       // numberPicker.setValue(max);
    }
    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener=new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            System.out.println("onProgressChanged "+i);
            try {
                switch (seekBar.getId()){
                    case R.id.buttonLeft:
                        /**
                         if (0x80>i){
                         clientString.write("Left="+Integer.toHexString(i/(0x80/10)));
                         }else if (0x80<i){
                         clientString.write("Left="+Integer.toHexString(0xff-(0xff-i)/(0x80/10)));
                         }else  clientString.write("Left="+Integer.toHexString(i));
                         */
                        clientString.write("Left="+Integer.toHexString(i));
                        break;
                    case R.id.buttonRight:
                        /**
                         if (0x80>i){
                         clientString.write("Right="+Integer.toHexString(1));
                         }else if (0x80<i){
                         clientString.write("Right="+Integer.toHexString(254));
                         }else  clientString.write("Right="+Integer.toHexString(i));
                         */
                        clientString.write("Right="+Integer.toHexString(i));
                        break;
                    case R.id.seekBarBitmap:
                        clientString.write("seekBarBitmap="+i);
                        break;
                    case R.id.seekBarQuality:
                        clientString.write("seekBarQuality="+i);
                        break;
                    case R.id.seekBarThisBitmap:
                        ClientBitmap2.setBitmapH(i);
                        System.out.println("BitmapH "+ClientBitmap2.BitmapH);
                        break;
                    case R.id.seekBarThisQuality:
                        ClientBitmap2.setQuality(i);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
                e.printStackTrace();
                System.out.println(""+clientString);
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    public CameraPreview CameraInit(int ID){
        CameraManager  mCameraManager = new CameraManager(this,ID);
        // Create our Preview view and set it as the content of our activity.
        return new CameraPreview(this, mCameraManager.getCamera());
    }
    /**
     * 接收返回的视频数据
     */
    Handler handlerBitmap=new Handler(){
        byte bytes[] = new byte[1024*1024*2];
        int s=0;
        @Override
        public void handleMessage(Message msg) {
           switch (msg.what){
               case 0:
                   byte[] bytes= (byte[]) msg.obj;
                   try {
                       int temp=bytes.length;
                       System.arraycopy(bytes, 0, this.bytes,s, temp);
                       s=s+temp;
                       if (bytes[temp-2]==-1&&bytes[temp-1]==-39){
                           Bitmap bm0 = BitmapFactory.decodeByteArray(this.bytes,0,s);
                           Matrix m = new Matrix();
                           m.setRotate(90,(float) bm0.getWidth() / 2, (float) bm0.getHeight() / 2);
                           Bitmap bm = Bitmap.createBitmap(bm0, 0, 0, bm0.getWidth(), bm0.getHeight(), m, true);
                           imageView.setImageBitmap(bm);
                           this.bytes = new byte[this.bytes.length];
                           s=0;
                       }
                   }catch (Exception e){
                       e.printStackTrace();
                       Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                   }
                   break;
               case -1:
                   break;
           }
        }
    };
    /**
     * 接收返回的视频数据
     */
    Handler handlerBitmap2=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Bitmap bitmap= (Bitmap) msg.obj;
                    imageView.setImageBitmap(bitmap);
                    break;
                case -1:
                    break;
            }
        }
    };
    /**
     * 接收返回的语音数据
     */
    Handler handlerAudio=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    byte[] bytes= (byte[]) msg.obj;
                    try {
                        //calc1(bytes,0,bytes.length);
                        audioTrack.write(bytes,0,bytes.length);
                    }catch (Exception e){
                        Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                    break;
                case -1:
                    break;
            }
        }
    };

    /**
     * 接收返回的串口数据
     */
    Handler handlerString=new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case 0: byte[] bytes= (byte[]) msg.obj;

                    break;
                case 1:

                    break;
                case 2:
                    imageView3.setImageBitmap((Bitmap)msg.obj);
                    break;
            }
           // textView.setText(""+new String(bytes));
        }
    };
    public Bitmap draw(byte[] bytes){
        Bitmap bitmap=Bitmap.createBitmap(1000, 255, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStrokeWidth(1);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.BLACK);
        canvas.drawLine(0,255/2+255%2,bytes.length,255/2+255%2,p);
        p.setColor(Color.GREEN);
        int spacing=4;
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
            bytes[i]=(byte) ((int)(255*Math.random())& 0xFF);
        }
        return bytes;
    }
}
