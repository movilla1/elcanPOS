package com.elcansoftware.elcanpos;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ElcanFolderSelect extends Activity {
	File startdir;
	String path;
	Boolean cancel;
	TextView tvDFPath;
	ListView lvEDF;
	ArrayAdapter<String> adapter;
	ArrayList<String> listItems=new ArrayList<String>();

	public void onCreate(Bundle savedInstanceState) {
		Bundle bundle = getIntent().getExtras();
		path=bundle.getString("startPath");
		if (path.length()<=1) {
			path="/mnt";
		}
		cancel=false;
		adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.elcanfolderselect);
		Button btCancel=(Button) findViewById(R.id.btDFCancel);
		Button btSelect=(Button) findViewById(R.id.btDFSelect);
		Button btBack=(Button) findViewById(R.id.btBackPath);
		tvDFPath=(TextView) findViewById(R.id.tvDFPath);
		tvDFPath.setText(path);
		lvEDF=(ListView) findViewById(R.id.lvDFList);
		lvEDF.setAdapter(adapter);
		btCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				cancel=true;
				finish();
			}
		});
		btSelect.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				cancel=false;
				Intent inte=new Intent();
				inte.putExtra("path", tvDFPath.getText().toString());
				setResult(RESULT_OK,inte);
				finish();
			}
		});
		btBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				goBackPath();
			}
		});
		lvEDF.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				changeFolder(arg2);
			}
		});
		this.fillList(path);
	}
	public void onDestroy() {
		Intent inte=new Intent();
		if (cancel) {
			inte.putExtra("path", "");
			setResult(RESULT_CANCELED,inte);
		}
		super.onDestroy();
	}
	public void changeFolder(int pos) {
		String itemText=(String) lvEDF.getItemAtPosition(pos);
		String newpath="/";
		String parts[]=tvDFPath.getText().toString().split("/"); //get all the folder names to rejoin.
		for (int x=1;x<parts.length;x++) {
			newpath=newpath.concat(parts[x])+"/";
		}
		tvDFPath.setText(newpath+itemText);
		fillList(tvDFPath.getText().toString());
	}

	public void fillList(String path) {
		Integer cont=0;
		String flist[];
		File tmpfile;
		startdir=new File(path);
		if (startdir.isDirectory()) {
			//System.out.println("Is a folder! "+ startdir.getPath());
			listItems.clear();
			flist=startdir.list();
			//System.out.println("GOT:"+flist.toString());
			while(flist!=null && flist.length>cont) {
				tmpfile=new File(flist[cont]);
				if (tmpfile.isDirectory()) listItems.add(flist[cont]);
				cont++;
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	public void goBackPath() {
		String path=tvDFPath.getText().toString();
		String parts[]=path.split("/");
		if (parts.length<2) {
			Toast t=Toast.makeText(this.getBaseContext(), R.string.cantgoback, Toast.LENGTH_LONG);
			t.show();
			return;
		}
		String newpath="/";
		for (int x=1;x<parts.length-1;x++) {
			newpath=newpath.concat(parts[x])+"/";
		}
		tvDFPath.setText(newpath);
		this.fillList(newpath);
	}
}
