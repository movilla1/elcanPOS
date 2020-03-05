package com.elcansoftware.elcanpos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class elcanPosDataHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "elcanpos.db";
	private static final int DATABASE_VERSION = 1;
    private static final String POS_TABLE_NAME = "articles";
    private static final String POS_TABLE_CREATE =
                "CREATE TABLE " + POS_TABLE_NAME + " (" +
                "_id integer primary key autoincrement," +
                "sku TEXT, " +
                "name TEXT, " +
                "image TEXT, " +
                "stock integer, " +
                "description TEXT, " +
                "category TEXT, " +
                "price FLOAT);";
	private static final String POS_TABLE_NAMEMFS = "mostfrecuent";
    private static final String POS_TABLE_MFS_CREATE =
            "CREATE TABLE " + POS_TABLE_NAMEMFS + " (" +
            "_id integer primary key autoincrement,"+
            "sku TEXT UNIQUE, " +
            "tcount interger);";
    private static final String POS_TABLE_CAT = "categories";
    private static final String POS_TABLE_CAT_CREATE = 
            "CREATE TABLE " + POS_TABLE_CAT + " (" +
            "_id integer primary key autoincrement,"+
            "category TEXT UNIQUE, " +
            "catdesc TEXT);";

    private static final String POS_TABLE_ORDERS = "orders";
    private static final String POS_TABLE_ORDERS_CREATE = 
    		"CREATE TABLE "+POS_TABLE_ORDERS+" ( " +
    		"_id integer primary key autoincrement,"+
    		" orderdate TEXT," +
    		" total FLOAT," +
    		" posid TEXT,"+
    		" synced integer,"+
    		" skulist TEXT)";
    public elcanPosDataHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(POS_TABLE_CREATE);
		db.execSQL(POS_TABLE_MFS_CREATE);
		db.execSQL(POS_TABLE_ORDERS_CREATE);
		db.execSQL(POS_TABLE_CAT_CREATE);
		db.execSQL("INSERT INTO mostfrecuent VALUES(1,'tst1',2)");
		db.execSQL("INSERT INTO mostfrecuent VALUES(2,'tst2',1)");
		db.execSQL("INSERT INTO mostfrecuent VALUES(3,'tst3',3)");
		db.execSQL("INSERT INTO articles VALUES (1,'tst1','Test art1','lock',5,'Test Article 1, while a test','01/01','12.3')");
		db.execSQL("INSERT INTO articles VALUES (2,'tst2','Test art2','lock',7,'Test Article 2, while a test','01/01','13.3')");
		db.execSQL("INSERT INTO articles VALUES (3,'tst3','Test art3','lock',3,'Test Article 3, while a test','01/02','1.3')");
		//db.execSQL("INSERT INTO settings VALUES (1,'pos1','http://www.aeromodelismoelcan.com/index.php?option=com_vm2elcanpos&tmpl=component&task=getData&format=raw','movilla','pass123')");
		db.execSQL("INSERT INTO categories VALUES (1,'01/01','Propelers/Folding')");
		db.execSQL("INSERT INTO categories VALUES (2,'01/02','Propelers/Electric')");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS "+POS_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+POS_TABLE_ORDERS);
		db.execSQL("DROP TABLE IF EXISTS "+POS_TABLE_NAMEMFS);
		db.execSQL("DROP TABLE IF EXISTS "+POS_TABLE_CAT);
		onCreate(db);
	}

}
