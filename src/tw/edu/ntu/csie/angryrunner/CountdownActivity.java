package tw.edu.ntu.csie.angryrunner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class CountdownActivity extends Activity {
	ListView countdownlist;
	ArrayAdapter<String> countdownAdapter;
	String[] countdowns = {"Off", "10 secs", "20 secs", "30 secs", "60 secs"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.countdown);
		
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
				bun.putString("value", (String) countdownlist.getItemAtPosition(arg2));
				it.putExtras(bun);
				setResult(RESULT_OK, it);
				finish();
			}
			
		});
	}
}
