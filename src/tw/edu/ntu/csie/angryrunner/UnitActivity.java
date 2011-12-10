package tw.edu.ntu.csie.angryrunner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class UnitActivity extends Activity {
	ListView unitlist;
	ArrayAdapter<String> unitAdapter;
	String[] units = {"Kilometers", "Miles"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.unit);
		
		unitlist = (ListView) findViewById(R.id.listView1);
		
		unitAdapter = new ArrayAdapter<String>(UnitActivity.this, android.R.layout.simple_list_item_1, units);
		unitlist.setAdapter(unitAdapter);
		
		unitlist.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// arg0: parent, arg1: clicked view, arg2: position, arg3: id
				Intent it = new Intent();
				Bundle bun = new Bundle();
				bun.putString("value", (String) unitlist.getItemAtPosition(arg2));
				it.putExtras(bun);
				setResult(RESULT_OK, it);
				finish();
			}
			
		});
	}
}
