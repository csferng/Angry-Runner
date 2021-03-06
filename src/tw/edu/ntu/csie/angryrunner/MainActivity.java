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
        
        addTab(R.string.STR_MAIN, WorkoutActivity.class, res.getDrawable(R.drawable.icon_tab_main));
        addTab(R.string.STR_PLAYLIST, MusicActivity.class, res.getDrawable(R.drawable.icon_tab_playlist));
        addTab(R.string.STR_HISTORY, HistoryActivity.class, res.getDrawable(R.drawable.icon_tab_history));
        addTab(R.string.STR_SETTING, SettingActivity.class, res.getDrawable(R.drawable.icon_tab_setting));
        
    }

	private void addTab(int tag, Class<? extends Activity> activity, Drawable icon) {
		Intent intent = new Intent().setClass(this, activity);
        TabHost.TabSpec spec = tabHost.newTabSpec(getString(tag))
        		.setIndicator(getString(tag), icon)
        		.setContent(intent);
        tabHost.addTab(spec);
	}
}