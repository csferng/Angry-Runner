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
	private ListView wslist;
	private ArrayList<HashMap<String,String>> alhm = new ArrayList<HashMap<String,String>>();
	private SimpleAdapter wsAdapter;
	private String[] workoutsettings = new String[4];
	private String[] inits = new String[4];
	private String[] goalsettings = new String[4];
	private String[] display = new String[4];
	private String goalinit;
	private String[] values = new String[4];
	private Button bt_confirm, bt_cancel;
	private SharedPreferences settingPref;
	private UnitHandler unitHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workout_setting);
		setTitle("Goal Setting " + getString(R.string.TITLE));
		
		workoutsettings[0] = getString(R.string.KEY_MODE);
		workoutsettings[1] = getString(R.string.KEY_SPEED);
		workoutsettings[2] = getString(R.string.KEY_TIME);
		workoutsettings[3] = getString(R.string.KEY_DISTANCE);
		inits[0] = getString(R.string.VALUE_WALKING);
		inits[1] = ""; inits[2] = ""; inits[3] = "";
		goalsettings[0] = getString(R.string.KEY_PACEGOAL);
		goalsettings[1] = getString(R.string.KEY_SPEEDGOAL);
		goalsettings[2] = getString(R.string.KEY_TIMEGOAL);
		goalsettings[3] = getString(R.string.KEY_DISTANCEGOAL);
		goalinit = getString(R.string.INIT_GOALVALUES);
		display[0] = getString(R.string.DISPLAY_MODE);
		display[1] = getString(R.string.DISPLAY_PACE);
		display[2] = getString(R.string.DISPLAY_TIME);
		display[3] = getString(R.string.DISPLAY_DISTANCE);
		
		wslist = (ListView) findViewById(R.id.listView1);
		bt_confirm = (Button) findViewById(R.id.btConfirm);
		bt_cancel = (Button) findViewById(R.id.btCancel);
		settingPref = getSharedPreferences(
				getString(R.string.NAME_SHAREDPREFERENCE), 
				MODE_PRIVATE);
        unitHandler = new UnitHandler(this, settingPref);
		
		for(int i=0; i<workoutsettings.length; ++i){
			values[i] = settingPref.getString(goalsettings[i], goalinit);

			HashMap<String,String> tmphm = new HashMap<String,String>();
			switch(i) {
			case 0:
				tmphm.put("name", display[i]);
				tmphm.put("value", getModeDisplay(settingPref.getString(workoutsettings[i], inits[i])));
				break;
			case 2:
				tmphm.put("name", display[i]);
				tmphm.put("value", settingPref.getString(workoutsettings[i], inits[i]));
				break;
			case 3:
				tmphm.put("name", display[i]);
				tmphm.put("value", unitHandler.presentDistanceWithUnit(Double.parseDouble(values[i])));
				break;
			case 1:
				double speed = Double.parseDouble(values[1]);
				double pace = Double.parseDouble(values[0])/unitHandler.distanceToUnit(1.0);
				String unit = unitHandler.getDisplayUnit();
				tmphm.put("name", display[1]);
				tmphm.put("value", String.format("%.2f m/s & %.0f s/%s", speed, pace, unit));
				break;
			}
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
				Bundle bun = new Bundle();
				switch(arg2){
					case 0:
						it.setClass(WorkoutSettingActivity.this, ModeActivity.class);
						startActivityForResult(it, 0);
						break;
					case 1:
						it.setClass(WorkoutSettingActivity.this, PaceActivity.class);
						bun.putString(goalsettings[0], values[0]);
						bun.putString(goalsettings[1], values[1]);
						it.putExtras(bun);
						startActivityForResult(it, 1);
						break;
					case 2:
						it.setClass(WorkoutSettingActivity.this, TimeActivity.class);
						bun.putString(goalsettings[2], values[2]);
						it.putExtras(bun);
						startActivityForResult(it, 2);
						break;
					case 3:
						it.setClass(WorkoutSettingActivity.this, DistanceActivity.class);
						bun.putString(goalsettings[3], values[3]);
						it.putExtras(bun);
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
		        SharedPreferences.Editor settingPrefEdt = settingPref.edit();
				settingPrefEdt.putString(workoutsettings[0], getMode(alhm.get(0).get("value")));
				settingPrefEdt.putString(workoutsettings[1], alhm.get(1).get("value"));
				settingPrefEdt.putString(workoutsettings[2], alhm.get(2).get("value"));
				settingPrefEdt.putString(workoutsettings[3], alhm.get(3).get("value"));
				settingPrefEdt.putString(goalsettings[0], values[0]);
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
	
	String getModeDisplay(String mode){
		if(mode.equals(getString(R.string.VALUE_WALKING))){
			return getString(R.string.STR_WALKING);
		}else if(mode.equals(getString(R.string.VALUE_RUNNING))){
			return getString(R.string.STR_RUNNING);
		}else if(mode.equals(getString(R.string.VALUE_CYCLING))){
			return getString(R.string.STR_CYCLING);
		}
		
		return getString(R.string.INIT_MODE);
	}
	
	String getMode(String mode){
		if(mode.equals(getString(R.string.STR_WALKING))){
			return getString(R.string.VALUE_WALKING);
		}else if(mode.equals(getString(R.string.STR_RUNNING))){
			return getString(R.string.VALUE_RUNNING);
		}else if(mode.equals(getString(R.string.STR_CYCLING))){
			return getString(R.string.VALUE_CYCLING);
		}
		
		return getString(R.string.VALUE_WALKING);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
    		alhm.get(requestCode).put("value", data.getExtras().getString("display"));
    		wsAdapter.notifyDataSetChanged();
    		switch(requestCode){
    			case 0:
    				break;
    			case 1:
    				values[0] = data.getExtras().getString(goalsettings[0]);
    				values[1] = data.getExtras().getString(goalsettings[1]);
    				break;
    			case 2:
    			case 3:
    				values[requestCode] = data.getExtras().getString(goalsettings[requestCode]);
    				break;
    			default:
    		}
    	}
	}
	
}
