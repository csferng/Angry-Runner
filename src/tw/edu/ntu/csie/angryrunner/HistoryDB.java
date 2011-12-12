package tw.edu.ntu.csie.angryrunner;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class HistoryDB extends SQLiteOpenHelper {

	public HistoryDB(Context context) {
		super(context, "AngryRunnerHistoryDB", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE ARhistory (mode TEXT, date TEXT, " +
				"distance TEXT, duration TEXT, speed TEXT)");
		/*
		ContentValues testcv = new ContentValues();
		testcv.put("mode", "walking");
		testcv.put("date", "Jan 1, 2012");
		testcv.put("distance", "1.0 km");
		testcv.put("duration", "0:00:00");
		testcv.put("speed", "0.0 m/s");
		db.insert("ARhistoryTEST", null, testcv);
		*/
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
	}

}
