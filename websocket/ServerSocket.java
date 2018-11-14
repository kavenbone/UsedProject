package com.example.websocket.util;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import android.os.Environment;
import android.util.Log;
 
/**
 * 各种事件的处理<br>
 * 判断是哪一个命令,并做对应的处理
 */
public class ServerSocket extends WebSocketServer {
 
    private ServerManager _serverManager;
    FileMark mark=new FileMark();
    
    public ServerSocket(ServerManager serverManager,int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
        _serverManager=serverManager;
    }
 
 
 
 
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Log.i("TAG","Some one Connected...");
    }
 
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        _serverManager.UserLeave(conn);
    }
 
    @Override
    public void onMessage(WebSocket conn, String message) {
        Log.i("TAG","OnMessage:"+message.toString());
        
        String msg=message.toString();
        String[] result=msg.split(mark.separator);
        System.out.println("result len:"+result.length);
        
        if (result.length>=3) {//不能等于3,因为内容有可能是有分隔符的
        	System.out.println("指令的文件:"+result[1]);
        	File fil1=Environment.getExternalStorageDirectory();//
        	if(fil1.isDirectory()){
        		System.out.println("可以是文件夹");
        	}
        	File file=null;//
        	if(result[1].trim().equals("")){
        		file=new File(mark.getRootPath());//根目录 ,不存在就创建
        		if(!file.exists()){
        			file.mkdir();
        		}
        	}else{
        		file=new File(mark.getRootPath()+File.separator+result[1]);//
        	}
        	
            if (result[0].equals(FileMark.cmdReadFile)) {//读取文件
            	if(file.isDirectory()){//是文件夹,返回文件夹下的所有文件,不包含子文件
            		System.out.println("是文件夹");
            		String folders=mark.readFolder(file);
            		String returnTxt=mark.formatReturn(FileMark.cmdReadFolder, folders);
            		returnTxt=returnTxt.replace(mark.getRootPath()+File.separator, "");//去掉根目录
            		_serverManager.SendMessageToUser(conn,returnTxt);
            	}else if(file.isFile()){//是文件
            		System.out.println("是文件");
            		String txt=mark.readFile(file,"UTF-8");
            		String returnTxt=mark.formatReturn(FileMark.cmdReadFile, txt);
            		_serverManager.SendMessageToUser(conn,returnTxt);
            	}else{
            		System.out.println("不是文件夹");
            	}
            	
            }else if(result[0].equals(FileMark.cmdWriteFile)){//写入文件
            	String context=msg.replace(result[0]+mark.separator+result[1]+mark.separator, "");//防止文件内容中有mark.separator
            	
            	String readFile=replaceBlank(mark.readFile(file,"UTF-8"));//防止保存错,判断前N位相同
            	readFile=readFile.length()>10?readFile.substring(0, 10):"";
            	String contextPre10=replaceBlank(context);
            	contextPre10=contextPre10.length()>10?contextPre10.substring(0, 10):"";
            	
            	if(readFile.equals("")||readFile.equals(contextPre10)){
            		int re=mark.writeFile(file,context);
                	String txt=mark.formatReturn(FileMark.cmdWriteFile, re+"");
                	_serverManager.SendMessageToUser(conn,txt);
            	}else{
            		String txt=mark.formatReturn(FileMark.cmdWriteFile, "是否保存错文件?");
                	_serverManager.SendMessageToUser(conn,txt);
            	}
            }else if(result[0].equals(FileMark.cmdCreateFile)){//创建文件
            	int re=mark.createFile(file);
            	String txt=mark.formatReturn(FileMark.cmdCreateFile, re+"");
            	_serverManager.SendMessageToUser(conn,txt);
            }else if(result[0].equals(FileMark.cmdCreateFolder)){//创建文件夹
            	int re=mark.createFolder(file);
            	String txt=mark.formatReturn(FileMark.cmdCreateFolder, re+"");
            	_serverManager.SendMessageToUser(conn,txt);
            }else{
            	System.out.println("什么命令都不是");
            }
        }
    }
    
    @Override
    public void onError(WebSocket conn, Exception ex) {
        Log.i("TAG","Socket Exception:"+ex.toString());
    }
 
    @Override
    public void onStart() {
 
    }
    
    ///////////////辅助方法====================
    //去掉回车,换行等字符
    protected String replaceBlank(String str){
    	String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
}

