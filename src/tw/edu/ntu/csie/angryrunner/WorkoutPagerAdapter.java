package tw.edu.ntu.csie.angryrunner;

import java.util.ArrayList;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class WorkoutPagerAdapter extends PagerAdapter {
	ArrayList<View> myViews;

	public WorkoutPagerAdapter(ArrayList<View> views) {
		myViews = views;
	}
	
	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView(myViews.get(arg1));
	}

	@Override
	public int getCount() {
		return myViews.size();
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		((ViewPager) arg0).addView(myViews.get(arg1));
		return myViews.get(arg1);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0 == arg1);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {}

	@Override
	public void finishUpdate(View arg0) {}

}
