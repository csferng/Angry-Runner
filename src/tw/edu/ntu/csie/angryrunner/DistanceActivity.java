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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class DistanceActivity extends Activity {

	private TextView dec_tv;
	private WheelView cen, dec, unit;
	private Button confirm_bt, cancel_bt;
	
	private int curCenItemIndex, curDecItemIndex, curUnitItemIndex;
	private int curCen, curDec;
	private double curUnit;
	private String [] digits, dpoints;

	private double distance;
	private SharedPreferences settingPref;
	private UnitHandler unitHandler;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.distance);
        setTitle(getString(R.string.KEY_DISTANCE) + " " + getString(R.string.TITLE));

        settingPref = getSharedPreferences(
        		getString(R.string.NAME_SHAREDPREFERENCE), 
        		MODE_PRIVATE);
        unitHandler = new UnitHandler(this, settingPref);
        
        distance = getDistance(this.getIntent().getExtras());
        Log.i("Distance", Double.toString(distance));        
        
        dec_tv = (TextView)findViewById(R.id.minuteText);
        dec_tv.setTypeface(Typeface.DEFAULT_BOLD);
        dec_tv.setTextColor(Color.YELLOW);
        dec_tv.setTextSize(24);
        dec_tv.setText(unitHandler.getDisplayUnit());
        
        confirm_bt = (Button)findViewById(R.id.confirmBT);
        confirm_bt.setTextSize(16);
        confirm_bt.setOnClickListener(new Button.OnClickListener(){
        	@Override
        	public void onClick(View v) {
        		
        		distance = unitHandler.distanceFromUnit(calculateDistance());
        		Log.i("Distance", Double.toString(distance));
        		
        		Intent it = new Intent();
				Bundle bun = new Bundle();
				Log.i("d-value", Double.toString(distance));
				bun.putString(
						getString(R.string.KEY_DISTANCEGOAL),
						Double.toString(distance));
				String display = unitHandler.presentDistanceWithUnit(distance); 
				Log.i("d-display", display);
				bun.putString("display", display);
				it.putExtras(bun);
				
				setResult(RESULT_OK, it);
				finish();
        	}
        });
        
        cancel_bt = (Button)findViewById(R.id.cancelBT);
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
            	curCenItemIndex = cen.getCurrentItem();
            	curCen = Integer.parseInt( digits[curCenItemIndex] );
            	cen.setViewAdapter(new DateArrayAdapter(DistanceActivity.this, digits, curCenItemIndex));
            	cen.setCurrentItem( curCenItemIndex );
            	distance = calculateDistance();
            }
        };
        
    	//curCenItemIndex = 0;
    	curCen = Integer.parseInt( digits[curCenItemIndex] );
    	cen.setViewAdapter(new DateArrayAdapter(DistanceActivity.this, digits, curCenItemIndex));
        cen.setCurrentItem( curCenItemIndex );
        cen.addChangingListener(cenListener);
        
        OnWheelChangedListener decListener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
            	curDecItemIndex = dec.getCurrentItem();
            	curDec = Integer.parseInt( digits[curDecItemIndex] );
            	dec.setViewAdapter(new DateArrayAdapter(DistanceActivity.this, digits, curDecItemIndex));
            	dec.setCurrentItem( curDecItemIndex );
            	distance = calculateDistance();
            }
        };
        
        //curDecItemIndex = 0;
    	curDec = Integer.parseInt( digits[curDecItemIndex] );
    	dec.setViewAdapter(new DateArrayAdapter(DistanceActivity.this, digits, curDecItemIndex));
    	dec.setCurrentItem( curDecItemIndex );
        dec.addChangingListener(decListener);
        
        OnWheelChangedListener unitListener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
            	curUnitItemIndex = unit.getCurrentItem();
            	curUnit = Double.parseDouble( dpoints[curUnitItemIndex] );
            	unit.setViewAdapter(new DateArrayAdapter(DistanceActivity.this, dpoints, curUnitItemIndex));
            	unit.setCurrentItem( curUnitItemIndex );
            	distance = calculateDistance();
            	Log.i("on change", String.format("idx %d val %.2f str %s", curUnitItemIndex, curUnit, distance));
            }
        };
        
        //curUnitItemIndex = 0;
    	curUnit = Double.parseDouble( dpoints[curUnitItemIndex] );
    	unit.setViewAdapter(new DateArrayAdapter(DistanceActivity.this, dpoints, curUnitItemIndex));
    	unit.setCurrentItem( curUnitItemIndex );
        unit.addChangingListener(unitListener);
        distance = calculateDistance();
	}	

	
	private String [] initArray(int size, int start) {
		String [] as = new String[size];
		for (int i=0; i < size; ++i) {
			as[i] = new Integer(start+i).toString();
		}
		return as;
	}

	private String [] initArray(int size, double start, double gap) {
		String [] as = new String[size];
		String s = "";
		int pos = -1;
		for (int i=0; i < size; ++i) {
			s = String.format("%.2f", start);
			pos = s.indexOf(".");
			as[i] = s.substring(pos);
			start += gap;
		}
		return as;
	}
	
	private double getDistance(Bundle bun) {
		String str = bun.getString(getString(R.string.KEY_DISTANCEGOAL));
		if (str == null || str.equals("")) {
			str = getString(R.string.INIT_GOALVALUES);
		}
		return unitHandler.distanceToUnit(Double.parseDouble(str));
	}
	
	private double calculateDistance() {
		return curCen*10 + curDec + curUnit;
	}
	
	private void initWheelValueIndex() {
		Log.i("wheel-d", Double.toString(distance));
		Double value = distance;
		//Double value = Double.parseDouble(distance);
		int distance = value.intValue();
		double dec = value.doubleValue() - distance;
		curUnitItemIndex = (int)(dec*20+0.5);
		curDecItemIndex = distance%10;
		curCenItemIndex = distance/10;
		if(curCenItemIndex > digits.length) {
			curCenItemIndex = digits.length - 1;
			curDecItemIndex = digits.length - 1;
			curUnitItemIndex = dpoints.length - 1;
		}
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
