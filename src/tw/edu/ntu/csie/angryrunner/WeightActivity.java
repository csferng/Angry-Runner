package tw.edu.ntu.csie.angryrunner;

//import kankan.wheel.R;
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

public class WeightActivity extends Activity {
	
	String Weight;
	SharedPreferences settingPref;
	SharedPreferences.Editor settingPrefEdt;

	TextView dec_tv;
	WheelView cen, dec, unit;
	Button confirm_bt, cancel_bt;
	
	int curCenItemIndex, curDecItemIndex, curUnitItemIndex;
	int curCen, curDec, curUnit;
	String [] digits;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weight);

        settingPref = getSharedPreferences("PREF_ANGRYRUNNER_SETTING", MODE_PRIVATE);
        settingPrefEdt = settingPref.edit();
        
        Weight = getWeight();
        setTitle("Weight");
        
        dec_tv = (TextView)findViewById(R.id.minuteText);
        dec_tv.setTypeface(Typeface.DEFAULT_BOLD);
        dec_tv.setTextColor(Color.YELLOW);
        dec_tv.setTextSize(24);
        
        
        confirm_bt = (Button)findViewById(R.id.confirmBT);
        //confirm_bt.setTypeface(Typeface.DEFAULT_BOLD);
        confirm_bt.setTextSize(16);
        confirm_bt.setOnClickListener(new Button.OnClickListener(){
        	@Override
        	public void onClick(View v) {
        		
        		//int sum = curCen*100 + curDec*10 + curUnit;
        		Weight = getWeight();
        		String target = Weight+" Kg";
        		
        		Intent it = new Intent();
				Bundle bun = new Bundle();
				bun.putString("value", Weight);
				bun.putString("display", target);
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
        
        cen = (WheelView)findViewById(R.id.centesimal);
        dec = (WheelView)findViewById(R.id.decimal);
        unit = (WheelView)findViewById(R.id.unit);
        
        initWheelValueIndex();
        
        
        OnWheelChangedListener cenListener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                //updateDays(year, month, day);
            	curCenItemIndex = cen.getCurrentItem();
                curCen = Integer.parseInt( digits[curCenItemIndex] );
                cen.setViewAdapter(new DateArrayAdapter(WeightActivity.this, digits, curCenItemIndex));
                cen.setCurrentItem(curCenItemIndex);
                Weight = calculateWeight();
            }
        };
        
        curCenItemIndex = 0;
        curCen = Integer.parseInt( digits[curCenItemIndex] );
        cen.setViewAdapter(new DateArrayAdapter(this, digits, curCenItemIndex));
        cen.setCurrentItem(curCenItemIndex);
        cen.addChangingListener(cenListener);

        
        OnWheelChangedListener decListener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                //updateDays(year, month, day);
            	curDecItemIndex = dec.getCurrentItem();
                curDec = Integer.parseInt( digits[curDecItemIndex] );
                dec.setViewAdapter(new DateArrayAdapter(WeightActivity.this, digits, curDecItemIndex));
                dec.setCurrentItem(curDecItemIndex);
            	Weight = calculateWeight();
            }
        };
        
        curDecItemIndex = 0;
        curDec = Integer.parseInt( digits[curDecItemIndex] );
        dec.setViewAdapter(new DateArrayAdapter(this, digits, curDecItemIndex));
        dec.setCurrentItem(curDecItemIndex);
        dec.addChangingListener(decListener);

        
        OnWheelChangedListener unitListener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                //updateDays(year, month, day);
            	curUnitItemIndex = unit.getCurrentItem();
                curUnit = Integer.parseInt( digits[curUnitItemIndex] );
                unit.setViewAdapter(new DateArrayAdapter(WeightActivity.this, digits, curUnitItemIndex));
                unit.setCurrentItem(curUnitItemIndex);
            	Weight = calculateWeight();
            }
        };
        
        curUnitItemIndex = 0;
        curUnit = Integer.parseInt( digits[curUnitItemIndex] );
        unit.setViewAdapter(new DateArrayAdapter(this, digits, curUnitItemIndex));
        unit.setCurrentItem(curUnitItemIndex);
        unit.addChangingListener(unitListener);
        
	}
	
	
	String [] initArray(int size, int start) {
		String [] as = new String[size];
		for (int i=0; i < size; ++i) {
			as[i] = new Integer(start+i).toString();
		}
		return as;
	}
	
	String getWeight() {
		String str = settingPref.getString("Weight", "0");
        if (str.equals("")) {
			return "0";
		}
        Log.i("getWeight()", str);
        return str;
	}
	
	String calculateWeight() {
		Integer sum = curCen*100 + curDec*10 + curUnit;
		return sum.toString();
	}
	
	void initWheelValueIndex() {
		int weight = Integer.parseInt(Weight);
		curUnitItemIndex = weight%10;
		curDecItemIndex = (weight/10)%10;
		curCenItemIndex = (weight/10)/10;
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
            setTextSize(28);
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