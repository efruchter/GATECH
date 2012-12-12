package toritools.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Simple DOM XML Parser wrapper.
 * 
 */
public class ToriXML {

	/**
	 * Parse an xml file into a DOM Document.
	 * 
	 * @param file
	 *            the file
	 * @return null if error, otherwise a Document.
	 */
	public static Document parse(final File file) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document dom = null;
		try {
			db = dbf.newDocumentBuilder();
			dom = db.parse(file);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dom;
	}

	/**
	 * Encode an xml document in a file.
	 * 
	 * @param file
	 *            the file.
	 * @param doc
	 *            the doc to save.
	 * @throws TransformerException
	 */
	public static void saveXMLDoc(final File file, final Document doc)
			throws TransformerException {
		/*
		 * SAVE ALL THE THINGS
		 */
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(doc);
		trans.transform(source, result);
		String xmlString = sw.toString();

		System.err.println(xmlString);

		try {
			FileWriter f = new FileWriter(file);
			f.write(xmlString);
			f.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
