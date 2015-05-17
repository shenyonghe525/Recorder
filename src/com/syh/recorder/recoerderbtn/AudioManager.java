package com.syh.recorder.recoerderbtn;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import android.media.MediaRecorder;

public class AudioManager {

	private MediaRecorder mMediaRecorder;
	private String mDir;
	private String mCurFilePath;
	private boolean isPrepared;

	private static AudioManager mInstance;

	private AudioManager(String dir) {
		mDir = dir;
	};

	/**
	 * 回调准备完毕
	 * 
	 * @author shenyonghe
	 */
	public interface AudioStateListener {
		void wellPrepared();
	}

	public AudioStateListener mListener;

	public void setAudioStateListener(AudioStateListener listener) {
		mListener = listener;
	}

	public static AudioManager getInstance(String dir) {
		if (mInstance == null) {
			synchronized (AudioManager.class) {
				if (mInstance == null) {
					mInstance = new AudioManager(dir);
				}
			}
		}
		return mInstance;
	}

	public void prepareAudio() {
		try {
			isPrepared = false;
			File dir = new File(mDir);
			if (!dir.exists())
				dir.mkdirs();

			String fileName = generateFileName();
			File file = new File(dir, fileName);

			mCurFilePath = file.getAbsolutePath();
			mMediaRecorder = new MediaRecorder();
			// 设置输出文件
			mMediaRecorder.setOutputFile(file.getAbsolutePath());
			// 设置audiorecorder的音频源为麦克风
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			// 设置音频的格式
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
			// 设置音频文件的编码为AMR
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

			mMediaRecorder.prepare();
			mMediaRecorder.start();
			// 准备结束
			isPrepared = true;

			if (mListener != null)
				mListener.wellPrepared();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 随机生成文件名 Tile:generateFileName
	 * 
	 * @return String
	 */
	private String generateFileName() {
		return UUID.randomUUID().toString() + ".amr";
	}

	public int getVoiceLevel(int maxLevel) {
		if (isPrepared) {
			try {
				// mMediaRecorder.getMaxAmplitude() 1-32767
				return maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 1;
	}

	public void release() {
		try {
			mMediaRecorder.stop();
			mMediaRecorder.release();
			mMediaRecorder = null;
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void cancel() {
		release();

		if (mCurFilePath != null) {
			File file = new File(mCurFilePath);
			file.delete();
			mCurFilePath = null;
		}
	}

	public String getCurrentFilePath() {
		return mCurFilePath;
	}
}
