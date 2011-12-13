package tw.edu.ntu.csie.angryrunner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ModeActivity extends Activity {
	ListView modelist;
	ArrayAdapter<String> modeAdapter;
	String[] modes = {"Walking", "Running", "Cycling", "Other"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mode);
		
		modelist = (ListView) findViewById(R.id.listView1);
		
		modeAdapter = new ArrayAdapter<String>(ModeActivity.this, android.R.layout.simple_list_item_1, modes);
		modelist.setAdapter(modeAdapter);
		
		modelist.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// arg0: parent, arg1: clicked view, arg2: position, arg3: id
				Intent it = new Intent();
				Bundle bun = new Bundle();
				bun.putString("value", (String) modelist.getItemAtPosition(arg2));
				bun.putString("display", (String) modelist.getItemAtPosition(arg2));
				it.putExtras(bun);
				setResult(RESULT_OK, it);
				finish();
			}
			
		});
	}

}
