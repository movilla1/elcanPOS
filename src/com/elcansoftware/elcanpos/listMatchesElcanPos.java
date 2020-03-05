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

public class listMatchesElcanPos extends Activity {
    Cursor cur;
    SimpleCursorAdapter adapter;
    elcanPosDataHelper edb;
    SQLiteDatabase srcDb;
    ListView lv;
    Button bt;
    String sku,partSku;
    Context ctx;
    boolean out;
    
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		out=false;
		Bundle bundle = getIntent().getExtras();
		partSku=bundle.getString("sku");
		sku=partSku;
		setContentView(R.layout.listmatches);
		lv=(ListView) findViewById(R.id.lvMatches);
		bt=(Button) findViewById(R.id.btMDone);
		ctx=getApplicationContext();
  		bt.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				returnSKU();
			}
  		});
		try {
			edb=new elcanPosDataHelper(ctx);
			srcDb = edb.getWritableDatabase();
			cur=srcDb.rawQuery("SELECT _id, name, sku, price FROM articles WHERE sku LIKE ? OR name LIKE ?",
							   new String[]{partSku+"%", partSku+"%"});
  	  		adapter= new SimpleCursorAdapter(
  	  				this, 
  	  				R.layout.itemmatches, 
  	  				cur, 
  	  				new String[] {"name","sku","price"}, 
  	  				new int[] {R.id.tvMName,R.id.tvMSku,R.id.tvMPrice});
  	  		lv.setAdapter(adapter);
  	  		lv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View vi,
						int pos, long id) {
					// TODO Auto-generated method stub
					cur.moveToPosition(pos);
					sku=cur.getString(cur.getColumnIndex("sku"));
					returnSKU();
				}
  	  		});
  	  		if (cur.getCount()<1) {
  	  			Toast t=Toast.makeText(ctx, R.string.nomatchingfound, Toast.LENGTH_LONG);
  	  			t.show();
  	  		}
		} catch (Exception e) {
			Toast t=Toast.makeText(getApplicationContext(), "Error:"+e.getMessage(), Toast.LENGTH_SHORT);
			t.show();
		}
		
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	  setContentView(R.layout.listmatches);
	}
	
	public void onDestroy() {
		if (!out) {
		Intent inte=new Intent();
		inte.putExtra("sku", "");
		setResult(RESULT_CANCELED,inte);
		}
		super.onDestroy();
	}
	
	public void returnSKU() {
		Intent result=new Intent();
		result.putExtra("sku",sku);
		setResult(RESULT_OK,result);
		out=true;
		finish();
	}
}
