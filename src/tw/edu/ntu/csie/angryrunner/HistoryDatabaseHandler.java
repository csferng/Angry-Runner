package tw.edu.ntu.csie.angryrunner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class HistoryDatabaseHandler extends SQLiteOpenHelper {

	public HistoryDatabaseHandler(Context context) {
		super(context, "AngryRunnerHistoryDB", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE ARhistory (Mode TEXT, Date TEXT, " +
				"Distance TEXT, Duration TEXT, Speed TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	}

}
