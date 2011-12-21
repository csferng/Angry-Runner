package tw.edu.ntu.csie.angryrunner;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.provider.MediaStore;

public class Mplayer implements OnCompletionListener, OnBufferingUpdateListener {
	/*/ Usage: [new Mplayer(Activity)][init()][playFromTheFirstSong()] /*/
	
	private WorkoutActivity fromActivity;
	private MediaPlayer mplayer;
	private static MediaPlayer _mplayer;
	
	private int playlistSize = 0;
	private int songPosToPlay;
	private int chosenPlaylistId;
	private String [] musicDataStream;
	
	public Mplayer(final WorkoutActivity activity) {
		this.mplayer = Mplayer.getPlayer();
		this.mplayer.setOnCompletionListener(this);
		this.mplayer.setOnBufferingUpdateListener(this);
		this.fromActivity = activity;
	}
	
	private static MediaPlayer getPlayer() {
		if(_mplayer == null) _mplayer = new MediaPlayer();
		return _mplayer;
	}
	
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		mp.reset();
		if(this.playlistSize > 0) {
			this.songPosToPlay = (this.songPosToPlay+1 < this.playlistSize) ? this.songPosToPlay+1 : 0;
			PlayMusic( this.musicDataStream[this.songPosToPlay] );
		}
	}
	
	public int getPlaylistId() {
		return this.chosenPlaylistId;
	}
	
	private void initPlaylistAndSongs() {
		String[] proj = { 
				MediaStore.Audio.Playlists._ID, 
				MediaStore.Audio.Playlists.NAME };
		
		Cursor cursor = this.fromActivity.managedQuery(
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, proj,
				MediaStore.Audio.Playlists._ID + " = " + this.chosenPlaylistId + "", null,
				null);
		
		this.fromActivity.startManagingCursor(cursor);
		cursor.moveToFirst();

		if (cursor.isAfterLast()) {
			this.playlistSize = 0;
			this.musicDataStream = new String[0];
		} else {
			long playlist_id_long = cursor.getLong(0);
			cursor.close();
			
			String[] projection = {
					MediaStore.Audio.Playlists.Members.PLAYLIST_ID, 	//0
					MediaStore.Audio.Playlists.Members.AUDIO_ID, 		//1
					MediaStore.Audio.Playlists.Members._ID, 			//2
					MediaStore.Audio.Playlists.Members.TITLE, 			//3
					MediaStore.Audio.Playlists.Members.DISPLAY_NAME, 	//4
					MediaStore.Audio.Playlists.Members.SIZE, 			//5
					MediaStore.Audio.Playlists.Members.DATA, 			//6
					MediaStore.Audio.Playlists.Members.PLAY_ORDER,		//7
					MediaStore.MediaColumns.DATA						//8
			};

			cursor = this.fromActivity.managedQuery(MediaStore.Audio.Playlists.Members
					.getContentUri("external", playlist_id_long), projection,
					MediaStore.Audio.Media.IS_MUSIC + " != 0 ", null, 
					MediaStore.Audio.Playlists.Members.PLAY_ORDER);

			this.playlistSize = cursor.getCount();
			this.musicDataStream = new String[this.playlistSize];
			cursor.moveToFirst();
			for(int i = 0; i < this.playlistSize; ++i, cursor.moveToNext()) {
				this.musicDataStream[i] = cursor.getString(6);
			}
			cursor.close();
		}
	}
	
	private int getPlaylistIdFromPreference(SharedPreferences sp) {
		int id;
		try {
			id = Integer.parseInt(sp.getString(this.fromActivity.getString(R.string.KEY_PLAYLISTID), "-1"));
		} catch(NumberFormatException e) {
			id = -1;
		}
		return id;
	}
	
	private void setPlaylistId(int id) {
		this.chosenPlaylistId = id;
	}
	
	private void PlayMusic(String DataStream) {
		try {
			this.mplayer.setDataSource(DataStream);
			this.mplayer.prepare();
			this.mplayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void init(SharedPreferences sp) {
		init(getPlaylistIdFromPreference(sp));
	}
	
	public void init(int id) {
		this.songPosToPlay = 0;
		setPlaylistId(id);
		initPlaylistAndSongs();
	}
	
	public void playFromTheFirstSong() {
		this.songPosToPlay = 0;
		if(this.musicDataStream.length > 0) {
			PlayMusic( this.musicDataStream[this.songPosToPlay] );
		}
	}
	
	public void start() {
		this.mplayer.start();
	}
	
	public void pause() {
		if(this.mplayer.isPlaying())
			this.mplayer.pause();
	}
	
	public void resume() {
		this.mplayer.start();
	}
	
	public void stop() {
		this.songPosToPlay = 0;
		if(this.mplayer.isPlaying())
			this.mplayer.stop();
	}
	
	public void reset() {
		this.mplayer.reset();
	}

	public void cleanUp() {
		reset();
		this.mplayer.release();
		this.mplayer = null;
		Mplayer._mplayer = null;
	}
}
