package com.elcansoftware.elcanpos;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class detailsElcanPos extends Activity {
	String sku;
	Context ctx;
	elcanPosDataHelper dbh;
	Cursor cur;
	SQLiteDatabase db;
	boolean out;
	public static final String PREFS_NAME = "elcanpos_settings";
	public void onCreate(Bundle savedInstanceState) {
		Bundle bundle = getIntent().getExtras();
		sku=bundle.getString("sku");
		ctx=getApplicationContext();
		dbh=new elcanPosDataHelper(ctx);
		out=false;
		super.onCreate(savedInstanceState);
		try {
			db=dbh.getReadableDatabase();
		} catch (Exception e) {
			Toast t=Toast.makeText(ctx, "Error: "+e.getMessage(), Toast.LENGTH_LONG);
			t.show();
		}
        setContentView(R.layout.details);
        Button btClose=(Button) findViewById(R.id.btClose);
        
        btClose.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				out=true;
				finish();
			}
        });
        FillValues();
	}	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	  setContentView(R.layout.details);
	}
	
	public void FillValues() {
		TextView tvIName=(TextView) findViewById(R.id.tvItemName);
		TextView tvIDesc=(TextView) findViewById(R.id.tvItemDesc);
		TextView tvIPrice=(TextView) findViewById(R.id.tvItemPrice);
		ImageView ivImage=(ImageView) findViewById(R.id.ivItemImage);
		TextView tvStock=(TextView) findViewById(R.id.tvItemStock);
		TextView tvCategory=(TextView) findViewById(R.id.tvCategory);
		SharedPreferences preferences =  getSharedPreferences(PREFS_NAME, 0);
		String picPath=preferences.getString("folder", Environment.getExternalStorageDirectory().getPath());
		Bitmap myBitmap;
		try {
			cur=db.rawQuery("SELECT a.*, c.catdesc FROM articles as a LEFT JOIN categories as c ON a.category=c.category WHERE sku=?", new String[]{sku});
			cur.moveToFirst();
			tvIName.setText(cur.getString(cur.getColumnIndex("name")));
			tvIDesc.setText(cur.getString(cur.getColumnIndex("description")));
			tvIPrice.setText(cur.getString(cur.getColumnIndex("price")));
			tvStock.setText(cur.getString(cur.getColumnIndex("stock")));
			tvCategory.setText(cur.getString(cur.getColumnIndex("catdesc")));
			String file=picPath+"/elcanpos/images/"+cur.getString(cur.getColumnIndex("image"));
			File tst=new File(file);
			if (tst.exists()) {
				myBitmap = BitmapFactory.decodeFile(file);
			} else {
				myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.noimage);
				Toast t=Toast.makeText(ctx, "File:"+file+" doesn't exist", Toast.LENGTH_LONG);
				t.show();
			}
			ivImage.setImageBitmap(myBitmap);
			ivImage.setAdjustViewBounds(true);
		} catch (Exception e) {
			Toast t=Toast.makeText(ctx, "Failure:"+e.getMessage()+" sku:"+sku, Toast.LENGTH_LONG);
			t.show();
		}
	}

	public void onDestroy() {
		if (!out) {
			Intent inte=new Intent();
			inte.putExtra("sku", "");
			setResult(RESULT_CANCELED,inte);
		}
		super.onDestroy();
	}
}
