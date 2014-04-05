package com.kuna.lyricsmaster;

import java.io.File;
import java.util.ArrayList;

import com.kuna.lyricsmaster.dialog.SearchDialog;
import com.kuna.lyricsmaster.lyrics.LyricsData;
import com.kuna.lyricsmaster.lyrics.LyricsParser;
import com.kuna.lyricsmaster.lyrics.MusicMD5Hash;
import com.kuna.lyricsmaster.musicview.MusicAdapter;
import com.kuna.lyricsmaster.tag.TagModifier;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	String nPath = "";
	MediaLibrary ml;
	
	Button bPath;
	Button bSetting;
	Button bSubmit;
	ListView lvMusicList;
	
	ArrayList<MusicInfo> arrmi;
	Handler mhandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// get obj
		bPath = (Button) findViewById(R.id.btn_path);
		bSetting = (Button) findViewById(R.id.btn_setting);
		bSubmit = (Button) findViewById(R.id.btnSearch);
		lvMusicList = (ListView)findViewById(R.id.listView1);
		
		// gather music list
		ml = new MediaLibrary();
		ml.getMusicList(this);
		
		// init handler
		mhandler = new MainHandler(this);
		
		// attach event
		lvMusicList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v, int pos,
					long arg3) {
				// do music update
				MusicInfo mi = arrmi.get(pos);
				addMI2Queue(mi);
			}
		});
		lvMusicList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				MusicInfo mi = arrmi.get(pos);
				
				// show search dialog
				SearchDialog sDlg = new SearchDialog(_c, mi, mhandler);
				sDlg.setTitle("가사 검색창");
				sDlg.show();
				
				return false;
			}
		});
		bPath.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// show path select dialog
				final ArrayList<String> pathArr = new ArrayList<String>();
				final ArrayList<String> pathNameArr = new ArrayList<String>();
				pathArr.add("(ALL)");
				pathArr.add("(PARENT)");
				pathArr.addAll(ml.getParentDirectoryList(nPath));
				
				for (String s: pathArr) {
					pathNameArr.add(getOnlyDirName(s));
				}
				
				AlertDialog.Builder dirDlg = new AlertDialog.Builder(_c);
				dirDlg.setTitle("폴더 고르셈...");
				ArrayAdapter<String> aAdapt = new ArrayAdapter<String>(_c, android.R.layout.select_dialog_item, pathNameArr);
				
				dirDlg.setAdapter(aAdapt, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String newPath = pathArr.get(which);
						bPath.setText(pathNameArr.get(which));
						if (newPath.equals("(ALL)")) {
							nPath = "";
						} else if (newPath.equals("(PARENT)")) {
							int i = nPath.lastIndexOf(File.separator);
							if (i < 0) nPath = "";
							else nPath = nPath.substring(0,i);
							bPath.setText(getOnlyDirName(nPath));
						} else {
							nPath = newPath;
						}
						setListViewItem(ml.getMusicListFromDirectory(nPath));
					}
				});
				dirDlg.show();
			}
		});
		bSetting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(_c, "구현 안 함.\n이건 도움말로 대체함.\n노래 오래 누르면 검색창으로 노래 가사 집어넣을수도 있다.\n꼬우면 여기로 멘션 보내던가...\n@lazykuna", Toast.LENGTH_SHORT).show();
			}
		});
		bSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// add all song 2 queue (or stop)
				if (workArr.size() > 0) {
					workArr.clear();
				} else {
					addMI2Queue(arrmi);
				}
			}
		});
		
		// init listview
		setListViewItem(ml.getMusicListFromDirectory(""));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void setListViewItem(ArrayList<MusicInfo> mi) {
		// add listview item
		arrmi = mi;
		ListAdapter mlAdapter = new MusicAdapter(this, 0, mi);
		lvMusicList.setAdapter(mlAdapter);
	}
	
	private final static int NOTIFYID = 1;
	private boolean isNotificationCreated = false;
	private Notification nf;
	private NotificationManager nm;
	private void CreateNotificationBar() {
		Context c = getApplicationContext();
		
		if (!isNotificationCreated) {
			String ns = Context.NOTIFICATION_SERVICE;
			nm = (NotificationManager) getSystemService(ns);
			
			int icon = R.drawable.ic_launcher;
			nf = new Notification(icon, "LYRICSMASTER", System.currentTimeMillis());
			
			isNotificationCreated = true;
		}
		
		Intent nfIntent = new Intent(c, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, nfIntent, 0);
		if (workArr.size() > 0)
			nf.setLatestEventInfo(c, workArr.get(0).title+" 처리중...", workArr.size()+"개 남음", contentIntent);
		
		nm.notify(NOTIFYID, nf);
	}
	
	private void CloseNotificationBar() {
		if (isNotificationCreated) {
			nm.cancel(NOTIFYID);
			isNotificationCreated = false;
		}
	}
	
	private void addMI2Queue(ArrayList<MusicInfo> mi) {
		workArr.addAll(mi);
		if (!isNotificationCreated) {
			Thread t = new Thread(null, workThread, "Background");
			t.start();
		}	
	}
	
	private void addMI2Queue(MusicInfo mi) {
		workArr.add(mi);
		if (!isNotificationCreated) {
			Thread t = new Thread(null, workThread, "Background");
			t.start();
		}	
	}
	
	/**
	 * Background working
	 */
	private ArrayList<MusicInfo> workArr = new ArrayList<MusicInfo>();
	private boolean threadFinished = true;
	private Context _c = this;
	private Runnable workThread = new Runnable() {
		@Override
		public void run() {
			try {
				if (!threadFinished)
					return;
				threadFinished = false;
				
				// START WORK
				while (workArr.size() > 0) {
					CreateNotificationBar();
					MainHandler _m = (MainHandler)mhandler;
					_m.setTitle(workArr.get(0).title);
							
					MusicMD5Hash mMD5Hash = new MusicMD5Hash();
					String fPath = workArr.get(0).path;
					
					mMD5Hash.LoadFile(fPath);

					LyricsParser lp = new LyricsParser();
					lp.RequestAlsongServer(mMD5Hash.MD5Hash);
					LyricsData arrLyrics = new LyricsData();
					if (lp.parseLyricsResult(arrLyrics)) {
						arrLyrics.saveLRC( LyricsData.convertExtension2LRC(fPath) );
						System.out.println("가사 저장 완료.");
						
						//if (writeLyrics2File) {
						TagModifier.writeUSLTTag(fPath, arrLyrics.getOnlyLyrics());
						mhandler.sendEmptyMessage(MainHandler.H_LYRICS_SUCCESS);
						//}
					} else {
						mhandler.sendEmptyMessage(MainHandler.H_LYRICS_FAIL);
					}
					
					workArr.remove(0);
				}
				
				CloseNotificationBar();
				
				// END WORK
				threadFinished = true;
			} catch (Exception e) {
				e.printStackTrace();
				threadFinished = true;
			}
		}
	};
	
	private String getOnlyDirName(String dir) {
		int i = dir.lastIndexOf(File.separator);
		if (i < 0) return dir;
		return dir.substring(i+1);
	}
}
