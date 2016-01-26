package handler;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import constants.Constants;
import model.Item;

public class ItemHandler extends DefaultHandler {

	private String position;
	private Item item;
	private List<Item> itemList;

	public List<Item> getItemList() {
		return itemList;
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		this.itemList = new ArrayList<>();
	}

	@Override
	public void endDocument() throws SAXException {
		// Nothing to do
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub

		if (qName.equals(Constants.STRING_ITEM)) {
			item = new Item();
		} else if (qName.equals(Constants.STRING_TITLE)) {
			position = Constants.STRING_TITLE;
		} else if (qName.equals(Constants.STRING_TELEPHONE)) {
			position = Constants.STRING_TELEPHONE;
		} else if (qName.equals(Constants.STRING_ADDRESS)) {
			position = Constants.STRING_ADDRESS;
		} else if (qName.equals(Constants.STRING_ROADADDRESS)) {
			position = Constants.STRING_ROADADDRESS;
		} else if (qName.equals(Constants.STRING_MAPX)) {
			position = Constants.STRING_MAPX;
		} else if (qName.equals(Constants.STRING_MAPY)) {
			position = Constants.STRING_MAPY;
		} else {
			// Nothing to do
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// TODO Auto-generated method stub

		if (qName.equals(Constants.STRING_ITEM)) {
			itemList.add(item);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// TODO Auto-generated method stub

		if (item == null) {
			return;
		}

		if (position.equals(Constants.STRING_TITLE)) {
			item.setTitle((new String(ch, start, length).replace("<b>", "").replace("</b>", "")).trim());
			position = "";
		}

		if (position.equals(Constants.STRING_TELEPHONE)) {
			item.setTelephone((new String(ch, start, length).replace("<b>", "").replace("</b>", "")).trim());
			position = "";
		}

		if (position.equals(Constants.STRING_ADDRESS)) {
			item.setAddress((new String(ch, start, length).replace("<b>", "").replace("</b>", "")).trim());
			position = "";
		}

		if (position.equals(Constants.STRING_ROADADDRESS)) {
			item.setRoadAddress((new String(ch, start, length).replace("<b>", "").replace("</b>", "")).trim());
			position = "";
		}

		if (position.equals(Constants.STRING_MAPX)) {
			item.setMapx((new String(ch, start, length).replace("<b>", "").replace("</b>", "")).trim());
			position = "";
		}

		if (position.equals(Constants.STRING_MAPY)) {
			item.setMapy((new String(ch, start, length).replace("<b>", "").replace("</b>", "")).trim());
			position = "";
		}
	}
}
