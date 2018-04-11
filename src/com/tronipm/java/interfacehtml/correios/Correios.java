package com.tronipm.java.interfacehtml.correios;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.tronipm.java.interfacehtml.Browser;
import com.tronipm.java.interfacehtml.Endereco;
import com.tronipm.java.interfacehtml.HTMLObject;
import com.tronipm.java.interfacehtml.Parameter;

/**
 * 
 * @author Paulo Mateus
 * @email paulomatew@gmail.com
 * @date 09/04/2018
 *
 */
public class Correios {
	private ArrayList<Endereco> enderecos = null;
	private boolean isValid = false;
	private static final String diferenciador = "tmptabela";
	private static final String placeholder = "NULL";
	private static final String parametro_CEP = "relaxation", parametro_TIPO = "tipoCEP";

	public ArrayList<Endereco> getEnderecos() {
		return enderecos;
	}

	//	public void setEnderecos(ArrayList<Endereco> enderecos) {
	//		this.enderecos = enderecos;
	//	}

	public boolean isValid() {
		return isValid;
	}

	//	public void setValid(boolean isValid) {
	//		this.isValid = isValid;
	//	}

	public Correios(String cep) {
		String url1 = "http://www.buscacep.correios.com.br/sistemas/buscacep/resultadoBuscaCepEndereco.cfm";
		Browser b = new Browser(false, StandardCharsets.UTF_8);

		b.get(url1);

		Parameter[] p = new Parameter[] {new Parameter(parametro_CEP, cep), new Parameter(parametro_TIPO, "ALL")};
		String a1 = b.post(url1, p);

		//salvando pagina para conferencia
		//		String path3 = "C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\cccccc.html";
		//		Util.escreverEmArquivo(path3, a1, false);
		//
		//		try {
		//			Desktop.getDesktop().open(new File(path3));
		//		} catch (IOException e1) {
		//			// TODO Auto-generated catch block
		//			e1.printStackTrace();
		//		}


		HTMLObject html = HTMLObject.parse(a1);

		ArrayList<HTMLObject> aaa = html.getObjectByClass(diferenciador);

		enderecos = new ArrayList<>();

		if(aaa.size() > 0 && aaa.get(0).getChildrens().size() >= 2) {

			for(int i = 1; i < aaa.get(0).getChildrens().size(); i++) {

				String rua = (aaa.get(0).getChildrens().get(i).getChildrens().get(0).getHtmlSourceAsHtml());
				rua = html.extractDataFromTags(rua).replace("&nbsp;", "").trim();
				rua = (rua.isEmpty() ? placeholder : rua);
				String bairro = (aaa.get(0).getChildrens().get(i).getChildrens().get(1).getHtmlSourceAsHtml());
				bairro = html.extractDataFromTags(bairro).replace("&nbsp;", "").trim();
				bairro = (bairro.isEmpty() ? placeholder : bairro);
				String uf = (aaa.get(0).getChildrens().get(i).getChildrens().get(2).getHtmlSourceAsHtml());
				uf = html.extractDataFromTags(uf).replace("&nbsp;", "").trim();
				uf = (uf.isEmpty() ? placeholder : uf);
				cep = (aaa.get(0).getChildrens().get(i).getChildrens().get(3).getHtmlSourceAsHtml());
				cep = html.extractDataFromTags(cep).replace("&nbsp;", "").trim();
				cep = (cep.isEmpty() ? placeholder : cep);


				Endereco e = new Endereco(rua, bairro, uf, cep);

				enderecos.add(e);
			}
			isValid = true;
		} else {
			isValid = false;
			enderecos = null;
		}
	}

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
}
