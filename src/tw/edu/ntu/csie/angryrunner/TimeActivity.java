package tw.edu.ntu.csie.angryrunner;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class TimeActivity extends Activity {
	
	TextView hour_tv, min_tv, sec_tv;
	Button confirm_bt, cancel_bt;
	WheelView hour, min, sec;
	
	int curHour, curMin, curSec;
	String [] hours, ms;
	
	
	String [] initArray(int size, int start) {
		String [] as = new String[size];
		for (int i=0; i < size; ++i) {
			as[i] = new Integer(start+i).toString();
		}
		return as;
	}
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time);
        
        setTitle("Time");
        
        hour_tv = (TextView)findViewById(R.id.hourText);
        hour_tv.setTypeface(Typeface.DEFAULT_BOLD);
        hour_tv.setTextColor(Color.YELLOW);
        hour_tv.setTextSize(18);
        min_tv = (TextView)findViewById(R.id.minuteText);
        min_tv.setTypeface(Typeface.DEFAULT_BOLD);
        min_tv.setTextColor(Color.YELLOW);
        min_tv.setTextSize(18);
        sec_tv = (TextView)findViewById(R.id.secondText);
        sec_tv.setTypeface(Typeface.DEFAULT_BOLD);
        sec_tv.setTextColor(Color.YELLOW);
        sec_tv.setTextSize(18);
        
        
        confirm_bt = (Button)findViewById(R.id.confirmBT);
        //confirm_bt.setTypeface(Typeface.DEFAULT_BOLD);
        confirm_bt.setTextSize(16);
        confirm_bt.setOnClickListener(new Button.OnClickListener(){
        	@Override
        	public void onClick(View v) {

        		String target = "";
        		int seconds = 0;
        		
        		if (curHour != 0) {
        			target += new Integer(curHour).toString()+" hr ";
        			seconds += (curHour * 60 * 60);
        		}
        		
        		if (curMin != 0) {
        			target += new Integer(curMin).toString()+" min ";
        			seconds += (curMin * 60);
        		}else {
        			if (curHour != 0 && curSec != 0) {
        				target += new Integer(curMin).toString()+" min ";
        			}
        		}
        		
        		if (curSec != 0) {
        			target += new Integer(curSec).toString()+" sec ";
        			seconds += curSec;
        		}
        		
        		Intent it = new Intent();
				Bundle bun = new Bundle();
				bun.putString("display", target);
				bun.putString("value", seconds + "");
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
        sec = (WheelView)findViewById(R.id.second);
        
        OnWheelChangedListener listener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                //updateDays(year, month, day);
            	curHour = hour.getCurrentItem();
            	hour.setViewAdapter(new DateArrayAdapter(TimeActivity.this, hours, curHour));
            	curMin = min.getCurrentItem();
            	min.setViewAdapter(new DateArrayAdapter(TimeActivity.this, ms, curMin));
            	curSec = sec.getCurrentItem();
            	sec.setViewAdapter(new DateArrayAdapter(TimeActivity.this, ms, curSec));
            	//tv.setText(day.getCurrentItem()+"_"+month.getCurrentItem()+"_"+year.getCurrentItem());
            }
        };
        
        curHour = 0;
        hour.setViewAdapter(new DateArrayAdapter(this, hours, curHour));
        hour.setCurrentItem(curHour);
        hour.addChangingListener(listener);
        
        curMin = 0;
        min.setViewAdapter(new DateArrayAdapter(this, ms, curMin));
        min.setCurrentItem(curMin);
        min.addChangingListener(listener);
        
        curSec = 0;
        sec.setViewAdapter(new DateArrayAdapter(this, ms, curSec));
        sec.setCurrentItem(curSec);
        sec.addChangingListener(listener);
        
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
