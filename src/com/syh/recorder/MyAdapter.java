package com.syh.recorder;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MyAdapter extends CommonAdapter<Recorder> {

	public MyAdapter(Context context, List<Recorder> datas, int itemLayout) {
		super(context, datas, itemLayout);
	}

	@SuppressWarnings("deprecation")
	public void convert(View convertView, ViewGroup parent,
			final Recorder recorder) {
		int minWidth, maxWidth;
		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		minWidth = (int) (0.15f * wm.getDefaultDisplay().getWidth());
		maxWidth = (int) (0.7f * wm.getDefaultDisplay().getWidth());
		TextView time = ViewHolder.get(convertView, R.id.tv_time_length);
		time.setText(Math.round(recorder.getTime()) + "\"");
		FrameLayout frameLayout = ViewHolder.get(convertView,
				R.id.fl_recorder_length);
		ViewGroup.LayoutParams mParams = frameLayout.getLayoutParams();
		mParams.width = (int) (minWidth + (maxWidth / 60f * recorder.getTime()));
	}

}
