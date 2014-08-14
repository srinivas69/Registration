package com.seenu.registration;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {

	// database name
	public static final String DATABASE_NAME = "UsersDB";
	// table name
	public static final String TABLE_NAME = "usersTable";
	// database version
	public static final int DATABASE_VERSION = 1;

	// defining columns
	public static final String COL_ROWID = "rowid";
	public static final String COL_NAME = "name";
	public static final String COL_ACCOUNT_NAME = "accountName";
	public static final String COL_GENDER = "gender";
	public static final String COL_DOB = "dob";
	public static final String COL_LOCATION = "location";

	String CREATE_TABLE = "create table usersTable(rowid integer primary key autoincrement,name text not null,accountName text not null,gender text not null,dob text not null,location text not null) ";

	Context context;

	DBHelper dbHelper;

	SQLiteDatabase db;

	public DBAdapter(Context c) {
		// TODO Auto-generated constructor stub
		this.context = c;
		dbHelper = new DBHelper(context);
	}

	DBAdapter openDatabase() {
		db = dbHelper.getWritableDatabase();
		return this;

	}

	void close() {
		dbHelper.close();
	}

	long insertRecord(String name, String acctName, String gender, String dob,
			String location) {
		ContentValues con = new ContentValues();
		con.put(COL_NAME, name);
		con.put(COL_ACCOUNT_NAME, acctName);
		con.put(COL_GENDER, gender);
		con.put(COL_DOB, dob);
		con.put(COL_LOCATION, location);

		return db.insert(TABLE_NAME, null, con);

	}

	Cursor getAllRecords() {
		String[] columns = { COL_ROWID, COL_NAME, COL_ACCOUNT_NAME, COL_GENDER,
				COL_DOB, COL_LOCATION };
		return db.query(TABLE_NAME, columns, null, null, null, null, null);

	}

	class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(CREATE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}

	}

}
