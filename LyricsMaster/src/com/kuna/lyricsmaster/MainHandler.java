package com.kuna.lyricsmaster;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class MainHandler extends Handler {

	public final static int H_LYRICS_SUCCESS = 1;
	public final static int H_LYRICS_FAIL = 2;
	public final static int H_SEARCH_SUCCESS = 3;
	public final static int H_SEARCH_FAIL = 4;
	public final static int H_WAIT = 5;
	private String workingMusicTitle = "";
	private Context _c;
	
	public MainHandler(Context c) {
		_c = c;
	}
	
	public void setTitle(String str) {
		workingMusicTitle = str;
	}
	
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		
		switch (msg.what) {
		case H_LYRICS_SUCCESS:
			Toast.makeText(_c, workingMusicTitle + " - 가사 저장 완료.", Toast.LENGTH_SHORT).show();
			break;
		case H_LYRICS_FAIL:
			Toast.makeText(_c, workingMusicTitle + " - 가사를 찾지 못했습니다.", Toast.LENGTH_SHORT).show();
			break;
		case H_SEARCH_SUCCESS:
			Toast.makeText(_c, "가사 저장 완료.", Toast.LENGTH_SHORT).show();
			break;
		case H_SEARCH_FAIL:
			Toast.makeText(_c, "가사를 찾지 못했습니다.", Toast.LENGTH_SHORT).show();
			break;
		case H_WAIT:
			Toast.makeText(_c, "작업중이니 좀만 기다려 보랑께...", Toast.LENGTH_SHORT).show();
			break;
		}
	}
}
