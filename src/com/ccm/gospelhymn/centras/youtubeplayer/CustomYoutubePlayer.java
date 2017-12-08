package com.ccm.gospelhymn.centras.youtubeplayer;


import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.admixer.AdAdapter;
import com.admixer.AdInfo;
import com.admixer.AdMixerManager;
import com.admixer.AdView;
import com.admixer.AdViewListener;
import com.admixer.InterstitialAd;
import com.admixer.InterstitialAdListener;
import com.ccm.gospelhymn.centras.R;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.OnFullscreenListener;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStyle;
import com.google.android.youtube.player.YouTubePlayer.PlaylistEventListener;
import com.google.android.youtube.player.YouTubePlayerView;


public class CustomYoutubePlayer extends YouTubeFailureRecoveryActivity implements OnFullscreenListener, InterstitialAdListener, AdViewListener {  
	ArrayList<String> array_url;
	ArrayList<String> plus_array_url;
	public MyPlaylistEventListener playlistEventListener;
	public MyPlayerStateChangeListener playerStateChangeListener;
	public MyPlaybackEventListener playbackEventListener;
	public static YouTubePlayer player;
	public YouTubePlayerView youtube_view;
	public static InterstitialAd interstialAd;
	public Context context;
	public Handler handler = new Handler();
	public static RelativeLayout ad_layout;
	@Override    
	public void onCreate(Bundle savedInstanceState){ 
		super.onCreate(savedInstanceState);   
		setContentView(R.layout.custom_youtubeplayer);
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_TAD, "AX0004B36");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_TAD_FULL, "AX0004B37");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_SHALLWE, "4wSC9xA6l_fwOJDgXNn_JA");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMIXER, "zkl206g4");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMOB, "ca-app-pub-4637651494513698/5794143361");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_CAULY, "4qW0j8xS");
		context = this;
		addBannerView();
		youtube_view = (YouTubePlayerView) findViewById(R.id.youtube_view); 
    	array_url = getIntent().getStringArrayListExtra("array_videoid");
    	for(int i=0; i < array_url.size(); i++){
    		
    	}
		youtube_view.initialize(DeveloperKey.DEVELOPER_KEY, this);
		playlistEventListener = new MyPlaylistEventListener();
	    playerStateChangeListener = new MyPlayerStateChangeListener();
	    playbackEventListener = new MyPlaybackEventListener();
	}
	
	public void addBannerView() {
    	AdInfo adInfo = new AdInfo("zkl206g4");
    	adInfo.setTestMode(false);
        AdView adView = new AdView(this);
        adView.setAdInfo(adInfo, this);
        adView.setAdViewListener(this);
        ad_layout = (RelativeLayout)findViewById(R.id.ad_layout);
        if(ad_layout != null){
        	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            ad_layout.addView(adView, params);	
        }
    }
	
	public void addInterstitialView() {
    	if(interstialAd == null) {
        	AdInfo adInfo = new AdInfo("zkl206g4");
//        	adInfo.setTestMode(false);
        	interstialAd = new InterstitialAd(this);
        	interstialAd.setAdInfo(adInfo, this);
        	interstialAd.setInterstitialAdListener(this);
        	interstialAd.startInterstitial();
    	}
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();
	}
	@Override
	  protected YouTubePlayer.Provider getYouTubePlayerProvider() {
		return youtube_view;
	}
	
	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
		CustomYoutubePlayer.player = player;
	    player.setPlaylistEventListener(playlistEventListener);
	    player.setPlayerStateChangeListener(playerStateChangeListener);
	    player.setPlaybackEventListener(playbackEventListener);
	    if (!wasRestored) {
	      playVideoAtSelection();
	    }
	  }

	  public void playVideoAtSelection() {
		  player.cueVideos(array_url);
		  player.setPlayerStyle(PlayerStyle.DEFAULT);
		  player.addFullscreenControlFlag(8);
		  player.setOnFullscreenListener(this);
		  player.setFullscreen(true);
	  }
	
	
	public class MyPlaylistEventListener implements PlaylistEventListener {
		@Override
	    public void onNext() {
	    }

	    @Override
	    public void onPrevious() {
	    }

	    @Override
	    public void onPlaylistEnded() {
	      Log.i("dsu", "PLAYLIST ENDED");
	    }
	  }
	
	private final class MyPlayerStateChangeListener implements PlayerStateChangeListener {
	    String playerState = "UNINITIALIZED";

	    @Override
	    public void onLoading() {
	      playerState = "LOADING";
	      Log.i("dsu", "onLoading : " + playerState);
	    }

	    @Override
	    public void onLoaded(String videoId) {
	      playerState = String.format("LOADED %s", videoId);
	      Log.i("dsu", "onLoaded : " + videoId);
	      try{
			  player.play();
			 }catch (IllegalStateException e) {
				 e.printStackTrace();
			 }
			 Log.i("dsu", "비디오시작");
	    }
	    
	    @Override
	    public void onAdStarted() {
	      playerState = "AD_STARTED";
	      Log.i("dsu", "onAdStarted : " + playerState);
	    }

	    @Override
	    public void onVideoStarted() {
	      playerState = "VIDEO_STARTED";
	      Log.i("dsu", "onVideoStarted : " + playerState);
	    }

	    @Override
	    public void onVideoEnded() {
	      playerState = "VIDEO_ENDED";
	      if(player.hasNext()){
	    	  player.next();  
	      }else{
	    	  playVideoAtSelection();
	    	  return;
	      }
	    }

	    @Override
	    public void onError(ErrorReason reason) {
	      playerState = "ERROR (" + reason + ")";
	      Log.i("dsu", "onError : " + reason);
	      if (reason == ErrorReason.UNEXPECTED_SERVICE_DISCONNECTION) {
	        // When this error occurs the player is released and can no longer be used.
	        player = null;
	      }
	    }
	  }
	
	public class MyPlaybackEventListener implements PlaybackEventListener {
	    String playbackState = "NOT_PLAYING";
	    @Override
	    public void onPlaying() {
	      playbackState = "PLAYING";
	      Log.i("dsu", "onPlaying" + playbackState);
	    }

	    @Override
	    public void onBuffering(boolean isBuffering) {
	      Log.i("dsu", "isBuffering" + isBuffering);
	    }

	    @Override
	    public void onStopped() {
	      playbackState = "STOPPED";
	      Log.i("dsu", "onStopped" + playbackState);
	    }

	    @Override
	    public void onPaused() {
	      playbackState = "PAUSED";
	      Log.i("dsu", "onPaused" + playbackState);
	    }

	    @Override
	    public void onSeekTo(int endPositionMillis) {
//	      Log.i("dsu", (String.format("\tSEEKTO: (%s/%s)",
	    }
	}
	
		//** BannerAd 이벤트들 *************
		@Override
		public void onClickedAd(String arg0, AdView arg1) {
		}

		@Override
		public void onFailedToReceiveAd(int arg0, String arg1, AdView arg2) {
			
		}

		@Override
		public void onReceivedAd(String arg0, AdView arg1) {
		}
		
		//** InterstitialAd 이벤트들 *************
		@Override
		public void onInterstitialAdClosed(InterstitialAd arg0) {
			interstialAd = null;
			 onDestroy();
		}

		@Override
		public void onInterstitialAdFailedToReceive(int arg0, String arg1,
				InterstitialAd arg2) {
			interstialAd = null;
		}

		@Override
		public void onInterstitialAdReceived(String arg0, InterstitialAd arg1) {
			interstialAd = null;
		}
		
		@Override
		public void onInterstitialAdShown(String arg0, InterstitialAd arg1) {
			
		}
	

		@Override
		public void onFullscreen(boolean arg0) {
		}
		
		@Override
		public void onLeftClicked(String arg0, InterstitialAd arg1) {
		}

		@Override
		public void onRightClicked(String arg0, InterstitialAd arg1) {
		}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
			Toast.makeText(context, context.getString(R.string.txt_custom_videoplayer3), Toast.LENGTH_SHORT).show();
//			addInterstitialView();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					onDestroy();
				 }
			 },3000);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
