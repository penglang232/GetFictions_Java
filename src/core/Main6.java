package core;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * http://www.88dushu.cc 小说下载
 * https://www.baka.cc/ 小说下载
 * https://www.xncwxw.net/ 
 * 
 * @author 彭琅
 *
 */

public class Main6 {
	
//	private static final String url_charset = "GBK";
//	private static final String charset = "GBK";
	private static final String charset = "UTF-8";
//	private static final String charset = "ISO-8859-1";

	public static String url = "http://www.biquge.info/52_52726/";
	
	public static int c_index = 1;
	
	public static FileWriter writer = null;
	
	public static int retry = 0;
	
	public static final int MAX_RETRY = 10;
	
//	public static Proxy proxy = null;
	
	static {
		//创建代理服务器
//        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("221.224.156.82", 8080));
        //设置代理的用户名密码
//        Authenticator.setDefault(new MyAuth("用户名", "密码"));
        // 设定连接的相关参数
//        URL url = new URL(locationUrl);
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
	}
	
	public static void main(String[] args){
//		System.setProperty("http.maxRedirects", "50");  
//        System.getProperties().setProperty("proxySet", "true");  
//        // 如果不设置，只要代理IP和代理端口正确,此项不设置也可以  
//        String ip = "221.224.156.82";  
//        System.getProperties().setProperty("http.proxyHost", ip);  
//        System.getProperties().setProperty("http.proxyPort", "80");  
          
        //确定代理是否设置成功  
//        try {
//			System.out.println((getDocument("http://baidu.com")));
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		} 
		
		long start = System.currentTimeMillis();
		try {
			getFiction(url);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(writer!=null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
		}
		System.out.println("exec time " + (System.currentTimeMillis() - start) + " ms");
	}
	
	private static void getFiction(String url) throws Exception {
		// 获取目录内容
		String introduce = getDocument(url);
		System.out.println(introduce);
		introduce = introduce.replaceAll("&nbsp;", "");
		String mainInfo = introduce.substring(introduce.indexOf("maininfo"), introduce.indexOf("intro"));
//		System.out.println(mainInfo);
		
		// 获取小说名及作者
		String title = mainInfo.substring(mainInfo.indexOf("h1")+3, mainInfo.indexOf("</"));
		writer = new FileWriter(title + ".txt",true);
//		System.out.println(title);
		String auth = mainInfo.substring(mainInfo.indexOf("作者")+3, mainInfo.indexOf("</p"));
//		System.out.println(auth);
		
		// 第一次时写入小说名及作者
		if(c_index == 1) {
			writer.write(title);
			writer.write("\r\n");
			writer.write("\r\n");
			writer.write(auth);
			writer.write("\r\n");
		}
		
		// 获取章节名称及对应url
		String chapters = introduce.substring(introduce.indexOf("list2"), introduce.indexOf("footer_cont"));
//		System.out.println(chapters);
		String[] chapterList = chapters.split("href");
		String c_path = "";
		String c_name = "";
		String c ="";
		// 获取章节具体内容
		for(int i=c_index;i<chapterList.length;i++) {
			retry = 0;
			c = chapterList[i];
			c_path = c.substring(c.indexOf("\"")+1,c.lastIndexOf("html")+4);
			c_name = c.substring(c.indexOf(">")+1,c.indexOf("</"));
			System.out.println(url + c_path +" " + c_name);

			// 写入章节名
			writer.write(c_name);
			writer.write("\r\n");
			// 获取写入章节内容
			try {
				getAndSaveFiction(url + c_path);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("【"+i+"】:"+c_name+" 下载失败,正在重试");
				for(;retry<MAX_RETRY;retry++) {
					try {
						getAndSaveFiction(url + c_path);
						break;
					} catch (Exception e1) {
						System.out.println("【"+i+"】:"+c_name+" 第"+(retry+1)+"重试失败");
					}
					// 等待2秒再重试
					Thread.sleep(2000);
				}
				if(retry >= MAX_RETRY) {
					throw e;
				}
			}

			System.out.println("【"+i+"】:"+c_name+" 下载完成！");
			Thread.sleep(2000);
		}
	}

	public static String getDocument(String strURL) throws IOException
	{
	    URL url = new URL(strURL);  
//		HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);  
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
		conn.setReadTimeout(5000);
//	    conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
	    conn.addRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
	    conn.addRequestProperty("Accept-Encoding", "gzip, deflate");
	    conn.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
	    conn.addRequestProperty("User-Agent", "Mozilla");
	    conn.addRequestProperty("Content-Type", "text/html; charset=GBK");
//	    conn.addRequestProperty("Content-Type", "text/html; charset="+url_charset);
	    //conn.addRequestProperty("Referer", "google.com");

	    //System.out.println("Request URL ... " + url);

	    boolean redirect = false;

	    // 通常, 3xx 代表重定向
	    int status = conn.getResponseCode();
	    if (status != HttpURLConnection.HTTP_OK && 
    		(status == HttpURLConnection.HTTP_MOVED_TEMP
            || status == HttpURLConnection.HTTP_MOVED_PERM
            || status == HttpURLConnection.HTTP_SEE_OTHER)) {
	        redirect = true;
	    }

//	    System.out.println("Response Code ... " + status);
	    if (redirect) {

	        // 从header "location" 获取重定向url
	        String newUrl = conn.getHeaderField("Location");

	        // 获取cookie
	        String cookies = conn.getHeaderField("Set-Cookie");

	        // 重新打开连接
	        conn = (HttpURLConnection) new URL(newUrl).openConnection();
	        conn.setRequestProperty("Cookie", cookies);
	        conn.addRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
		    conn.addRequestProperty("Accept-Encoding", "gzip, deflate");
		    conn.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
		    conn.addRequestProperty("User-Agent", "Mozilla");
	       // System.out.println("Redirect to URL : " + newUrl);

	    }
	    BufferedReader bufReader = null;
	    
	    String buf;
		try {
			InputStreamReader input = new InputStreamReader(conn.getInputStream(), charset);
			bufReader = new BufferedReader(input);  
			String line = "";  
			StringBuilder contentBuf = new StringBuilder();  
			while ((line = bufReader.readLine()) != null) { 
			    contentBuf.append(line);  
			}  
			buf = contentBuf.toString();
		} catch (Exception e) {
			throw e;
		} finally {
			bufReader.close();
		}
	    return buf;
	}
	
	public static void getAndSaveFiction(String index) throws IOException
	{
		// 获取章节内容
		String buf = getDocument(index);
		
		System.out.println(buf.lastIndexOf("content")+15);
		System.out.println(buf.lastIndexOf("bottem")-22);
		String fictionContent = buf.substring(buf.lastIndexOf("content")+15,buf.lastIndexOf("bottem")-22);
		fictionContent = fictionContent.replace("&nbsp;", "").replace("<br />", "\r\n");
	    System.out.println(fictionContent);
	    writer.write(fictionContent);
	    writer.write("\r\n");
	    writer.write("\r\n");
	}
	
	
	public static void printStrByte(String str) {
		try {
			byte[] bytes = str.getBytes(charset);
			StringBuffer sb = new StringBuffer();
			for(byte b:bytes) {
				sb.append(Integer.toHexString(b&0xff)).append(" ");
			}
			System.out.println(sb.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
