package core.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TrackFile { 
	private Document trackFile;
	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	
	public TrackFile() throws ParserConfigurationException {
		factory = DocumentBuilderFactory.newInstance();
		builder = factory.newDocumentBuilder();
		reset();
	}
	public TrackFile(String fname) throws SAXException, IOException, ParserConfigurationException {
		this();
		read(fname);
	}
	
	public void reset(){
		trackFile = builder.newDocument();
		Element root = trackFile.createElement("ABOSTrackFile");
		trackFile.appendChild(root);
		Element header = trackFile.createElement("header");
		root.appendChild(header);
		Element data = trackFile.createElement("data");
		root.appendChild(data);
	}
	public void read(String fname) throws SAXException, IOException{
		trackFile=builder.parse(fname);
	}
	public void write(String fname) throws TransformerException{
		File outf = new File(fname);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(trackFile);
		StreamResult res = new StreamResult(outf);
		transformer.transform(source,res);
	}
	public int numRows(){ 
		return trackFile.getElementsByTagName("row").getLength();
	}
	public NodeList getAllTracks(){
		return trackFile.getElementsByTagName("row");
	}
	public ArrayList<String> getTracksArrayList(){
		NodeList tmp = trackFile.getElementsByTagName("row");
		ArrayList<String> rv = new ArrayList<String>();
		for(int i=0;i<tmp.getLength();i++){
			rv.add(tmp.item(i).getFirstChild().getNodeValue());
		}
		return rv;
	}
	public ArrayList<HashMap<String,String>> getTracksHashMap(){
		return getTracksHashMap(null,null);
	}
	public ArrayList<HashMap<String,String>> getTracksHashMap(String field, String val){
		NodeList fieldDesc = getFieldDescriptions();
		String[] fieldNames = new String[fieldDesc.getLength()];
		for(int i=0;i<fieldNames.length;i++){
			int fieldIdx = Integer.parseInt(((Element)fieldDesc.item(i)).getAttribute("index"));
			fieldNames[fieldIdx-1] = ((Element)fieldDesc.item(i)).getAttribute("name");
		}
		NodeList tracks = getAllTracks();
		ArrayList<HashMap<String,String>> rv = new ArrayList<HashMap<String,String>>();
		for(int i=0;i<tracks.getLength();i++){
			String[] row = tracks.item(i).getFirstChild().getNodeValue().split("\\s+");
			HashMap<String,String> map = new HashMap<String,String>(fieldNames.length);
			for(int j=0;j<row.length;j++){
				//System.out.println("***"+row[j]+"*** row["+j+"]");
				String[] idxVal = row[j].split(":");
				int idx = Integer.parseInt(idxVal[0]);
				map.put(fieldNames[idx-1],idxVal[1]);
			}
			if(field == null || val == null || map.get(field).equals(val))
				rv.add(map);
		}
		return rv;
	}
	public NodeList getFieldDescriptions(){
		return trackFile.getElementsByTagName("field");
	}
	public void addFieldDescription(int index, String type, String name, String descr, String units){
		Node header = trackFile.getElementsByTagName("header").item(0);
		Element newField = trackFile.createElement("field");
		newField.setAttribute("index",""+index);
		newField.setAttribute("type",type);
		newField.setAttribute("name",name);
		newField.setAttribute("description",descr);
		newField.setAttribute("unit",units);
		header.appendChild(newField);
	}
	public int getFieldIdx(String name){
		NodeList fields = trackFile.getElementsByTagName("field");
		for(int i=0;i<fields.getLength();i++){
			String foo = ((Element)fields.item(i)).getAttribute("name");
			if(foo.equals(name)){
				return Integer.parseInt(((Element)fields.item(i)).getAttribute("index"));
			}
		}
		return -1;
	}
	
	public void addRow(String txt){
		Node data = trackFile.getElementsByTagName("data").item(0);
		Element newRow = trackFile.createElement("row");
		newRow.appendChild(trackFile.createTextNode(txt));
		data.appendChild(newRow);
	}

	public static void main(String[] args){
		try{
			TrackFile tf;
			tf = new TrackFile();
			/*
			tf.read("tracks/log1.atf");
			System.out.println("1");
			System.out.println(tf.numRows());
			System.out.println("2");
			System.out.println(tf.numRows());
			System.out.println("3");
			System.out.println(tf.getAllTracks().get(0));
			tf.reset();
			*/
			//System.out.println(tf.getFieldDescriptions());
			tf.addFieldDescription(1,"foo","foo","foo","foo");
			tf.write("Test.atf");
		} catch(Exception e){
			System.out.println("Woops!");
			System.out.println(e);
		}
	}
	
}
