package com.elcansoftware.elcanpos;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ElcanPOSActivity extends Activity {
	private static final int ACTIVITY_LISTMATCHES = 3;
	private static final int ACTIVITY_VIEW_ORDER = 5;
	private static final int FINISH_SALE = 11;
	private static final int ACTIVITY_SETTINGS = 8;
	private static final int ACTIVITY_SHOWCATEG = 24;
	private static final int UPDATED_ORDER = 45;
	Button btQuery, btAdd, btList, btOrder, btBrowseCat,btnScan;
	ListView elv;
	SQLiteDatabase srcDb;
	Context ctx;
	elcanPosDataHelper edb;
	Cursor res, cur;
	EditText eSku;
	TextView tvCount, tvTotal;
	Integer count;
	Float total;
	String posID;
	Map<String, elcanPosOrder> order;
	Map<String, String> settings;
	public static final String PREFS_NAME = "elcanpos_settings";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		btQuery = (Button) findViewById(R.id.btQuery);
		btAdd = (Button) findViewById(R.id.btAdd);
		btList = (Button) findViewById(R.id.btQList);
		btOrder = (Button) findViewById(R.id.btOrder);
		btBrowseCat = (Button) findViewById(R.id.btBrowsCat);
		btnScan = (Button) findViewById(R.id.btScan);
		elv = (ListView) findViewById(R.id.lvMFS);
		eSku = (EditText) findViewById(R.id.etSku);
		tvCount = (TextView) findViewById(R.id.tvCountMain);
		tvTotal = (TextView) findViewById(R.id.tvTotalMain);
		ctx = getApplicationContext();
		btQuery.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				QueryItem();
			}
		});
		btAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AddItem();
			}
		});
		btList.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ListMatches();
			}
		});
		btOrder.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ShowOrder();
			}
		});
		btBrowseCat.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ShowCategories();
			}
		});
		btnScan.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showScan();
			}
		});
		try {
			edb = new elcanPosDataHelper(ctx);
			srcDb = edb.getWritableDatabase();
		} catch (Exception e) {
			return;
		}
		PopulateListMFS();
		getPosID();
		order = new HashMap<String, elcanPosOrder>();
		count = 0;
		total = (float) 0;
		settings = new HashMap<String, String>();
		reloadSettings();
	}

	private void getPosID() {
		try {
			SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
			posID = preferences.getString("posid", "0");
		} catch(Exception e) {
			Toast t = Toast.makeText(ctx, "Error:" + e.getMessage(),
					Toast.LENGTH_SHORT);
			t.show();
		}
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	  setContentView(R.layout.main);
	}

	private void PopulateListMFS() {
		SimpleCursorAdapter adapter;
		try {
			res = srcDb
					.rawQuery(
							"SELECT a.name,a._id,a.sku FROM articles as a, mostfrecuent as msf WHERE a.sku=msf.sku  ORDER BY msf.tcount DESC LIMIT 10",
							null);
			adapter = new SimpleCursorAdapter(this, R.layout.msflist, res,
					new String[] { "a.name" }, new int[] { R.id.lname });
			elv.setAdapter(adapter);
			elv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int pos, long id) {
					res.moveToPosition(pos);
					eSku.setText(res.getString(res.getColumnIndex("sku")));
					eSku.setSelection(eSku.getText().toString().length());
				}
			});
		} catch (Exception e) {
			Toast t = Toast.makeText(ctx, "FAILED:" + e.getMessage(),
					Toast.LENGTH_LONG);
			t.show();
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		boolean ret = false;
		switch (item.getItemId()) {
		case R.id.newSale:
			if (true) {
				newSale();
			}
			ret = true;
			break;
		case R.id.sync:
			ret = true;
			syncOrders();
			break;
		case R.id.finishSale:
			ret = true;
			finishSale();
			break;
		case R.id.settings:
			showSettings();
			ret = true;
			break;
		case R.id.itExit:
			ret = true;
			finish();
			break;
		case R.id.itOrders:
			showOrders();
			break;
		case R.id.itBackup:
			makeBackupToSD();
			break;
		}
		return ret;
	}

	private void showOrders() {
		Intent myIntent = new Intent(this, showOrderListElcanPos.class);
		startActivity(myIntent);
	}

	private void QueryItem() {
		Intent myIntent = new Intent(this, detailsElcanPos.class);
		myIntent.putExtra("sku", eSku.getText().toString()); // send the sku to query
		startActivity(myIntent);
	}

	private void AddItem() {
		String sku = eSku.getText().toString();
		if (sku.trim().length() < 1) {
			Toast t = Toast.makeText(ctx, R.string.noitemtoadd,
					Toast.LENGTH_SHORT);
			t.show();
			return;
		}
		elcanPosOrder itemOrd = new elcanPosOrder();
		if (checkStock(sku)) {
			count++;
			total += getValue(sku);
			if (order.containsKey(sku)) {
				itemOrd = order.get(sku);
				itemOrd.incItem();
				order.put(sku, itemOrd);
			} else {
				itemOrd.setItem(sku, getName(sku), (double) getValue(sku));
				order.put(sku, itemOrd);
			}
			tvCount.setText(count.toString());
			tvTotal.setText(String.format("%.2f", total));
		} else {
			Toast t = Toast.makeText(ctx, R.string.no_stock, Toast.LENGTH_LONG);
			t.show();
		}
	}

	private void ListMatches() {
		Intent myIntent = new Intent(this, listMatchesElcanPos.class);
		myIntent.putExtra("sku", eSku.getText().toString());// sku or name part to search for.
		startActivityForResult(myIntent, ACTIVITY_LISTMATCHES);
	}

	protected void onActivityResult(int rqCode, int resCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(
				rqCode, resCode, intent);
		if (scanResult != null) {
			// got a result from the data scanner
			eSku.setText(scanResult.getContents().toString());
		} else {
			if (intent != null) {
				Bundle extras = intent.getExtras();
				switch (rqCode) {
				case ACTIVITY_LISTMATCHES:
					eSku.setText(extras.getString("sku"));
					eSku.setSelection(extras.getString("sku").length());
					break;
				case ACTIVITY_VIEW_ORDER:
					if (resCode == FINISH_SALE) {
						finishSale();
					}
					if (resCode == UPDATED_ORDER) {
						String orderlist = extras.getString("skulist");
						decodeOrder(orderlist);
					}
					break;
				case ACTIVITY_SETTINGS:
					if (resCode == RESULT_OK) { // if the user pressed ok, then
												// reload
												// the settings.
						reloadSettings();
					}
					break;
				case ACTIVITY_SHOWCATEG:
					if (resCode == RESULT_OK) {
						eSku.setText(extras.getString("sku"));
						eSku.setSelection(extras.getString("sku").length());
					}
					break;
				}
			}
		}
	}
	
	private void decodeOrder(String orderlist) {
		JSONObject jObj;
		try {
			JSONArray jArray = new JSONArray(orderlist);
			order.clear();
			total=Float.valueOf(0);
			count=0;
			for (int i = 0; i < jArray.length(); i++) {
				jObj = jArray.getJSONObject(i);
				total = (float) (total + jObj.getDouble("subtotal"));
				count += jObj.getInt("qty");
				order.put(
						jObj.getString("sku"),
						new elcanPosOrder(jObj.getString("name"), jObj
								.getDouble("price"), (int) jObj
								.getInt("qty"), jObj
								.getDouble("subtotal"), jObj
								.getString("sku")));
			}
			tvTotal.setText(String.format("%.2f", total));
			tvCount.setText(String.valueOf(count));
		} catch (Exception e) {
			Toast t = Toast.makeText(ctx, "Error:" + e.getMessage(),
					Toast.LENGTH_SHORT);
			t.show();
		}
	}
	
	private float getValue(String sku) {
		float pr = (float) 0;
		try {
			cur = srcDb.rawQuery(
					"SELECT name,sku,price FROM articles WHERE sku LIKE ?",
					new String[] { sku });
			cur.moveToFirst();
			pr = Float.parseFloat(cur.getString(cur.getColumnIndex("price")));
		} catch (Exception e) {
			Toast t = Toast.makeText(ctx, "Error:" + e.getMessage() + " col:"
					+ cur.getColumnIndex("price"), Toast.LENGTH_LONG);
			t.show();
		}
		return pr;
	}

	private String getName(String sku) {
		String ret = null;
		try {
			cur = srcDb.rawQuery(
					"SELECT name,sku,price FROM articles WHERE sku LIKE ?",
					new String[] { sku });
			cur.moveToFirst();
			ret = cur.getString(cur.getColumnIndex("name"));
		} catch (Exception e) {
			Toast t = Toast.makeText(ctx, "Error:" + e.getMessage() + " col:"
					+ cur.getColumnIndex("price"), Toast.LENGTH_LONG);
			t.show();
		}
		return ret;
	}

	private void discountStock(String sku, String qty) {
		try {
			srcDb.execSQL("UPDATE articles SET stock=(stock-" + qty
					+ ") WHERE sku='" + sku + "'");
		} catch (Exception e) {
			Toast t = Toast.makeText(ctx, "Error: " + e.getMessage(),
					Toast.LENGTH_LONG);
			t.show();
		}
	}

	private boolean checkStock(String sku) {
		boolean ret = false;
		try {
			cur = srcDb.rawQuery("SELECT stock FROM articles WHERE sku=?",
					new String[] { sku });
			cur.moveToFirst();
			if (cur.getInt(0) > 0)
				ret = true;
		} catch (Exception e) {
			return false;
		}
		return ret;
	}

	protected void ShowOrder() {
		Intent myIntent = new Intent(this, showOrderElcanPos.class);
		JSONStringer jstringer = new JSONStringer();
		String orderString = "";
		try {
			jstringer.array();
			for (elcanPosOrder val : order.values()) {
				jstringer.object().key("name").value(val.getName())
						.key("price").value(val.getPrice()).key("qty")
						.value(val.getQty()).key("subtotal")
						.value(val.getSubTotal()).key("sku")
						.value(val.getSku()).endObject();
			}
			jstringer = jstringer.endArray();
			orderString = jstringer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		myIntent.putExtra("alreadystored", false);
		myIntent.putExtra("order", orderString); // JSOn string with the order data inside.
		startActivityForResult(myIntent, ACTIVITY_VIEW_ORDER);
	}

	public void finishSale() {
		Double total, subtotal;
		String skulist;
		Toast t;
		SQLiteDatabase db = edb.getWritableDatabase();
		Calendar rightNow = Calendar.getInstance();
		total = (double) 0;
		subtotal = (double) 0;
		skulist = "";
		if (order.size() > 0) {
			for (elcanPosOrder val : order.values()) {
				total += val.getSubTotal();
				subtotal += val.getSubTotal();
				skulist += val.getSku() + "\\" + val.getQty().toString() + ";";
				discountStock(val.getSku(), val.getQty().toString());
				updateMFS(val.getSku(), 1);
			}
			ContentValues values = new ContentValues();
			values.put("total", total.floatValue());
			values.put("posid", posID);
			values.put("skulist", skulist);
			values.put("synced", 0);
			values.put("orderdate",
					DateFormat.format("yyyy-MM-dd hh:mm", rightNow).toString());
			try {
				Long newid = db.insertOrThrow("orders", null, values);
				t = Toast.makeText(ctx,
						R.string.order_stored + " Id:" + newid.toString(),
						Toast.LENGTH_SHORT);
			} catch (Exception e) {
				t = Toast.makeText(ctx, "Error: " + e.getMessage(),
						Toast.LENGTH_SHORT);
			}
		} else {
			t = Toast.makeText(ctx, R.string.order_empty, Toast.LENGTH_SHORT);
		}
		t.show();
		newSale();
	}

	public void newSale() {
		order.clear();
		total = (float) 0;
		count = 0;
		tvCount.setText("0");
		tvTotal.setText("0");
	}

	public void showSettings() {
		Intent myIntent = new Intent(this, showSettingsElcanPos.class);
		startActivityForResult(myIntent, ACTIVITY_SETTINGS);
	}

	protected void updateMFS(String sku, int qty) {
		SQLiteDatabase db = edb.getWritableDatabase();
		try {
			db.rawQuery("INSERT OR IGNORE INTO mostfrecuent VALUES (NULL,'?', 0);",
					new String[] { sku });
			db.rawQuery("UPDATE mostfrecuent SET tcount=tcount+1 WHERE sku='?'",
					new String[] { sku });
		} catch (Exception e) {
			Toast t = Toast.makeText(ctx, "Error:" + e.getMessage(),
					Toast.LENGTH_SHORT);
			t.show();
		}
	}

	public void reloadSettings() {
		try {
			SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
			settings.put("posid", preferences.getString("posid", "0"));
			settings.put("url", preferences.getString("url", "http://www.google.com"));
			settings.put("user", preferences.getString("user", "uname"));
			settings.put("pass", preferences.getString("pass", "pwd"));
			settings.put("folder", preferences.getString("folder", Environment.getExternalStorageDirectory().getPath()));
			//.println("URL:"+settings.get("url"));
		} catch (Exception e) {
			Toast t = Toast.makeText(ctx, "Error:" + e.getMessage(),
					Toast.LENGTH_LONG);
			t.show();
		}
		return;
	}

	public void syncOrders() {
		SQLiteDatabase db=edb.getWritableDatabase();
		db.close();
		syncDataElcanPos sd=new syncDataElcanPos(this);
		String data[]=new String[4];
		data[0]=settings.get("url");
		data[1]=settings.get("user");
		data[2]=settings.get("pass");
		data[3]=settings.get("posid");
		sd.execute(data);
	}

	public void ShowCategories() {
		Intent myIntent = new Intent(this, elcanposBrowseCategories.class);
		myIntent.putExtra("sku", eSku.getText().toString()); // sku to pass, just in case.
		startActivityForResult(myIntent, ACTIVITY_SHOWCATEG);
	}
	
	public void showScan() {
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.initiateScan();
	}

	public void makeBackupToSD() {
		// Local database
		Toast t;
		try {
			SQLiteDatabase db = edb.getReadableDatabase();
			String state = Environment.getExternalStorageState();

			if (Environment.MEDIA_MOUNTED.equals(state)) {
			File sd = new File (Environment.getExternalStorageDirectory().getPath());

			if (sd.canWrite()) {
				String backupDBPath = "orders_elcanpos_bkp_"+DateFormat.format("yyyy-MM-dd", new Date())+".json";
				File backupDB = new File(sd, backupDBPath);
				FileOutputStream bkp=new FileOutputStream(backupDB);
				Cursor cur=db.rawQuery("SELECT * FROM orders WHERE synced<>1", null);
				JSONStringer jst=new JSONStringer();
				jst.array();
				while (cur.moveToNext()) {
					jst.object().key("orderdate").value(cur.getString(cur.getColumnIndex("orderdate"))).
							key("posid").value(cur.getString(cur.getColumnIndex("posid"))).key("total").
							value(cur.getFloat(cur.getColumnIndex("total"))).key("skulist").
							value(cur.getString(cur.getColumnIndex("skulist"))).endObject();	
				}
				jst.endArray();
				String str=jst.toString();
				bkp.write(str.getBytes());
				bkp.flush();
				bkp.close();
				t = Toast.makeText(getBaseContext(), backupDB.toString(),Toast.LENGTH_LONG);
			} else {
				t=Toast.makeText(getApplicationContext(), "Can't write on SD", Toast.LENGTH_LONG);
			}
		  } else {
			  t=Toast.makeText(getApplicationContext(), R.string.mediaunavailable, Toast.LENGTH_LONG);
		  }
		  t.show();

		} catch (Exception e) {
			t = Toast.makeText(getBaseContext(), e.toString(),Toast.LENGTH_LONG);
			t.show();
		}
	}
}