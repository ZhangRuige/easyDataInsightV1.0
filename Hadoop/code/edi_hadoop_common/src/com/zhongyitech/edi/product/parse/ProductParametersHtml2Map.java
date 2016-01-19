package com.zhongyitech.edi.product.parse;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class ProductParametersHtml2Map {
	
	/**
	 * 从规格参数xml串中，返回参数Map表。
	 * @param strXML
	 * @return
	 */
	public static Map<String, String> doParse(String htmlstr){
		Map<String, String> paraMap = new HashMap<String, String>();
    	
    	////TBODY/TR[3]/TD[2]
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
		DocumentBuilder builder = null;
		Document doc = null;
		try {
			builder =factory.newDocumentBuilder();  
			doc = builder.parse(new InputSource(new StringReader("<?xml version=\"1.0\" encoding=\"utf-8\"?>"+htmlstr)));
			Element root = doc.getDocumentElement();  
			NodeList nodes = root.getElementsByTagName("TR");
            
            if (nodes == null || nodes.getLength() <= 0) {
                return null;
            }
            
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                NodeList TDs = node.getChildNodes();
                if (TDs.getLength() == 2) {
					String key = TDs.item(0).getTextContent();
					String value = TDs.item(1).getTextContent();
					paraMap.put(key, value);
				} 
            }   
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return paraMap;
	}
}
