package com.tronipm.java.interfacehtml;

import java.util.Comparator;

/**
 * 
 * @author Paulo Mateus
 * @email paulomatew@gmail.com
 * @date 09/04/2018
 *
 */
public class ComparadorCrescente implements Comparator<HTMLObject> {
	@Override
	public int compare(HTMLObject o1, HTMLObject o2) {
		return (o1.getLine_start() < o2.getLine_start()) ? -1 : ((o1.getLine_start() > o2.getLine_start()) ? 1 : 0);
	}
}
