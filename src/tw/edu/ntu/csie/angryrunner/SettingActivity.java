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
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SettingActivity extends Activity {
	ListView settinglist;
	ArrayList<HashMap<String,String>> alhm = new ArrayList<HashMap<String,String>>();
	SimpleAdapter settingAdapter;
	String[] settings = {"Weight", "Units", "Mode", "Countdown"};
	String[] inits = {"60 kg", "Kilometers", "Walking", "Off"};
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
        
        settinglist = (ListView) findViewById(R.id.listView1);
        settingPref = getSharedPreferences("PREF_ANGRYRUNNER_SETTING", MODE_PRIVATE);
        settingPrefEdt = settingPref.edit();
        
        for(int i=0; i<settings.length; ++i){
        	HashMap<String,String> tmphm = new HashMap<String,String>();
        	tmphm.put("name", settings[i]);
        	tmphm.put("value", settingPref.getString(settings[i], inits[i]));
        	//tmphm.put("value", inits[i]);
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
    	if(requestCode != 0 && resultCode == RESULT_OK){
    		alhm.get(requestCode).put("value", data.getExtras().getString("value"));
    		settingAdapter.notifyDataSetChanged();
    		settingPrefEdt.putString(settings[requestCode], data.getExtras().getString("value"));
    		settingPrefEdt.commit();
    	}
    }
    
}
