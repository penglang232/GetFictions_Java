package core;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JTextArea;

import ui.MainFrame;
import util.ShowUtils;

/**
 * http://www.88dushu.cc 小说下载
 * 
 * @author 彭琅
 *
 */

public class Main3 {
	
//	private static final String charset = "gbk";
	private static final String charset = "utf-8";

	public static String url = "http://www.88dushu.cc/shu/108";
	
	public static int c_index = 1;
	
	public static FileWriter writer = null;
	
	public static void main(String[] args){
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
	}
	
	private static void getFiction(String url) throws Exception {
		// 获取目录内容
		String introduce = getDocument(url);
		String mainInfo = introduce.substring(introduce.indexOf("maininfo"), introduce.indexOf("right"));
//		System.out.println(mainInfo);
		
		// 获取小说名及作者
		String title = mainInfo.substring(mainInfo.indexOf("h1")+3, mainInfo.indexOf("</"));
		writer = new FileWriter(title + ".txt",true);
//		System.out.println(title);
		String auth = mainInfo.substring(mainInfo.indexOf("<span")+6, mainInfo.indexOf("</span"));
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
		String chapters = introduce.substring(introduce.lastIndexOf("chapter"), introduce.indexOf("<footer"));
		//System.out.println(chapters);
		String[] chapterList = chapters.split("href");
		String c_path = "";
//		String c_name = "";
		String c =chapterList[c_index];
		c_path = c.substring(c.indexOf("-"),c.lastIndexOf("\""));
//		c_name = c.substring(c.indexOf(">")+1,c.indexOf("</"));
//		System.out.println(url + c_path +" " + c_name);

		// 获取写入章节内容
		getAndSaveFiction(url + c_path);
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

		// 写入章节名
		String title = buf.substring(buf.indexOf("<h1>"), buf.indexOf("</h1>"));
		title = title.replace("<h1>", "");
		System.out.println(title);
		writer.write(title);
		writer.write("\r\n");
		
		String fictionContent = buf.substring(buf.indexOf("read")+6,buf.lastIndexOf("tb")-18);
		fictionContent = fictionContent.replace("<p>", "").replace("</p>", "");
//	    System.out.println(fictionContent);
	    writer.write(fictionContent);
	    writer.write("\r\n");
	    writer.write("\r\n");
	    
	    String nextIndex = getNextIndex(buf);
		if(nextIndex!=null)
		{
			getAndSaveFiction(url+nextIndex);
		}
	}

	private static String getNextIndex(String buf) {
		String nextIndex = buf.substring(buf.indexOf("章节目录"), buf.indexOf("下一章"));
		int splitIndex = nextIndex.indexOf("-");
		if(splitIndex < 0) {
			return null;
		}
		nextIndex = nextIndex.replace("href=\"", "");
		nextIndex = nextIndex.substring(nextIndex.indexOf("-"),nextIndex.indexOf("\""));
		return nextIndex;
	}
	
}
