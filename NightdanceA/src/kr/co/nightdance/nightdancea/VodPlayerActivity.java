package kr.co.nightdance.nightdancea;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.ProgressBar;
import android.widget.VideoView;

public class VodPlayerActivity extends Activity implements MediaPlayerControl {
	public static final String TAG = "VodPlayerActivity";
    MediaPlayer mPlayer;
    Button mPlayBtn;
    private Uri mClipUrl;
    ProgressBar progressBar	= null;
    VideoView mVideoView = null;
    private MediaPlayer mMediaPlayer;
    
    @Override
	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.vod_player_activity);
        
        mVideoView = (VideoView) findViewById(R.id.videoview);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        
        progressBar.setVisibility(View.VISIBLE);
        
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(mVideoView);
        mVideoView.setMediaController(mediaController);
        
        mClipUrl	= Uri.parse(getIntent().getStringExtra("clip_url"));
        mVideoView.setVideoURI(mClipUrl);
        mVideoView.start();
        
        mVideoView.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.start();
                mp.setOnVideoSizeChangedListener(new OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int arg1,
                            int arg2) {
                        // TODO Auto-generated method stub
                        progressBar.setVisibility(View.GONE);
                        mp.start();
                    }
                });
 
            }
        });
    }
    
    Button.OnClickListener mClickPlay = new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           // TODO Auto-generated method stub
           if(mPlayer.isPlaying()==false){
               mPlayer.start();
               mPlayBtn.setText("Pause");
           }else{
               mPlayer.pause();
               mPlayBtn.setText("Play");
           }
       }
   };
   
   Button.OnClickListener mClickStop = new View.OnClickListener() {
       
       @Override
       public void onClick(View v) {
           // TODO Auto-generated method stub
           mPlayer.stop();
           try{
               mPlayer.prepare();
           }catch(Exception e){;}
       }
   };
   
   MediaPlayer.OnCompletionListener mComplete = new MediaPlayer.OnCompletionListener() {
       
       @Override
       public void onCompletion(MediaPlayer mp) {
           // TODO Auto-generated method stub
//           mPlayBtn.setText("Play");
           finish();
       }
   };
   
   MediaPlayer.OnVideoSizeChangedListener mSizeChange = new MediaPlayer.OnVideoSizeChangedListener() {
       
       @Override
       public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
           // TODO Auto-generated method stub
           
       }
   };
   
   protected void onDestroy(){
       super.onDestroy();
       if(mPlayer!=null){
           mPlayer.release();
       }
   }

   @Override
   public boolean canPause() {
	   return false;
   }

   @Override
   public boolean canSeekBackward() {
	   return true;
   }

   @Override
   public boolean canSeekForward() {
	   return true;
   }

   public int getAudioSessionId() {
	   return 0;
   }

	@Override
	public int getBufferPercentage() {
		return 0;
	}

	@Override
	public int getCurrentPosition() {
		return mMediaPlayer.getCurrentPosition();
	}

	@Override
	public int getDuration() {
		return mMediaPlayer.getDuration();
	}

	@Override
	public boolean isPlaying() {
		return mMediaPlayer.isPlaying(); 
	}

	@Override
	public void pause() {
		mMediaPlayer.pause();
	}

	@Override
	public void seekTo(int i) {
		mMediaPlayer.seekTo(i);
	}

	@Override
	public void start() {
		mMediaPlayer.start();
	}
}
