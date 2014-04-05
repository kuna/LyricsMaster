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
			Toast.makeText(_c, workingMusicTitle + " - ���� ���� �Ϸ�.", Toast.LENGTH_SHORT).show();
			break;
		case H_LYRICS_FAIL:
			Toast.makeText(_c, workingMusicTitle + " - ���縦 ã�� ���߽��ϴ�.", Toast.LENGTH_SHORT).show();
			break;
		case H_SEARCH_SUCCESS:
			Toast.makeText(_c, "���� ���� �Ϸ�.", Toast.LENGTH_SHORT).show();
			break;
		case H_SEARCH_FAIL:
			Toast.makeText(_c, "���縦 ã�� ���߽��ϴ�.", Toast.LENGTH_SHORT).show();
			break;
		case H_WAIT:
			Toast.makeText(_c, "�۾����̴� ���� ��ٷ� ������...", Toast.LENGTH_SHORT).show();
			break;
		}
	}
}
