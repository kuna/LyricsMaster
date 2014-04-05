package com.kuna.lyricsmaster.musicview;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kuna.lyricsmaster.MusicInfo;
import com.kuna.lyricsmaster.tag.TagModifier;

public class MusicAdapter extends BaseAdapter {
	private Context c;
	private LayoutInflater flater;
	private ArrayList<MusicInfo> arrMusicInfo;
	
	public MusicAdapter (Context c, int layout,ArrayList<MusicInfo> arrMusicInfo) {
		this.c = c;
		flater = (LayoutInflater)c.getSystemService(c.LAYOUT_INFLATER_SERVICE);
		this.arrMusicInfo = arrMusicInfo;
	}

	@Override
	public int getCount() {
		return this.arrMusicInfo.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// position
		return arg0;
	}

	@Override
	public View getView(int pos, View v, ViewGroup parent) {
		// TODO Auto-generated method stub
		// IMPORTANT@@@@@@@
		// run thread about getting isUSLTtag Exisitng
		
		if (v == null) {
			v = flater.inflate(com.kuna.lyricsmaster.R.layout.lv_musicinfo, parent, false);
		}
		
		TextView tv_title = (TextView) v.findViewById(com.kuna.lyricsmaster.R.id.tv_title);
		tv_title.setText(arrMusicInfo.get(pos).title);
		
		TextView tv_artist = (TextView) v.findViewById(com.kuna.lyricsmaster.R.id.tv_artist);
		tv_artist.setText(arrMusicInfo.get(pos).artist);

		//TextView tv_lyrics = (TextView) v.findViewById(com.kuna.lyricsmaster.R.id.tv_lrc);
		//tv_lyrics.setText( (TagModifier.checkUSLTTag(arrMusicInfo.get(pos).path))?"O":"X" );
		
		return v;
	}
}
