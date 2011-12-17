package tw.edu.ntu.csie.angryrunner;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
	String[] workoutsettings = new String[4];
	String[] inits = new String[4];
	String[] goalsettings = new String[4];
	String[] values = new String[4];
	Button bt_confirm, bt_cancel;
	SharedPreferences settingPref;
	SharedPreferences.Editor settingPrefEdt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workout_setting);
		
		workoutsettings[0] = this.getResources().getString(R.string.KEY_MODE);
		workoutsettings[1] = this.getResources().getString(R.string.KEY_SPEED);
		workoutsettings[2] = this.getResources().getString(R.string.KEY_TIME);
		workoutsettings[3] = this.getResources().getString(R.string.KEY_DISTANCE);
		inits[0] = this.getResources().getString(R.string.INIT_MODE);
		inits[1] = ""; inits[2] = ""; inits[3] = "";
		goalsettings[0] = "";
		goalsettings[1] = this.getResources().getString(R.string.KEY_SPEEDGOAL);
		goalsettings[2] = this.getResources().getString(R.string.KEY_TIMEGOAL);
		goalsettings[3] = this.getResources().getString(R.string.KEY_DISTANCEGOAL);
		
		wslist = (ListView) findViewById(R.id.listView1);
		bt_confirm = (Button) findViewById(R.id.btConfirm);
		bt_cancel = (Button) findViewById(R.id.btCancel);
		settingPref = getSharedPreferences(
				this.getResources().getString(R.string.NAME_SHAREDPREFERENCE), 
				MODE_PRIVATE);
        settingPrefEdt = settingPref.edit();
		
		for(int i=0; i<workoutsettings.length; ++i){
			HashMap<String,String> tmphm = new HashMap<String,String>();
			tmphm.put("name", workoutsettings[i]);
			tmphm.put("value", settingPref.getString(workoutsettings[i], inits[i]));
			alhm.add(tmphm);
			
			if(i > 0)
				values[i] = settingPref.getString(goalsettings[i], 
						this.getResources().getString(R.string.INIT_GOALVALUES));
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
						it.setClass(WorkoutSettingActivity.this, PaceActivity.class);
						startActivityForResult(it, 1);
						break;
					case 2:
						it.setClass(WorkoutSettingActivity.this, TimeActivity.class);
						startActivityForResult(it, 2);
						break;
					case 3:
						it.setClass(WorkoutSettingActivity.this, DistanceActivity.class);
						startActivityForResult(it, 3);
						break;
					default:
				}
			}
        	
        });

        bt_confirm.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO 
				settingPrefEdt.putString(workoutsettings[0], alhm.get(0).get("value"));
				settingPrefEdt.putString(workoutsettings[1], alhm.get(1).get("value"));
				settingPrefEdt.putString(workoutsettings[2], alhm.get(2).get("value"));
				settingPrefEdt.putString(workoutsettings[3], alhm.get(3).get("value"));
				settingPrefEdt.putString(goalsettings[1], values[1]);
				settingPrefEdt.putString(goalsettings[2], values[2]);
				settingPrefEdt.putString(goalsettings[3], values[3]);
	    		settingPrefEdt.commit();
	    		
	    		setResult(RESULT_OK);
	    		finish();
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
    		alhm.get(requestCode).put("value", data.getExtras().getString("display"));
    		wsAdapter.notifyDataSetChanged();
    		values[requestCode] = data.getExtras().getString("value");
    	}
	}
	
}
