package com.kuna.lyricsmaster.dialog;

import com.kuna.lyricsmaster.MainHandler;
import com.kuna.lyricsmaster.MusicInfo;
import com.kuna.lyricsmaster.lyrics.LyricsData;
import com.kuna.lyricsmaster.lyrics.LyricsParser;
import com.kuna.lyricsmaster.lyrics.MusicMD5Hash;
import com.kuna.lyricsmaster.tag.TagModifier;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SearchDialog extends Dialog {
	private Context _c; 
	
	private EditText tvTitle, tvArtist;
	private Button btn; 
	
	private String title, artist;
	private MusicInfo mi;
	private Handler mhandler;
	
	public SearchDialog(Context context, MusicInfo _mi, Handler _mhandler) {
		super(context);
		setContentView(com.kuna.lyricsmaster.R.layout.dlg_search);

		this.mi = _mi;
		this.mhandler = _mhandler;
		setText(mi.title, mi.artist);

		tvTitle = (EditText)findViewById(com.kuna.lyricsmaster.R.id.tvTitle);
		tvArtist = (EditText)findViewById(com.kuna.lyricsmaster.R.id.tvArtist);
		tvTitle.setText(title);
		tvArtist.setText(artist);
		
		
		btn = (Button)findViewById(com.kuna.lyricsmaster.R.id.btnSearch);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!isThreadWorking) {
					title = tvTitle.getText().toString();
					artist = tvArtist.getText().toString();
					Thread t = new Thread(searchThread);
					t.start();
				} else {
					mhandler.sendEmptyMessage(MainHandler.H_WAIT);
				}
			}
		});
		
		btn = (Button)findViewById(com.kuna.lyricsmaster.R.id.btnCancel);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dismiss();
			}
		});
		
		_c = context;
	}
	
	public void setText(String title, String artist) {
		this.title = title;
		this.artist = artist;
	}

	private boolean isThreadWorking = false;
	private Runnable searchThread = new Runnable() {
		@Override
		public void run() {
			if (isThreadWorking) return;
			isThreadWorking = true;
			
			/* work start */
			if (title.length() > 0) {
				String fPath = mi.path;
				LyricsParser lp = new LyricsParser();
				lp.SearchAlsongServer(title, artist);
				LyricsData arrLyrics = new LyricsData();
				if (lp.parseLyricsResult(arrLyrics)) {
					arrLyrics.saveLRC( LyricsData.convertExtension2LRC(fPath) );
					
					TagModifier.writeUSLTTag(fPath, arrLyrics.getOnlyLyrics());
					mhandler.sendEmptyMessage(MainHandler.H_SEARCH_SUCCESS);
				} else {
					mhandler.sendEmptyMessage(MainHandler.H_SEARCH_FAIL);
				}
			}
			/* work end */
			
			isThreadWorking = false;
		}
	};
}
