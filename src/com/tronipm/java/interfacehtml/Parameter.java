package com.tronipm.java.interfacehtml;

/**
 * 
 * @author Paulo Mateus
 * @email paulomatew@gmail.com
 * @date 09/04/2018
 *
 */
public class Parameter {
	private String id;
	private String value;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Parameter(String id, String value) {
		super();
		this.id = id;
		this.value = value;
	}
	public String toString() {
		return getId()+"="+getValue();
	}
}
