package io.terminus.eaf.gal.proxy.server.proxy;


import io.terminus.eaf.gal.proxy.server.httpMessage.HttpRequestMessage;
import io.terminus.eaf.gal.proxy.server.utils.SocketUtil;

import java.net.Socket;

/**
 * 代理本地客户端
 * 
 * @author qq
 *
 */
public class ClientProxy extends Proxy {
	public ClientProxy(Socket clientSocket, Socket serverSocket, HttpRequestMessage requestMessage) {
		super(clientSocket, serverSocket, requestMessage);
	}

	@Override
	public void proxyHttps() {
		/*
		 * 将客户端数据发给服务端
		 */
		if (!SocketUtil.writeSocket(super.serverOutputStream, requestMessage, super.isEncryptRequest())) {
			return;
		}

		super.proxyHttps();
	}

}
