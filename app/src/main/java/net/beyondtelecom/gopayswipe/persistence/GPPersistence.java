package net.beyondtelecom.gopayswipe.persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.beyondtelecom.gopayswipe.common.UserDetails;

import static net.beyondtelecom.gopayswipe.common.AccountType.BANK_ACCOUNT;
import static net.beyondtelecom.gopayswipe.common.AccountType.MOBILE_BANK_ACCOUNT;
import static net.beyondtelecom.gopayswipe.common.AccountType.ONLINE_BANK_ACCOUNT;

/**
 * User: tkaviya
 * Date: 7/5/14
 * Time: 12:24 PM
 */
public class GPPersistence extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_NAME = "GoPayMerchant.db";

	public static final String USER_DETAILS = "user_details";

	public static final String CASHOUT_TYPE = "cashout_type";

	public static final String MOBILE_CASHOUT = "mobile_cashout";

	public static final String BANK_CASHOUT = "bank_cashout";

	public static final String ONLINE_CASHOUT = "online_cashout";

	private SQLiteDatabase sqlLiteDatabase = null;

    public GPPersistence(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION); }

    public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE IF NOT EXISTS " + USER_DETAILS + " (" +
				"username VARCHAR(20)," +
				"first_name VARCHAR(20)," +
				"last_name VARCHAR(20)," +
				"msisdn VARCHAR(12)," +
				"email VARCHAR(255)," +
				"pin VARCHAR(50))");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + CASHOUT_TYPE + " (" +
                "cashout_type_id INT(11)," +
                "cashout_type_name VARCHAR(50))");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + MOBILE_CASHOUT + " (" +
                "mobile_cashout_id INT(11)," +
				"mobile_account_name VARCHAR(50)," +
                "mobile_number INT(15))");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + BANK_CASHOUT + " (" +
				"bank_cashout_id INT(11)," +
				"bank_account_name VARCHAR(50)," +
				"bank_account_number INT(15)," +
				"bank_account_branch VARCHAR(50)," +
				"bank_account_phone VARCHAR(50)," +
				"bank_account_email VARCHAR(50))");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + ONLINE_CASHOUT + " (" +
				"online_cashout_id INT(11)," +
				"online_account_name VARCHAR(50)," +
				"online_email VARCHAR(50))");

		db.execSQL("INSERT INTO " + CASHOUT_TYPE + " VALUES (0,'" + MOBILE_BANK_ACCOUNT.name() + "')");
		db.execSQL("INSERT INTO " + CASHOUT_TYPE + " VALUES (1,'" + BANK_ACCOUNT.name() + "')");
		db.execSQL("INSERT INTO " + CASHOUT_TYPE + " VALUES (2,'" + ONLINE_BANK_ACCOUNT.name() + "')");

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CASHOUT_TYPE);
        db.execSQL("DROP TABLE IF EXISTS " + MOBILE_CASHOUT);
        db.execSQL("DROP TABLE IF EXISTS " + BANK_CASHOUT);
	    db.execSQL("DROP TABLE IF EXISTS " + ONLINE_BANK_ACCOUNT);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + CASHOUT_TYPE);
		db.execSQL("DROP TABLE IF EXISTS " + MOBILE_CASHOUT);
		db.execSQL("DROP TABLE IF EXISTS " + BANK_CASHOUT);
		db.execSQL("DROP TABLE IF EXISTS " + ONLINE_BANK_ACCOUNT);
		onCreate(db);
    }

	private SQLiteDatabase getGPWritableDatabase() {
		if (sqlLiteDatabase == null) { sqlLiteDatabase = getWritableDatabase(); }
		return sqlLiteDatabase;
	}
	
	public UserDetails getUserDetails() {
		Cursor userData = getGPWritableDatabase().rawQuery(
			"SELECT ud.username, ud.first_name, ud.last_name, ud.msisdn, ud.email, ud.pin " +
				 " FROM " + USER_DETAILS + " ud ", null);

		if (userData.isAfterLast()) {
			return null;
		}

		userData.moveToFirst();
		UserDetails userDetails = new UserDetails();
		userDetails.setUsername(userData.getString(0));
		userDetails.setFirstName(userData.getString(1));
		userDetails.setLastName(userData.getString(2));
		userDetails.setMsisdn(userData.getString(3));
		userDetails.setEmail(userData.getString(4));
		userDetails.setPin(userData.getString(5));
		return userDetails;
	}

	public void setUserDetails(UserDetails newUserDetails) {
		String sql = "INSERT INTO " + USER_DETAILS + "(username,first_name,last_name,msisdn,email,pin) VALUES (" +
			"'" + newUserDetails.getUsername() + "'," +
			"'" + newUserDetails.getFirstName() + "'," +
			"'" + newUserDetails.getLastName() + "'," +
			"'" + newUserDetails.getMsisdn() + "'," +
			"'" + newUserDetails.getEmail() + "'," +
			"'" + newUserDetails.getPin() + "');";
		getGPWritableDatabase().execSQL(sql);
	}
}
