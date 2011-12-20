package tw.edu.ntu.csie.angryrunner;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.provider.MediaStore;

public class Mplayer implements OnCompletionListener, OnBufferingUpdateListener {
	/*/ Usage: [new Mplayer(Activity)][init()][playFromTheFirstSong()] /*/
	
	private WorkoutActivity fromActivity;
	private SharedPreferences settingPref = null;
	//private SharedPreferences.Editor settingPrefEdt;
	
	static MediaPlayer mplayer;
	
	int playlistSize;
	int counterIndicater;
	int songPosToPlay;
	int chosenPlaylistId;
	String [] musicDataStream;
	
	//Playlist playlist;
	//Song [] songs;

	
	@SuppressWarnings("static-access")
	public Mplayer(final WorkoutActivity activity) {
		this.mplayer = new MediaPlayer();
		this.mplayer.setOnCompletionListener(this);
		this.mplayer.setOnBufferingUpdateListener(this);
		this.fromActivity = activity;
	}
	
	
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		mp.reset();
		this.songPosToPlay = (++this.counterIndicater) % this.playlistSize;
		PlayMusic( this.musicDataStream[this.songPosToPlay] );
	}
	
	
	public void getSongs() {
		this.musicDataStream = new String [this.playlistSize];
		for (int i = 0; i < this.playlistSize; ++i) {
			this.musicDataStream[i] = getSongDataStream(this.chosenPlaylistId, i);
		}
	}
	
	public int getPlaylistId() {
		return this.chosenPlaylistId;
	}
	
	public void getPlaylist() {
		
		Cursor cursor = null;

		String[] proj = { 
				MediaStore.Audio.Playlists._ID, 
				MediaStore.Audio.Playlists.NAME };

		
		cursor = this.fromActivity.managedQuery(
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, proj,
				MediaStore.Audio.Playlists._ID + " = " + this.chosenPlaylistId + "", null,
				null);
		
		this.fromActivity.startManagingCursor(cursor);
		cursor.moveToFirst();

		//String playlist_id_str = cursor.getString(0);
		long playlist_id_long = cursor.getLong(0);
		
		if (playlist_id_long > 0) {
			
			String[] projection = {
					//MediaStore.Audio.Playlists.Members.AUDIO_ID,
					//MediaStore.Audio.Playlists.Members.ARTIST,
					//MediaStore.Audio.Playlists.Members.TITLE,
					//MediaStore.Audio.Playlists.Members._ID
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
			
			cursor = null;
			cursor = this.fromActivity.managedQuery(MediaStore.Audio.Playlists.Members
					.getContentUri("external", playlist_id_long), projection,
					MediaStore.Audio.Media.IS_MUSIC + " != 0 ", null, null);

			setPlaylistSize(cursor);
		}
		//return cursor;
	}

	public String getSongDataStream(int playlistId, int position) {

		String[] ARG_STRING = { MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.DISPLAY_NAME,
				MediaStore.Video.Media.SIZE,
				android.provider.MediaStore.MediaColumns.DATA };

		Uri membersUri = MediaStore.Audio.Playlists.Members.getContentUri(
				"external", playlistId);
		
		Cursor songsWithingAPlayList = this.fromActivity.managedQuery(membersUri,
				ARG_STRING, null, null,
				MediaStore.Audio.Playlists.Members.PLAY_ORDER);

		if (songsWithingAPlayList != null) {
			int theSongIDIwantToPlay = position;
			songsWithingAPlayList.moveToPosition(theSongIDIwantToPlay);
			return songsWithingAPlayList.getString(4);
		}

		return "";
	}
	
	
	public void setPlaylistId(SharedPreferences sp) {
		this.chosenPlaylistId = Integer.parseInt( 
				this.settingPref.getString(this.fromActivity.getString(R.string.KEY_PLAYLISTID), "NULL") );
	}

	public void setPlaylistId(int id) {
		this.chosenPlaylistId = id;
	}
	
	public void setPlaylistSize(Cursor c) {
		this.playlistSize = c.getCount();
	}
	
	
	public void PlayTheSongFromAPlaylist(int playlistId, int position) {
		String DataStream = getSongDataStream(playlistId, position);
		if (!DataStream.equals("")) {
			PlayMusic(DataStream);
		}
	}
	
	@SuppressWarnings("static-access")
	public void PlayMusic(String DataStream) {
		try {
			this.mplayer.setDataSource(DataStream);
			//mpObject.setLooping(true);
			this.mplayer.prepare();
			this.mplayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public void init(SharedPreferences sp) {
		this.counterIndicater = 0;
		this.songPosToPlay = 0;
		this.settingPref = sp;
		setPlaylistId(sp);
		getPlaylist();
		getSongs();
	}
	
	public void init(int id) {
		this.counterIndicater = 0;
		this.songPosToPlay = 0;
		setPlaylistId(id);
		getPlaylist();
		getSongs();
	}
	
	public void playFromTheFirstSong() {
		this.counterIndicater = 0;
		this.songPosToPlay = 0;
		PlayMusic( this.musicDataStream[this.songPosToPlay] );
	}
	
	@SuppressWarnings("static-access")
	public void start() {
		this.mplayer.start();
	}
	
	@SuppressWarnings("static-access")
	public void pause() {
		this.mplayer.pause();
	}
	
	@SuppressWarnings("static-access")
	public void resume() {
		this.mplayer.start();
	}
	
	@SuppressWarnings("static-access")
	public void stop() {
		this.counterIndicater = 0;
		this.songPosToPlay = 0;
		this.mplayer.stop();
	}
	
	@SuppressWarnings("static-access")
	public void reset() {
		this.mplayer.reset();
	}

}
