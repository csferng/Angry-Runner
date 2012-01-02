package tw.edu.ntu.csie.angryrunner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class UnitActivity extends Activity {
	ListView unitlist;
	ArrayAdapter<String> unitAdapter;
	String[] units = new String[2];
	String[] unitDisplay = new String[2];
	//String[] units = {"Kilometer"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.unit);
		setTitle(getString(R.string.DISPLAY_UNIT) + " " + getString(R.string.TITLE));
		units[0] = getString(R.string.VALUE_KILOMETER);
		units[1] = getString(R.string.VALUE_MILE);
		unitDisplay[0] = getString(R.string.STR_KILOMETER);
		unitDisplay[1] = getString(R.string.STR_MILE);
		
		unitlist = (ListView) findViewById(R.id.listView1);
		
		unitAdapter = new ArrayAdapter<String>(UnitActivity.this, android.R.layout.simple_list_item_1, unitDisplay);
		unitlist.setAdapter(unitAdapter);
		
		unitlist.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// arg0: parent, arg1: clicked view, arg2: position, arg3: id
				Intent it = new Intent();
				Bundle bun = new Bundle();
				bun.putString("value", units[arg2]);
				bun.putString("display", (String) unitlist.getItemAtPosition(arg2));
				it.putExtras(bun);
				setResult(RESULT_OK, it);
				finish();
			}
			
		});
	}
}
