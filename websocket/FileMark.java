package com.example.websocket.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.os.Environment;

/**
 * 命令:命令:路径:内容<br>
 * 所以命令路径都不能有:
 * 返回值:命令:内容
 * @author 
 *
 */
public class FileMark {
	public static String cmdReadFile="read_file";//读取文件
	public static String cmdReadFolder="read_folder";//读取文件夹列表
	public static String cmdWriteFile="write_file";//写文件
	public static String cmdCreateFile="create_file";//创建文件
	public static String cmdCreateFolder="create_folder";//创建文件夹
	
	/*
	 * <!-- 在SD卡中创建与删除文件权限 --> <uses-permission
	 * android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/> <!--
	 * 向SD卡写入数据权限 --> <uses-permission
	 * android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
	 */

	String separator=":";//分隔符:
	// 获取当前sdcard的工作目录
	String rootPath = Environment.getExternalStorageDirectory()+File.separator+"recordVN";
/*
//记录当前跟径
        //是文件夹,返回列表,file.isDirectory(),
        //是文件,返回文本,读取文件,String fileName = file.getName();
        //保存:保存文件
        //创建文件夹,创建文件,移动只能靠手机,另一个页面?
        //获取 /storage/emulated/0
        //Environment.getExternalStorageDirectory().getPath()
        //https://blog.csdn.net/chenjiang2936/article/details/51181244
        String folder="";
        File sdDir = Environment.getExternalStorageDirectory();
        File path = new File(sdDir+File.separator+folder);//
        path.isDirectory();//是文件夹
        File[] files = path.listFiles();// 读取文件夹下文件
        files[0].getPath();

 */

	/**
	 * 
	 * https://blog.csdn.net/chenjiang2936/article/details/51181244<br>
	 * 获取路径的例子<br>
	 * Environment.getExternalStorageDirectory().getPath()<br>
	 * 返回: /storage/emulated/0
	 * getApplicationContext().getFilesDir().getAbsolutePath();<br>
	 * 返回: /data/data/com.example.fileoperation/files<br>
	 */
	public void getPathEg(){
		System.out.println(Environment.getExternalStorageDirectory().getPath());
		//System.out.println(getApplicationContext().getFilesDir().getAbsolutePath());
	}
	/**
	 * 获取内存根目录路径<br>
	 * /storage/emulated/0/recordVN<br>
	 * 不能带/,因为后面接文件为空时,有问题
	 * @return
	 */
	public String getRootPath(){
		return rootPath;
	}
	/**
	 * 因为指令使用:分隔,所以也使用:分隔
	 * @param file
	 * @return
	 */
	public String readFolder(File file){
		if(!file.exists()){//不存在
			return "";
		}
		String result="";
		File[] list=file.listFiles();
		for(File item:list){
			result+=item.getAbsolutePath()+this.separator;
		}
		return result;
	}
	
	/**
	 * 创建文件,文件已存在返回0
	 * @param file
	 * @return
	 */
	public int createFile(File file) {
		try {
			boolean bool=file.createNewFile();
			if(bool){
				return 1;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 创建文件夹
	 * @param file
	 * @return
	 */
	public int createFolder(File file) {
		boolean bool=file.mkdir();
		if(bool){
			return 1;
		}
		return 0;
	}
	/**
	 * File myfile = new File(sdcard, "File.txt");
	 * @param file 
	 * @param encode "UTF-8"
	 * @return
	 */
	public String readFile(File file,String encode) {
		String result = "";
		File myfile = file;
		try {
			FileInputStream fis = new FileInputStream(myfile);
			InputStreamReader isr = new InputStreamReader(fis, encode);
			//方法一:多出N个空字符
//			char[] input = new char[fis.available()];
//			isr.read(input);
//			String in = new String(input);
//			result=in;
			
			StringBuffer str = new StringBuffer("");
			BufferedReader br = new BufferedReader(isr);
			String data = "";
			while ((data = br.readLine()) != null) {
				str.append(data + "\r\n");
			}
			//\r,\n,length()判断不了换行,只能去掉最后一个换行,最后一行不为空,会多一行
			//使用readLine+\r\n,必然会多一行回车的
			int index=str.lastIndexOf("\r\n");
			if(index>-1){
				str.delete(index, str.length());
			}
			result=str.toString();
			
			
			System.out.println(result);
			isr.close();
			fis.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 格式化返回值
	 * @param cmd 命令
	 * @param context 内容
	 * @return
	 */
	public String formatReturn(String cmd,String context){
		return cmd+this.separator+context;
	}
	/**
	 * File myfile=new File(sdcard,"File.txt");
	 * @param file
	 * @param txt
	 * @return 0失败,1成功,2文件不存在
	 */
	public int writeFile(File file,String txt) {
		File myfile=file;
        try {
            myfile.createNewFile();
            FileOutputStream fos=new FileOutputStream(myfile);
            OutputStreamWriter osw=new OutputStreamWriter(fos);
            osw.write(txt);
            osw.flush();
            osw.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
	}
}
