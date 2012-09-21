package org.powerlabor.main;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Главное Activity
 * связанный layout: main.xml
 */
public class PowerlaborActivity extends Activity {
	
	// Логи
    // Set to true to enable verbose logging.
    final static boolean LOGV_ENABLED = true; //false;
    // Set to true to enable extra debug logging.
    final static boolean LOGD_ENABLED = true;
	private final static String LOGTAG = "myLog_PowerlaborActivity";

	
	ListItemAdapter TaskItemAdapter;

	private static final int CM_DELETE_ID = 1;
	
	ListView lvData;
	DB inv_db;
	SimpleCursorAdapter scAdapter;
	Cursor cursor;

	// SharedPreferences для работы с файлом настроек
	SharedPreferences sp;


	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (LOGV_ENABLED) Log.v(LOGTAG, "PowerlaborActivity.onCreate: this=" + this);
        if (LOGD_ENABLED) Log.d(LOGTAG, "PowerlaborActivity.onCreate()");
   
	    // получаем SharedPreferences, которое работает с файлом настроек
	    sp = PreferenceManager.getDefaultSharedPreferences(this);
	    // полная очистка настроек
	    // sp.edit().clear().commit();
        
        
	    // открываем подключение к БД
	    inv_db = new DB(this);
	    inv_db.open();

	    // получаем курсор
	    cursor = inv_db.getAllData();
	    startManagingCursor(cursor);
	    
	    // формируем столбцы сопоставления
	    String[] from = new String[] { DB.COLUMN_PLAYING, DB.COLUMN_TASK };
	    int[] to = new int[] { R.id.ivPlayPause, R.id.tvTask};
    
	    // создаем адаптер
	    TaskItemAdapter = new ListItemAdapter(this, R.layout.taskitem, cursor, from, to);

        // настраиваем список
        ListView lvData = (ListView) findViewById(R.id.lvData);
        lvData.setAdapter(TaskItemAdapter);
	    
	    // добавляем контекстное меню к списку
	    registerForContextMenu(lvData);
   
	    // Событие OnItemClick на списке lvData
	    // TODO: Вынести в отдельную функцию обработчик нажатий на строках списка
	    lvData.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	            
	        	if (LOGD_ENABLED) Log.d(LOGTAG, "itemClick: position = " + position + ", id = " + id);
        	
	            // перейдём к текущей записи
	    		cursor.moveToPosition(position);
	    		
	    		
//    			// вытащим хронометр
    			final Chronometer mChronometer = (Chronometer) view.findViewById(R.id.chronometer);

	    		// вытащим текущее значение COLUMN_PLAYING (признак того, что текущая строка в работе, отсчитывется время, хронометр крутится)
	    		int plaing_value = cursor.getInt(cursor.getColumnIndex(DB.COLUMN_PLAYING));
	    		boolean	b_playing = false;
	    		
	    		if (plaing_value == 0) {
	    			// поменяем картинку
	    			b_playing = true;
	    			
	    			
	    			// добавим запись в t_step и начнём отсчёт
	    			inv_db.addRec2Steps(id);
	    			

	    			// TODO: Самое главное!!! Сделать, чтоб для каждой строки запускался свой независимый хронометр (видимо надо в разных потоках это делать). Сейчас хронометр как бы единый, т.е. нажимаешь старт в одной строке, начинают работать хронометры во ВСЕХ строчках.
	    			mChronometer.start(); //(SystemClock.elapsedRealtime());
	    	        if (LOGD_ENABLED) Log.d(LOGTAG, "mChronometer.start() Position - " + position);
	    	
	    			
	    			String title = ((TextView)view.findViewById(R.id.tvTask)).getText().toString();
	    			Toast.makeText(getApplicationContext(), title, Toast.LENGTH_LONG).show(); 

	    		}
	    		else if (plaing_value == 1) {
	    			// поменяем картинку
	    			b_playing = false;
	    			// остановим хронометр
	    			mChronometer.stop(); //(SystemClock.elapsedRealtime());
	    	        if (LOGD_ENABLED) Log.d(LOGTAG, "mChronometer.stop() Position - " + position);
	    	
	    		}
	    		
	    		// запишем в базу текущий статус строки
	    		inv_db.changeRec (id, DB.COLUMN_PLAYING, b_playing);
	    		
	    		// обновляем курсор
	    		cursor.requery();
	    		
	        }
	    });

	    // TODO: Вынести в отдельную функцию
	    // OnItemSelected, onNothingSelected
	    lvData.setOnItemSelectedListener(new OnItemSelectedListener() {
	        public void onItemSelected(AdapterView<?> parent, View view,
	            int position, long id) {
	            if (LOGD_ENABLED) Log.d(LOGTAG, "itemSelect: position = " + position + ", id = " + id);
	        }

	        public void onNothingSelected(AdapterView<?> parent) {
	            if (LOGD_ENABLED) Log.d(LOGTAG, "itemSelect: nothing");
	        }
	    });
    }
	
	
	protected void onResume() {
//// Пример работы с выставленными настройками.
//		Boolean notif = sp.getBoolean("notif", false);
//	    String address = sp.getString("address", "");
//	    String text = "Notifications are "
//	        + ((notif) ? "enabled, address = " + address : "disabled");
//        Toast.makeText(this, text, Toast.LENGTH_LONG).show(); 
        
	    super.onResume();

        if (LOGD_ENABLED) Log.d(LOGTAG, "PowerlaborActivity.onResume()");  
	}


	

	// вернулись после ввода имени задачи и продолжаем работу уже здесь
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (LOGD_ENABLED) Log.d(LOGTAG, "PowerlaborActivity.onActivityResult()");
		if (data == null) {return;}
		if (data.getStringExtra("name").length()>0) {
			inv_db.addRec(data.getStringExtra("name"));
		    // обновляем курсор
		    cursor.requery();
		}
	}

	// Единый обработчик нажатий кнопок
	public void onButtonClick(View v) {
		if (LOGD_ENABLED) Log.d(LOGTAG, "PowerlaborActivity.onButtonClick()");
		
        // выберем кнопочку
        switch(v.getId()){
		case R.id.button_addrow:
		    // добавляем запись
			// запросим название задачи
		    Intent intent = new Intent(this, InputDataActivity.class);
		    startActivityForResult(intent, 1);
		    // дальнейшая обработка происходит в onActivityResult
			break;
		}
	}


    // назначим меню
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.mymenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	// обработчик меню
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (LOGD_ENABLED) Log.d(LOGTAG, "PowerlaborActivity.onOptionsItemSelected()");
		
		switch(item.getItemId()){
		
		case R.id.menu_clear_db:
			//выход из приложения
			inv_db.clearTable("t_tasks");
		    // обновляем курсор
		    cursor.requery();
			break;
		case R.id.menu_options:
		    item.setIntent(new Intent(this, PrefActivity.class));
			break;
		case R.id.menu_exit:
			//выход из приложения
			finish();
			break;
		}
	
		return super.onOptionsItemSelected(item);
	}

	
    // назначим контекстное меню
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    
	    if (LOGD_ENABLED) Log.d(LOGTAG, "PowerlaborActivity.onCreateContextMenu()");
	    
		switch(v.getId()){
		case R.id.lvData:
			//
		    menu.add(0, CM_DELETE_ID, 0, R.string.button_delete_record);
			break;
		}
	}
	
	
	// обработчик контекстного меню 
	public boolean onContextItemSelected(MenuItem item) {
        if (LOGD_ENABLED) Log.d(LOGTAG, "PowerlaborActivity.onContextItemSelected()");
	    if (item.getItemId() == CM_DELETE_ID) {
	      // получаем из пункта контекстного меню данные по пункту списка 
	      AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
	      // извлекаем id записи и удаляем соответствующую запись в БД
	      inv_db.delRec(acmi.id);
	      // обновляем курсор
	      cursor.requery();
	      return true;
	    }
	    return super.onContextItemSelected(item);
	}

	
	// при выходе гасите свет
	protected void onDestroy() {
		super.onDestroy();
        if (LOGD_ENABLED) Log.d(LOGTAG, "PowerlaborActivity.onDestroy()");
		// закрываем подключение при выходе
		inv_db.close();
	}
}