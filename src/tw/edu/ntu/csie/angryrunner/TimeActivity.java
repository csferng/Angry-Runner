package tw.edu.ntu.csie.angryrunner;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class TimeActivity extends Activity {

	String Time;
	SharedPreferences settingPref;
	SharedPreferences.Editor settingPrefEdt;

	WheelView hour, min;
	TextView hour_tv, min_tv;
	Button confirm_bt, cancel_bt;
	
	int curHourItemIndex, curMinItemIndex;
	int curHour, curMin;
	String [] hours, ms;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time);
        
        settingPref = getSharedPreferences("PREF_ANGRYRUNNER_SETTING", MODE_PRIVATE);
        settingPrefEdt = settingPref.edit();
        
        setTitle("Time");
        Time = getTime();
        
        hour_tv = (TextView)findViewById(R.id.hourText);
        hour_tv.setTypeface(Typeface.DEFAULT_BOLD);
        hour_tv.setTextColor(Color.YELLOW);
        hour_tv.setTextSize(18);
        min_tv = (TextView)findViewById(R.id.minuteText);
        min_tv.setTypeface(Typeface.DEFAULT_BOLD);
        min_tv.setTextColor(Color.YELLOW);
        min_tv.setTextSize(18);
        
        
        confirm_bt = (Button)findViewById(R.id.confirmBT);
        //confirm_bt.setTypeface(Typeface.DEFAULT_BOLD);
        confirm_bt.setTextSize(16);
        confirm_bt.setOnClickListener(new Button.OnClickListener(){
        	@Override
        	public void onClick(View v) {

        		String target = "";
        		int mins = 0;
        		
        		if (curHour != 0) {
        			target += new Integer(curHour).toString()+" hr ";
        			mins += (curHour * 60);
        		}
        		
        		if (curMin != 0) {
        			target += new Integer(curMin).toString()+" min ";
        			mins += curMin;
        		}
        		
        		Time = calculateTime();
				//settingPrefEdt.putString("Time", Time).commit();
        		
        		Intent it = new Intent();
				Bundle bun = new Bundle();
				bun.putString("display", target);
				bun.putString(
						TimeActivity.this.getResources().getString(R.string.KEY_TIMEGOAL), 
						Time);
				it.putExtras(bun);
				
				setResult(RESULT_OK, it);
				finish();
        	}
        });
        
        cancel_bt = (Button)findViewById(R.id.cancelBT);
        //cancel_bt.setTypeface(Typeface.DEFAULT_BOLD);
        cancel_bt.setTextSize(16);
        cancel_bt.setOnClickListener(new Button.OnClickListener(){
        	@Override
        	public void onClick(View v) {
        		finish();
        	}
        });
        
        
        hours = initArray(24, 0);
        ms = initArray(60, 0);
        
        hour = (WheelView)findViewById(R.id.hour);
        min = (WheelView)findViewById(R.id.minute);
        
        initWheelValueIndex();
        
        
        OnWheelChangedListener hourListener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                //updateDays(year, month, day);
            	curHourItemIndex = hour.getCurrentItem();
            	curHour = Integer.parseInt( hours[curHourItemIndex] );
            	hour.setViewAdapter(new DateArrayAdapter(TimeActivity.this, hours, curHourItemIndex));
            	hour.setCurrentItem(curHourItemIndex);
            	Time = calculateTime();
            	
            }
        };
        
        //curHourItemIndex = 0;
    	curHour = Integer.parseInt( hours[curHourItemIndex] );
    	hour.setViewAdapter(new DateArrayAdapter(TimeActivity.this, hours, curHourItemIndex));
    	hour.setCurrentItem(curHourItemIndex);
        hour.addChangingListener(hourListener);

        
        OnWheelChangedListener minListener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                //updateDays(year, month, day);
            	curMinItemIndex = min.getCurrentItem();
            	curMin = Integer.parseInt( hours[curMinItemIndex] );
            	min.setViewAdapter(new DateArrayAdapter(TimeActivity.this, ms, curMinItemIndex));
            	min.setCurrentItem(curMinItemIndex);
            	Time = calculateTime();
            }
        };
        
        //curMinItemIndex = 0;
    	curMin = Integer.parseInt( hours[curMinItemIndex] );
    	min.setViewAdapter(new DateArrayAdapter(TimeActivity.this, ms, curMinItemIndex));
    	min.setCurrentItem(curMinItemIndex);
        min.addChangingListener(minListener);
        
	}

	
	String [] initArray(int size, int start) {
		String [] as = new String[size];
		for (int i=0; i < size; ++i) {
			as[i] = new Integer(start+i).toString();
		}
		return as;
	}
	
	String getTime() {
		String str = settingPref.getString("Time", "0");
		if (str.equals("")) {
			return "0";
		}
		return str;
	}
	
	String calculateTime() {
		Integer sum = curHour*3600+curMin*60;
		return sum.toString();
	}
	
	void initWheelValueIndex() {
		int time = Integer.parseInt(Time);
		curMinItemIndex = (time/60)%60;
		curHourItemIndex = (time/60)/60;
	}
	
	
	public class DateArrayAdapter extends ArrayWheelAdapter<String> {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;
        
        /**
         * Constructor
         */
        public DateArrayAdapter(Context context, String[] items, int current) {
            super(context, items);
            this.currentValue = current;
            setTextSize(32);
        }
        
        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }
        
        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }

}
