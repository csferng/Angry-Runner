package tw.edu.ntu.csie.angryrunner;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SettingActivity extends Activity {
	ListView settinglist;
	ArrayList<HashMap<String,String>> alhm = new ArrayList<HashMap<String,String>>();
	SimpleAdapter settingAdapter;
	String[] display = new String[4];
	String[] settings = new String[4];
	String[] inits = new String[4];
	SharedPreferences settingPref;
	SharedPreferences.Editor settingPrefEdt;
	
	@Override
	protected void onResume() {
		super.onResume();
		for(int i=0; i<settings.length; ++i){
			alhm.get(i).put("value", settingPref.getString(settings[i], inits[i]));
			settingAdapter.notifyDataSetChanged();
		}
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        
        settings[0] = getString(R.string.KEY_WEIGHT);
        settings[1] = getString(R.string.KEY_UNIT);
        settings[2] = getString(R.string.KEY_MODE);
        settings[3] = getString(R.string.KEY_COUNTDOWN);
        inits[0] = getString(R.string.INIT_WEIGHT);
        inits[1] = getString(R.string.INIT_UNIT);
        inits[2] = getString(R.string.INIT_MODE);
        inits[3] = getString(R.string.INIT_COUNTDOWN);
        display[0] = getString(R.string.DISPLAY_WEIGHT);
        display[1] = getString(R.string.DISPLAY_UNIT);
        display[2] = getString(R.string.DISPLAY_MODE);
        display[3] = getString(R.string.DISPLAY_COUNTDOWN);
        
        settinglist = (ListView) findViewById(R.id.listView1);
        settingPref = getSharedPreferences(
        		getString(R.string.NAME_SHAREDPREFERENCE), 
        		MODE_PRIVATE);
        settingPrefEdt = settingPref.edit();
        
        for(int i=0; i<settings.length; ++i){
        	HashMap<String,String> tmphm = new HashMap<String,String>();
        	tmphm.put("name", display[i]);
        	tmphm.put("value", settingPref.getString(settings[i], inits[i]));
        	alhm.add(tmphm);
        }
        settingAdapter = new SimpleAdapter(SettingActivity.this, 
        		alhm, 
        		android.R.layout.simple_list_item_2, 
        		new String[] {"name", "value"}, 
        		new int[] {android.R.id.text1, android.R.id.text2}
        );
        settinglist.setAdapter(settingAdapter);
        
        settinglist.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// arg0: parent, arg1: clicked view, arg2: position, arg3: id
				Intent it = new Intent();
				switch(arg2){
					case 0:
						it.setClass(SettingActivity.this, WeightActivity.class);
						startActivityForResult(it, 0);
						break;
					case 1:
						it.setClass(SettingActivity.this, UnitActivity.class);
						startActivityForResult(it, 1);
						break;
					case 2:
						it.setClass(SettingActivity.this, ModeActivity.class);
						startActivityForResult(it, 2);
						break;
					case 3:
						it.setClass(SettingActivity.this, CountdownActivity.class);
						startActivityForResult(it, 3);
						break;
					default:
				}
			}
        	
        });
        
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if(resultCode == RESULT_OK){
    		alhm.get(requestCode).put("value", data.getExtras().getString("display"));
    		settingAdapter.notifyDataSetChanged();
    		settingPrefEdt.putString(settings[requestCode], data.getExtras().getString("display"));
    		if(requestCode == 0){
    			settingPrefEdt.putString(
    					getString(R.string.KEY_WEIGHTVALUE), 
    					data.getExtras().getString("value"));
    		}else if(requestCode == 3){
    			settingPrefEdt.putString(
    					getString(R.string.KEY_COUNTDOWNVALUE), 
    					data.getExtras().getString("value"));
    		}
    		settingPrefEdt.commit();
    	}
    }
    
    @Override
    public void onBackPressed() {
    	((TabActivity) this.getParent()).getTabHost().setCurrentTab(0);
    }
}
