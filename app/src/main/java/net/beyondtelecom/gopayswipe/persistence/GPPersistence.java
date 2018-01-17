package net.beyondtelecom.gopayswipe.persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.beyondtelecom.gopayswipe.dto.BankType;
import net.beyondtelecom.gopayswipe.dto.CashoutDetails;
import net.beyondtelecom.gopayswipe.dto.CurrencyType;
import net.beyondtelecom.gopayswipe.dto.UserDetails;

import java.util.ArrayList;

import static net.beyondtelecom.gopayswipe.dto.AccountType.BANK_ACCOUNT;
import static net.beyondtelecom.gopayswipe.dto.AccountType.MOBILE_BANK_ACCOUNT;
import static net.beyondtelecom.gopayswipe.dto.AccountType.ONLINE_BANK_ACCOUNT;

/**
 * User: tkaviya
 * Date: 7/5/14
 * Time: 12:24 PM
 */
public class GPPersistence extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

	private static final String DATABASE_NAME = "GoPayMerchant.db";

	public static final String USER_DETAILS = "user_details";

	public static final String BANK_TYPE = "bank_type";

	public static final String CURRENCY_TYPE = "currency_type";

	public static final String MOBILE_WALLET_TYPE = "mobile_wallet_type";

	public static final String ONLINE_BANK_TYPE = "online_bank_type";

	public static final String CASHOUT_TYPE = "cashout_type";

	public static final String CASHOUT_ACCOUNT = "cashout_account";

	public static final String MOBILE_CASHOUT_ACCOUNT = "mobile_cashout_account";

	public static final String BANK_CASHOUT_ACCOUNT = "bank_cashout_account";

	public static final String ONLINE_CASHOUT_ACCOUNT = "online_cashout_account";

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

		db.execSQL("CREATE TABLE IF NOT EXISTS " + BANK_TYPE + " (" +
				"bank_type_id INT(11)," +
				"bank_type_name VARCHAR(20))");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + CURRENCY_TYPE + " (" +
				"currency_type_id INT(11)," +
				"currency_type_name VARCHAR(20))");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + MOBILE_WALLET_TYPE + " (" +
				"mobile_wallet_type_id INT(11)," +
				"mobile_wallet_type_name VARCHAR(20))");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + ONLINE_BANK_TYPE + " (" +
				"online_bank_type_id INT(11)," +
				"online_bank_type_name VARCHAR(20))");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + CASHOUT_TYPE + " (" +
                "cashout_type_id INT(11)," +
                "cashout_type_name VARCHAR(50))");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + CASHOUT_ACCOUNT + " (" +
				"cashout_account_id INT(11)," +
                "cashout_type_id INT(11))");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + MOBILE_CASHOUT_ACCOUNT + " (" +
				"cashout_account_id INT(11)," +
                "mobile_wallet_type_id INT(11)," +
				"mobile_account_name VARCHAR(50)," +
                "mobile_number INT(15))");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + BANK_CASHOUT_ACCOUNT + " (" +
				"cashout_account_id INT(11)," +
				"bank_type_id INT(11)," +
				"bank_account_name VARCHAR(50)," +
				"bank_account_number INT(15)," +
				"bank_account_branch VARCHAR(50)," +
				"bank_account_phone VARCHAR(50)," +
				"bank_account_email VARCHAR(50))");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + ONLINE_CASHOUT_ACCOUNT + " (" +
				"cashout_account_id INT(11)," +
				"online_cashout_type_id INT(11)," +
				"online_account_name VARCHAR(50)," +
				"online_account_email VARCHAR(50))");

		db.execSQL("INSERT INTO " + CASHOUT_TYPE + "(cashout_type_id,cashout_type_name) VALUES (0,'" + MOBILE_BANK_ACCOUNT.name() + "')");
		db.execSQL("INSERT INTO " + CASHOUT_TYPE + "(cashout_type_id,cashout_type_name) VALUES (1,'" + BANK_ACCOUNT.name() + "')");
		db.execSQL("INSERT INTO " + CASHOUT_TYPE + "(cashout_type_id,cashout_type_name) VALUES (2,'" + ONLINE_BANK_ACCOUNT.name() + "')");

		db.execSQL("INSERT INTO " + BANK_TYPE + " (bank_type_id,bank_type_name) VALUES" +
				" (1,'Agribank')," +
				" (2,'BancABC')," +
				" (3,'Barclays Bank')," +
				" (4,'CABS')," +
				" (5,'CBZ')," +
				" (6,'Ecobank')," +
				" (7,'FBC')," +
				" (8,'MBCA')," +
				" (9,'Metbank')," +
				" (10,'NMB')," +
				" (11,'POSB')," +
				" (12,'Stanbic Bank')," +
				" (13,'Standard Chartered')," +
				" (14,'Steward Bank')," +
				" (15,'ZB Bank')");

		db.execSQL("INSERT INTO " + MOBILE_WALLET_TYPE + " (mobile_wallet_type_id,mobile_wallet_type_name) VALUES" +
				" (1,'EcoCash')," +
				" (2,'Telecash')," +
				" (3,'GetCash')");

		db.execSQL("INSERT INTO " + ONLINE_BANK_TYPE + " (online_bank_type_id,online_bank_type_name) VALUES" +
				" (1,'PayPal')," +
				" (2,'Skrill')");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + BANK_TYPE);
        db.execSQL("DROP TABLE IF EXISTS " + MOBILE_WALLET_TYPE);
        db.execSQL("DROP TABLE IF EXISTS " + ONLINE_BANK_TYPE);
        db.execSQL("DROP TABLE IF EXISTS " + CASHOUT_TYPE);
        db.execSQL("DROP TABLE IF EXISTS " + CASHOUT_ACCOUNT);
        db.execSQL("DROP TABLE IF EXISTS " + MOBILE_CASHOUT_ACCOUNT);
        db.execSQL("DROP TABLE IF EXISTS " + BANK_CASHOUT_ACCOUNT);
	    db.execSQL("DROP TABLE IF EXISTS " + ONLINE_CASHOUT_ACCOUNT);
		onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
    }

	private SQLiteDatabase getGPWritableDatabase() {
		if (sqlLiteDatabase == null) { sqlLiteDatabase = getWritableDatabase(); }
		return sqlLiteDatabase;
	}

    public ArrayList<BankType> getBankTypes() {
		Cursor bankData = getGPWritableDatabase().rawQuery(
			"SELECT bd.bank_type_id, bd.bank_type_name FROM " + BANK_TYPE + " bd", null);

		if (bankData.isAfterLast()) { return null; }

		ArrayList<BankType> bankTypes = new ArrayList<>();

		while (!bankData.isAfterLast()) {
			bankData.moveToNext();
			if (bankData.isAfterLast()) { break; }
			bankTypes.add(new BankType(bankData.getInt(0), bankData.getString(1)));
		}

		if (bankTypes.size() == 0) { return null; }

		return bankTypes;
	}

    public ArrayList<CurrencyType> getCurrencyTypes() {
		Cursor currencyData = getGPWritableDatabase().rawQuery(
			"SELECT ct.currency_type_id, ct.currency_type_name FROM " + CURRENCY_TYPE + " ct", null);

		if (currencyData.isAfterLast()) { return null; }

		ArrayList<CurrencyType> currencyType = new ArrayList<>();

		while (!currencyData.isAfterLast()) {
			currencyData.moveToNext();
			if (currencyData.isAfterLast()) { break; }
			currencyType.add(new CurrencyType(currencyData.getInt(0), currencyData.getString(1)));
		}

		if (currencyType.size() == 0) { return null; }

		return currencyType;
	}

	public UserDetails getUserDetails() {
		Cursor userData = getGPWritableDatabase().rawQuery(
			"SELECT ud.username, ud.first_name, ud.last_name, ud.msisdn, ud.email, ud.pin " +
				 " FROM " + USER_DETAILS + " ud ", null);

		if (userData.isAfterLast()) { return null; }

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

	public void insertCashoutDetails(CashoutDetails cashoutDetails) {

    	int cashout_id = 0;
    	if (cashoutDetails.getAccountType().equals(MOBILE_BANK_ACCOUNT)) {
			String sql = "INSERT INTO " + MOBILE_CASHOUT_ACCOUNT + " (cashout_account_id,mobile_wallet_type_id,mobile_account_name,mobile_number) " +
					     "VALUES (" + cashout_id + "," +
					"'" + cashoutDetails.getBankType().getBankTypeId() + "'," +
					"'" + cashoutDetails.getAccountName() + "'," +
					"'" + cashoutDetails.getAccountPhone() + "');";
			getGPWritableDatabase().execSQL(sql);
		}
    	else if (cashoutDetails.getAccountType().equals(BANK_ACCOUNT)) {
			String sql = "INSERT INTO " + BANK_CASHOUT_ACCOUNT +
				" (cashout_account_id, bank_type_id, bank_account_name," +
				" bank_account_number, bank_account_branch, bank_account_phone," +
				" bank_account_email) VALUES (" + cashout_id + "," +
				"'" + cashoutDetails.getBankType().getBankTypeId() + "'," +
				"'" + cashoutDetails.getAccountName() + "'," +
				"'" + cashoutDetails.getAccountNumber() + "'," +
				"'" + cashoutDetails.getAccountBranch() + "'," +
				"'" + cashoutDetails.getAccountPhone() + "'," +
				"'" + cashoutDetails.getAccountEmail() + "');";
			getGPWritableDatabase().execSQL(sql);
		}
	}

	public void setUserDetails(UserDetails newUserDetails) {
		getGPWritableDatabase().execSQL("DELETE FROM " + USER_DETAILS);
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
