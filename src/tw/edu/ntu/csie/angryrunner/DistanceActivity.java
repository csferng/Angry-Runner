package tw.edu.ntu.csie.angryrunner;

//import kankan.wheel.R;
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

public class DistanceActivity extends Activity {

	TextView dec_tv, speed_tv;
	WheelView cen, dec, unit;
	Button confirm_bt, cancel_bt;
	
	int curCenItemIndex, curDecItemIndex, curUnitItemIndex;
	int curCen, curDec;
	double curUnit;
	String [] digits, dpoints;

	String Unit, Distance;
	SharedPreferences settingPref;
	SharedPreferences.Editor settingPrefEdt;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.distance);

        settingPref = getSharedPreferences(
        		this.getResources().getString(R.string.NAME_SHAREDPREFERENCE), 
        		MODE_PRIVATE);
        settingPrefEdt = settingPref.edit();
        
        Unit = getUnit();
        Distance = getDistance(this.getIntent().getExtras());
        Log.i("Distance", Distance);
        
        setTitle(this.getResources().getString(R.string.KEY_DISTANCE));
        
        
        dec_tv = (TextView)findViewById(R.id.minuteText);
        dec_tv.setTypeface(Typeface.DEFAULT_BOLD);
        dec_tv.setTextColor(Color.YELLOW);
        dec_tv.setTextSize(24);
        dec_tv.setText(Unit);
        
        
        confirm_bt = (Button)findViewById(R.id.confirmBT);
        //confirm_bt.setTypeface(Typeface.DEFAULT_BOLD);
        confirm_bt.setTextSize(16);
        confirm_bt.setOnClickListener(new Button.OnClickListener(){
        	@Override
        	public void onClick(View v) {
        		
        		//double sum = new Double(curCen*10) + new Double(curDec) + curUnit;
        		Distance= calculateDistance();
//        		settingPrefEdt.putString("Distance", Distance).commit();
//        		Log.i("Distance", settingPref.getString(Distance, "NULL"));
        		Log.i("Distance", Distance);
        		
        		Intent it = new Intent();
				Bundle bun = new Bundle();
				Log.i("d-value", Distance);
				bun.putString(
						DistanceActivity.this.getResources().getString(R.string.KEY_DISTANCEGOAL), 
						Distance);
				Log.i("d-display", Distance+" "+Unit);
				bun.putString("display", Distance+" "+Unit);
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
        
        
        digits = initArray(10, 0);
        dpoints = initArray(20, 0, 0.05);
        
        cen = (WheelView)findViewById(R.id.centesimal);
        dec = (WheelView)findViewById(R.id.decimal);
        unit = (WheelView)findViewById(R.id.unit);
        
        
        initWheelValueIndex();
        
        
        OnWheelChangedListener cenListener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                //updateDays(year, month, day);
            	curCenItemIndex = cen.getCurrentItem();
            	curCen = Integer.parseInt( digits[curCenItemIndex] );
            	cen.setViewAdapter(new DateArrayAdapter(DistanceActivity.this, digits, curCenItemIndex));
            	cen.setCurrentItem( curCenItemIndex );
            	Distance = calculateDistance();
            }
        };
        
    	//curCenItemIndex = 0;
    	curCen = Integer.parseInt( digits[curCenItemIndex] );
    	cen.setViewAdapter(new DateArrayAdapter(DistanceActivity.this, digits, curCenItemIndex));
        cen.setCurrentItem( curCenItemIndex );
        cen.addChangingListener(cenListener);

        
        OnWheelChangedListener decListener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                //updateDays(year, month, day);
            	curDecItemIndex = dec.getCurrentItem();
            	curDec = Integer.parseInt( digits[curDecItemIndex] );
            	dec.setViewAdapter(new DateArrayAdapter(DistanceActivity.this, digits, curDecItemIndex));
            	dec.setCurrentItem( curDecItemIndex );
            	Distance = calculateDistance();
            }
        };
        
        //curDecItemIndex = 0;
    	curDec = Integer.parseInt( digits[curDecItemIndex] );
    	dec.setViewAdapter(new DateArrayAdapter(DistanceActivity.this, digits, curDecItemIndex));
    	dec.setCurrentItem( curDecItemIndex );
        dec.addChangingListener(decListener);
        
        
        OnWheelChangedListener unitListener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                //updateDays(year, month, day);
            	curUnitItemIndex = unit.getCurrentItem();
            	curUnit = Double.parseDouble( dpoints[curUnitItemIndex] );
            	unit.setViewAdapter(new DateArrayAdapter(DistanceActivity.this, dpoints, curUnitItemIndex));
            	unit.setCurrentItem( curUnitItemIndex );
            	Distance = calculateDistance();
            }
        };
        
        //curUnitItemIndex = 0;
    	curUnit = Double.parseDouble( dpoints[curUnitItemIndex] );
    	unit.setViewAdapter(new DateArrayAdapter(DistanceActivity.this, dpoints, curUnitItemIndex));
    	unit.setCurrentItem( curUnitItemIndex );
        unit.addChangingListener(unitListener);
        
	}	

	
	String [] initArray(int size, int start) {
		String [] as = new String[size];
		for (int i=0; i < size; ++i) {
			as[i] = new Integer(start+i).toString();
		}
		return as;
	}

	String [] initArray(int size, double start, double gap) {
		String [] as = new String[size];
		String s = "";
		int pos = -1;
		for (int i=0; i < size; ++i) {
			s = String.format("%.2f", start);
			Log.i("s", s);
			Log.i("pos", new Integer(pos).toString());
			pos = s.indexOf(".");
			as[i] = s.substring(pos);
			start += gap;
		}
		return as;
	}
	String getUnit(){
        String str = settingPref.getString(
        		this.getResources().getString(R.string.KEY_UNIT), 
        		this.getResources().getString(R.string.INIT_UNIT));
        if (str.equals("Kilometer")) {
        	return "Km";
        }else if (str.equals("Mile")) {
        	return "Mile";
        }else {
        	return "Km";
        }
	}
	
	String getDistance(Bundle bun){
		/*
        String str = settingPref.getString(
        		this.getResources().getString(R.string.KEY_DISTANCEGOAL), 
        		this.getResources().getString(R.string.INIT_GOALVALUES));
        */
		String str = bun.getString(
				this.getResources().getString(R.string.KEY_DISTANCEGOAL));
		if (str.equals("")) {
			return this.getResources().getString(R.string.INIT_UNIT);
		}
        Log.i("getDistance()", str);
        
        int pos = str.indexOf(" ");
        if (pos != -1) {
        	return str.substring(0, pos);
        }
        return str;
	}
	
	String calculateDistance() {
		double sum = new Double(curCen*10) + new Double(curDec) + curUnit;
	    return doubleStringFormation( String.format("%.2f", sum) );
		//return doubleStringFormation( String.format("%2f", sum) );
	}

	String doubleStringFormation(String target) {
		int pos = target.indexOf(".");
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
	
	int findIndex(String s) {
		for (int i = 0; i < dpoints.length; ++i) {
			if (dpoints[i].equals(s)) {
				return i;
			}
		}
		return -1;
	}

	void initWheelValueIndex() {
		Log.i("wheel-d", Distance);
		int pos = Distance.indexOf(".");
		int distance = 0;
		
		if (pos == -1) {
			curUnitItemIndex = 0;
			distance = Integer.parseInt(Distance);
		}else {
			curUnitItemIndex = findIndex(Distance.substring(pos));
			distance = Integer.parseInt(Distance.substring(0, pos));
		}
		
		curDecItemIndex = distance%10;
		curCenItemIndex = distance/10;
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
