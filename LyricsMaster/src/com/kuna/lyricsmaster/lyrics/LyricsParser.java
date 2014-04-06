package com.kuna.lyricsmaster.lyrics;

import android.annotation.SuppressLint;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

// http://blog.naver.com/PostView.nhn?blogId=silhwan5125&logNo=100102227811

public class LyricsParser {
	private String lrcData;
	
	public boolean SearchAlsongServer(String title, String artist) {
		String parameter = CreateSearchString(title, artist);
		return RequestAlsongServerParameter(parameter);
	}
	
	public boolean RequestAlsongServer(String md5) {
		String parameter = CreateRequestString(md5);//URLEncoder.encode(CreateRequestString(md5), "UTF-8");
		return RequestAlsongServerParameter(parameter);
	}
	
	
	public boolean RequestAlsongServerParameter(String parameter) {
		lrcData = "";
		
		try {
			URL url = new URL("http://lyrics.alsong.co.kr/alsongwebservice/service1.asmx");
			URLConnection con = url.openConnection();
			
			System.setProperty("http.agent", "gSOAP/2.7");
			con.setRequestProperty("User-Agent", "gSOAP/2.7");
			con.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
			con.setRequestProperty("Connection", "close");
			con.setRequestProperty("SOAPAction", "ALSongWebServer/GetLyric5");
			con.setDoOutput(true);		// set POST request
			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
			wr.write(parameter);
			wr.flush();
			
			// get response
			BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			String line;
			while ((line = rd.readLine()) != null) {
				lrcData += line;
			}
			
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	private String CreateRequestString(String md5) {
		String PostStr_A1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://www.w3.org/2003/05/soap-envelope\" " +
                "xmlns:SOAP-ENC=\"http://www.w3.org/2003/05/soap-encoding\" " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                "xmlns:ns2=\"ALSongWebServer/Service1Soap\" " +
                "xmlns:ns1=\"ALSongWebServer\" xmlns:ns3=\"ALSongWebServer/Service1Soap12\">" +
                "<SOAP-ENV:Body>" +
                "<ns1:GetLyric5>" +
                "<ns1:stQuery>" +
                "<ns1:strChecksum>";
		
		String PostStr_A2 = "</ns1:strChecksum>" +
                "<ns1:strVersion>2.0 beta2</ns1:strVersion>" +
                "<ns1:strMACAddress>";

		String PostStr_A3 = "</ns1:strMACAddress>" +
                "<ns1:strIPAddress>255.255.255.0</ns1:strIPAddress>" +
                "</ns1:stQuery>" +
                "</ns1:GetLyric5>" +
                "</SOAP-ENV:Body>" +
                "</SOAP-ENV:Envelope>"; 

		return PostStr_A1 + md5 + PostStr_A2 + GetMACAddress() + PostStr_A3;
	}
	
	private String CreateSearchString(String title, String artist) {
		String PostStr_A1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://www.w3.org/2003/05/soap-envelope\" " +
                "xmlns:SOAP-ENC=\"http://www.w3.org/2003/05/soap-encoding\" " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                "xmlns:ns2=\"ALSongWebServer/Service1Soap\" " +
                "xmlns:ns1=\"ALSongWebServer\" xmlns:ns3=\"ALSongWebServer/Service1Soap12\">" +
                "<SOAP-ENV:Body>" +
                "<ns1:GetSyncLyricBySearch>" +
                "<ns1:title>";

		String PostStr_A2 = 
                "</ns1:title>" +
                "<ns1:artist>";
		
		String PostStr_A3 = 
                "</ns1:artist>" + "<ns1:encData>b6437d6c99ef9324af560b3ea659828e63b1087616d9e458e7b0fa2c93ee87e36d9c4aa0094b02b53b0e3bf008c4bfec89898f8cbf15a2f6cc8000e78d71e7899cfbfaffacf100618bb7d0dfa726b67429637d34cc1325e99b68a3e45a2cdeae9ee357832a3697a651a8ae4c52d4ea34746e1e35c1462a4df889a00c5fbe46f9</ns1:encData>" +
                "</ns1:GetSyncLyricBySearch>" +
                "</SOAP-ENV:Body>" +
                "</SOAP-ENV:Envelope>"; 

		return PostStr_A1 + title + PostStr_A2 + artist + PostStr_A3;
	}
	
	@SuppressLint("NewApi")
	private String GetMACAddress() {
	    try {
	    	InetAddress addr = InetAddress.getLocalHost();
		     /* IP 주소 가져오기 */
		    String ipAddr = addr.getHostAddress();
		    //System.out.println(ipAddr);
		
		    /* 호스트명 가져오기 */
		    String hostname = addr.getHostName();
		    //System.out.println(hostname);
			NetworkInterface ni = NetworkInterface.getByInetAddress(addr);
			byte[] mac = ni.getHardwareAddress();
			String macAddr = "";
			for (int i = 0; i < mac.length; i++) {
				macAddr += String.format("%02X%s", mac[i], (i < mac.length - 1) ? "" : "");
			}
			//System.out.println(macAddr);
			return macAddr;
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    
	    // failed
	    return "";
	}
	
	public boolean parseLyricsResult(LyricsData ld) {
		// XML
		try {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(lrcData)));

			NodeList nStatus = document.getElementsByTagName("strStatusID");
			if (!nStatus.item(0).getTextContent().equals("1"))
				return false;
			
			NodeList nTitle = document.getElementsByTagName("strTitle");
			NodeList nArtist = document.getElementsByTagName("strArtist");
			NodeList nAlbum = document.getElementsByTagName("strAlbum");
			NodeList nLyrics = document.getElementsByTagName("strLyric");
			NodeList nCreator = document.getElementsByTagName("strRegisterFirstName");
			NodeList nLRCCreator = document.getElementsByTagName("strRegisterName");

			ld.clear();
			
			ld.title = nTitle.item(0).getTextContent();
			ld.artist = nArtist.item(0).getTextContent();
			ld.album = nAlbum.item(0).getTextContent();
			ld.creator = nCreator.item(0).getTextContent();
			ld.LRCcreator = nLRCCreator.item(0).getTextContent();
			ld.parseAlsongLyricsString(nLyrics.item(0).getTextContent());
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
