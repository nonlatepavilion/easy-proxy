package io.terminus.eaf.gal.proxy.server.threads;

import io.terminus.eaf.gal.proxy.server.constants.Properties;
import io.terminus.eaf.gal.proxy.server.constants.RequestMethod;
import io.terminus.eaf.gal.proxy.server.httpMessage.HttpRequestMessage;
import io.terminus.eaf.gal.proxy.server.httpMessage.exception.BuildHttpMessageError;
import io.terminus.eaf.gal.proxy.server.httpMessage.exception.ConnectServerError;
import io.terminus.eaf.gal.proxy.server.httpMessage.startLine.RequestStartLine;
import io.terminus.eaf.gal.proxy.server.proxy.Proxy;
import io.terminus.eaf.gal.proxy.server.proxy.ServerProxy;
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
 * 
 */
public class HttpProxyThread extends Thread {
	private final static Logger log = LoggerFactory.getLogger(HttpProxyThread.class);

	/*
	 * 客户端相关
	 */
	/**
	 * 客户端Socket
	 */
	private Socket clientSocket;

	/**
	 * 客户端输入流
	 */
	private InputStream clientInputStream;

	/*
	 * 服务端相关
	 */
	/**
	 * 服务端Socket
	 */
	private Socket serverSocket;

	/*
	 * constructor
	 */
	private HttpProxyThread() {
	}

	public HttpProxyThread(Socket clientSocket) {
		super();
		this.clientSocket = clientSocket;
		try {
			this.clientInputStream = clientSocket.getInputStream();
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
			requestMessage = new HttpRequestMessage(clientInputStream);
		} catch (BuildHttpMessageError e1) {
			e1.printStackTrace();

			SocketUtil.closeSocket(clientSocket, serverSocket);
			return;
		}
//		requestMessage = (HttpRequestMessage) requestMessage.decryptHttpMessage(); // 解密

		// 请求方法
		RequestMethod httpRequestMethod = ((RequestStartLine) requestMessage.getStartLine()).getMethod();

		System.out.println("[原始请求:]");
		System.out.print(requestMessage);
		System.out.println("----------");

		/*
		 * 连接服务器
		 */
		String hostHeader = requestMessage.getHeader("host"); // 如果没有Host首部，则从Url中解析
		String host = hostHeader;
		int port = 80; // 默认端口
		if (RequestMethod.CONNECT == httpRequestMethod) { // https
			port = 443;
		}
		if (hostHeader.contains(":")) {
			String[] hostArr = hostHeader.split(":");
			host = hostArr[0];
			port = Integer.parseInt(hostArr[1]);
		}

		/*
		 * 
		 */
		try {
			this.serverSocket = SocketUtil.connectServer(host, port, Properties.ServerConnectTimeout);
		} catch (ConnectServerError e) {
			e.printStackTrace();

			SocketUtil.closeSocket(clientSocket, serverSocket);
			return;
		}

		if (RequestMethod.CONNECT != httpRequestMethod) { // 代理http
			try {
				this.clientSocket.setSoTimeout(Properties.ClientSoceketReadTimeout); // 设置读取浏览器Socket超时时间
				this.serverSocket.setSoTimeout(Properties.ServerSocketReadTimeout); // 设置读取代理服务器Socket超时时间
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}

		/*
		 * 转发流量
		 */
		Proxy proxy = new ServerProxy(clientSocket, serverSocket, requestMessage);
		proxy.proxy();
	}

}
