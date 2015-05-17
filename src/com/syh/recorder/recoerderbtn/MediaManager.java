package com.syh.recorder.recoerderbtn;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;

public class MediaManager {

	private static MediaPlayer mPlayer;
	private static boolean isPause;

	public static void playSound(String filePath,
			OnCompletionListener onCompletionListener) {
		// TODO Auto-generated method stub
		if (mPlayer == null) {
			mPlayer = new MediaPlayer();
			mPlayer.setOnErrorListener(new OnErrorListener() {

				public boolean onError(MediaPlayer mp, int what, int extra) {
					mPlayer.reset();
					return false;
				}
			});
		} else {
			mPlayer.reset();
		}

		try {
			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mPlayer.setOnCompletionListener(onCompletionListener);
			mPlayer.setDataSource(filePath);
			mPlayer.prepare();
			mPlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void pause() {
		if (mPlayer != null && mPlayer.isPlaying()) {
			mPlayer.stop();
			isPause = true;
		}
	}

	public static void resume() {
		if (mPlayer != null && isPause) {
			mPlayer.start();
			isPause = false;
		}
	}

	public static void release() {
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}
}
