package tw.edu.ntu.csie.angryrunner;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;


public class MusicActivity extends Activity {
    /** Called when the activity is first created. */
	
	Button add_bt;
	ListView playlist_list;
	SimpleAdapter playListItemAdapter;
	ArrayList<HashMap<String, Object>> playListItem;
	
	int count, playlist_column_index;
	Cursor playlistCursor;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music);
        
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
        
        playlist_list = (ListView)findViewById(R.id.playlist_list);
        playListItem = new ArrayList<HashMap<String, Object>>();
        playListItemAdapter = new SimpleAdapter(MusicActivity.this, playListItem, 
        										R.layout.playlist_item, 
        										new String[]{"playlistName"}, 
        										new int[]{R.id.playlistName});
        playlist_list.setAdapter(playListItemAdapter);
        
        getPlaylists();
        
        playlist_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
        		HashMap<String, Object> clickMap = (HashMap<String, Object>)playlist_list.getItemAtPosition(position);
        		Intent intent = new Intent(MusicActivity.this, PlaylistActivity.class);
        		Bundle b = new Bundle();
        		b.putString("playlistName", clickMap.get("playlistName").toString());
        		b.putString("playlistId", clickMap.get("playlistId").toString());
        		intent.putExtras(b);
        		startActivity(intent);
        	}
		});
        
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	playListItem = new ArrayList<HashMap<String, Object>>();
        playListItemAdapter = new SimpleAdapter(MusicActivity.this, playListItem, 
        										R.layout.playlist_item, 
        										new String[]{"playlistName"}, 
        										new int[]{R.id.playlistName});
        playlist_list.setAdapter(playListItemAdapter);
        getPlaylists();
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
    	
    	try {
			// the uri of the table that we want to query
			Uri uri = android.provider.MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
			// we now launch the query (be sure not to do this in the UI thread
			// should it take a while)
			playlistCursor = getApplicationContext().getContentResolver().query(
					uri, proj, null, null, null);
			count = playlistCursor.getCount();
			
			if (playlistCursor != null) {
				
				int i = 0;
				String kstr = "";

				playlistCursor.moveToFirst();
				while (!playlistCursor.isAfterLast()) {
					// get the 1st col in our returned data set
					// (AlbumColumns.ALBUM)
					HashMap<String, Object> playlist = new HashMap<String, Object>();
					
					playlist_column_index = playlistCursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.NAME);
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
    
    public static void addToPlaylist(ContentResolver resolver, int audioId, int playlistId) {

        String[] cols = new String[] {
                "count(*)"
        };
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
        final int base = cur.getInt(0);
        cur.close();
        ContentValues values = new ContentValues();

        resolver.delete(uri, MediaStore.Audio.Playlists.Members.AUDIO_ID +" = "+audioId, null);
    }
    
}