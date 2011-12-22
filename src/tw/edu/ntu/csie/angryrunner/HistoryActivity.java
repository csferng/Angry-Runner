package tw.edu.ntu.csie.angryrunner;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.TabActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class HistoryActivity extends Activity {
	ListView historylist;
	SimpleAdapter historyAdapter;
	ArrayList<HashMap<String, Object>> historyItems;
	SQLiteDatabase historydb;
	String[] DatabaseTableColumn = new String[5];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);
		//setTitle("History " + getString(R.string.TITLE));

		DatabaseTableColumn[0] = getString(R.string.KEY_MODE);
		DatabaseTableColumn[1] = getString(R.string.KEY_DATE);
		DatabaseTableColumn[2] = getString(R.string.KEY_DISTANCE);
		DatabaseTableColumn[3] = getString(R.string.KEY_DURATION);
		DatabaseTableColumn[4] = getString(R.string.KEY_SPEED);
		
		historylist = (ListView) findViewById(R.id.listView1);
		historydb = (new HistoryDatabaseHandler(HistoryActivity.this)).getWritableDatabase();

		historyItems = new ArrayList<HashMap<String, Object>>();
		historyAdapter = new SimpleAdapter(HistoryActivity.this, historyItems,
				R.layout.history_item, DatabaseTableColumn, new int[] {
						R.id.imageView1, R.id.tvDate, R.id.tvDistance,
						R.id.tvDuration, R.id.tvSpeed });
		historylist.setAdapter(historyAdapter);
		/*
		historylist.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		*/
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Cursor historyCursor;
		
		historyItems.clear();
		historyCursor = historydb.query(
				getString(R.string.NAME_DATABASETABLE), 
				null, null, null, null, null, null);
		if(historyCursor.isBeforeFirst())	historyCursor.moveToNext();
		while (!historyCursor.isAfterLast()) {
			HashMap<String, Object> tmpitem = new HashMap<String, Object>();
			
			String mode = historyCursor.getString(0);
			if(mode.equals("Walking")){
				tmpitem.put(DatabaseTableColumn[0], R.drawable.mode_walking);
			}else if(mode.equals("Running")){
				tmpitem.put(DatabaseTableColumn[0], R.drawable.mode_running);
			}else if(mode.equals("Cycling")){
				tmpitem.put(DatabaseTableColumn[0], R.drawable.mode_cycling);
			}else{
				tmpitem.put(DatabaseTableColumn[0], R.drawable.ic_launcher);
			}
			
			//tmpitem.put(DatabaseTableColumn[0], R.drawable.walking_v5);
			tmpitem.put(DatabaseTableColumn[1], historyCursor.getString(1));
			tmpitem.put(DatabaseTableColumn[2], historyCursor.getString(2));
			tmpitem.put(DatabaseTableColumn[3], historyCursor.getString(3));
			tmpitem.put(DatabaseTableColumn[4], historyCursor.getString(4));
			historyItems.add(0, tmpitem);
			
			historyCursor.moveToNext();
		}
		historyCursor.close();
		historyAdapter.notifyDataSetChanged();
	}
	
    @Override
    public void onBackPressed() {
    	((TabActivity) this.getParent()).getTabHost().setCurrentTab(0);
    }
}
