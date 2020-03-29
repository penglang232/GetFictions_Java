package test;

public class RegexTest {
	public static void main(String[] args) {
		String tem = "https://blog.csdn.net/wangchaoqi1985/article/details/82810471";
		boolean matches = tem.matches("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
		
		System.out.println(matches);
	}
}
