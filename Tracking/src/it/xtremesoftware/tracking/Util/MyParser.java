package it.xtremesoftware.tracking.Util;


import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

public class MyParser {
	
	
	//*****************************************************
	//Variabili di Oggetto (GET e SET)
	//*****************************************************
	private Document myDocument;
	//*****************************************************
	//FINE Variabili di Oggetto (GET e SET)
	//*****************************************************



	
	//*****************************************************
  	//COSTRUTTORE
  	//*****************************************************
	public MyParser(String xml) {
		myDocument=getDomElement(xml);
	}
	public MyParser() {
		
	}
	//*****************************************************
  	//FINE COSTRUTTORE
  	//*****************************************************
	
	
	
	
	
	
	
	
	//*****************************************************
  	//METODI OGGETTO
  	//*****************************************************
	/**
	 * Getting XML from URL making HTTP request
	 * @param url string
	 * */
	/*public String getXmlFromUrl(String url) {
		String xml = null;

		try {
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// return XML
		return xml;
	}*/
	
	
	
	/**
	 * Getting XML DOM element
	 * @param XML string
	 * */
	public Document getDomElement(String xml){
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			DocumentBuilder db = dbf.newDocumentBuilder();

			InputSource is = new InputSource();
		        is.setCharacterStream(new StringReader(xml));
		        doc = db.parse(is); 
		        
			} catch (ParserConfigurationException e) {
				Log.e("Error: ", e.getMessage());
				return null;
			} catch (SAXException e) {
				Log.e("Error: ", e.getMessage());
	            return null;
			} catch (IOException e) {
				Log.e("Error: ", e.getMessage());
				return null;
			}
			
	        return doc;
	}
	
	
	//Ottengo un node list dal suo nome
	public NodeList GetNodeList(String Name)
	{
		return myDocument.getElementsByTagName(Name);
	}
	//ottengo il Nodelist, figlio di un elemento, dal suo nome
	public NodeList GetNodeList(Element element, String Name)
	{
		return element.getElementsByTagName(Name);
	}
	
	
	
	public Element GetElement(NodeList nodelist, int position)
	{
		return (Element) nodelist.item(position);
	}
	
	public final String getElementValue( Node elem ) {
	     Node child;
	     if( elem != null){
	         if (elem.hasChildNodes()){
	             for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
	                 if( child.getNodeType() == Node.TEXT_NODE  ){
	                     return child.getNodeValue();
	                 }
	             }
	         }
	     }
	     return "";
	 }
	 

	 public String getValue(Element item, String str) {		
			NodeList n = item.getElementsByTagName(str);		
			return this.getElementValue(n.item(0));
		}

	public String getValue(String str) {
		NodeList n = myDocument.getElementsByTagName(str);		
		return this.getElementValue(n.item(0));
	}
	
	public String GetAttributaValue(Element currentElement, String attributeName) {
		return currentElement.getAttribute(attributeName);
	}
	
	//*****************************************************
  	//FINE METODI OGGETTO
  	//*****************************************************

}

