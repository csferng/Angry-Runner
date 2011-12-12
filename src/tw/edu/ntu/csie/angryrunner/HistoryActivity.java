package tw.edu.ntu.csie.angryrunner;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class HistoryActivity extends Activity {
	ListView historylist;
	SimpleAdapter historyAdapter;
	ArrayList<HashMap<String, Object>> historyItems;
	SQLiteDatabase historydb;
	Cursor historyCursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);

		historylist = (ListView) findViewById(R.id.listView1);
		historydb = (new HistoryDB(HistoryActivity.this)).getWritableDatabase();

		historyItems = new ArrayList<HashMap<String, Object>>();
		historyCursor = historydb.query("ARhistory", null, null, null, null,
				null, null);
		if(historyCursor.isBeforeFirst())	historyCursor.moveToNext();
		while (!historyCursor.isAfterLast()) {
			HashMap<String, Object> tmpitem = new HashMap<String, Object>();
			tmpitem.put("pic", R.drawable.ic_launcher);
			tmpitem.put("date", historyCursor.getString(1));
			tmpitem.put("distance", historyCursor.getString(2));
			tmpitem.put("duration", historyCursor.getString(3));
			tmpitem.put("speed", historyCursor.getString(4));
			historyItems.add(tmpitem);
			
			historyCursor.moveToNext();
		}
		historyCursor.close();

		historyAdapter = new SimpleAdapter(HistoryActivity.this, historyItems,
				R.layout.history_item, new String[] { "pic", "date",
						"distance", "duration", "speed" }, new int[] {
						R.id.imageView1, R.id.tvDate, R.id.tvDistance,
						R.id.tvDuration, R.id.tvSpeed });
		historylist.setAdapter(historyAdapter);

	}
	
}
