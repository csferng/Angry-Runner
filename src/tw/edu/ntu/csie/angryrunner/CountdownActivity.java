package tw.edu.ntu.csie.angryrunner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CountdownActivity extends Activity {
	ListView countdownlist;
	ArrayAdapter<String> countdownAdapter;
	//String[] countdowns = {"Off", "10 secs", "20 secs", "30 secs", "60 secs"};
	String[] countdowns = new String[5];
	String[] cdValues = {"0", "10", "20", "30", "60"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.countdown);
		setTitle(getString(R.string.DISPLAY_COUNTDOWN) + " " + getString(R.string.TITLE));
		
		countdowns[0] = getString(R.string.INIT_COUNTDOWN);
		countdowns[1] = cdValues[1] + " " + getString(R.string.STR_SEC);
		countdowns[2] = cdValues[2] + " " + getString(R.string.STR_SEC);
		countdowns[3] = cdValues[3] + " " + getString(R.string.STR_SEC);
		countdowns[4] = cdValues[4] + " " + getString(R.string.STR_SEC);
		countdownlist = (ListView) findViewById(R.id.listView1);
		
		countdownAdapter = new ArrayAdapter<String>(CountdownActivity.this, android.R.layout.simple_list_item_1, countdowns);
		countdownlist.setAdapter(countdownAdapter);
		
		countdownlist.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// arg0: parent, arg1: clicked view, arg2: position, arg3: id
				Intent it = new Intent();
				Bundle bun = new Bundle();
				bun.putString("display", (String) countdownlist.getItemAtPosition(arg2));
				bun.putString("value", cdValues[arg2]);
				it.putExtras(bun);
				setResult(RESULT_OK, it);
				finish();
			}
			
		});
	}
}
