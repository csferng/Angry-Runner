package tw.edu.ntu.csie.angryrunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;


public class MusicActivity extends Activity {
    /** Called when the activity is first created. */
	
	private int selectedPos = -1;
	
	private Button add_bt, unset_bt;
	private ListView playlist_list;
	private MyAdapter playListItemAdapter;
	private ArrayList<HashMap<String, Object>> playListItem;
	
	private SharedPreferences settingPref;
	
	class MyAdapter extends SimpleAdapter {
		public MyAdapter(Activity activity, List<HashMap<String, Object>> items,
				int resource, String[] from, int[] to) {
			super(activity, items, resource, from, to);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			Log.i("position", new Integer(position).toString());
			if (position == selectedPos && selectedPos != -1) {
				Log.i("selectedPos", new Integer(selectedPos).toString());
				view = super.getView(position, convertView, parent);
				view.setBackgroundColor(Color.rgb(70, 50, 120));
			}else {
				view = super.getView(position, null, parent);
			}
			return view;
		}
	}
	
	
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
                //intent.setClass(MusicPlayerActivity.this, MusicListActivity.class);
                intent.setClass(MusicActivity.this, MusicPlaylistActivity.class);
        		//startActivityForResult(intent, 0);
                startActivity(intent);
        	}
        });

        unset_bt = (Button)findViewById(R.id.unsetPlaylist);
        unset_bt.setTextSize(18);
        unset_bt.setOnClickListener(new Button.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		selectedPos = -1;
        		SharedPreferences.Editor settingPrefEdt = settingPref.edit();
        		settingPrefEdt.putString(getString(R.string.KEY_PLAYLISTID), "-1").commit();
        	}
        });
        
        playlist_list = (ListView)findViewById(R.id.playlist_list);
        
        playlist_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	
        	@Override
        	public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
        		HashMap<String, Object> clickMap = playListItem.get(position);
        		Intent intent = new Intent(MusicActivity.this, PlaylistActivity.class);
        		Bundle b = new Bundle();
        		b.putString("playlistName", clickMap.get("playlistName").toString());
        		b.putString("playlistId", clickMap.get("playlistId").toString());
        		b.putInt("pos", position);
        		intent.putExtras(b);
        		startActivityForResult(intent, 0);
				Log.i("m-pos", new Integer(position).toString());
        		//setPos(position);
        	}
        	
		});
        
    }
    
	@Override
	public void onResume() {
		super.onResume();
		playListItem = new ArrayList<HashMap<String, Object>>();
		playListItemAdapter = new MyAdapter(MusicActivity.this, playListItem,
				R.layout.playlist_item, new String[] { "playlistName" },
				new int[] { R.id.playlistName });
		playlist_list.setAdapter(playListItemAdapter);
		getPlaylists();
	}
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
			Log.i("setPos-1", new Integer(selectedPos).toString());
			if (data.getExtras().getString("state").equals("true")) {
				selectedPos = data.getExtras().getInt("pos");
				Log.i("setPos-2", new Integer(selectedPos).toString());
			}
    	}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
    @Override
    public void onBackPressed() {
    	((TabActivity) this.getParent()).getTabHost().setCurrentTab(0);
    }
   
    //@Override
    //protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //	super.onActivityResult(requestCode, resultCode, data);
    //	if(requestCode != 0 && resultCode == RESULT_OK){
    		//HashMap<String, Object> playlist = new HashMap<String, Object>();
    		//playlist.put("playlistName", data.getExtras().getString("value"));
    		//playListItem.add(playlist);
    		//playListItemAdapter.notifyDataSetChanged();
    //	}
    //}
    
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
				
				String kstr = "";

				playlistCursor.moveToFirst();
				while (!playlistCursor.isAfterLast()) {
					// get the 1st col in our returned data set
					// (AlbumColumns.ALBUM)
					HashMap<String, Object> playlist = new HashMap<String, Object>();
					
					int playlist_column_index = playlistCursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.NAME);
					kstr = playlistCursor.getString(playlist_column_index);
		    		playlist.put("playlistName", kstr);
		    		
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
    
/*    public static void addToPlaylist(ContentResolver resolver, int audioId, int playlistId) {

        String[] cols = new String[] {"count(*)"};
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
        Cursor cur = resolver.query(uri, cols, null, null, null);
        cur.moveToFirst();
        final int base = cur.getInt(0);
        cur.close();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Integer.valueOf(base + audioId));
        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);
        resolver.insert(uri, values);
    }

   public static void removeFromPlaylist(ContentResolver resolver, int audioId, int playlistId) {
       Log.v("made it to add",""+audioId);
        String[] cols = new String[] {
                "count(*)"
        };
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
        Cursor cur = resolver.query(uri, cols, null, null, null);
        cur.moveToFirst();
        cur.close();
        resolver.delete(uri, MediaStore.Audio.Playlists.Members.AUDIO_ID +" = "+audioId, null);
    }*/
    
}