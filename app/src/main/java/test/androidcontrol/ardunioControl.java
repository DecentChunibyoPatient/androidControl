package test.androidcontrol;

/**
 * Created by ibm on 2017/9/26.
 */

public class ardunioControl {
    final static String string="0,0,0,0";


    final static String s=
            "00000" +
            "00000" +
                    "00000" +
                    "00000" +

                    "00100" +
                    "00010" +

                    "00000" +
                    "00000" +

                    "00000" +
                    "00000" +
                    "00";//52
    static String StringToTtlLeft(String string,int a,int b,int c){
        //22 24 26
        if (string.length()==s.length()){
            string=new StringBuffer(string).replace(22,22+1,""+a).toString();
            string=new StringBuffer(string).replace(24,24+1,""+b).toString();
            string=new StringBuffer(string).replace(26,26+1,""+c).toString();
            return string;
        }
        return null;
    }
    static String StringToTtlRight(String string,int a,int b,int c){
        //28 32 34
        if (string.length()==s.length()){
            string=new StringBuffer(string).replace(28,28+1,""+a).toString();
            string=new StringBuffer(string).replace(32,32+1,""+b).toString();
            string=new StringBuffer(string).replace(32,32+1,""+c).toString();
            return string;
        }
        return null;
    }
}
