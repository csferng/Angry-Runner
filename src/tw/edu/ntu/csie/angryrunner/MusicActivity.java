package tw.edu.ntu.csie.angryrunner;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MusicActivity extends Activity {
	
	private int selectedPos = -1;
	
	private Button add_bt, unset_bt;
	private SimpleAdapter playListItemAdapter;
	private ArrayList<HashMap<String, String>> playListItem;
	private SharedPreferences settingPref;
	
    /** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music);
        
        settingPref = getSharedPreferences(
        		getString(R.string.NAME_SHAREDPREFERENCE), 
        		MODE_PRIVATE);
        selectedPos = Integer.parseInt(settingPref.getString(getString(R.string.KEY_PLAYLISTID), "-1"));
        
        setTitle("Playlists");
        
        add_bt = (Button)findViewById(R.id.addPlaylist);
        add_bt.setTextSize(18);
        add_bt.setOnClickListener(new Button.OnClickListener() {
        	@Override
        	public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MusicActivity.this, MusicPlaylistActivity.class);
                startActivity(intent);
        	}
        });

        unset_bt = (Button)findViewById(R.id.unsetPlaylist);
        unset_bt.setTextSize(18);
        unset_bt.setOnClickListener(new Button.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		if(0 <= selectedPos && selectedPos < playListItem.size()) {
        			String val = playListItem.get(selectedPos).get("playlistNameHL");
        			if(!val.equals("")) {
        				playListItem.get(selectedPos).put("playlistName", val);
        				playListItem.get(selectedPos).put("playlistNameHL", "");
        				playListItemAdapter.notifyDataSetChanged();
            		}
        		}
        		selectedPos = -1;
        		SharedPreferences.Editor settingPrefEdt = settingPref.edit();
        		settingPrefEdt.putString(getString(R.string.KEY_PLAYLISTID), "-1").commit();
        	}
        });
        
        ListView playlist_list = (ListView)findViewById(R.id.playlist_list);
        playlist_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
        		HashMap<String, String> clickMap = playListItem.get(position);
        		Intent intent = new Intent(MusicActivity.this, PlaylistActivity.class);
        		Bundle b = new Bundle();
        		b.putString("playlistName", clickMap.get("playlistName").toString());
        		b.putString("playlistId", clickMap.get("playlistId").toString());
        		b.putInt("pos", position);
        		intent.putExtras(b);
        		startActivityForResult(intent, 0);
        	}
		});
		playListItem = new ArrayList<HashMap<String, String>>();
		playListItemAdapter = new SimpleAdapter(MusicActivity.this, playListItem,
				R.layout.playlist_item, new String[] { "playlistName", "playlistNameHL" },
				new int[] { R.id.playlistName, R.id.playlistNameHL });
		playlist_list.setAdapter(playListItemAdapter);
    }
    
	@Override
	public void onResume() {
		super.onResume();
		getPlaylists();
	}
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 0 && resultCode == RESULT_OK){
			if (data.getExtras().getString("state").equals("true")) {
				selectedPos = data.getExtras().getInt("pos");
			}
    	}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
    @Override
    public void onBackPressed() {
    	((TabActivity) this.getParent()).getTabHost().setCurrentTab(0);
    }
   
    private void getPlaylists() {
    	
		String[] proj = { 
				MediaStore.Audio.Playlists._ID,
				MediaStore.Audio.Playlists.NAME };
    	playListItem.clear();
    	Cursor playlistCursor = null;
    	
    	try {
			// the uri of the table that we want to query
			Uri uri = android.provider.MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
			// we now launch the query (be sure not to do this in the UI thread
			// should it take a while)
			playlistCursor = getApplicationContext().getContentResolver().query(
					uri, proj, null, null, null);
			playlistCursor.getCount();
			
			if (playlistCursor != null) {
				
				playlistCursor.moveToFirst();
				while (!playlistCursor.isAfterLast()) {
					int playlist_column_index = playlistCursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.NAME);
					String kstr = playlistCursor.getString(playlist_column_index);
					if (kstr.replaceAll("\\s", "").equals("")){
						playlistCursor.moveToNext();
						continue;
					}
					// get the 1st col in our returned data set
					// (AlbumColumns.ALBUM)
					HashMap<String, String> playlist = new HashMap<String, String>();
					
					if(playListItem.size() != selectedPos) {
						playlist.put("playlistName", kstr);
						playlist.put("playlistNameHL", "");
					} else {
						playlist.put("playlistNameHL", kstr);
						playlist.put("playlistName", "");
					}
		    		
		    		playlist_column_index = playlistCursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists._ID);
					kstr = playlistCursor.getString(playlist_column_index);
		    		playlist.put("playlistId", kstr);
		    		
		    		playListItem.add(playlist);
					playlistCursor.moveToNext();
				}
	    		playListItemAdapter.notifyDataSetChanged();
			}

		} finally {
			if (playlistCursor != null) {
				playlistCursor.close();
			}
		}
    }
}
