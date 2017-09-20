package io.terminus.eaf.gal.proxy.server.threads.https;

import io.terminus.eaf.gal.proxy.server.utils.SocketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * 处理客户端转发Https数据给服务端线程类
 * 
 * 将客户端的Https数据流输出给服务端
 * 
 * @author qq
 * 
 */
public class ClientTranslateHttpsToServerThread extends Thread {
	private final static Logger log = LoggerFactory.getLogger(ClientTranslateHttpsToServerThread.class);

	/*
	 * Socket
	 */
	/**
	 * 客户端Socket
	 */
	private Socket clientSocket;

	/**
	 * 服务端Socket
	 */
	private Socket serverSocket;

	/*
	 * constructor
	 */
	private ClientTranslateHttpsToServerThread() {
	}

	public ClientTranslateHttpsToServerThread(Socket clientSocket, Socket serverSocket) {
		super();
		this.clientSocket = clientSocket;
		this.serverSocket = serverSocket;
	}

	@Override
	public void run() {
		/*
		 * 
		 */
		InputStream clientInputStream; // 客户端输入流
		OutputStream serverOutputStream;// 服务端输出流
		try {
			clientInputStream = clientSocket.getInputStream();
			serverOutputStream = serverSocket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		/*
		 * 
		 */
		int readByte;
		try {
			while ((readByte = clientInputStream.read()) != -1) {
				serverOutputStream.write(readByte);
			}
		} catch (SocketTimeoutException e) { // 读取Socket超时
			e.printStackTrace();
			SocketUtil.closeSocket(clientSocket, serverSocket);
		} catch (IOException e) {
			e.printStackTrace();
			SocketUtil.closeSocket(clientSocket, serverSocket);
		}
	}
}
