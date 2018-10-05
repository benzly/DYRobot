package com.pelucky.danmu;

import com.pelucky.danmu.message.Handler;
import com.pelucky.danmu.message.Looper;
import com.pelucky.danmu.message.Message;

import java.io.UnsupportedEncodingException;
import java.util.Timer;

public class Test {

    public static void main(String[] args) throws UnsupportedEncodingException {

        String srcString = ", 哦嚯!  粉丝牌亮瞎了我的钛合金眼!!!";

        System.out.println(utf2GBK(srcString));

    }

    public static String utf2GBK(String utf) {
        try {
            return new String(utf.getBytes("GBK"));//转换成gbk编码
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
