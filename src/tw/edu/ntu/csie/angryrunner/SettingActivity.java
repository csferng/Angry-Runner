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
	private ListView settinglist;
	private ArrayList<HashMap<String,String>> alhm = new ArrayList<HashMap<String,String>>();
	private SimpleAdapter settingAdapter;
	private String[] title = new String[4];
	private String[] settings = new String[4];
	private String[] inits = new String[4];
	private SharedPreferences settingPref;
	private UnitHandler unitHandler;
	private ModeHandler modeHandler;
	
	@Override
	protected void onResume() {
		super.onResume();
		for(int i=0; i<settings.length; ++i){
			if(i == 1) {
				alhm.get(i).put("value", unitHandler.getSpeakUnit());
			}else if(i == 2){
				alhm.get(i).put("value", modeHandler.getModeDisplay(
						settingPref.getString(settings[i], inits[i])));
			}else if(i == 3){
				alhm.get(i).put("value", getCountdownDisplay(
						Integer.parseInt(settingPref.getString(settings[i], inits[i]))));
			}else{
				alhm.get(i).put("value", settingPref.getString(settings[i], inits[i])
						+ " " + getString(R.string.STR_KG));
			}
			settingAdapter.notifyDataSetChanged();
		}
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        
        settings[0] = getString(R.string.KEY_WEIGHTVALUE);
        settings[1] = getString(R.string.KEY_UNIT);
        settings[2] = getString(R.string.KEY_MODE);
        settings[3] = getString(R.string.KEY_COUNTDOWN);
        inits[0] = getString(R.string.INIT_WEIGHTVALUE);
        inits[1] = getString(R.string.INIT_UNIT);
        inits[2] = getString(R.string.VALUE_WALKING);
        inits[3] = getString(R.string.INIT_COUNTDOWNVALUE);
        title[0] = getString(R.string.DISPLAY_WEIGHT);
        title[1] = getString(R.string.DISPLAY_UNIT);
        title[2] = getString(R.string.DISPLAY_MODE);
        title[3] = getString(R.string.DISPLAY_COUNTDOWN);
        
        settinglist = (ListView) findViewById(R.id.listView1);
        settingPref = getSharedPreferences(
        		getString(R.string.NAME_SHAREDPREFERENCE), 
        		MODE_PRIVATE);
        unitHandler = new UnitHandler(this, settingPref);
        modeHandler = new ModeHandler(this);
        
        for(int i=0; i<settings.length; ++i){
        	HashMap<String,String> tmphm = new HashMap<String,String>();
        	tmphm.put("name", title[i]);
        	if(i == 1) {
        		tmphm.put("value", unitHandler.getSpeakUnit());
        	}else if(i == 2){
        		tmphm.put("value", modeHandler.getModeDisplay(
        				settingPref.getString(settings[i], inits[i])));
        	}else if(i == 3){
        		tmphm.put("value", getCountdownDisplay(
						Integer.parseInt(settingPref.getString(settings[i], inits[i]))));
			}else{
        		tmphm.put("value", settingPref.getString(settings[i], inits[i])
						+ " " + getString(R.string.STR_KG));
        	}
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
            SharedPreferences.Editor settingPrefEdt = settingPref.edit();
    		alhm.get(requestCode).put("value", data.getExtras().getString("display"));
    		settingAdapter.notifyDataSetChanged();
    		settingPrefEdt.putString(settings[requestCode], data.getExtras().getString("value"));
    		
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
    
    String getCountdownDisplay(int sec){
    	if(sec > 0){
    		return (sec + " " + getString(R.string.STR_SEC));
    	}
    	
    	return getString(R.string.INIT_COUNTDOWN);
    }
	    
    @Override
    public void onBackPressed() {
    	((TabActivity) this.getParent()).getTabHost().setCurrentTab(0);
    }
}
