package tw.edu.ntu.csie.angryrunner;

//import kankan.wheel.R;
import java.math.BigDecimal;

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

public class PaceActivity extends Activity {
	
	TextView min_tv, sec_tv, pace_tv;
	Button confirm_bt, cancel_bt;
	WheelView min, sec;
	
	int curMin, curSec;
	String [] ms;

	String Unit;
	SharedPreferences settingPref;
	
	
	String [] initArray(int size, int start) {
		String [] as = new String[size];
		for (int i=0; i < size; ++i) {
			as[i] = new Integer(start+i).toString();
		}
		return as;
	}
	
	String getUnit(){
        String str = settingPref.getString("Units", "");
        if (str.equals("Kilometers")) {
        	return "Km";
        }else if (str.equals("Miles")) {
        	return "Mile";
        }
        return "";
	}
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pace);

        settingPref = getSharedPreferences("PREF_ANGRYRUNNER_SETTING", MODE_PRIVATE);
        Unit = getUnit();
        
        setTitle("Pace");
        
        min_tv = (TextView)findViewById(R.id.minuteText);
        min_tv.setTypeface(Typeface.DEFAULT_BOLD);
        min_tv.setTextColor(Color.YELLOW);
        min_tv.setTextSize(18);
        sec_tv = (TextView)findViewById(R.id.secondText);
        sec_tv.setTypeface(Typeface.DEFAULT_BOLD);
        sec_tv.setTextColor(Color.YELLOW);
        sec_tv.setTextSize(18);
        
        pace_tv = (TextView)findViewById(R.id.paceText);
        pace_tv.setTextSize(32);
        pace_tv.setText("/ "+Unit);
        
        
        confirm_bt = (Button)findViewById(R.id.confirmBT);
        //confirm_bt.setTypeface(Typeface.DEFAULT_BOLD);
        confirm_bt.setTextSize(16);
        confirm_bt.setOnClickListener(new Button.OnClickListener(){
        	@Override
        	public void onClick(View v) {
        		
        		double speed = 0.0;
        		if (Unit.equals("Km")) {
        			speed = new Double(1000)/new Double(curMin*60 + curSec);
        		}else if (Unit.equals("Mile")) {
        			speed = new Double(1609.344)/new Double(curMin*60 + curSec);
        		}
        		
        		String target = new BigDecimal(speed).setScale(2, 1).toString()+" m/s";
        		
        		Intent it = new Intent();
				Bundle bun = new Bundle();
				bun.putString("value", target);
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
        
        
        ms = initArray(60, 0);
        
        min = (WheelView)findViewById(R.id.minute);
        sec = (WheelView)findViewById(R.id.second);
        
        OnWheelChangedListener listener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                //updateDays(year, month, day);
            	curMin = min.getCurrentItem();
            	min.setViewAdapter(new DateArrayAdapter(PaceActivity.this, ms, curMin));
            	curSec = sec.getCurrentItem();
            	sec.setViewAdapter(new DateArrayAdapter(PaceActivity.this, ms, curSec));
            	//tv.setText(day.getCurrentItem()+"_"+month.getCurrentItem()+"_"+year.getCurrentItem());
            }
        };
        
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