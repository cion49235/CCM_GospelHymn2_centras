package com.ccm.gospelhymn.centras.db;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Favorite_DBopenHelper extends SQLiteOpenHelper {
	public Favorite_DBopenHelper(Context context) {
		super(context, "favorite.db", null, 1);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
			String createtable = "create table favorite_list ( _id integer primary key autoincrement,"+
			"id text, num text, subject text, thumb text, portal text);";
			db.execSQL(createtable);

	}		                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               		
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exits favorite_list"); 
		onCreate(db);
	}
}