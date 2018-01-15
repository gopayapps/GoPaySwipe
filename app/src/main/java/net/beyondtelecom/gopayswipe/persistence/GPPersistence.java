package net.beyondtelecom.gopayswipe.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

	private static final String CASHOUT_TYPE = "cashout_type";

	private static final String MOBILE_CASHOUT = "mobile_cashout";

	private static final String BANK_CASHOUT = "bank_cashout";

	private static final String ONLINE_CASHOUT = "online_cashout";

	private SQLiteDatabase sqlLiteDatabase = null;

    public GPPersistence(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION); }

    public void onCreate(SQLiteDatabase db) {

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

		db.execSQL("INSERT INTO " + CASHOUT_TYPE + " VALUES (0," + MOBILE_BANK_ACCOUNT.name() + ")");
		db.execSQL("INSERT INTO " + CASHOUT_TYPE + " VALUES (1," + BANK_ACCOUNT.name() + ")");
		db.execSQL("INSERT INTO " + CASHOUT_TYPE + " VALUES (2," + ONLINE_BANK_ACCOUNT.name() + ")");

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
}
