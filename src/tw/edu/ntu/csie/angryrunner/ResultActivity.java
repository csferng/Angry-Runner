package tw.edu.ntu.csie.angryrunner;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ResultActivity extends Activity {
	ListView resultlist;
	ArrayList<HashMap<String,String>> alhm = new ArrayList<HashMap<String,String>>();
	SimpleAdapter resultAdapter;
	String[] resultItems = new String[5];
	Button btConfirm, btCancel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result);
		setTitle("Result " + getString(R.string.TITLE));
		
		resultItems[0] = getString(R.string.KEY_MODE);
		resultItems[1] = getString(R.string.KEY_SPEED);
		resultItems[2] = getString(R.string.KEY_DURATION);
		resultItems[3] = getString(R.string.KEY_DISTANCE);
		resultItems[4] = getString(R.string.KEY_CALORIE);
		
		resultlist = (ListView) findViewById(R.id.listView1);
		btConfirm = (Button) findViewById(R.id.btConfirm);
		btCancel = (Button) findViewById(R.id.btCancel);
		
		final Bundle bun = this.getIntent().getExtras();
		for(int i=0; i<resultItems.length; ++i){
			HashMap<String,String> tmphm = new HashMap<String,String>();
			tmphm.put("name", resultItems[i]);
			tmphm.put("value", (String) bun.get(resultItems[i]));
			alhm.add(tmphm);
		}
		resultAdapter = new SimpleAdapter(ResultActivity.this, 
				alhm, 
				R.layout.result_item, 
				new String[] {"name", "value"}, 
				new int[] {R.id.tvItemName, R.id.tvItemValue}
		);
		resultlist.setAdapter(resultAdapter);
		
		btCancel.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		
		btConfirm.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent it = new Intent();
				it.putExtras(bun);
				setResult(RESULT_OK, it);
				finish();
			}
		});
	}
}
