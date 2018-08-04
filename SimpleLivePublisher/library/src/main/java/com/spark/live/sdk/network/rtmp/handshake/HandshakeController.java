package com.spark.live.sdk.network.rtmp.handshake;

import com.spark.live.sdk.util.LogUtil;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 握手协议实现
 * Created by devzhaoyou on 8/17/16.
 */

public class HandshakeController {

    private InputStream in;
    private OutputStream out;

    private CS0 requestC0, responseS0;
    private CS1 requestC1, responseS1;
    private CS2 requestC2, responseS2;

    public HandshakeController(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
    }

    public boolean handshake() {

        try {
            if (in != null && out != null) {
                versionSend();
                return ackSent();
            } else {
                LogUtil.e("Handshake: The IO Stream can not be used!!");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private void versionSend() throws Exception{

        /**send c0 c1*/
        requestC0 = new CS0();
        requestC1 = new CS1();
        out.write(requestC0.toBinary());
        out.write(requestC1.toBinary());
        LogUtil.i("Handshake: send C0 C1 to server C0: " +
                requestC0.toString() + " C1: " + requestC1.toString());

        /**receive s0 s1*/
        responseS0 = new CS0((byte) in.read());
        requestC0.setVersion(responseS0.getVersion());
        LogUtil.i("Handshake: receive S0 from server " + responseS0.toString());
        byte[] s1Array = new byte[CS1.REQUEST_LENGTH];
        int offset = 0;
        int remained = CS1.REQUEST_LENGTH;
        int actual;
        do {
            actual = in.read(s1Array, offset, remained);
            offset += actual;
            remained -= actual;
        } while (actual > 0 && remained > 0);
        if (offset != CS1.REQUEST_LENGTH) {
            LogUtil.e("Handshake: receive S1 error!");
            return;
        }

        responseS1 = new CS1(s1Array);
        LogUtil.i("Handshake: receive S1 form server S1: " + responseS1.toString());

        /**send c2*/
        requestC2 = new CS2();
        requestC2.setTime(responseS1.getTime());
        requestC2.setTime2(requestC1.getTime());
        requestC2.setRandomEcho(responseS1.getRandomData());
        out.write(requestC2.toBinary());
        LogUtil.i("Handshake: send C2 to server C2: " + requestC2.toString());
    }

    private boolean ackSent() throws Exception{
        /**receive S2*/
        byte[] s2Array = new byte[CS2.REQUEST_LENGTH];
        int offset = 0;
        int remained = CS2.REQUEST_LENGTH;
        int actual;
        do {
            actual = in.read(s2Array, offset, remained);
            offset += actual;
            remained -= actual;
        } while (actual > 0 && remained > 0);
        if (offset != CS2.REQUEST_LENGTH) {
            LogUtil.e("Handshake: receive S2 error!");
            return false;
        }

        responseS2 = new CS2(s2Array);
        LogUtil.i("Handshake: receive S2 from server S2: " + responseS2.toString());
        return true;
    }

    public CS0 getRequestC0() {
        return requestC0;
    }

    public CS0 getResponseS0() {
        return responseS0;
    }

    public CS1 getRequestC1() {
        return requestC1;
    }

    public CS1 getResponseS1() {
        return responseS1;
    }

    public CS2 getRequestC2() {
        return requestC2;
    }

    public CS2 getResponseS2() {
        return responseS2;
    }
}
