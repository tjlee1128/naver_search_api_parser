package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
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

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import constants.Constants;
import handler.ItemHandler;
import model.Item;

public class Driver {

	private static String start;
	private static String display;
	private static String query;

	public static void main(String args[]) {
		try {

			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			DocumentBuilder b = f.newDocumentBuilder();
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

			// 카텍좌표계 => 경위도 좌표계로 변환
			List<Item> items = new ArrayList<>();
			for (Item item : handler.getItemList()) {
				if (item.getTelephone() == null) {
					continue;
				}
				
				Item i = new Item();
				i.setTitle(item.getTitle());
				i.setTelephone(item.getTelephone());
				i.setAddress(item.getAddress());

				String requestURL = "https://apis.daum.net/local/geo/transcoord?apikey=419acaeb7386cadce04ab8cf369e535c&fromCoord=KTM&y="
						+ item.getMapy() + "&x=" + item.getMapx() + "&toCoord=WGS84&output=json";

				// java.net.URL
				URL url = new URL(requestURL);
				// Connection 객체를 InputStreamReader로 읽고 utf-8로 인코딩.
				InputStreamReader isr = new InputStreamReader(url.openConnection().getInputStream(), "UTF-8");

				// org.json.simple.JSONObject 객체로 형변환
				JSONObject object = (JSONObject)JSONValue.parseWithException(isr);
				
				i.setMapx(String.valueOf(object.get("x")));
				i.setMapy(String.valueOf(object.get("y")));
				
				items.add(i);
			}

			makeTxtFile(items);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String makeUrl() {

		Scanner scanner = new Scanner(System.in);
		System.out.print("start ?");
		start = scanner.nextLine();
		System.out.print("display ?");
		display = scanner.nextLine();
		System.out.print("query ?");
		query = scanner.nextLine();

		// encoding
		String encodeResult = null;
		try {
			encodeResult = URLEncoder.encode(query, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String url = Constants.BASE_URL + "start=" + start + "&display=" + display + "&query=" + encodeResult;

		return url;
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
			if (item.getTelephone() == null) {
				continue;
			}
			String line = item.getTitle() + ", " + item.getTelephone() + ", " + item.getAddress() + ", " + item.getMapx() + ", " + item.getMapy();
			lines.add(line);
		}
		Path file = Paths.get(start + "-" + display + "-" + query + ".txt");
		Files.write(file, lines, Charset.forName("UTF-8"));
	}
}