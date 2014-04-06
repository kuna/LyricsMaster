package com.kuna.lyricsmaster;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.Log;

// http://developer.android.com/reference/android/provider/MediaStore.Audio.html
// http://stackoverflow.com/questions/8994625/display-all-music-on-sd-card

public class MediaLibrary {
	// inner Arraylist
	public ArrayList<String> mlTitle = new ArrayList<String>();
	public ArrayList<String> mlArtist = new ArrayList<String>();
	public ArrayList<String> mlPath = new ArrayList<String>();
	public int mlCount;
	
	private ArrayList<String> mlDirList = new ArrayList<String>();
	
	// outer Arraylist
	/**/
	
	// MUST CALLED BEFORE PROCEDURE ANY PROC.
	public boolean getMusicList(Context c) {
		//MediaPlayer mMediaPlayer;
		Cursor mCursor = c.getContentResolver().query(
	            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
	            new String[] { MediaStore.Audio.Media.DATA,
	            		MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM }, null, null,
	            "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");
		
		mlCount = 0;
		if (mCursor.moveToFirst()) {
			do {
				mlPath.add(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
				mlTitle.add(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
				mlArtist.add(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
				mlCount++;
			} while (mCursor.moveToNext());
		}
		
		mCursor.close();
		
		// create dir list
		Log.i("SIZE", Integer.toString(mlPath.size()));
		for (String p: mlPath) {
			String dir = getDirfromString(p);
			if (!mlDirList.contains(dir))
				mlDirList.add(getDirfromString(p));
		}
		
		return true;
	}
	
	private String getDirfromString(String path) {
		int i = path.lastIndexOf(File.separator);
		if (i < 0) return path;
		return path.substring(0, i);
	}
	
	// check path & set display list
	public ArrayList<MusicInfo> getMusicListFromDirectory(String parentPath) {
		ArrayList<MusicInfo> arrmi = new ArrayList<MusicInfo>();
		for (int i=0; i<mlPath.size(); i++) {
			if (parentPath.length() == 0 || getDirfromString(mlPath.get(i).toLowerCase()).compareToIgnoreCase(parentPath.toLowerCase()) == 0) {
				MusicInfo mi = new MusicInfo();
				mi.path = mlPath.get(i);
				mi.title = mlTitle.get(i);
				mi.artist = mlArtist.get(i);
				arrmi.add(mi);
			}
		}
		
		return arrmi;
	}
	
	public ArrayList<String> getParentDirectoryList(String parentPath) {
		// if parentpath is null? then get it from null string
		// you'd better to add '..' function to go back
		
		@SuppressWarnings("unchecked")
		ArrayList<String> pathListcpy = (ArrayList<String>) mlDirList.clone();
		
		// 1. filters first
		Log.i("REMOVE", parentPath);
		if (parentPath.length() > 0) {
			//parentPath += "\\";
			for (int i=0; i<pathListcpy.size(); i++) {
				if (!pathListcpy.get(i).toLowerCase().startsWith(parentPath.toLowerCase())) {
					pathListcpy.remove(i);
					i--;
				}
			}
		}
		
		// 2. make only top folder list
		/*for (int j=0; j<pathListcpy.size(); j++) {
			for (int i=j+1; i<pathListcpy.size(); i++) {
				String s = pathListcpy.get(j);
				if (s.length() > 0) {
					s += "\\";
					if (pathListcpy.get(i).startsWith(s)) {
						pathListcpy.remove(i);
						i--;
					}
				}
			}
		}*/
		
		return pathListcpy;
	}
}
