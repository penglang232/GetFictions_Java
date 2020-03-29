package test;

import java.net.HttpURLConnection;
import java.net.URL;

public class TestMethod {
	public static void main(String[] args) throws Exception {
		String str = "http://www.88dus.com/xiaoshuo/107/107446/41090607.html";
		
		/*System.out.println(str.substring(str.indexOf("xiaoshuo")-1,str.lastIndexOf("/")+1));
		System.out.println(str.substring(str.lastIndexOf("/")+1));
		System.out.println(str.substring(0,str.lastIndexOf("/")+1));*/ 
		URL url = new URL(str);  
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();  
		System.out.println(httpConn.getResponseCode());
	}
}


