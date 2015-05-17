package com.syh.recorder.recoerderbtn;

import com.syh.recorder.R;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("InflateParams") public class DialogManager {

	private Dialog mDialog;
	private ImageView mIcon;
	private ImageView mVoice;
	private TextView mLabel;
	private Context mContext;

	public DialogManager(Context context) {
		this.mContext = context;
	}

	public void showRecordingDialog() {
		mDialog = new Dialog(mContext, R.style.Them_AudioDialog);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.dialog, null);
		mDialog.setContentView(view);

		mIcon = (ImageView) mDialog.findViewById(R.id.dialog_recorder_icon);
		mVoice = (ImageView) mDialog.findViewById(R.id.dialog_recorder_voice);
		mLabel = (TextView) mDialog.findViewById(R.id.dialog_recorder_text);

		mDialog.show();
	}

	public void recording() {
		if (mDialog != null && mDialog.isShowing()) {
			mIcon.setVisibility(View.VISIBLE);
			mVoice.setVisibility(View.VISIBLE);
			mLabel.setVisibility(View.VISIBLE);

			mIcon.setImageResource(R.drawable.recorder);
			mLabel.setText(R.string.str_dialog_recorder_text);
		}
	}

	public void showCancelDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			mIcon.setVisibility(View.VISIBLE);
			mVoice.setVisibility(View.GONE);
			mLabel.setVisibility(View.VISIBLE);

			mIcon.setImageResource(R.drawable.cancel);
			mLabel.setText(R.string.str_dialog_recorder_text_cancel);
		}
	}

	public void tooShort() {
		if (mDialog != null && mDialog.isShowing()) {
			mIcon.setVisibility(View.VISIBLE);
			mVoice.setVisibility(View.GONE);
			mLabel.setVisibility(View.VISIBLE);

			mIcon.setImageResource(R.drawable.voice_to_short);
			mLabel.setText(R.string.str_dialog_recorder_text_short);
		}
	}

	public void dimissDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
			mDialog = null;
		}
	}

	/**
	 * 通过level去更新voice上的图片 Tile:updateVoiceLevel
	 * 
	 * @param level 1-7
	 */
	public void updateVoiceLevel(int level) {
		if (mDialog != null && mDialog.isShowing()) {

			int resId = mContext.getResources().getIdentifier("v" + level,
					"drawable", mContext.getPackageName());
			mVoice.setImageResource(resId);
		}
	}
}
