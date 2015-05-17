package com.syh.recorder.recoerderbtn;

import com.syh.recorder.R;
import com.syh.recorder.recoerderbtn.AudioManager.AudioStateListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

@SuppressLint("ClickableViewAccessibility")
public class AudioRecorderButton extends Button implements AudioStateListener {

	private static final int DISTANCE_Y_CANCEL = 50;
	private static final int STATE_NORMAL = 1;
	private static final int STATE_RECORDING = 2;
	private static final int STATE_CANCEL = 3;
	private static final float SHORT_TIME = 0.6f;

	private int mCurState = STATE_NORMAL;
	// 已经开始录音了
	private boolean isRecording = false;
	// 是否触发了onlongclick
	private boolean mReady;

	private DialogManager mDialogManager;

	private AudioManager mAudioManager;

	public AudioRecorderButton(Context context) {
		this(context, null);
	}

	public AudioRecorderButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		mDialogManager = new DialogManager(getContext());

		String dir = Environment.getExternalStorageDirectory()+ "/RecoederAudios";
		mAudioManager = AudioManager.getInstance(dir);
		mAudioManager.setAudioStateListener(this);

		setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				mReady = true;
				mAudioManager.prepareAudio();
				return false;
			}
		});
	}

	/**
	 * 录音完成后的回调
	 * 
	 * @author shenyonghe
	 */
	public interface AudioFinishedRecorderListener {
		void onFinish(float seconds, String filePath);
	}

	private AudioFinishedRecorderListener mAudioFinishedRecorderListener;

	public void setAudioFinishedRecorderListener(
			AudioFinishedRecorderListener listener) {
		mAudioFinishedRecorderListener = listener;
	}

	private float mTime;

	/**
	 * 获取音量大小的Runnable
	 */
	private Runnable mGetVoiceRunnable = new Runnable() {

		@Override
		public void run() {
			while (isRecording) {
				try {
					Thread.sleep(100);
					mTime += 0.1f;
					mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
				} catch (InterruptedException e) {
				}
			}
		}
	};

	private static final int MSG_AUDIO_PREPARED = 10;
	private static final int MSG_VOICE_CHANGED = 11;
	private static final int MSG_DIALOG_DIMISS = 12;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_AUDIO_PREPARED:
				// 真正显示应该在 audio end pre 以后
				mDialogManager.showRecordingDialog();
				isRecording = true;
				new Thread(mGetVoiceRunnable).start();
				break;
			case MSG_VOICE_CHANGED:
				mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
				break;

			case MSG_DIALOG_DIMISS:
                mDialogManager.dimissDialog();
				break;
			}
		};
	};

	public void wellPrepared() {
		mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
		System.out.println("wellPrepared");
	}

	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int x = (int) event.getX();
		int y = (int) event.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			changeState(STATE_RECORDING);
			break;
		case MotionEvent.ACTION_MOVE:
			if (isRecording) {
				// 根据xy坐标判断是否取消录音
				if (wantToCancel(x, y)) {
					changeState(STATE_CANCEL);
				} else {
					changeState(STATE_RECORDING);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if (!mReady) {
				reset();
				return super.onTouchEvent(event);
			}

			if (!isRecording || mTime < SHORT_TIME) {
				mDialogManager.tooShort();
				mAudioManager.cancel();
				mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1000);
			}

			else if (mCurState == STATE_RECORDING) {// 正常录制结束
				mDialogManager.dimissDialog();

				if (mAudioFinishedRecorderListener != null) {
					mAudioFinishedRecorderListener.onFinish(mTime,
							mAudioManager.getCurrentFilePath());
				}

				mAudioManager.release();

			} else if (mCurState == STATE_CANCEL) {
				mDialogManager.dimissDialog();
				mAudioManager.cancel();
			}
			reset();
			break;

		}
		return super.onTouchEvent(event);
	}

	private boolean wantToCancel(int x, int y) {
		if (x < 0 || x > getWidth()) {
			return true;
		}
		if (y < -DISTANCE_Y_CANCEL || y > DISTANCE_Y_CANCEL + getHeight()) {
			return true;
		}
		return false;
	}

	private void changeState(int state) {
		if (mCurState != state) {
			mCurState = state;

			switch (state) {
			case STATE_NORMAL:
				setBackgroundResource(R.drawable.btn_recorder_normal);
				setText(R.string.str_recorderbtn_normal);
				break;
			case STATE_RECORDING:
				setBackgroundResource(R.drawable.btn_recording);
				setText(R.string.str_recorderbtn_recordering);
				if (isRecording) {
					mDialogManager.recording();
				}
				break;
			case STATE_CANCEL:
				setBackgroundResource(R.drawable.btn_recording);
				setText(R.string.str_recorderbtn_cancel);
				mDialogManager.showCancelDialog();
				break;
			}
		}
	}

	/**
	 * 恢复状态及标志位 Tile:reset void
	 */
	private void reset() {
		isRecording = false;
		mReady = false;
		mTime = 0.0f;
		changeState(STATE_NORMAL);		
	}

}
