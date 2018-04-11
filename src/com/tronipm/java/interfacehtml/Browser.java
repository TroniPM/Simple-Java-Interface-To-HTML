package com.tronipm.java.interfacehtml;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class Browser {
	private Charset charset = null;
	private List<String> cookies;
	private Object conn = null;/*HttpURLConnection*//*HttpURLConnection*/
	private String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";
	private boolean isHTTPS = false;

	public static void main(String[] args) throws Exception {
		Browser b = new Browser(false, StandardCharsets.UTF_8);
		
		b.example1();	
	}

	public void example1() throws Exception {
		// 1. setting up
		String url1 = "http://example.com/wp/wp-login.php";//Login page
		String url2 = "http://example.com/wp/wp-admin/edit.php";//logged page to test
		Browser b = new Browser(false, StandardCharsets.UTF_8);

		// 2. Send a "GET" request, so you can extract the cookies/cache.
		b.get(url1);

		// 3. Construct above post's content and then send a POST request for authentication
		Parameter[] p = new Parameter[] {new Parameter("log", "pmateus"), new Parameter("pwd", "120593")};
		String a1 = b.post(url1, p);

		// 4. success then go to logged page.
		String a2 = b.get(url2);

		// 5. printing out the result
		String path2 = "C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\aa.html";
		Util.escreverEmArquivo(path2, a1, false);
		Desktop.getDesktop().open(new File(path2));
		String path3 = "C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\aa1.html";
		Util.escreverEmArquivo(path3, a2, false);
		Desktop.getDesktop().open(new File(path3));
	}



	public Browser(boolean isHTTPS, Charset charset) {
		this.isHTTPS = isHTTPS;
		this.charset = charset;

		// make sure cookies is turn on
		CookieHandler.setDefault(new CookieManager());		
	}

	@SuppressWarnings("unused")
	private void getHeader() {
		Map<String, List<String>> map = ((conn instanceof HttpURLConnection)?(HttpURLConnection)conn:(HttpsURLConnection)conn).getHeaderFields();


		System.out.println("Printing Response Header...\n");

		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			System.out.println("Key : " + entry.getKey() 
			+ " ,Value : " + entry.getValue());
		}

		String server = ((conn instanceof HttpURLConnection)?(HttpURLConnection)conn:(HttpsURLConnection)conn).getHeaderField("Server");

		if (server == null) {
			System.out.println("Key 'Server' is not found!");
		} else {
			System.out.println("--> Server: " + server + " <--");
		}
	}

	public String post(String url, Parameter[] params) {
		String postParams = "";
		if(params!=null) {
			for(Parameter in: params) {
				postParams += in.toString() + "&";
			}
		}

		URL obj = null;
		try {
			obj = new URL(url);

			if(isHTTPS) {
				conn = (HttpsURLConnection)obj.openConnection();
			} else {
				conn = (HttpURLConnection)obj.openConnection();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Acts like a browser
		((conn instanceof HttpURLConnection)?(HttpURLConnection)conn:(HttpsURLConnection)conn).setUseCaches(false);
		try {
			((conn instanceof HttpURLConnection)?(HttpURLConnection)conn:(HttpsURLConnection)conn).setRequestMethod("POST");
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
		((conn instanceof HttpURLConnection)?(HttpURLConnection)conn:(HttpsURLConnection)conn).setRequestProperty("Host", "google.com");
		((conn instanceof HttpURLConnection)?(HttpURLConnection)conn:(HttpsURLConnection)conn).setRequestProperty("User-Agent", USER_AGENT);
		((conn instanceof HttpURLConnection)?(HttpURLConnection)conn:(HttpsURLConnection)conn).setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		((conn instanceof HttpURLConnection)?(HttpURLConnection)conn:(HttpsURLConnection)conn).setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		if (cookies != null) {
			for (String cookie : cookies) {
				((conn instanceof HttpURLConnection)?(HttpURLConnection)conn:(HttpsURLConnection)conn).addRequestProperty("Cookie", cookie.split(";", 1)[0]);
			}
		}

		((conn instanceof HttpURLConnection)?(HttpURLConnection)conn:(HttpsURLConnection)conn).setRequestProperty("Connection", "keep-alive");
		((conn instanceof HttpURLConnection)?(HttpURLConnection)conn:(HttpsURLConnection)conn).setRequestProperty("Referer", "google.com");
		((conn instanceof HttpURLConnection)?(HttpURLConnection)conn:(HttpsURLConnection)conn).setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		((conn instanceof HttpURLConnection)?(HttpURLConnection)conn:(HttpsURLConnection)conn).setRequestProperty("Content-Length", Integer.toString(postParams.length()));
		((conn instanceof HttpURLConnection)?(HttpURLConnection)conn:(HttpsURLConnection)conn).setDoOutput(true);
		((conn instanceof HttpURLConnection)?(HttpURLConnection)conn:(HttpsURLConnection)conn).setDoInput(true);

		// Send post request
		DataOutputStream wr = null;
		try {
			wr = new DataOutputStream(((conn instanceof HttpURLConnection)?(HttpURLConnection)conn:(HttpsURLConnection)conn).getOutputStream());
			wr.write(postParams.getBytes(charset));
			wr.flush();
			wr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		int responseCode = -1;
		try {
			responseCode = ((conn instanceof HttpURLConnection)?(HttpURLConnection)conn:(HttpsURLConnection)conn).getResponseCode();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + postParams);
		System.out.println("Response Code : " + responseCode);

		//getHeader();

		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(((conn instanceof HttpURLConnection)?(HttpURLConnection)conn:(HttpsURLConnection)conn).getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String inputLine;
		StringBuffer response = new StringBuffer();

		try {
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//return new String(response.toString().getBytes(), Util.UTF_8);
		return response.toString();
	}

	public String get(String url)   {

		URL obj;
		try {
			obj = new URL(url);

			if(isHTTPS) {
				conn = (HttpsURLConnection)obj.openConnection();
			} else {
				conn = (HttpURLConnection)obj.openConnection();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


		// default is GET
		try {
			((conn instanceof HttpURLConnection)?(HttpURLConnection)conn:(HttpsURLConnection)conn).setRequestMethod("GET");
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
		((conn instanceof HttpURLConnection)?(HttpURLConnection)conn:(HttpsURLConnection)conn).setUseCaches(false);

		// act like a browser
		((conn instanceof HttpURLConnection)?(HttpURLConnection)conn:(HttpsURLConnection)conn).setRequestProperty("User-Agent", USER_AGENT);
		((conn instanceof HttpURLConnection)?(HttpURLConnection)conn:(HttpsURLConnection)conn).setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		((conn instanceof HttpURLConnection)?(HttpURLConnection)conn:(HttpsURLConnection)conn).setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		if (cookies != null) {
			for (String cookie : cookies) {
				((conn instanceof HttpURLConnection)?(HttpURLConnection)conn:(HttpsURLConnection)conn).addRequestProperty("Cookie", cookie.split(";", 1)[0]);
			}
		}
		int responseCode = -1;
		try {
			responseCode = ((conn instanceof HttpURLConnection)?(HttpURLConnection)conn:(HttpsURLConnection)conn).getResponseCode();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		//		getHeader();

		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(((conn instanceof HttpURLConnection)?(HttpURLConnection)conn:(HttpsURLConnection)conn).getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String inputLine;
		StringBuffer response = new StringBuffer();

		try {
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Get the response cookies
		setCookies(((conn instanceof HttpURLConnection)?(HttpURLConnection)conn:(HttpsURLConnection)conn).getHeaderFields().get("Set-Cookie"));

		//		return new String(response.toString().getBytes(), Util.UTF_8);
		return response.toString();
	}

	public List<String> getCookies() {
		return cookies;
	}

	public void setCookies(List<String> cookies) {
		this.cookies = cookies;
	}
}
