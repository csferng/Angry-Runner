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
	ArrayList<HashMap<String,Object>> alhm = new ArrayList<HashMap<String,Object>>();
	SimpleAdapter resultAdapter;
	String[] resultItems = new String[5];
	String[] display = new String[5];
	Button btConfirm, btCancel;
	private ModeHandler modeHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result);
		setTitle(getString(R.string.STR_RESULT) + " " + getString(R.string.TITLE));
		
		resultItems[0] = getString(R.string.KEY_MODE);
		resultItems[1] = getString(R.string.KEY_SPEED);
		resultItems[2] = getString(R.string.KEY_DURATION);
		resultItems[3] = getString(R.string.KEY_DISTANCE);
		resultItems[4] = getString(R.string.KEY_CALORIE);
		display[0] = getString(R.string.DISPLAY_MODE);
		display[1] = getString(R.string.DISPLAY_SPEED);
		display[2] = getString(R.string.DISPLAY_DURATION);
		display[3] = getString(R.string.DISPLAY_DISTANCE);
		display[4] = getString(R.string.DISPLAY_CALORIE);
		
		resultlist = (ListView) findViewById(R.id.listView1);
		btConfirm = (Button) findViewById(R.id.btConfirm);
		btCancel = (Button) findViewById(R.id.btCancel);
		modeHandler = new ModeHandler(this);
		
		final Bundle bun = this.getIntent().getExtras();
		for(int i=0; i<resultItems.length; ++i){
			HashMap<String,Object> tmphm = new HashMap<String,Object>();
			String value = (String) bun.get(resultItems[i]);
			/*
			if(i == 0){
				if(value.equals("Walking")){
					tmphm.put("pic", R.drawable.walking_v5);
				}else if(value.equals("Running")){
					tmphm.put("pic", R.drawable.running_v5);
				}else if(value.equals("Cycling")){
					tmphm.put("pic", R.drawable.cycling_v5);
				}else{
					tmphm.put("pic", R.drawable.ic_launcher);
				}
			}else if(i == 1){
				tmphm.put("pic", R.drawable.ic_launcher);
			}else if(i == 2){
				tmphm.put("pic", R.drawable.clock_48);
			}else if(i == 3){
				tmphm.put("pic", R.drawable.map_pin_48);
			}else if(i == 4){
				tmphm.put("pic", R.drawable.fire);
			}
			*/
			//tmphm.put("pic", R.drawable.ic_launcher);
			tmphm.put("name", display[i]);
			if(i == 0){
				tmphm.put("value", modeHandler.getModeDisplay(value));
			}else{
				tmphm.put("value", value);
			}
			alhm.add(tmphm);
		}
		/*
		resultAdapter = new SimpleAdapter(ResultActivity.this, 
				alhm, 
				R.layout.result_item, 
				new String[] {"pic", "name", "value"}, 
				new int[] {R.id.imageView1, R.id.tvItemName, R.id.tvItemValue}
		);
		*/
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
