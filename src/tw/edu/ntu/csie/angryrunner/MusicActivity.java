package tw.edu.ntu.csie.angryrunner;

import java.util.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MusicActivity extends Activity {
	
	Button add_bt;
	ListView playlist_list;
	SimpleAdapter playListItemAdapter;
	ArrayList<HashMap<String, Object>> playListItem;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        setTitle("Playlists");
        
        add_bt = (Button)findViewById(R.id.addPlaylist);
        add_bt.setTextSize(18);
        add_bt.setOnClickListener(new Button.OnClickListener() {
        	@Override
        	public void onClick(View v) {
                Intent intent = new Intent();
                //intent.setClass(MusicPlayerActivity.this, MusicListActivity.class);
                intent.setClass(MusicActivity.this, MusicPlaylistActivity.class);
        		startActivity(intent);
        	}
        });
        
        playlist_list = (ListView)findViewById(R.id.playlist_list);
        playListItem = new ArrayList<HashMap<String, Object>>();
        playListItemAdapter = new SimpleAdapter(MusicActivity.this, playListItem, 
        										R.layout.playlist_item, 
        										new String[]{"playlistName"}, 
        										new int[]{R.id.playlistName});
        playlist_list.setAdapter(playListItemAdapter);
        
    }
	
}
