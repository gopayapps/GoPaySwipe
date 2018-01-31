package net.beyondtelecom.gopayswipe.persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.beyondtelecom.gopayswipe.dto.UserDetails;

import java.util.ArrayList;

import static net.beyondtelecom.gopayswipe.common.ActivityCommon.getTag;
import static net.beyondtelecom.gopayswipe.common.Validator.isNullOrEmpty;

/**
 * User: tkaviya
 * Date: 7/5/14
 * Time: 12:24 PM
 */
public class GPPersistence extends SQLiteOpenHelper {

	// If you change the database schema, you must increment the database version.
	private static final int DATABASE_VERSION = 3;

	private static final String TAG = getTag(GPPersistence.class);

	private static final String DATABASE_NAME = "GoPayMerchant.db";

	private static final ArrayList<String> ALL_TABLES = new ArrayList<>();

	public static final String USER_DETAILS = "user_details";						static { ALL_TABLES.add(USER_DETAILS); }

	private SQLiteDatabase sqlLiteDatabase = null;

    public GPPersistence(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION); }

    public void onCreate(SQLiteDatabase db) {

		Log.i(TAG, "Redeploying core database...");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + USER_DETAILS + " (" +
				"bt_user_id INTEGER PRIMARY KEY," +
				"username VARCHAR(50)," +
				"first_name VARCHAR(50)," +
				"last_name VARCHAR(50)," +
				"company_name VARCHAR(50)," +
				"msisdn VARCHAR(12)," +
				"email VARCHAR(255)," +
				"pin VARCHAR(50))");

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

//	public Hashtable<Integer, FinancialInstitution> getFinancialInstitutions() {
//		Cursor cashoutData = getGPWritableDatabase().rawQuery(
//			"SELECT * FROM " + CASHOUT_OPTION, null);
//		if (cashoutData.isAfterLast()) { return null; }
//		Hashtable<Integer, FinancialInstitution> cashoutOptions = new Hashtable<>();
//		cashoutData.moveToNext();
//		while (!cashoutData.isAfterLast()) {
//			cashoutOptions.put(cashoutData.getInt(0), new FinancialInstitution(
//					cashoutData.getInt(0),
//					cashoutData.getString(1),
//					InstitutionType.valueOf(cashoutData.getString(2)))
//			);
//			cashoutData.moveToNext();
//		}
//		Log.i(TAG, "Returning " + cashoutOptions.size() + " cashout options");
//		if (cashoutOptions.size() == 0) { return null; }
//		return cashoutOptions;
//	}
//
//    public ArrayList<CurrencyType> getCurrencyTypes() {
//		Cursor currencyData = getGPWritableDatabase().rawQuery(
//			"SELECT * FROM " + CURRENCY_TYPE, null);
//		if (currencyData.isAfterLast()) { return null; }
//		ArrayList<CurrencyType> currencyType = new ArrayList<>();
//		currencyData.moveToNext();
//		while (!currencyData.isAfterLast()) {
//			currencyType.add(new CurrencyType(
//					currencyData.getInt(0),
//					currencyData.getString(1))
//			);
//			currencyData.moveToNext();
//		}
//		Log.i(TAG, "Returning " + currencyType.size() + " currencyType");
//		if (currencyType.size() == 0) { return null; }
//		return currencyType;
//	}

	public UserDetails getUserDetails() {
		Cursor userData = getGPWritableDatabase().rawQuery(
			"SELECT * FROM " + USER_DETAILS + " ud ", null);
		if (userData.isAfterLast()) { return null; }
		userData.moveToFirst();
		UserDetails userDetails = new UserDetails();
		userDetails.setBtUserId(userData.isNull(0) ? null : userData.getLong(0));
		userDetails.setUsername(userData.isNull(1) ? null : userData.getString(1));
		userDetails.setFirstName(userData.isNull(2) ? null : userData.getString(2));
		userDetails.setLastName(userData.isNull(3) ? null : userData.getString(3));
		userDetails.setCompanyName(userData.isNull(4) ? null : userData.getString(4));
		userDetails.setMsisdn(userData.isNull(5) ? null : userData.getString(5));
		userDetails.setEmail(userData.isNull(6) ? null : userData.getString(6));
		userDetails.setPin(userData.isNull(7) ? null : userData.getString(7));
		return userDetails;
	}

//	public void insertWalletAccount(CashoutAccount walletAccount) {
//			String sql = "INSERT INTO " + WALLET_ACCOUNT +
//				" (cashout_option_id, wallet_nick_name, wallet_name," +
//				" wallet_account_number, wallet_branch, wallet_phone," +
//				" wallet_email) VALUES (" +
//				"'" + walletAccount.getFinancialInstitution().getInstitutionId() + "'," +
//				(isNullOrEmpty(walletAccount.getAccountNickName()) ? null : "'" + walletAccount.getAccountNickName() + "'") + "," +
//				(isNullOrEmpty(walletAccount.getAccountName()) ? null : "'" + walletAccount.getAccountName() + "'") + "," +
//				(isNullOrEmpty(walletAccount.getAccountNumber()) ? null : "'" + walletAccount.getAccountNumber() + "'") + "," +
//				(isNullOrEmpty(walletAccount.getAccountBranchCode()) ? null : "'" + walletAccount.getAccountBranchCode() + "'") + "," +
//				(isNullOrEmpty(walletAccount.getAccountPhone()) ? null : "'" + walletAccount.getAccountPhone() + "'") + "," +
//				(isNullOrEmpty(walletAccount.getAccountEmail()) ? null : "'" + walletAccount.getAccountEmail() + "'") + ")";
//			getGPWritableDatabase().execSQL(sql);
//	}

//	public ArrayList<CashoutAccount> getCashoutAccounts(Activity activity) {
//
//		ArrayList<FinancialInstitution> cashoutOptions = getFinancialInstitutions(activity);
//		Cursor accountData = getGPWritableDatabase().rawQuery("SELECT * FROM " + WALLET_ACCOUNT, null);
//		if (accountData.isAfterLast()) { return null; }
//		ArrayList<CashoutAccount> walletAccounts = new ArrayList<>();
//		accountData.moveToNext();
//		while (!accountData.isAfterLast()) {
//			FinancialInstitution cashoutOption = null;
//			for (int c = 0; c < cashoutOptions.size(); c++) {
//				if (cashoutOptions.get(c).getInstitutionId().equals(accountData.getInt(1))) {
//					cashoutOption = cashoutOptions.get(c);
//					break;
//				}
//			}
//
//			walletAccounts.add(new CashoutAccount(
//					accountData.isNull(0) ? null : accountData.getInt(0), //wallet_account_id
//					accountData.isNull(1) ? null : cashoutOption, 						 //cashout_option_id
//					accountData.isNull(2) ? null : accountData.getString(2), //wallet_nick_name
//					accountData.isNull(3) ? null : accountData.getString(3), //wallet_name
//					accountData.isNull(4) ? null : accountData.getString(4), //wallet_account_number
//					accountData.isNull(5) ? null : accountData.getString(5), //wallet_branch
//					accountData.isNull(6) ? null : accountData.getString(6), //wallet_phone
//					accountData.isNull(7) ? null : accountData.getString(7)  //wallet_email
//				)
//			);
//			accountData.moveToNext();
//		}
//		Log.i(TAG, "Returning " + walletAccounts.size() + " wallet accounts");
//		if (walletAccounts.size() == 0) { return null; }
//		return walletAccounts;
//	}
//
//	public void deleteWalletAccount(Integer walletAccountId) {
//		getGPWritableDatabase().execSQL(
//			"DELETE FROM " + WALLET_ACCOUNT + " WHERE wallet_account_id = " + walletAccountId
//		);
//	}

	public void setUserDetails(UserDetails newUserDetails) {
		getGPWritableDatabase().execSQL("DELETE FROM " + USER_DETAILS);
		String sql = "INSERT INTO " + USER_DETAILS + "(bt_user_id,username,first_name,last_name,company_name,msisdn,email,pin) VALUES (" +
			(newUserDetails.getBtUserId() == null ? null : "'" + newUserDetails.getBtUserId() + "'") + "," +
			(isNullOrEmpty(newUserDetails.getUsername()) ? null : "'" + newUserDetails.getUsername() + "'") + "," +
			(isNullOrEmpty(newUserDetails.getFirstName()) ? null : "'" + newUserDetails.getFirstName() + "'") + "," +
			(isNullOrEmpty(newUserDetails.getLastName()) ? null : "'" + newUserDetails.getLastName() + "'") + "," +
			(isNullOrEmpty(newUserDetails.getCompanyName()) ? null : "'" + newUserDetails.getCompanyName() + "'") + "," +
			(isNullOrEmpty(newUserDetails.getMsisdn()) ? null : "'" + newUserDetails.getMsisdn() + "'") + "," +
			(isNullOrEmpty(newUserDetails.getEmail()) ? null : "'" + newUserDetails.getEmail() + "'") + "," +
			(isNullOrEmpty(newUserDetails.getPin()) ? null : "'" + newUserDetails.getPin() + "'") + ")";
		getGPWritableDatabase().execSQL(sql);
	}
}
