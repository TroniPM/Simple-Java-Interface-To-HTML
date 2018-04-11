# Simple Java interface to html

You can use these classes to submit simple post/get requests without libs like [Selenium] and [HtmlUnit]. Supports:
  - http and https
  - get requests
  - post requests
# Example 1

```JAVA
	public static void main(String[] args) throws Exception {
		// 1. setting up
		String url1 = "http://example.com/wp/wp-login.php";//Login page
		String url2 = "http://example.com/wp/wp-admin/edit.php";//logged page to test
		Browser b = new Browser(false, StandardCharsets.UTF_8);

		// 2. Send a "GET" request, so you can extract the cookies/cache.
		b.get(url1);

		// 3. Construct above post's content and then send a POST request for authentication
		Parameter[] p = new Parameter[] {new Parameter("log", "pmateus"), new Parameter("pwd", "kkkkk")};
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
```

   [Selenium]: <https://mvnrepository.com/artifact/org.seleniumhq.selenium>
   [HtmlUnit]: <https://mvnrepository.com/artifact/net.sourceforge.htmlunit>

# Extrator de cep e endereço correios JAVA

Para extrair dados do site dos Correios utilizar apenas:

```JAVA
	public static void main(String[] args) {
		Correios c = new Correios("25 de marco");
		//Correios c = new Correios("55295555");

		if(c.isValid()) {
			ArrayList<Endereco> end = c.getEnderecos();
			System.out.println("\n----ENCONTRADOS (" + end.size() + ")----");
			for(Endereco in : end) {
				System.out.println(in);
			}
		}
	}
```
