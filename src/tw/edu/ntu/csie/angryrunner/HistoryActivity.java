package tw.edu.ntu.csie.angryrunner;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class HistoryActivity extends Activity {
	ListView historylist;
	SimpleAdapter historyAdapter;
	ArrayList<HashMap<String,Object>> historyItems;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);
		
		historylist = (ListView) findViewById(R.id.listView1);
		
		historyItems = new ArrayList<HashMap<String,Object>>();
		HashMap<String,Object> tmpitem = new HashMap<String,Object>();
		tmpitem.put("pic", R.drawable.ic_launcher);
		tmpitem.put("date", "Oct 27 2011");
		tmpitem.put("distance", "0.0 km");
		tmpitem.put("duration", "0:00:00");
		tmpitem.put("speed", "0.0 m/s");
		historyItems.add(tmpitem);
		
		historyAdapter = new SimpleAdapter(HistoryActivity.this, 
				historyItems, 
				R.layout.history_item, 
				new String[] {"pic", "date", "distance", "duration", "speed"}, 
				new int[] {R.id.imageView1, R.id.tvDate, R.id.tvDistance, R.id.tvDuration, R.id.tvSpeed}
		);
		historylist.setAdapter(historyAdapter);
		
	}
}
