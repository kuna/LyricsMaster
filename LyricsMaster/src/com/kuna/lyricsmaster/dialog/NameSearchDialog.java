package com.kuna.lyricsmaster.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NameSearchDialog extends Dialog {
	public Button btn_cancel, btn_search;
	public EditText edit_search;
	
	public NameSearchDialog(Context context, View.OnClickListener searchListener) {
		super(context);
		setContentView(com.kuna.lyricsmaster.R.layout.dlg_namesearch);

		btn_cancel = (Button)findViewById(com.kuna.lyricsmaster.R.id.btn_cancel);
		btn_search = (Button)findViewById(com.kuna.lyricsmaster.R.id.btn_search);
		edit_search = (EditText)findViewById(com.kuna.lyricsmaster.R.id.edit_search);
		
		btn_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dismiss();
			}
		});
		
		btn_search.setOnClickListener(searchListener);
	}

}
