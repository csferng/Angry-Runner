package tw.edu.ntu.csie.angryrunner;

import java.text.DecimalFormat;

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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class PaceActivity extends Activity {

	String Unit, Pace, Speed;
	SharedPreferences settingPref;
	SharedPreferences.Editor settingPrefEdt;
	
	TextView min_tv, sec_tv, pace_tv, speed_tv;
	Button confirm_bt, cancel_bt;
	WheelView min, sec;
	
	int curMinItemIndex, curSecItemIndex;
	int curMin, curSec;
	String [] ms;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pace);

        final DecimalFormat f = new DecimalFormat("0000.00");

        settingPref = getSharedPreferences("PREF_ANGRYRUNNER_SETTING", MODE_PRIVATE);
        settingPrefEdt = settingPref.edit();
        
        Unit = getUnit();
        Pace = getPace();
        Speed = getSpeed();
        
        setTitle("Pace/Speed");
        
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
        
        
        speed_tv = (TextView)findViewById(R.id.speedText);
        speed_tv.setTypeface(Typeface.DEFAULT_BOLD);
        speed_tv.setTextColor(Color.CYAN);
        speed_tv.setTextSize(24);
		speed_tv.setText("Speed:    "+f.format( Double.parseDouble(Speed) )+" m/s");
        
        
        confirm_bt = (Button)findViewById(R.id.confirmBT);
        //confirm_bt.setTypeface(Typeface.DEFAULT_BOLD);
        confirm_bt.setTextSize(16);
        confirm_bt.setOnClickListener(new Button.OnClickListener(){
        	@Override
        	public void onClick(View v) {
        		
        		Pace = setPace();
				settingPrefEdt.putString("Pace", Pace).commit();
        		Speed = calculateSpeed();
				settingPrefEdt.putString("Speed", Speed).commit();

        		Intent it = new Intent();
				Bundle bun = new Bundle();
				bun.putString("value", Speed);
				bun.putString("display", Speed + " m/s");
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
        
        initWheelValueIndex();
        
        OnWheelChangedListener minListener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                //updateDays(year, month, day);
            	curMinItemIndex = min.getCurrentItem();
            	curMin = Integer.parseInt( ms[curMinItemIndex] );
            	min.setViewAdapter(new DateArrayAdapter(PaceActivity.this, ms, curMinItemIndex));
            	min.setCurrentItem( curMinItemIndex );
                
                Pace = setPace();
        		Speed = calculateSpeed();
        		speed_tv.setText("Speed:    "+f.format( Double.parseDouble(Speed) )+" m/s");
            }
        };
        
        //curMinItemIndex = findIndex( paceDigits[0] );
        curMin = Integer.parseInt( ms[curMinItemIndex] );
        min.setViewAdapter(new DateArrayAdapter(this, ms, curMinItemIndex));
        min.setCurrentItem( curMinItemIndex );
        min.addChangingListener(minListener);

        OnWheelChangedListener secListener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                //updateDays(year, month, day);
            	curSecItemIndex = sec.getCurrentItem();
            	curSec = Integer.parseInt( ms[curSecItemIndex] );
            	sec.setViewAdapter(new DateArrayAdapter(PaceActivity.this, ms, curSecItemIndex));
            	sec.setCurrentItem( curSecItemIndex );
                
                Pace = setPace();
        		Speed = calculateSpeed();
        		speed_tv.setText("Speed:    "+f.format( Double.parseDouble(Speed) )+" m/s");
            }
        };
        
        //curSecItemIndex = findIndex( paceDigits[1] );
        curSec = Integer.parseInt( ms[curSecItemIndex] );
        sec.setViewAdapter(new DateArrayAdapter(this, ms, curSecItemIndex));
        sec.setCurrentItem( curSecItemIndex );
        sec.addChangingListener(secListener);
        
	}
	
	
	String [] initArray(int size, int start) {
		String [] as = new String[size];
		for (int i=0; i < size; ++i) {
			as[i] = new Integer(start+i).toString();
		}
		return as;
	}
	
	String getUnit(){
        String str = settingPref.getString("Unit", "Kilometer");
        if (str.equals("Kilometer")) {
        	return "Km";
        }else if (str.equals("Mile")) {
        	return "Mile";
        }
        return "";
	}

	String getPace(){
        String str = settingPref.getString("Pace", "0");
        Log.i("getPace()", Pace);
        return str;
	}
	
	String setPace() {
		Integer sum = curMin*60 + curSec;
		return sum.toString();
	}

	String getSpeed(){
        String str = settingPref.getString("Speed", "0");
        Log.i("getSpeed()", Speed);
        return str;
	}
	
	String calculateSpeed() {
		Integer sum = curMin*60 + curSec;
		if (sum.equals(0)) return "0";
		
		Double speed = new Double(0);
		if (Unit.equals("Km")) {
			speed = new Double(1000)/new Double(sum);
		}else if (Unit.equals("Mile")) {
			speed = new Double(1609.344)/new Double(sum);
		}

		return doubleStringFormation( String.format("%.2f", speed) );
	}
	
	String doubleStringFormation(String target) {
		int pos = target.indexOf(".");
		Log.i("target", target);
		Double d0 = Double.parseDouble(target);
		Double d1 = Double.parseDouble(target.substring(0, pos+2));
		Double d2 = Double.parseDouble(target.substring(0, pos+3));
		if ( d0.equals(d1) && d0.equals(d2)) {
			return target.substring(0, pos);
		}else if ( d1.equals(d2) ) {
			return target.substring(0, pos+2);
		}else {
			return target;
		}
	}
	
	void initWheelValueIndex() {
		int pace = Integer.parseInt(Pace);
		curSecItemIndex = pace%60;
		curMinItemIndex = pace/60;
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