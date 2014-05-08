/**
 * 缓存文件读写工具
 */
package com.autotiming.csck.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

public class FileUtil {

	private static FileUtil _fileUtil = new FileUtil();

	public static FileUtil getInstance() {
		return _fileUtil;
	}

	/**
	 * 是否存在
	 * 
	 * @param filepath
	 * @return
	 */
	public boolean isExist(String filepath) throws IOException {
		File file = new File(filepath);
		return file.isFile();
	}

	/**
	 * 删除缓存文件及目录
	 * 
	 * @param deleteThisPath
	 * @param filepath
	 * @return
	 */
	public void deleteFolderFile(String filePath, boolean deleteThisPath)
			throws IOException {
		if (!TextUtils.isEmpty(filePath)) {
			File file = new File(filePath);

			if (file.isDirectory()) {// 处理目录
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteFolderFile(files[i].getAbsolutePath(), true);
				}
			}
			if (deleteThisPath) {
				if (!file.isDirectory()) {// 如果是文件，删除
					file.delete();
				} else {// 目录
					if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
						file.delete();
					}
				}
			}
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param filePath
	 * @throws IOException
	 */
	public void deleteFile(String filePath) {
		try {
			File file = new File(filePath);
			if (!isExist(filePath)) {
				return;
			}
			if (!file.isDirectory()) {// 如果是文件，删除
				file.delete();
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 移动文件到指定目录,必须同在一个磁盘
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt 文件名可不带，但必须以‘/’结尾
	 * @return boolean
	 */
	public void moveFile(String oldPath, String newPath) throws IOException {
		File oldFile = new File(oldPath);
		if (!oldFile.isFile()) {
			return;
		}
		File newFile = new File(newPath);
		if (!newFile.isFile()) {
			String path = newPath.substring(0, newPath.lastIndexOf("/"));
			File dirFile = new File(path);
			if(!dirFile.exists())dirFile.mkdirs();
			if(newPath.endsWith("/"))newFile = new File(dirFile.getAbsolutePath() + File.separator
					+ oldFile.getName());
		}
		oldFile.renameTo(newFile);
	}

	/**
	 * 读取写入文件
	 * 
	 * @param oldPath
	 * @param newPath
	 */

	public void copyFileToWrite(String oldPath, String newPath)
			throws IOException {
		FileInputStream fins = null;
		FileOutputStream fous = null;
		try {
			if (isExist(oldPath)) {
				File oldfile = new File(oldPath);
				fins = new FileInputStream(oldfile);

				File newfile = new File(newPath);
				if (!newfile.isFile()) {
					String path = newPath
							.substring(0, newPath.lastIndexOf("/"));
					File dirFile = new File(path);
					dirFile.mkdirs();
					newfile.createNewFile();
				}

				fous = new FileOutputStream(newfile);
				byte[] buffer = new byte[4096];
				int length = 0;
				while ((length = fins.read(buffer)) != -1) {
					fous.write(buffer, 0, length);
				}

				fins.close();
				fous.close();
			}
		} finally {
			if (fins != null) {
				try {
					fins.close();
				} catch (IOException e) {

				}
			}
			if (fous != null) {
				try {
					fous.close();
				} catch (IOException e) {

				}
			}
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param filePathAndName
	 *            String 文件路径及名称 如c:/fqf.txt
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public void delFile(String filePathAndName) {
		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			java.io.File myDelFile = new java.io.File(filePath);
			myDelFile.delete();

		} catch (Exception e) {
			System.out.println("删除文件操作出错 ");
			e.printStackTrace();

		}

	}

	/**
	 * 新建目录
	 * 
	 * @param folderPath
	 *            String 如 c:/fqf
	 * @return boolean
	 */
	public boolean newFolder(String folderPath) {
		try {
			// String filePath = folderPath;
			// filePath = folderPath.toString();
			File myFilePath = new File(folderPath);
			if (!myFilePath.exists()) {
				myFilePath.mkdirs();
			}
			return true;
		} catch (Exception e) {
			DLog.e("FileUtil","新建目录操作出错 ");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 读取文件内容
	 * 
	 * @param filepath
	 * @return
	 */
	public String readFileByString(Context context, String filePath)
			throws IOException {
		if (!isExist(filePath)) {
			return null;
		}

		File file = new File(filePath);
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		StringBuffer xml = new StringBuffer();
		try {
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis, "UTF-8");
			char[] b = new char[4096];
			for (int n; (n = isr.read(b)) != -1;) {
				xml.append(new String(b, 0, n));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return xml.toString();
	}

	public String readByteByString(byte[] y) throws IOException {
		StringBuilder sb = new StringBuilder();
		InputStreamReader isr = null;
		try {
			int length;
			char[] b = new char[4096];
			isr = new InputStreamReader(new ByteArrayInputStream(y), "UTF-8");
			while ((length = isr.read(b)) != -1) {
				sb.append(new String(b, 0, length));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			if (isr != null) {
				isr.close();
			}
		}
		return sb.toString();
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param filePath
	 * @return
	 */
	public boolean isFileExist(String filePath) {
		try {
			File f = new File(filePath);
			if (f.exists() && f.isFile()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 读取文件内容
	 * 
	 * @param filepath
	 * @return
	 * @throws IOException
	 */
	public byte[] readFileByByte(String filePath) throws IOException {

		if (!isExist(filePath)) {
			return null;
		}

		File file = new File(filePath);
		int length = (int) file.length();
		byte content[] = new byte[length];
		FileInputStream fis = new FileInputStream(file);
		fis.read(content, 0, length);
		fis.close();

		return content;
	}

	/**
	 * 内容写入文件
	 * 
	 * @param context
	 * @param filepath
	 * @param data
	 */
	public void writeFileByString(String filePath, String data)
			throws IOException {
		FileOutputStream fOut = null;
		OutputStreamWriter osw = null;
		File file = new File(filePath);

		try {
			if (!file.isFile()) {
				String path = filePath.substring(0, filePath.lastIndexOf("/"));
				File dirFile = new File(path);
				dirFile.mkdirs();
				file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut = new FileOutputStream(file);
			osw = new OutputStreamWriter(fOut, "UTF-8");
			osw.write(data);
			osw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				osw.close();
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/***/
	public void writeinFileByString(String filePath, String data)
			throws IOException {
		FileOutputStream fOut = null;
		OutputStreamWriter osw = null;
		File file = new File(filePath);

		try {
			if (file.exists()) {
				file.delete();
				file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut = new FileOutputStream(file);
			osw = new OutputStreamWriter(fOut, "UTF-8");
			osw.write(data);
			osw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				osw.close();
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 内容写入文件
	 * 
	 * @param context
	 * @param filepath
	 * @param data
	 */
	public void writeFileByByte(String filePath, byte content[])
			throws IOException {
		FileOutputStream fOut = null;
		File file = new File(filePath);

		try {
			if (!file.isFile()) {
				String path = filePath.substring(0, filePath.lastIndexOf("/"));
				File dirFile = new File(path);
				dirFile.mkdirs();
				file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut = new FileOutputStream(file);
			fOut.write(content, 0, content.length);
			fOut.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 从安装包中读取数据
	 * 
	 * @param context
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public String readFileFromLocal(Context context, String filePath)
			throws IOException {

		AssetManager assets = context.getAssets();
		InputStream is = assets.open(filePath);

		InputStreamReader isr = null;
		BufferedReader br = null;
		StringBuffer xml = new StringBuffer();
		try {
			isr = new InputStreamReader(is, "UTF-8");
			char[] b = new char[4096];
			for (int n; (n = isr.read(b)) != -1;) {
				xml.append(new String(b, 0, n));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return xml.toString();
	}

	/**
	 * 调用系统第三方应用打开文件
	 * @param context 上下文
	 * @param filePath 文件路径
	 */
	public void openFile(Context context, String filePath) {
		if(TextUtils.isEmpty(filePath)){
			DLog.w("openFile", "filePath is empty");
			return;
		}
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			Uri uri = Uri.parse("file://" + filePath);
			
			String mimeType = null;
			String extension = null;
			if(filePath.contains(".")){
				extension = filePath.substring(filePath.lastIndexOf(".")+1);
			}
			DLog.i("openFile", "extension of "+filePath+" is "+extension);
			mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
			DLog.i("openFile","mimeType is "+mimeType);
			
			if(TextUtils.isEmpty(mimeType)){
				mimeType = "*/" + (TextUtils.isEmpty(extension) ? "*" : extension);
			}
			
			intent.setDataAndType(uri, mimeType);
			context.startActivity(intent);

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "无法识别该文件类型", Toast.LENGTH_SHORT).show();
		}
	}

	// private String getMIMEType(File file) {
	// String[][] MIME_MapTable={
	// //{后缀名，MIME类型}
	// {".3gp", "video/3gpp"},
	// {".apk", "application/vnd.android.package-archive"},
	// {".asf", "video/x-ms-asf"},
	// {".avi", "video/x-msvideo"},
	// {".bin", "application/octet-stream"},
	// {".bmp", "image/bmp"},
	// {".c", "text/plain"},
	// {".class", "application/octet-stream"},
	// {".conf", "text/plain"},
	// {".cpp", "text/plain"},
	// {".doc", "application/msword"},
	// {".docx",
	// "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
	// {".xls", "application/vnd.ms-excel"},
	// {".xlsx",
	// "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
	// {".exe", "application/octet-stream"},
	// {".gif", "image/gif"},
	// {".gtar", "application/x-gtar"},
	// {".gz", "application/x-gzip"},
	// {".h", "text/plain"},
	// {".htm", "text/html"},
	// {".html", "text/html"},
	// {".jar", "application/java-archive"},
	// {".java", "text/plain"},
	// {".jpeg", "image/jpeg"},
	// {".jpg", "image/jpeg"},
	// {".js", "application/x-javascript"},
	// {".log", "text/plain"},
	// {".m3u", "audio/x-mpegurl"},
	// {".m4a", "audio/mp4a-latm"},
	// {".m4b", "audio/mp4a-latm"},
	// {".m4p", "audio/mp4a-latm"},
	// {".m4u", "video/vnd.mpegurl"},
	// {".m4v", "video/x-m4v"},
	// {".mov", "video/quicktime"},
	// {".mp2", "audio/x-mpeg"},
	// {".mp3", "audio/x-mpeg"},
	// {".mp4", "video/mp4"},
	// {".mpc", "application/vnd.mpohun.certificate"},
	// {".mpe", "video/mpeg"},
	// {".mpeg", "video/mpeg"},
	// {".mpg", "video/mpeg"},
	// {".mpg4", "video/mp4"},
	// {".mpga", "audio/mpeg"},
	// {".msg", "application/vnd.ms-outlook"},
	// {".ogg", "audio/ogg"},
	// {".pdf", "application/pdf"},
	// {".png", "image/png"},
	// {".pps", "application/vnd.ms-powerpoint"},
	// {".ppt", "application/vnd.ms-powerpoint"},
	// {".pptx",
	// "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
	// {".prop", "text/plain"},
	// {".rc", "text/plain"},
	// {".rmvb", "audio/x-pn-realaudio"},
	// {".rtf", "application/rtf"},
	// {".sh", "text/plain"},
	// {".tar", "application/x-tar"},
	// {".tgz", "application/x-compressed"},
	// {".txt", "text/plain"},
	// {".wav", "audio/x-wav"},
	// {".wma", "audio/x-ms-wma"},
	// {".wmv", "audio/x-ms-wmv"},
	// {".wps", "application/vnd.ms-works"},
	// {".xml", "text/plain"},
	// {".z", "application/x-compress"},
	// {".zip", "application/x-zip-compressed"},
	// {"", "*/*"}
	// };
	// String type="*/*";
	// String fName = file.getName();
	// //获取后缀名前的分隔符"."在fName中的位置。
	// int dotIndex = fName.lastIndexOf(".");
	// if(dotIndex < 0){
	// return type;
	// }
	// /* 获取文件的后缀名*/
	// String end=fName.substring(dotIndex,fName.length()).toLowerCase();
	// if(end=="")return type;
	// //在MIME和文件类型的匹配表中找到对应的MIME类型。
	// for(int i=0;i<MIME_MapTable.length;i++){
	// //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
	// if(end.equals(MIME_MapTable[i][0]))
	// type = MIME_MapTable[i][1];
	// }
	// return type;
	// }

	public String setUploadFileName(String filename) {

		if (filename == null) {
			return null;
		}
		String newName = null;
		String tempName = filename;

		if (filename.lastIndexOf("/") > -1) {
			newName = tempName.substring(filename.lastIndexOf("/") + 1,
					filename.length());
			System.out.println("newName" + newName);
		}
		return newName;
	}

	public static String getFileExtension(String filePath) {
		if (filePath == null) {
			return null;
		}
		String newName = null;
		String tempName = filePath.trim();

		if (filePath.indexOf(".") > -1) {
			newName = tempName.substring(filePath.lastIndexOf(".") + 1,
					filePath.length());
		}
		return newName;
	}

	public String setFileName(String filename) {

		if (filename == null) {
			return null;
		}
		String newName = null;
		String tempName = filename.trim();

		if (filename.indexOf(".") > -1) {
			String names1 = tempName.substring(0, filename.lastIndexOf("."));
			String names2 = tempName.substring(filename.lastIndexOf(".") + 1,
					filename.length());
			newName = Integer.toString(names1.hashCode()) + "." + names2;
			System.out.println("names1" + names1);
			System.out.println("names1" + names2);
			System.out.println("newName" + newName);
		}
		return newName;
	}

	
	
	public boolean isFileImageType(String filename){
		if (filename == null) {
			return false;
		}
		List<String> imageTypes = new ArrayList<String>(5);
		imageTypes.add("jpg");
		imageTypes.add("png");
		imageTypes.add("gif");
		imageTypes.add("jpeg");
		imageTypes.add("psd");
		imageTypes.add("bmp");
		try {
			if (filename.indexOf(".") > -1) {
				String names[] = filename.split("\\.");
				String tempName = names[names.length - 1];
				// 图片

				String str = tempName.toLowerCase();
				if (imageTypes.contains(str)) {
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 文件大小单位转换
	 * 
	 * @param size
	 * @return
	 */
	public String setFileSize(long size) {
		DecimalFormat df = new DecimalFormat("###.##");
		float f = ((float) size / (float) (1024 * 1024));

		if (f < 1.0) {
			float f2 = ((float) size / (float) (1024));

			return df.format(new Float(f2).doubleValue()) + "K";

		} else {
			return df.format(new Float(f).doubleValue()) + "M";
		}
	}
	
	/**
	 * 用户空间容量转换，GB单位以下只显示整数，否则显示"x.xxG"样式
	 * @param space
	 * @return
	 */
	public String setCapacityInfo(long space){
		DecimalFormat df_GB = new DecimalFormat("###.##");
		double dGB = space / (1024*1024*1024.0); //单位GB
		double dMB = space / (1024*1024.0); //单位MB
		
		if (dGB < 1.0) {
			if (dMB < 1.0) {
				return String.valueOf(space/1024) + "K";
			}else {
				return String.valueOf((long)dMB) + "M";
			}
		} else {
			return df_GB.format(dGB) + "G";
		}
	}

	/**
	 * 获取当前系统时间
	 * 
	 * @return String
	 */
	public static String getTimeString() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd-HH-mm-ss");
		java.util.Date nowDate = new java.util.Date();
		String date = sDateFormat.format(nowDate);
		return date;
	}
	
	/**
	 * 获取文件夹大小
	 * @param file File实例
	 * @return long 单位为M
	 * @throws Exception
	 */
	public static long getFolderSize(java.io.File file)throws Exception{
		long size = 0;
        java.io.File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++)
        {
            if (fileList[i].isDirectory())
            {
                size = size + getFolderSize(fileList[i]);
            } else
            {
                size = size + fileList[i].length();
            }
        }
        return size/1048576;
	}
	

}
