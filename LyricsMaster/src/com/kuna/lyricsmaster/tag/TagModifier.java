package com.kuna.lyricsmaster.tag;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.generic.AbstractTag;
import org.jaudiotagger.audio.generic.GenericTag;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.images.Artwork;


public class TagModifier {
	public static boolean writeUSLTTag(String src, String lrc) {
		try {
			AudioFile f = AudioFileIO.read(new File(src));
			Tag t = f.getTag();
			if (t == null)
				t = new GenericTag() {
				};
			t.setField(FieldKey.LYRICS, lrc);
			AudioFileIO.write(f);
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean checkUSLTTag(String src) {
		try {
			AudioFile f = AudioFileIO.read(new File(src));
			Tag t = f.getTag();
			return (t.getFields(FieldKey.LYRICS).size() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
