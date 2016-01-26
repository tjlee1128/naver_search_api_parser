package main;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import constants.Constants;
import handler.ItemHandler;
import model.Item;

public class Driver {
	public static void main(String args[]) {
		try {
			
			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			DocumentBuilder b = f.newDocumentBuilder();
//			String s = makeUrl();
			Document doc = b.parse(makeUrl());

			doc.getDocumentElement().normalize();

			String docToString = convertDocumentToString(doc);
			docToString = docToString.replace("&lt;b&gt;", "").replace("&lt;/b&gt;", "");

			SAXParserFactory sf = SAXParserFactory.newInstance();
			SAXParser sp = sf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			ItemHandler handler = new ItemHandler();
			xr.setContentHandler(handler);
			xr.parse(new InputSource(new StringReader(docToString)));

			for (Item item : handler.getItemList()) {
				System.out.println("title : " + item.getTitle());
				System.out.println("telephone : " + item.getTelephone());
				System.out.println("address : " + item.getAddress());
				System.out.println("roadAddress : " + item.getRoadAddress());
				System.out.println("mapx : " + item.getMapx());
				System.out.println("mapy : " + item.getMapy());
			}
			
			makeTxtFile(handler.getItemList());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String makeUrl() {
		
		Scanner scanner = new Scanner(System.in);
		System.out.print("start ?");
		String start = scanner.nextLine();
		System.out.print("display ?");
		String display = scanner.nextLine();
		System.out.print("query ?");
		String query = scanner.nextLine();
		query = query.replace(" ", "%20");
		
		String url = Constants.BASE_URL + "start=" + start + "&display=" + display + "&query=" + query;
		byte[] encodingUrl = null;
		try {
			encodingUrl = url.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new String(encodingUrl);
	}

	private static String convertDocumentToString(Document doc) {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tf.newTransformer();
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			String output = writer.getBuffer().toString();
			return output;
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	private static void makeTxtFile(List<Item> itemList) throws IOException {
		List<String> lines = new ArrayList<>();
		for (Item item : itemList) {
			String line = item.getTitle() +", " + item.getTelephone() + ", " + item.getAddress() + ", " + item.getRoadAddress() + ", " + item.getMapx() + ", " + item.getMapy();
			lines.add(line);
		}
		Path file = Paths.get("the-file-name.txt");
		Files.write(file, lines, Charset.forName("UTF-8"));
	}
}