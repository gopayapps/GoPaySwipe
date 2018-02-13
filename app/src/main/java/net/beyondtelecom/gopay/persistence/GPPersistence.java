package net.beyondtelecom.gopay.persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.beyondtelecom.gopay.dto.UserDetails;

import java.util.ArrayList;

import static net.beyondtelecom.gopay.common.ActivityCommon.getTag;
import static net.beyondtelecom.gopay.common.Validator.isNullOrEmpty;

/**
 * User: tkaviya
 * Date: 7/5/14
 * Time: 12:24 PM
 */
public class GPPersistence extends SQLiteOpenHelper {

	// If you change the database schema, you must increment the database version.
	private static final int DATABASE_VERSION = 2;

	private static final String TAG = getTag(GPPersistence.class);

	private static final String DATABASE_NAME = "GoPayMerchant.db";

	private static final ArrayList<String> ALL_TABLES = new ArrayList<>();

	private static final String USER_DETAILS = "user_details";	static { ALL_TABLES.add(USER_DETAILS); }

	private SQLiteDatabase sqlLiteDatabase = null;

    public GPPersistence(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION); }

    public void onCreate(SQLiteDatabase db) {

		Log.i(TAG, "Redeploying core database...");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + USER_DETAILS + " (" +
			"bt_user_id INTEGER PRIMARY KEY," +
			"wallet_id INTEGER," +
			"username VARCHAR(50)," +
			"first_name VARCHAR(50)," +
			"last_name VARCHAR(50)," +
			"company_name VARCHAR(50)," +
			"msisdn VARCHAR(12)," +
			"email VARCHAR(255))");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "Upgrading database...");
		for (String tableName : ALL_TABLES) {
			Log.i(TAG, "Dropping table " + tableName);
			db.execSQL("DROP TABLE IF EXISTS " + tableName);
		}
		onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "Downgrading database...");
		for (String tableName : ALL_TABLES) {
			Log.i(TAG, "Dropping table " + tableName);
			db.execSQL("DROP TABLE IF EXISTS " + tableName);
		}
		onCreate(db);
	}

	private SQLiteDatabase getGPWritableDatabase() {
		if (sqlLiteDatabase == null) {
			sqlLiteDatabase = getWritableDatabase();
			Log.i(TAG, "Displaying all DB values:\r\n\r\n" + dumpDatabaseToString());
		}
		return sqlLiteDatabase;
	}

	public String dumpDatabaseToString() {
    	StringBuilder database = new StringBuilder();

		String[] allTables = new String[]{USER_DETAILS};
		for (String table : allTables) {
			Cursor dbCursor = getGPWritableDatabase().rawQuery("SELECT * FROM " + table, null);
			database.append("TABLE: ").append(table).append("\n");
			dbCursor.moveToNext();
			while (!dbCursor.isAfterLast()) {
				for (int c = 0; c < dbCursor.getColumnCount(); c++) {
					database.append(dbCursor.getColumnName(c)).append(":").append(dbCursor.getString(c)).append(",");
				}
				database.append("\n");
				dbCursor.moveToNext();
			}
			database.append("\n");
		}
		return database.toString();
	}

	public UserDetails getUserDetails() {
		Cursor userData = getGPWritableDatabase().rawQuery(
			"SELECT * FROM " + USER_DETAILS + " ud ", null);
		if (userData.isAfterLast()) { return null; }
		userData.moveToFirst();
		UserDetails userDetails = new UserDetails();
		userDetails.setBtUserId(userData.isNull(0) ? null : userData.getLong(0));
		userDetails.setWalletId(userData.isNull(1) ? null : userData.getLong(1));
		userDetails.setUsername(userData.isNull(2) ? null : userData.getString(2));
		userDetails.setFirstName(userData.isNull(3) ? null : userData.getString(3));
		userDetails.setLastName(userData.isNull(4) ? null : userData.getString(4));
		userDetails.setCompanyName(userData.isNull(5) ? null : userData.getString(5));
		userDetails.setMsisdn(userData.isNull(6) ? null : userData.getString(6));
		userDetails.setEmail(userData.isNull(7) ? null : userData.getString(7));
		return userDetails;
	}

	public void setUserDetails(UserDetails newUserDetails) {
		getGPWritableDatabase().execSQL("DELETE FROM " + USER_DETAILS);
		String sql = "INSERT INTO " + USER_DETAILS + "(bt_user_id,wallet_id,username,first_name,last_name,company_name,msisdn,email) VALUES (" +
			(newUserDetails.getBtUserId() == null ? null : "'" + newUserDetails.getBtUserId() + "'") + "," +
			(newUserDetails.getWalletId() == null ? null : "'" + newUserDetails.getWalletId() + "'") + "," +
			(isNullOrEmpty(newUserDetails.getUsername()) ? null : "'" + newUserDetails.getUsername() + "'") + "," +
			(isNullOrEmpty(newUserDetails.getFirstName()) ? null : "'" + newUserDetails.getFirstName() + "'") + "," +
			(isNullOrEmpty(newUserDetails.getLastName()) ? null : "'" + newUserDetails.getLastName() + "'") + "," +
			(isNullOrEmpty(newUserDetails.getCompanyName()) ? null : "'" + newUserDetails.getCompanyName() + "'") + "," +
			(isNullOrEmpty(newUserDetails.getMsisdn()) ? null : "'" + newUserDetails.getMsisdn() + "'") + "," +
			(isNullOrEmpty(newUserDetails.getEmail()) ? null : "'" + newUserDetails.getEmail() + "'") + ")";
		getGPWritableDatabase().execSQL(sql);
	}
}
