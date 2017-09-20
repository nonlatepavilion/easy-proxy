package io.terminus.eaf.gal.proxy.client.threads;

import io.terminus.eaf.gal.proxy.client.constants.Properties;
import io.terminus.eaf.gal.proxy.server.constants.RequestMethod;
import io.terminus.eaf.gal.proxy.server.httpMessage.HttpRequestMessage;
import io.terminus.eaf.gal.proxy.server.httpMessage.exception.BuildHttpMessageError;
import io.terminus.eaf.gal.proxy.server.httpMessage.exception.ConnectServerError;
import io.terminus.eaf.gal.proxy.server.httpMessage.startLine.RequestStartLine;
import io.terminus.eaf.gal.proxy.server.proxy.ClientProxy;
import io.terminus.eaf.gal.proxy.server.proxy.Proxy;
import io.terminus.eaf.gal.proxy.server.utils.SocketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * Http代理线程
 *
 * @author qq
 */
public class HttpProxyThread extends Thread {
    private final static Logger log = LoggerFactory.getLogger(HttpProxyThread.class);

	/*
     * 浏览器相关
	 */
    /**
     * 浏览器Socket
     */
    private Socket browserSocket;

    /**
     * 浏览器输入流
     */
    private InputStream browserInputStream;

	/*
	 * 代理服务器相关
	 */
    /**
     * 代理服务器Socket
     */
    private Socket proxyServerSocket;

    /*
     * constructor
     */
    private HttpProxyThread() {
    }

    public HttpProxyThread(Socket browserSocket) {
        this.browserSocket = browserSocket;
        try {
            this.browserInputStream = browserSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
		/*
		 * 解析客户端请求
		 */
        HttpRequestMessage requestMessage;
        try {
            requestMessage = new HttpRequestMessage(browserInputStream);
        } catch (BuildHttpMessageError e1) {
            e1.printStackTrace();
            SocketUtil.closeSocket(browserSocket, proxyServerSocket);
            return;
        }

        System.out.println("[原始请求:]");
        System.out.print(requestMessage);
        System.out.println("----------");

		/*
		 * 连接代理服务器
		 */
        try {
            this.proxyServerSocket = SocketUtil.connectServer(Properties.ServerIp, Properties.ServerPot,
                    Properties.ServerConnectTimeout);
        } catch (ConnectServerError e) {
            e.printStackTrace();
            SocketUtil.closeSocket(browserSocket, proxyServerSocket);
            return;
        }

        RequestMethod httpRequestMethod = ((RequestStartLine) requestMessage.getStartLine()).getMethod();
        if (RequestMethod.CONNECT != httpRequestMethod) { // 代理http
            try {
                this.browserSocket.setSoTimeout(Properties.ClientSocketReadTimeout); // 设置读取浏览器Socket超时时间
                this.proxyServerSocket.setSoTimeout(Properties.ServerSocketReadTimeout); // 设置读取代理服务器Socket超时时间
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

		/*
		 * 转发流量
		 */
        Proxy proxy = new ClientProxy(browserSocket, proxyServerSocket, requestMessage);
//        proxy.setEncryptRequest(true);// 客户端请求加密,服务端应解密请求
//        proxy.setDecryptResponse(true); // 解密响应
        proxy.proxy();
    }

}
