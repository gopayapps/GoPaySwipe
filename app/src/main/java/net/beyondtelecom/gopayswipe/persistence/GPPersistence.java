package net.beyondtelecom.gopayswipe.persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.beyondtelecom.gopayswipe.dto.AccountType;
import net.beyondtelecom.gopayswipe.dto.CashoutOption;
import net.beyondtelecom.gopayswipe.dto.CurrencyType;
import net.beyondtelecom.gopayswipe.dto.UserDetails;
import net.beyondtelecom.gopayswipe.dto.WalletAccount;

import java.util.ArrayList;
import java.util.Hashtable;

import static net.beyondtelecom.gopayswipe.common.ActivityCommon.getTag;

/**
 * User: tkaviya
 * Date: 7/5/14
 * Time: 12:24 PM
 */
public class GPPersistence extends SQLiteOpenHelper {

	// If you change the database schema, you must increment the database version.
	private static final int DATABASE_VERSION = 4;

	private static final String TAG = getTag(GPPersistence.class);

	private static final String DATABASE_NAME = "GoPayMerchant.db";

	private static final ArrayList<String> ALL_TABLES = new ArrayList<>();

	public static final String USER_DETAILS = "user_details";						static { ALL_TABLES.add(USER_DETAILS); }

	public static final String CURRENCY_TYPE = "currency_type";						static { ALL_TABLES.add(CURRENCY_TYPE); }
	public static final String CASHOUT_OPTION = "cashout_option";					static { ALL_TABLES.add(CASHOUT_OPTION); }

	public static final String WALLET_ACCOUNT = "wallet_account";					static { ALL_TABLES.add(WALLET_ACCOUNT); }

	private SQLiteDatabase sqlLiteDatabase = null;

    public GPPersistence(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION); }

    public void onCreate(SQLiteDatabase db) {

		Log.i(TAG, "Redeploying core database...");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + USER_DETAILS + " (" +
				"username VARCHAR(50)," +
				"first_name VARCHAR(50)," +
				"last_name VARCHAR(50)," +
				"msisdn VARCHAR(12)," +
				"email VARCHAR(255)," +
				"pin VARCHAR(50))");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + CURRENCY_TYPE + " (" +
				"currency_type_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"currency_type_name VARCHAR(50))");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + CASHOUT_OPTION + " (" +
				"cashout_option_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"cashout_option_name VARCHAR(50)," +
				"cashout_option_type VARCHAR(50))");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + WALLET_ACCOUNT + " (" +
				"wallet_account_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"cashout_option_id INTEGER," +
				"wallet_nick_name VARCHAR(50)," +
				"wallet_name VARCHAR(50)," +
				"wallet_account_number INT(30)," +
				"wallet_branch VARCHAR(50)," +
				"wallet_phone VARCHAR(50)," +
				"wallet_email VARCHAR(50))");

		db.execSQL("INSERT INTO " + CASHOUT_OPTION + " (cashout_option_name,cashout_option_type) VALUES" +
				" ('EcoCash','" + AccountType.MOBILE_BANK_ACCOUNT.name() + "')," +
				" ('Telecash','" + AccountType.MOBILE_BANK_ACCOUNT.name() + "')," +
				" ('NetCash','" + AccountType.MOBILE_BANK_ACCOUNT.name() + "')," +
				" ('PayPal','" + AccountType.ONLINE_BANK_ACCOUNT.name() + "')," +
				" ('Skrill','" + AccountType.ONLINE_BANK_ACCOUNT.name() + "')," +
				" ('Agribank','" + AccountType.BANK_ACCOUNT.name() + "')," +
				" ('BancABC','" + AccountType.BANK_ACCOUNT.name() + "')," +
				" ('Barclays Bank','" + AccountType.BANK_ACCOUNT.name() + "')," +
				" ('CABS','" + AccountType.BANK_ACCOUNT.name() + "')," +
				" ('CBZ','" + AccountType.BANK_ACCOUNT.name() + "')," +
				" ('Ecobank','" + AccountType.BANK_ACCOUNT.name() + "')," +
				" ('FBC','" + AccountType.BANK_ACCOUNT.name() + "')," +
				" ('MBCA','" + AccountType.BANK_ACCOUNT.name() + "')," +
				" ('Metbank','" + AccountType.BANK_ACCOUNT.name() + "')," +
				" ('NMB','" + AccountType.BANK_ACCOUNT.name() + "')," +
				" ('POSB','" + AccountType.BANK_ACCOUNT.name() + "')," +
				" ('Stanbic Bank','" + AccountType.BANK_ACCOUNT.name() + "')," +
				" ('Standard Chartered','" + AccountType.BANK_ACCOUNT.name() + "')," +
				" ('Steward Bank','" + AccountType.BANK_ACCOUNT.name() + "')," +
				" ('ZB Bank','" + AccountType.BANK_ACCOUNT.name() + "')");
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

		String[] allTables = new String[]{USER_DETAILS,CURRENCY_TYPE,CASHOUT_OPTION,WALLET_ACCOUNT};
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

	public Hashtable<Integer, CashoutOption> getCashoutOptions() {
		Cursor cashoutData = getGPWritableDatabase().rawQuery(
			"SELECT * FROM " + CASHOUT_OPTION, null);
		if (cashoutData.isAfterLast()) { return null; }
		Hashtable<Integer, CashoutOption> cashoutOptions = new Hashtable<>();
		cashoutData.moveToNext();
		while (!cashoutData.isAfterLast()) {
			cashoutOptions.put(cashoutData.getInt(0), new CashoutOption(
					cashoutData.getInt(0),
					cashoutData.getString(1),
					AccountType.valueOf(cashoutData.getString(2)))
			);
			cashoutData.moveToNext();
		}
		Log.i(TAG, "Returning " + cashoutOptions.size() + " cashout options");
		if (cashoutOptions.size() == 0) { return null; }
		return cashoutOptions;
	}

    public ArrayList<CurrencyType> getCurrencyTypes() {
		Cursor currencyData = getGPWritableDatabase().rawQuery(
			"SELECT * FROM " + CURRENCY_TYPE, null);
		if (currencyData.isAfterLast()) { return null; }
		ArrayList<CurrencyType> currencyType = new ArrayList<>();
		currencyData.moveToNext();
		while (!currencyData.isAfterLast()) {
			currencyType.add(new CurrencyType(
					currencyData.getInt(0),
					currencyData.getString(1))
			);
			currencyData.moveToNext();
		}
		Log.i(TAG, "Returning " + currencyType.size() + " currencyType");
		if (currencyType.size() == 0) { return null; }
		return currencyType;
	}

	public UserDetails getUserDetails() {
		Cursor userData = getGPWritableDatabase().rawQuery(
			"SELECT * FROM " + USER_DETAILS + " ud ", null);
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

	public void insertWalletAccount(WalletAccount walletAccount) {
			String sql = "INSERT INTO " + WALLET_ACCOUNT +
				" (cashout_option_id, wallet_nick_name, wallet_name," +
				" wallet_account_number, wallet_branch, wallet_phone," +
				" wallet_email) VALUES (" +
				"'" + walletAccount.getCashoutOption().getCashoutOptionId() + "'," +
				"'" + walletAccount.getWalletNickname() + "'," +
				"'" + walletAccount.getWalletName() + "'," +
				"'" + walletAccount.getWalletAccountNumber() + "'," +
				"'" + walletAccount.getWalletAccountBranch() + "'," +
				"'" + walletAccount.getWalletPhone() + "'," +
				"'" + walletAccount.getWalletEmail() + "');";
			getGPWritableDatabase().execSQL(sql);
	}

	public ArrayList<WalletAccount> getWalletAccounts() {

		Hashtable<Integer, CashoutOption> cashoutOptions = getCashoutOptions();
		Cursor accountData = getGPWritableDatabase().rawQuery(
				"SELECT * FROM " + WALLET_ACCOUNT, null);
		if (accountData.isAfterLast()) { return null; }
		ArrayList<WalletAccount> walletAccounts = new ArrayList<>();
		accountData.moveToNext();
		while (!accountData.isAfterLast()) {
			walletAccounts.add(new WalletAccount(
					accountData.getInt(0), //wallet_account_id
					cashoutOptions.get(accountData.getInt(1)), //cashout_option_id
					accountData.getString(2), //wallet_nick_name
					accountData.getString(3), //wallet_name
					accountData.getString(4), //wallet_account_number
					accountData.getString(5), //wallet_branch
					accountData.getString(6), //wallet_phone
					accountData.getString(7)  //wallet_email
				)
			);
			accountData.moveToNext();
		}
		Log.i(TAG, "Returning " + walletAccounts.size() + " wallet accounts");
		if (walletAccounts.size() == 0) { return null; }
		return walletAccounts;
	}

	public void deleteWalletAccount(Integer walletAccountId) {
		getGPWritableDatabase().execSQL(
			"DELETE FROM " + WALLET_ACCOUNT + " WHERE wallet_account_id = " + walletAccountId
		);
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
