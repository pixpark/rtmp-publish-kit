package com.spark.live.sdk.network.rtmp;

import android.text.TextUtils;

import com.spark.live.sdk.network.rtmp.handshake.HandshakeController;
import com.spark.live.sdk.util.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Created by devzhaoyou on 8/17/16.
 */

public abstract class RTMPKnight {

    private static final int DEFAULT_PORT = 1935;
    private static final int DEFAULT_TIME_OUT = 5000;
    private static final String REGEX = "^rtmp://([^/:]+)(:(\\d+))*/([^/]+)(/(.*))*$";
    protected Socket envoy;
    protected InputStream envoyIn;
    protected OutputStream envoyOut;
    protected IKnightSword dragonSword;
    //TODO...暂不使用
    /*private SocketChannel channel;*/

    private Pattern pattern = Pattern.compile(REGEX);
    protected String rtmpURL;
    protected String app, stream;
    protected String host;

    private final Object lock = new Object();

    private IRTMPCallback callback;
    public RTMPKnight(String rtmpURL, IRTMPCallback callback) {
        this.rtmpURL = rtmpURL;
        this.callback = callback;
    }

    /**
     * 初始化
     * @return true 初始化完成 false 初始化结束
     */
    protected boolean init() {
        return createRtmpEnvoy() && doRtmpHandshake();
    }

    public boolean createRtmpEnvoy() {

        try {
            Matcher matcher = pattern.matcher(rtmpURL);
            if (matcher.matches()) {

                final int port = TextUtils.isEmpty(matcher.group(3)) ? DEFAULT_PORT : Integer.valueOf(matcher.group(3));
                app = matcher.group(4);
                stream = matcher.group(6);
                host = matcher.group(1);
                InetSocketAddress envoyRoad = new InetSocketAddress(host, port);
                envoy = new Socket();
                int envoyChance = 5;
                boolean result;
                do {
                    envoyChance--;
                    envoy.connect(envoyRoad, DEFAULT_TIME_OUT);
                    result = envoy.isConnected();
                    envoyIn = envoy.getInputStream();
                    envoyOut = envoy.getOutputStream();
                    /*channel = envoy.getChannel();*/
                } while (!result && envoyChance > 0);

                if (result) {
                    LogUtil.i("RTMPKnight: Socket has connected to server after " + (5 - envoyChance) + " times try");
                    if (callback != null) {
                        callback.onCreateSocket();
                    }
                } else {
                    LogUtil.e("RTMPKnight: Socket has failed to connect to server after "
                            + (5 - envoyChance) + " times try please check your device's net setting!");
                    if (callback != null) {
                        callback.onError("RTMPKnight: Socket has failed to connect to server after "
                                + (5 - envoyChance) + " times try please check your device's net setting!");
                    }
                }
                return result;
            } else {
                LogUtil.e("RTMPKnight： 非法RTMP URL ！");
                if (callback != null) {
                    callback.onError("RTMPKnight： 非法RTMP URL ！");
                }
                return false;
            }

        } catch (IOException e) {
            if (callback != null) {
                callback.onError(e.getMessage());
            }
            return false;
        }
    }

    public boolean doRtmpHandshake() {
        if (envoy != null && envoy.isConnected()) {
            HandshakeController handshaker = new HandshakeController(envoyIn, envoyOut);
            boolean result = handshaker.handshake();
            if (result) {
                if (callback != null) {
                    callback.onHandshake();
                }
            } else {
                if (callback != null) {
                    callback.onError("RTMPKnight: rtmp handshake failed!");
                }
            }
            return result;
        } else {
            LogUtil.e("RTMPKnight: The Socket can not be used please call createRtmpEnvoy() first!");
            if (callback != null) {
                callback.onError("RTMPKnight: The Socket can not be used please call createRtmpEnvoy() first!");
            }
            return false;
        }
    }

}
