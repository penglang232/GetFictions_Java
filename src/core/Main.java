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

public class Main {
	
	private static final String charset = "gbk";
//	private static final String charset = "utf-8";

	//public static String url = "http://www.88dus.com";
	
	public static String full_url = "https://www.x88dushu.com/xiaoshuo/60/60527/16193609.html";
	
	public static String fiction_name = "深海战神";

	public static String url = "http://www.x88dushu.com";
	
	public static String fiction_path = "/xiaoshuo/93/93293/";
	
	public static String chapter_index = "30778732";
	
	public static boolean is_read_title = false;
	
	
	public static void main(String[] args){
		url = full_url.substring(0,full_url.indexOf("xiaoshuo")-1);
		fiction_path = full_url.substring(full_url.indexOf("xiaoshuo")-1,full_url.lastIndexOf('/')+1);
		chapter_index = full_url.substring(full_url.lastIndexOf('/')+1,full_url.indexOf("html")-1);
		
//		System.out.println(url + " " + fiction_path + " " + chapter_index);
		try {
			getAndSaveFiction(chapter_index + ".html");
			System.out.println("Done!");
			//MainFrame.getInstance().printLog("Done!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Toolkit.getDefaultToolkit().beep();
		//System.out.println('\007');
//		ShowUtils.infoMessage("《"+fiction_name + "》 下载结束！");
	}
	
	public static String getDocument(String index) throws IOException
	{
		String strURL = url + fiction_path +index;  
	    URL url = new URL(strURL);  
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
		conn.setReadTimeout(5000);
	    conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
	    conn.addRequestProperty("User-Agent", "Mozilla");
	    //conn.addRequestProperty("Referer", "google.com");

	    //System.out.println("Request URL ... " + url);

	    boolean redirect = false;

	    // normally, 3xx is redirect
	    int status = conn.getResponseCode();
	    if (status != HttpURLConnection.HTTP_OK && 
    		(status == HttpURLConnection.HTTP_MOVED_TEMP
            || status == HttpURLConnection.HTTP_MOVED_PERM
            || status == HttpURLConnection.HTTP_SEE_OTHER)) {
	        redirect = true;
	    }

	    //System.out.println("Response Code ... " + status);

	    if (redirect) {

	        // get redirect url from "location" header field
	        String newUrl = conn.getHeaderField("Location");

	        // get the cookie if need, for login
	        String cookies = conn.getHeaderField("Set-Cookie");

	        // open the new connnection again
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
		String buf = getDocument(index);
		if(!is_read_title) {
			String title = buf.substring(buf.indexOf("title"), buf.indexOf("/title"));
			fiction_name = title.split("-")[1].trim();
			is_read_title = true;
		}
		FileWriter writer = new FileWriter(fiction_name + ".txt",true);
		String title = buf.substring(buf.indexOf("<h1>"), buf.indexOf("</h1>"));
		title = title.replace("<h1> ", "");
		//System.out.println(title);
		System.out.println(index + ":" + title);
		//MainFrame.getInstance().printLog(index + ":" + title);
		writer.write(title);
		
		
		writer.write("\r\n");
		String fictionContent = buf.substring(buf.indexOf("yd_text2"));
	    fictionContent = fictionContent.substring(0, fictionContent.indexOf("</div>"));
	    fictionContent = fictionContent.replace("yd_text2\">    ", "").replaceAll("&nbsp;", " ");
	    fictionContent = fictionContent.replaceAll("<br /><br />", "\r\n").replaceAll("<br />", "\r\n");
	    //System.out.println(fictionContent);
	    writer.write(fictionContent);
	    writer.write("\r\n");
	    writer.write("\r\n");
	    /*String[] contentLines = fictionContent.split("<br /><br />");
	    for(String content:contentLines)
	    {
	    	writer.write(content);
	    	writer.write("\r\n");
	    	System.out.println(content);
	    }*/
	    writer.close();
	    String nextIndex = getNextIndex(buf);
		if(!"index.html".equals(nextIndex)&&!fiction_path.equals(nextIndex))
		{
			getAndSaveFiction(nextIndex);
		}
	}

	private static String getNextIndex(String buf) {
		String nextIndex = buf.substring(0, buf.indexOf("下一章"));
		nextIndex = nextIndex.substring(nextIndex.lastIndexOf("href"));
		nextIndex = nextIndex.replace("href=\"", "").replace("\" target=\"_top\">", "");
		return nextIndex;
	}
}
