package com.syh.recorder;

import java.util.ArrayList;
import java.util.List;
import com.syh.recorder.recoerderbtn.AudioRecorderButton;
import com.syh.recorder.recoerderbtn.MediaManager;
import com.syh.recorder.recoerderbtn.AudioRecorderButton.AudioFinishedRecorderListener;
import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity {

	private ListView mListView;
	private AudioRecorderButton mButton;
	private View mAnimView;
	private MyAdapter myAdapter;
	private List<Recorder> mDatas = new ArrayList<Recorder>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
	}

	private void initViews() {
		mButton = (AudioRecorderButton) findViewById(R.id.recorder_btn);
		mListView = (ListView) findViewById(R.id.listview_recorder_item);
		myAdapter = new MyAdapter(getBaseContext(), mDatas, R.layout.list_item);
		mListView.setAdapter(myAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mAnimView != null) {
					mAnimView.setBackgroundResource(R.drawable.adj);
					mAnimView = null;
				}
				// 播放动画
				mAnimView = view.findViewById(R.id.recorder_anim);
				mAnimView.setBackgroundResource(R.drawable.list_anim);
				AnimationDrawable animationDrawable = (AnimationDrawable) mAnimView
						.getBackground();
				animationDrawable.start();
				// 播放音频
				MediaManager.playSound(mDatas.get(position).getFilePath(),
						new MediaPlayer.OnCompletionListener() {

							@Override
							public void onCompletion(MediaPlayer mp) {
								// TODO Auto-generated method stub
								mAnimView.setBackgroundResource(R.drawable.adj);
							}
						});
			}
		});

		mButton.setAudioFinishedRecorderListener(new AudioFinishedRecorderListener() {

			public void onFinish(float seconds, String filePath) {
				Recorder recorder = new Recorder(seconds, filePath);
				mDatas.add(recorder);
				myAdapter.notifyDataSetChanged();
				mListView.setSelection(mDatas.size() - 1);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		MediaManager.resume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MediaManager.pause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MediaManager.release();
	}

}
