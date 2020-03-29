package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DownLoadFiction extends Thread{
	
	private String url = "http://www.88dus.com";
	private String fiction_path = "/xiaoshuo/78/78528/";
	private String fiction_name = "不朽青天";
	private String chapter_index = "34809191";
	private String full_url = "https://www.88dus.com/xiaoshuo/64/64632/";
	private String head_url = "34809191";
	private String author_name = "未知";
	private String fiction_dir = "34809191";
	private static final String CRLF = "\r\n";
	
	public DownLoadFiction(String full_url, String fiction_name) {
		this.full_url = full_url;
		this.fiction_name = fiction_name;
	}
	
	@Override
	public void run() {
		initParam();
		downloadFictions();
		System.out.println("Done!");
	}

	public static void main(String[] args) throws IOException {
		/*DownLoadFiction fiction = new DownLoadFiction();
		fiction.initParam();
		fiction.downloadFictions();
		System.out.println("Done!");*/
	}
	
	public void initParam() {
		fiction_path = full_url.substring(full_url.indexOf("xiaoshuo")-1,full_url.lastIndexOf("/")+1);
		chapter_index = full_url.substring(full_url.lastIndexOf("/")+1);
		head_url = full_url.substring(0,full_url.lastIndexOf("/")+1);
		String document = getDocument("");
		System.out.println(document);
		String author_keyword = "<em>作者";
		int author_location = document.indexOf(author_keyword);
		author_name = document.substring(author_location + 4, document.indexOf("</em",author_location));
		System.out.println(author_name);
		fiction_dir = new SimpleDateFormat("yyyyMMdd").format(new Date());
		File ficDir = new File(fiction_dir);
		if(!ficDir.exists()) {
			ficDir.mkdirs();
		}
		downloadAuthorInfo();
		//System.out.println(document.indexOf("<a href=\""));
	}
	
	private void downloadAuthorInfo() {
		FileWriter writer = null;
		try {
			writer = new FileWriter(fiction_dir + File.separator + fiction_name + ".txt",true);
			writer.write(author_name);
			writer.write(CRLF);
			writer.write(CRLF);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String downloadFictions() {
		String result = "Done!";
		String temp = chapter_index + "";
		String nextIndex = getAndSaveFiction(chapter_index + ".html");
		while(!"index.html".equals(nextIndex)&&!fiction_path.equals(nextIndex))
		{
			temp = nextIndex;
			nextIndex = getAndSaveFiction(nextIndex);
			if(nextIndex.isEmpty()) {
				result = temp + " Down Failed!";
				break;
			}
		}
		return result;
	}
	
	public String getDocument(String index){   
		String buf = "";
		try {
			String strURL = url + fiction_path +index;  
			URL url = new URL(strURL);  
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();  
			InputStreamReader input = new InputStreamReader(httpConn.getInputStream(), "gbk");
			BufferedReader bufReader = new BufferedReader(input);  
			String line = "";  
			StringBuilder contentBuf = new StringBuilder();  	
			while ((line = bufReader.readLine()) != null) {  
			    contentBuf.append(line);  
			}  
			buf = contentBuf.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	    //System.out.println(buf);
	    return buf;
	}
	
	public String getAndSaveFiction(String index){
		
		String nextIndex = "";
		try {
			String buf = getDocument(index);
			FileWriter writer = new FileWriter(fiction_dir + File.separator + fiction_name + ".txt",true);
			String title = buf.substring(buf.indexOf("<h1>"), buf.indexOf("</h1>"));
			title = title.replace("<h1> ", "");
			//System.out.println(title);
			System.out.println(index + ":" + title);
			writer.write(title);
			
			
			writer.write(CRLF);
			String fictionContent = buf.substring(buf.indexOf("yd_text2"));
			fictionContent = fictionContent.substring(0, fictionContent.indexOf("</div>"));
			fictionContent = fictionContent.replace("yd_text2\">    ", "").replaceAll("&nbsp;", " ");
			fictionContent = fictionContent.replaceAll("<br /><br />", CRLF).replaceAll("<br />", CRLF);
			//System.out.println(fictionContent);
			writer.write(fictionContent);
			writer.write(CRLF);
			writer.write(CRLF);
			/*String[] contentLines = fictionContent.split("<br /><br />");
			for(String content:contentLines)
			{
				writer.write(content);
				writer.write("\r\n");
				System.out.println(content);
			}*/
			writer.close();
			nextIndex = getNextIndex(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return nextIndex;
	}

	private String getNextIndex(String buf) {
		String nextIndex = buf.substring(0, buf.indexOf("下一章"));
		nextIndex = nextIndex.substring(nextIndex.lastIndexOf("href"));
		nextIndex = nextIndex.replace("href=\"", "").replace("\" target=\"_top\">", "");
		return nextIndex;
	}
}
