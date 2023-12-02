package com.imservices.im.bmm.utils;


import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.zip.GZIPInputStream;

public class FileProcess {

	public static void delFolder(String srcPath) throws Exception {
		File f = new File(srcPath);
		File[] file = f.listFiles();

		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				file[i].delete();
			}
			if (file[i].isDirectory()) {
				String sourceDir = srcPath + File.separator + file[i].getName();
				delFolder(sourceDir);
			}
		}
		if (f.listFiles().length == 0) {
			f.delete();
		}
	}

	/**
	 * 文件上传
	 * 
	 * @param file
	 * @param filename
	 * @param filepath
	 * @throws Exception
	 */
	public static void upload(File file, String filename, String filepath)
			throws Exception {
		File target = new File(filepath);
		if (!target.exists()) {
			target.mkdirs();
		}
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		FileOutputStream fos = new FileOutputStream(target + "/" + filename);
		byte[] b = new byte[1024];
		while (bis.read(b, 0, 1024) != -1) {
			fos.write(b);
			fos.flush();
		}
		fos.close();
		bis.close();
		fis.close();
	}

	/**
	 * 删除文件不删除所在的文件夹
	 * 
	 * @param filepath
	 */
	public static void del(String filepath) {
		try {
			File file = new File(filepath);
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除文件和所在的文件夹
	 * 
	 * @param filepath
	 * @throws Exception
	 */
	public static void delAndDir(String filepath) throws Exception {
		File file = new File(filepath);
		if (file.exists()) {
			file.delete();
		}
		File file2 = new File(filepath.replaceAll(file.getName(), ""));
		if (file2.exists()) {
			file2.delete();
		}
	}

	/**
	 * 下载文件到本地
	 * 
	 * @param urlString
	 *            被下载的文件地址
	 * @param filename
	 *            本地文件名
	 * @throws Exception
	 *             各种异常
	 */
	public static void download(String urlString, String filename)
			throws Exception {
		if (org.apache.commons.lang.StringUtils.isEmpty(urlString))
			return;
		String dir = filename.substring(0, filename.lastIndexOf("/"));
		File f = new File(dir);
		if (!f.exists()) {
			f.mkdirs();
		}
		// 构造URL
		//
		URL url = new URL(urlString);
		// 打开连接
		URLConnection con = url.openConnection();
		// 输入流
		InputStream is = con.getInputStream();

		String code = con.getHeaderField("Content-Encoding");
		//

		if ((null != code) && code.equals("gzip")) {
			GZIPInputStream gis = new GZIPInputStream(is);

			// 1K的数据缓冲
			byte[] bs = new byte[1024];
			// 读取到的数据长度
			int len;
			// 输出的文件流
			OutputStream os = new FileOutputStream(filename);
			// 开始读取
			while ((len = gis.read(bs)) != -1) {
				os.write(bs, 0, len);
			}
			// 完毕，关闭所有链接
			gis.close();
			os.close();
			is.close();

		} else {

			// 1K的数据缓冲
			byte[] bs = new byte[1024];
			// 读取到的数据长度
			int len;
			// 输出的文件流
			OutputStream os = new FileOutputStream(filename);
			// 开始读取
			while ((len = is.read(bs)) != -1) {
				os.write(bs, 0, len);
			}
			// 完毕，关闭所有链接
			os.close();
			is.close();
		}

	}

	public static void main(String[] args) throws Exception {
		// String[] urls = new String[] {
		// "http://a0.jmstatic.com/63376ba7c84b3352/popheadarrow01.png",
		// "http://a4.jmstatic.com/b81867418d6f27eb/brandwall.jpg",
		// "http://a0.jmstatic.com/f7a1f5bbe36c631f/brandprev.jpg",
		// "http://a4.jmstatic.com/fedd38afaa47e26b/brandnext.jpg",
		// "http://a0.jmstatic.com/2a78c5604ec1ff0f/tuijianad_bottombg.png",
		// "http://a1.jmstatic.com/c0dc4a1fbce7071e/shoug.png",
		// "http://a0.jmstatic.com/14ae394eb0ce04cc/ongoing_title.jpg",
		// "http://a4.jmstatic.com/2e4acf5cd0bb19fd/eric_countdown.png",
		// "http://a3.jmstatic.com/5e96438ab9ca188c/ongoing1.png",
		// "http://a3.jmstatic.com/ff2a7e483101b780/ongoing_rightarea_left.jpg",
		//"http://a1.jmstatic.com/be28fd46b5d74a47/ongoing_rightarea_right.jpg",
		// "http://a0.jmstatic.com/8a76b0e0d81cea34/guiji_bg_cat.jpg",
		// "http://a5.jmstatic.com/740e7b9758002d0b/sub_h2_background.jpg",
		// "http://a5.jmstatic.com/740e7b9758002d0b/sub_h2_background.jpg",
		// "http://a2.jmstatic.com/41de5f0f97d1b483/sub_a_background.jpg",
		// "http://a5.jmstatic.com/740e7b9758002d0b/sub_h2_background.jpg",
		// "http://a5.jmstatic.com/8c44828ae7f9db89/mall_abtest_pic.png",
		// "http://a3.jmstatic.com/49e05419896ab276/png24_001.png",
		// "http://a5.jmstatic.com/cf5af23ded66d166/ongoing_title_bg.jpg",
		// "http://a0.jmstatic.com/05f41ac6f54048d3/already_item_bg.jpg",
		// "http://a0.jmstatic.com/70e5eb71df1271d8/future_item_bg.jpg",
		//"http://a3.jmstatic.com/fab15cc142a58894/ongoing_future_title_bg.jpg",
		// "http://a5.jmstatic.com/473d5ba3cee938bb/ongoing_future_title_301_bg.jpg"
		// ,
		// "http://a4.jmstatic.com/34ef5b9b9f06306e/alert_phone.jpg",
		// "http://a1.jmstatic.com/640f90c864a678e2/mall_anchor_bar_1.png",
		// "http://a1.jmstatic.com/a3d195228691fb0e/mall_anchor_bar.png",
		// "http://a4.jmstatic.com/d21da1fe644cd044/home_bicon_v6.png",
		// "http://a2.jmstatic.com/36bfb02362a29ec6/eric_left_mall_index.png",
		// "http://a1.jmstatic.com/c78cff409f684974/eric_right_mall_index.png"
		// };
		// for (String url : urls) {
		// String fn = url.substring(url.lastIndexOf("/"), url.length());
		// download(url, "f:/d/" + fn);
		// }

//		fileChannelCopy(new File("f:/mw.jpg"), new File("e:/yyyyyyyyyyyy.jpg"));
//
	}

	/**
	 * 
	 * 使用文件通道的方式复制文件
	 * 
	 * 
	 * 
	 * @param s
	 * 
	 *            源文件
	 * 
	 * @param t
	 * 
	 *            复制到的新文件
	 */

	public static void fileChannelCopy(File s, File t) {

		FileInputStream fi = null;

		FileOutputStream fo = null;

		FileChannel in = null;

		FileChannel out = null;

		try {

			fi = new FileInputStream(s);

			fo = new FileOutputStream(t);

			in = fi.getChannel();// 得到对应的文件通道

			out = fo.getChannel();// 得到对应的文件通道

			in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				fi.close();

				in.close();

				fo.close();

				out.close();

			} catch (IOException e) {

				e.printStackTrace();

			}

		}

	}

	public static boolean fileExists(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			return false;// 如果存在输出结果
		} else {
			return true;
		}
	}

}
