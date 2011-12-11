package tw.edu.ntu.csie.angryrunner;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class WorkoutSettingActivity extends Activity {
	ListView wslist;
	ArrayList<HashMap<String,String>> alhm = new ArrayList<HashMap<String,String>>();
	SimpleAdapter wsAdapter;
	String[] workoutsettings = {"Mode", "Speed", "Time", "Distance"};
	Button bt_confirm, bt_cancel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workout_setting);
		
		wslist = (ListView) findViewById(R.id.listView1);
		bt_confirm = (Button) findViewById(R.id.btConfirm);
		bt_cancel = (Button) findViewById(R.id.btCancel);
		
		for(int i=0; i<workoutsettings.length; ++i){
			HashMap<String,String> tmphm = new HashMap<String,String>();
			tmphm.put("name", workoutsettings[i]);
			tmphm.put("value", "");
			alhm.add(tmphm);
		}
        wsAdapter = new SimpleAdapter(WorkoutSettingActivity.this, 
        		alhm, 
        		android.R.layout.simple_list_item_2, 
        		new String[] {"name", "value"}, 
        		new int[] {android.R.id.text1, android.R.id.text2}
        );
        wslist.setAdapter(wsAdapter);
        
        wslist.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// arg0: parent, arg1: clicked view, arg2: position, arg3: id
				Intent it = new Intent();
				switch(arg2){
					case 0:
						it.setClass(WorkoutSettingActivity.this, ModeActivity.class);
						startActivityForResult(it, 0);
						break;
					case 1:
						break;
					case 2:
						break;
					case 3:
						break;
					default:
				}
			}
        	
        });

		
		bt_cancel.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
    		alhm.get(requestCode).put("value", data.getExtras().getString("value"));
    		wsAdapter.notifyDataSetChanged();
    	}
	}
	
}
