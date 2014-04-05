package com.kuna.lyricsmaster.lyrics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LyricsData {
	public String title;
	public String artist;
	public String album;
	public String creator;
	public String LRCcreator;
	public ArrayList<LyricElement> arrLyrics = new ArrayList<LyricElement>();
	
	private String unescapeHTMLEntity(String str) {
		return str.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("<br>", "\n");
	}
	
	public void parseAlsongLyricsString(String str) {
		String[] lines = unescapeHTMLEntity(str).split("\n");
		for (String i : lines) {
			LyricElement l = new LyricElement();
			if (l.setElement(i))
				arrLyrics.add(l);
		}
	}
	
	public void saveLRC(String path) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(path));
			
			if (artist.length() > 0) {
				out.write("[ar:" + artist + "]");
				out.newLine();
			}
			if (album.length() > 0) {
				out.write("[al:" + album + "]");
				out.newLine();
			}
			if (title.length() > 0) {
				out.write("[ti:" + title + "]");
				out.newLine();
			}
			if (creator.length() > 0) {
				out.write("[au:" + creator + "]");
				out.newLine();
			}
			if (LRCcreator.length() > 0) {
				out.write("[by:" + LRCcreator + "]");
				out.newLine();
			}
			
			for (LyricElement l : arrLyrics) {
				out.write(l.getElement());
				out.newLine();
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadLRC(String path) {
		clear();
		
		try {
			BufferedReader br = new BufferedReader( new FileReader(path) );
			String buf;
			while ((buf = br.readLine())!=null) {
				Pattern lrcPat = Pattern.compile("\\[([A-z]*):([^\\]]*)](.*)");
				Matcher mc = lrcPat.matcher(buf);
				if (mc.matches()) {
					String attr = mc.group(1);
					String attrValue = mc.group(2);

					if (attr.compareToIgnoreCase("ar") == 0)
						artist = attrValue;
					if (attr.compareToIgnoreCase("al") == 0)
						album = attrValue;
					if (attr.compareToIgnoreCase("ti") == 0)
						title = attrValue;
					if (attr.compareToIgnoreCase("au") == 0)
						creator = attrValue;
					if (attr.compareToIgnoreCase("by") == 0)
						LRCcreator = attrValue;
				} else {
					LyricElement l = new LyricElement();
					if (l.setElement(buf)) {
						arrLyrics.add(l);
					}
				}
				
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getOnlyLyrics() {
		String r = "";
		for (LyricElement i: arrLyrics) {
			r += i.lyrics;
			r += "\r\n";
		}
		return r;
	}
	
	public void clear() {
		arrLyrics.clear();
		artist = "";
		album = "";
		title = "";
		creator = "";
		LRCcreator = "";
	}
	
	public static String convertExtension2LRC(String path) {
		String[] ar = path.split("\\.");
		if (ar.length == 1) {
			return path+".lrc";
		} else {
			ar[ar.length-1] = "lrc";
			String r = "";
			for (String i: ar) {
				r += i;
				r += ".";
			}
			r = r.substring(0, r.length()-1);
			return r;
		}
	}
}
