package test;

public class StringTest {
	// http://www.xx88dushu.com/
	
	public static void main(String[] args) {
		String c = "=\"/shu/108-1610\">第1647章 神物出</a></li><li><a";
		String c_path = c.substring(c.indexOf("-"),c.lastIndexOf("\""));
		String c_name = c.substring(c.indexOf(">")+1,c.indexOf("</"));
		System.out.println(c_path +" " + c_name);
	}
}
