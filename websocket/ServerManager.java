package com.example.websocket.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.java_websocket.WebSocket;

import android.util.Log;

/**
 * 
 */
public class ServerManager {
	private ServerSocket serverSocket = null;
	private Map<WebSocket, String> userMap = new HashMap<WebSocket, String>();

	public ServerManager() {

	}
	/**
	 * 账号登录时,通知所有人
	 * @param userName
	 * @param socket
	 */
	public void UserLogin(String userName, WebSocket socket) {
		if (userName != null || socket != null) {
			userMap.put(socket, userName);
			Log.i("TAG", "LOGIN:" + userName);
			SendMessageToAll(userName + "...Login...");
		}
	}
	/**
	 * 账号退出时,通知所有人
	 * @param socket
	 */
	public void UserLeave(WebSocket socket) {
		if (userMap.containsKey(socket)) {
			String userName = userMap.get(socket);
			Log.i("TAG", "Leave:" + userName);
			userMap.remove(socket);
			SendMessageToAll(userName + "...Leave...");
		}
	}
	/**
	 * 通知某个用户
	 * @param socket
	 * @param message
	 */
	public void SendMessageToUser(WebSocket socket, String message) {
		if (socket != null) {
			socket.send(message);
		}
	}
	/**
	 * 通知某个用户
	 * @param userName
	 * @param message
	 */
	public void SendMessageToUser(String userName, String message) {
		Set<WebSocket> ketSet = userMap.keySet();
		for (WebSocket socket : ketSet) {
			String name = userMap.get(socket);
			if (name != null) {
				if (name.equals(userName)) {
					socket.send(message);
					break;
				}
			}
		}
	}
	/**
	 * 通知所有用户
	 * @param message
	 */
	public void SendMessageToAll(String message) {
		Set<WebSocket> ketSet = userMap.keySet();
		for (WebSocket socket : ketSet) {
			String name = userMap.get(socket);
			if (name != null) {
				socket.send(message);
			}
		}
	}
	/**
	 * 启动
	 * @param port 端口
	 * @return
	 */
	public boolean Start(int port) {

		if (port < 0) {
			Log.i("TAG", "Port error...");
			return false;
		}

		Log.i("TAG", "Start ServerSocket...");

		//WebSocketImpl.DEBUG = false;
		try {
			serverSocket = new ServerSocket(this, port);
			serverSocket.start();
			Log.i("TAG", "Start ServerSocket Success...");
			return true;
		} catch (Exception e) {
			Log.i("TAG", "Start Failed...");
			e.printStackTrace();
			return false;
		}
	}

	public boolean Stop() {
		try {
			serverSocket.stop();
			Log.i("TAG", "Stop ServerSocket Success...");
			return true;
		} catch (Exception e) {
			Log.i("TAG", "Stop ServerSocket Failed...");
			e.printStackTrace();
			return false;
		}
	}

}
