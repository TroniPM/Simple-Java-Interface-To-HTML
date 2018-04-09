package com.tronipm.java.interfacehtml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 
 * @author Paulo Mateus
 * @email paulomatew@gmail.com
 * @date 09/04/2018
 *
 */
public class HTMLObject {
	private String tag;
	private int line_start, line_end;
	private String str_start, str_end;
	private ArrayList<HTMLObject> childrens = new ArrayList<>();
	private Map<String, String> attributes = new HashMap<String, String>();
	private static String TAG_START = "<", TAG_END = ">", TAG_FINISH = "</", TAG_FINISH_INLINE = "/>", CRLF = "\r\n", JAVA_CRLF = "\n", HTML_COMMENT_START = "<!--", HTML_COMMENT_END = "-->";
	private boolean hasParent = false;
	private boolean inlineTag = false;
	private String htmlSource = "";
	private ArrayList<HTMLObject> allObjects = new ArrayList<>();

	public String getHtmlSourceAsHtml() {
		return htmlSource;
	}

	public String getHtmlSourceAsText() {
		return extractDataFromTags(getHtmlSourceAsHtml());
	}

	public String extractDataFromTags (String args) {
		String nohtml = args.toString().replaceAll("\\" + TAG_START + ".*?" + TAG_END," ");
		return (nohtml);
	}

	public static HTMLObject parse(String html) {
		Pattern pattern_TAG_FINISH = Pattern.compile(TAG_FINISH);
		Pattern pattern_TAG_FINISH_INLINE = Pattern.compile(TAG_FINISH_INLINE);
		Pattern pattern_comment_start = Pattern.compile(HTML_COMMENT_START);
		Pattern pattern_comment_end = Pattern.compile(HTML_COMMENT_END);

		ArrayList<HTMLObject> allObjects = new ArrayList<>();

		html = html.replaceAll(TAG_START, CRLF + TAG_START);
		html = html.replaceAll(TAG_END, TAG_END + CRLF);
		html = html.replaceAll(TAG_FINISH, CRLF + TAG_FINISH);
		html = html.replaceAll("[" + CRLF + "]+", JAVA_CRLF).trim();
		html = html.replaceAll("[" + JAVA_CRLF + "]+", JAVA_CRLF).trim();

		//Remove trash here
		html = html.replaceAll("<!-->", "-->");

		//		String path3 = "C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\teste_HTMLOBJECT.html";
		//		Util.escreverEmArquivo(path3, html, false);

		//Removing comments: find ending tags
		ArrayList<HTMLObject> comments = new ArrayList<>();
		String[] main = html.split("\n");
		for(int i=0; i< main.length; i++) {
			if(pattern_comment_end.matcher(main[i]).find()) {
				comments.add(new HTMLObject("comment", -1, i+1, null, main[i]));
				//				System.out.println(main[i]);
			}
		}

		//Removing comments: find start tags
		String[] commentAux = html.split("\n");
		for(HTMLObject in : comments) {
			inner:
				for(int i = in.getLine_end() - 1; i >= 0; i--) {
					if(pattern_comment_start.matcher(commentAux[i]).lookingAt()) {
						in.setLine_start(i + 1);
						in.setStr_start(commentAux[i]);

						//after find the correspondent pattern, erase it all, to avoid conflict with equals tags
						for(int j = in.getLine_start() - 1; j <= in.getLine_end() - 1; j++){
							commentAux[j] = "";
						}
						break inner;
					}
				}
		}
		/**
		 * Find tag endings like <TAG>...</TAG> and <TAG ... />
		 */
		main = commentAux;

		for(int i=0; i< main.length; i++) {
			String in = main[i].trim();
			if(pattern_TAG_FINISH_INLINE.matcher(in).find()) {//Check if object is inline type
				String title = in.split(" ")[0].replace(TAG_START, "");
				HTMLObject o = new HTMLObject(title, i+1, i+1, in, in);
				o.setInlineTag(true);
				allObjects.add(o);
			} else if(pattern_TAG_FINISH.matcher(in).lookingAt()) {//has to come after pattern_TAG_FINISH_INLINE
				allObjects.add(new HTMLObject(in.replace(TAG_FINISH, "").replace(TAG_END, ""), -1, i+1, null, in));
			}
		}

		//Find tag beginnings
		String[] mainAux = html.split("\n");
		for(HTMLObject in : allObjects) {
			if(in.inlineTag) {
				continue;//because tags already was found
			} else if(in.isInlineTag()) {
				mainAux[in.getLine_start()-1] = "";
			}

			String aux = TAG_START + in.getTag();
			Pattern tagStart = Pattern.compile(aux);
			inner:
				for(int i = in.getLine_end() - 1; i >= 0; i--) {
					if(tagStart.matcher(mainAux[i]).lookingAt()) {
						in.setLine_start(i + 1);
						in.setStr_start(mainAux[i]);

						//after find the correspondent pattern, erase it all, to avoid conflict with equals tags
						for(int j = in.getLine_start() - 1; j <= in.getLine_end() - 1; j++){
							in.htmlSource += main[j].trim();
							//if(in.tag.equals("form"))
							//System.out.println(main[j].trim());
							mainAux[j] = "";
						}
						break inner;
					}
				}
		}

		//Ordering by start tag line
		Collections.sort(allObjects, new ComparadorCrescente());

		//Giving childrens
		for(int i = allObjects.size()-1; i >= 0; i--) {
			inner:
				for(int j = allObjects.size()-1; j >= 0; j--) {
					try {
						if(allObjects.get(i).equals(allObjects.get(j))) {
							break inner;//when reaches itself, break. There's no object that can be smaller (at list order) and still be children
						}
					} catch(NullPointerException e) {
						System.out.println(i + " <> "+ j + "\n\n");
						e.printStackTrace();
					}
					if((!allObjects.get(j).getHasParent()) && 
							allObjects.get(i).getLine_start() <= allObjects.get(j).getLine_start() && 
							allObjects.get(i).getLine_end() > allObjects.get(j).getLine_end()) {
						//						System.out.println(array.get(j).toString2() + "  |  " + array.get(i).toString2());
						allObjects.get(j).setHasParent(true);
						allObjects.get(i).addChildren(allObjects.get(j));

						//Ordering childrens at every moment
						Collections.sort(allObjects.get(i).getChildrens(), new ComparadorCrescente());
					}
				}
		}

		//getting attributes
		//TODO this

		allObjects.get(0).allObjects = allObjects;

		return allObjects.get(0);
	}

	public boolean isInlineTag() {
		return inlineTag;
	}

	private void setInlineTag(boolean inlineTag) {
		this.inlineTag = inlineTag;
	}

	private void addChildren(HTMLObject obj) {
		this.childrens.add(obj);
	}

	public ArrayList<HTMLObject> getObjectsByTag(String tag){
		ArrayList<HTMLObject> aux = new ArrayList<>();
		for(HTMLObject in : allObjects) {
			if(in.getTag().equals(tag)) {
				aux.add(in);
			}
		}

		return (aux.size()!=0) ? aux : null;
	}

	public ArrayList<HTMLObject> getChildrens() {
		return this.childrens;
	}

	public Map<String, String> getAttributes() {
		return this.attributes;
	}

	public ArrayList<HTMLObject> getObjectByClass(String clas){
		ArrayList<HTMLObject> arr = new ArrayList<>();
		for(HTMLObject in : allObjects) {
			if(in.getStr_start() != null && in.getStr_start().contains("class") && in.getStr_start().contains(clas))
				arr.add(in);
		}		
		return arr;
	}

	private void setHasParent(Boolean hasParent) {
		this.hasParent = hasParent;
	}

	private boolean getHasParent() {
		return this.hasParent;
	}

	public String getTag() {
		return this.tag;
	}

	public HTMLObject(String tag, int line_start, int line_end, String str_start, String str_end) {
		super();
		this.line_start = line_start;
		this.line_end = line_end;
		this.tag = tag;
		this.str_start = str_start;
		this.str_end = str_end;
	}

	@Override
	public String toString() {
		String a = tag + ": starting at (" + line_start + ") ending at (" + line_end + ")";
		return a;
	}

	@Override
	public boolean equals(Object obj) {
		HTMLObject o = (HTMLObject)obj;

		if(this.getLine_start() == o.getLine_start() &&
				this.getLine_end() == o.getLine_end() &&
				((this.getStr_start() == null && this.getStr_start() == null) || this.getStr_start().equals(o.getStr_start())) &&
				((this.getStr_end() == null && this.getStr_end() == null) || this.getStr_end().equals(o.getStr_end()))) {
			return true;
		}
		return false;
	}

	protected int getLine_start() {
		return line_start;
	}

	protected void setLine_start(int line_start) {
		this.line_start = line_start;
	}

	protected int getLine_end() {
		return line_end;
	}

	protected void setLine_end(int line_end) {
		this.line_end = line_end;
	}

	protected String getStr_start() {
		return str_start;
	}

	protected void setStr_start(String str_start) {
		this.str_start = str_start;
	}

	protected String getStr_end() {
		return str_end;
	}

	protected void setStr_end(String str_end) {
		this.str_end = str_end;
	}
}
