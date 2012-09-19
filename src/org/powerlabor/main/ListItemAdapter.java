package org.powerlabor.main;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


/**
 * Activity для ввода текстовой строки пользователем (н-р, название новой задачи)
 * связанный layout: input_data.xml
 */
public class ListItemAdapter extends SimpleCursorAdapter   {
	// переменные для управления логами (значения берутся из главного класса - PowerlaborActivity)
	private final static boolean LOGV_ENABLED = PowerlaborActivity.LOGV_ENABLED;
	private final static boolean LOGD_ENABLED = PowerlaborActivity.LOGD_ENABLED;
	private final static String LOGTAG = "myLog_TaskItemAdapter";
	
	private Context ctx;
	private Cursor cur;
	LayoutInflater lInflater;

	
	ListItemAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
		super(context, layout, c, from, to);
		
        if (LOGD_ENABLED) Log.d(LOGTAG, "ListItemAdapter.ListItemAdapter()");
        
		this.ctx = context;
		this.cur = c;
		this.lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	
	// пункт списка
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        if (LOGV_ENABLED) Log.v(LOGTAG, "ListItemAdapter.getView: this=" + this);
		
		// используем созданные, но не используемые view
		View view = convertView;
		if (view == null) {
			view = lInflater.inflate(R.layout.taskitem, parent, false);
		}

		this.cur.moveToPosition(position);
		
		// отобразим хронометр
		Chronometer mChronometer = (Chronometer) view.findViewById(R.id.chronometer);

		// заполним название задачи
		int taskColIndex = cur.getColumnIndex(DB.COLUMN_TASK);
		((TextView) view.findViewById(R.id.tvTask)).setText(cur.getString(taskColIndex));
		
		// отобразим картинку
		int plaing_value = cur.getInt(cur.getColumnIndex(DB.COLUMN_PLAYING));
		if (plaing_value == 0) {
			plaing_value = R.drawable.player_play;
//			mChronometer.stop(); //(SystemClock.elapsedRealtime());
	        if (LOGD_ENABLED) Log.d(LOGTAG, "getView() Position - " + position);
			}
		else if (plaing_value == 1) {
			plaing_value = R.drawable.player_pause;
	        if (LOGD_ENABLED) Log.d(LOGTAG,  "getView() Position - " + position );
			mChronometer.start(); //setBase(0 ); //(SystemClock.elapsedRealtime());

		}
		((ImageView) view.findViewById(R.id.ivPlayPause)).setImageResource(plaing_value);

		
		return view;
	}
}