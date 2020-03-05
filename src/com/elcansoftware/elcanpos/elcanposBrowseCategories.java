package com.elcansoftware.elcanpos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class elcanposBrowseCategories extends Activity {
	Button btCancel,btBack;
	ListView lvBrowse;
	Cursor res,res2;
	Context ctx;
	Boolean out;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browsecategories);
        ctx=getApplicationContext();
        out=false;
        btCancel=(Button) findViewById(R.id.btCancelBrows);
        btBack=(Button) findViewById(R.id.btBackCat);
        lvBrowse=(ListView) findViewById(R.id.lvBrowseCateg);
        btCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent inte=new Intent();
				inte.putExtra("sku", "0");
				setResult(RESULT_CANCELED,inte);
				out=true;
				finish();
			}
		});
        btBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				fillCategories();
			}
		});
        fillCategories();
    }

    @Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	  setContentView(R.layout.browsecategories);
	}
    
    public void onDestroy() {
    	Intent inte=new Intent();
		inte.putExtra("sku", "0");
    	if (!out) setResult(RESULT_CANCELED,inte);
    	super.onDestroy();
    }
    
    public void fillCategories() {
    	SimpleCursorAdapter adapter;
    	SQLiteDatabase srcDb;
    	elcanPosDataHelper edb=new elcanPosDataHelper(ctx);
    	srcDb=edb.getReadableDatabase();
    	try {
    	  res=srcDb.rawQuery("SELECT * FROM categories WHERE 1 ORDER BY category ASC", null);
    	  adapter= new SimpleCursorAdapter(
                this, 
                R.layout.categlist, 
                res, 
                new String[] {"catdesc","category"}, 
                new int[] {R.id.lcategDesc,R.id.lcateg});
    	  lvBrowse.setAdapter(adapter);
    	  lvBrowse.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				res.moveToPosition(pos);
				fillItems(res.getString(res.getColumnIndex("category")));
				return;
			}
    	  });
    	} catch (Exception e) {
    		Toast t= Toast.makeText(ctx,"FAILED:"+e.getMessage(), Toast.LENGTH_LONG);
    		t.show();
    	}
    }
    
    public void fillItems(String categ) {
    	SimpleCursorAdapter adapter;
    	SQLiteDatabase srcDb;
    	elcanPosDataHelper edb=new elcanPosDataHelper(ctx);
    	srcDb=edb.getReadableDatabase();
    	try {
    	  res2=srcDb.rawQuery("SELECT * FROM articles WHERE category LIKE ? ORDER BY name ASC", new String[]{categ});
    	  adapter= new SimpleCursorAdapter(
                this, 
                R.layout.itemmatches, 
                res2, 
                new String[] {"name","sku","price"}, 
	  			new int[] {R.id.tvMName,R.id.tvMSku,R.id.tvMPrice});
    	  lvBrowse.setAdapter(adapter);
    	  lvBrowse.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				res2.moveToPosition(pos);
				Intent inte=new Intent();
				inte.putExtra("sku", res2.getString(res2.getColumnIndex("sku")));
				out=true;
				setResult(RESULT_OK,inte);
				finish();
			}
    	  });
    	} catch (Exception e) {
    		Toast t=Toast.makeText(ctx, "Error:"+e.getMessage(), Toast.LENGTH_LONG);
    		t.show();
    	}
    }
}
