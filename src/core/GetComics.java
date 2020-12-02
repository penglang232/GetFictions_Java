package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * https://www.36mh.com/
 * 
 * @author 彭琅
 *
 */

public class GetComics {
	
//	private static final String charset = "gbk";
	private static final String charset = "utf-8";

	public static String url = "https://www.36mh.com/manhua/moshifanren/";
	
	public static int c_index = 1;
	
	public static FileWriter writer = null;
	
	public static int retry = 0;
	
	public static final int MAX_RETRY = 10;
	
	public static void main(String[] args){
		long start = System.currentTimeMillis();
		try {
			getComics(url);
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
	
	private static void getComics(String url) throws Exception {
		String introduce = getDocument(url);
//		System.out.println(introduce);
		String mainInfo = introduce.substring(introduce.indexOf("book-title"), introduce.indexOf("detail-list"));
		mainInfo = mainInfo.substring(mainInfo.indexOf("span")+5,mainInfo.indexOf("</span"));
//		System.out.println(mainInfo);
		
		if(c_index == 1) {
			File f = new File(mainInfo);
			if(!f.exists()) {
				f.mkdir();
			}
		}
		
		// 获取章节名称及对应url
		String chapters = introduce.substring(introduce.lastIndexOf("chapter-list"), introduce.indexOf("comment-bar"));
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
			String[] sArr = c_path.split("/");
			c_path = sArr[sArr.length-1];
			c_name = c.substring(c.indexOf("span>")+5,c.indexOf("</span"));
			System.out.println(url + c_path +" " + c_name);
//			System.out.println(c_name);

			// 写入章节名
//			writer.write(c_name);
//			writer.write("\r\n");
			// 获取写入章节内容
			try {
				getAndSaveComics(url + c_path);
			} catch (Exception e) {
				System.out.println(c_name+" 下载失败,正在重试");
				for(;retry<MAX_RETRY;retry++) {
					try {
						getAndSaveComics(url + c_path);
						break;
					} catch (Exception e1) {
						System.out.println(c_name+" 第"+(retry+1)+"重试失败");
						// 等待2秒再重试
						Thread.sleep(2000);
					}
					
				}
				if(retry >= MAX_RETRY) {
					throw e;
				}
			}
		}
	}

	public static String getDocument(String strURL) throws IOException
	{
	    URL url = new URL(strURL);  
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
		conn.setReadTimeout(5000);
	    conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
	    conn.addRequestProperty("User-Agent", "Mozilla");
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

	    //System.out.println("Response Code ... " + status);
	    if (redirect) {

	        // 从header "location" 获取重定向url
	        String newUrl = conn.getHeaderField("Location");

	        // 获取cookie
	        String cookies = conn.getHeaderField("Set-Cookie");

	        // 重新打开连接
	        conn = (HttpURLConnection) new URL(newUrl).openConnection();
	        conn.setRequestProperty("Cookie", cookies);
	        conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
	        conn.addRequestProperty("User-Agent", "Mozilla");
	        //conn.addRequestProperty("Referer", "google.com");
	       // System.out.println("Redirect to URL : " + newUrl);

	    }
	    InputStreamReader input = new InputStreamReader(conn.getInputStream(), charset);
	    BufferedReader bufReader = new BufferedReader(input);  
	    String line = "";  
	    StringBuilder contentBuf = new StringBuilder();  	
	    while ((line = bufReader.readLine()) != null) {  
	        contentBuf.append(line);  
	    }  
	    String buf = contentBuf.toString();
//	    System.out.println(strURL);
//	    System.out.println(buf);
	    return buf;
	}
	
	public static void getAndSaveFiction(String index) throws IOException
	{
		// 获取章节内容
		String buf = getDocument(index);
		
		String fictionContent = buf.substring(buf.indexOf("read")+6,buf.lastIndexOf("tb")-18);
		fictionContent = fictionContent.replace("<p>", "").replace("</p>", "\r\n");
//	    System.out.println(fictionContent);
	    writer.write(fictionContent);
	    writer.write("\r\n");
	    writer.write("\r\n");
	}
	
	public static void getAndSaveComics(String index) throws IOException
	{
		// 获取章节内容
		String buf = getDocument(index);
		
		System.out.println(buf);
//		String fictionContent = buf.substring(buf.indexOf("read")+6,buf.lastIndexOf("tb")-18);
//		fictionContent = fictionContent.replace("<p>", "").replace("</p>", "\r\n");
	}

}
