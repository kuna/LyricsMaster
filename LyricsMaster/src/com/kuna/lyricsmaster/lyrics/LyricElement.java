package com.kuna.lyricsmaster.lyrics;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LyricElement {
	public int time; // 10ms
	public String lyrics;
	
	public boolean setElement(String str) {
		Pattern lrcPat = Pattern.compile("\\[(\\d+):(\\d+)\\.(\\d+)\\](.*)");
		Matcher mc = lrcPat.matcher(str);
		if (mc.matches()) {
			int min = Integer.parseInt(mc.group(1));
			int sec = Integer.parseInt(mc.group(2));
			int ms = Integer.parseInt(mc.group(3));
			time = min*60*100 + sec*100 + ms;
			lyrics = mc.group(4);
			return true;
		}
		return false;
	}
	
	public String getElement() {
		String r = "";
		r = "[";
		r += String.format("%02d", (int)(time / 100 / 60));
		r += ":";
		r += String.format("%02d", (int)(time / 100 % 60));
		r += ".";
		r += String.format("%02d", time % 100);
		r += "]";
		r += lyrics;
		return r;
	}
}
