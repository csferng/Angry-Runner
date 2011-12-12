package tw.edu.ntu.csie.angryrunner;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TabHost;

public class MainActivity extends TabActivity {
    private TabHost tabHost;
	private Resources res;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        res = getResources();
        tabHost = getTabHost();
        
        addTab("Main", WorkoutActivity.class, res.getDrawable(R.drawable.icon_tab_test));
        addTab("Playlist", MusicActivity.class, res.getDrawable(R.drawable.icon_tab_test));
        addTab("History", HistoryActivity.class, res.getDrawable(R.drawable.icon_tab_test));
        addTab("Setting", SettingActivity.class, res.getDrawable(R.drawable.icon_tab_test));
        
    }

	private void addTab(String tag, Class<? extends Activity> activity, Drawable icon) {
		Intent intent = new Intent().setClass(this, activity);
        TabHost.TabSpec spec = tabHost.newTabSpec(tag)
        		.setIndicator(tag, icon)
        		.setContent(intent);
        tabHost.addTab(spec);
	}

	private void addTab(String tag, Class<? extends Activity> activity, Intent intent, Drawable icon) {
		intent.setClass(this, activity);
        TabHost.TabSpec spec = tabHost.newTabSpec(tag)
        		.setIndicator(tag, icon)
        		.setContent(intent);
        tabHost.addTab(spec);
	}
}