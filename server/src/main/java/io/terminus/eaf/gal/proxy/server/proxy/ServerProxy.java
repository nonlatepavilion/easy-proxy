package io.terminus.eaf.gal.proxy.server.proxy;

import io.terminus.eaf.gal.proxy.server.constants.HttpResponseStatus;
import io.terminus.eaf.gal.proxy.server.httpMessage.HttpRequestMessage;
import io.terminus.eaf.gal.proxy.server.httpMessage.HttpResponseMessage;
import io.terminus.eaf.gal.proxy.server.utils.SocketUtil;

import java.net.Socket;

/**
 * 代理服务端
 *
 * @author qq
 */
public class ServerProxy extends Proxy {

    public ServerProxy(Socket clientSocket, Socket serverSocket, HttpRequestMessage requestMessage) {
        super(clientSocket, serverSocket, requestMessage);
    }

    @Override
    public void proxyHttps() {
        /*
		 * 响应客户端Web隧道建立成功
		 */
        HttpResponseMessage httpResponseMessage = new HttpResponseMessage(HttpResponseStatus._200);
        httpResponseMessage.addHeader("Connection", "close");
        SocketUtil.writeSocket(super.clientOutputStream, httpResponseMessage, super.isEncryptResponse());

        super.proxyHttps();
    }

}
