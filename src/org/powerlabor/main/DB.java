package org.powerlabor.main;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Process;
import android.util.Log;

/**
 * Класс работы с базой данных
 */
public class DB {
	// переменные для управления логами (значения берутся из главного класса - PowerlaborActivity)
	private final static boolean LOGV_ENABLED = PowerlaborActivity.LOGV_ENABLED;
	private final static boolean LOGD_ENABLED = PowerlaborActivity.LOGD_ENABLED;
	private final static String LOGTAG = "myLog_DB";
    
	private static final String DB_NAME = "powerlabor_db";
	private static final int DB_VERSION = 1;
	private static final String DB_TABLE_TASKS = "t_tasks";
	private static final String DB_TABLE_STEPS = "t_steps";
 
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TASK = "s_task";
	public static final String COLUMN_PLAYING = "b_playing";
 
	public static final String COLUMN_TASKID = "l_task";
	public static final String COLUMN_STEPS_START = "l_start";
	
	
	private final Context mContext;

	private DBHelper mDBHelper;
	private SQLiteDatabase mDatabase;
 
	
	public DB(Context ctx) {
        if (LOGV_ENABLED) Log.v(LOGTAG, "DB(): Context=" + ctx);
        if (LOGD_ENABLED) Log.d(LOGTAG, "DB()");
        
		mContext = ctx;
	}
 
	
	
	// открыть подключение
	public void open() {
        if (LOGD_ENABLED) Log.d(LOGTAG, "DB.open()");
        
		mDBHelper = new DBHelper(mContext, DB_NAME, null, DB_VERSION);
		mDatabase = mDBHelper.getWritableDatabase();
	}
 
	
	
	// закрыть подключение
	public void close() {
        if (LOGD_ENABLED) Log.d(LOGTAG, "DB.close()");
        
		if (mDBHelper!=null) mDBHelper.close();
	}
 
	
	
	// получить все данные из таблицы DB_TABLE_TASKS
	public Cursor getAllData() {
        if (LOGD_ENABLED) Log.d(LOGTAG, "DB.getAllData()");
		
		return mDatabase.query(DB_TABLE_TASKS, null, null, null, null, null, null);
	}
	
	
	
	// создать таблицы, добавить запись в DB_TABLE_TASKS
	public void CreateDB(SQLiteDatabase db) {
        if (LOGD_ENABLED) Log.d(LOGTAG, "DB.CreateDB()");
		
		final String DB_CREATE1 =
			 	"CREATE TABLE [t_tasks] (" + "\r\n" +
				"			  [_id] INTEGER PRIMARY KEY AUTOINCREMENT," + "\r\n" +
				"			  [s_task] TEXT(255)," + "\r\n" +
				"			  [b_playing] BOOLEAN," + "\r\n" +
				"			  [b_del] BOOLEAN, " + "\r\n" +
				"			  [s_note] TEXT(255), " + "\r\n" +
				"			  [dt_generation] DATETIME DEFAULT (datetime('now')));";
			
		final String DB_CREATE2 =
				"CREATE TABLE [t_steps] (" + "\r\n" +
				"			  [l_id] INTEGER PRIMARY KEY AUTOINCREMENT, " + "\r\n" +
				"			  [l_task] INTEGER CONSTRAINT [fk_2tasks] REFERENCES [t_tasks]([l_id]), " + "\r\n" +
				"			  [s_step] TEXT(255), " + "\r\n" +
				"			  [l_start] LONG, " + "\r\n" +
				"			  [l_finish] LONG, " + "\r\n" +
				"			  [i_MinMinus] INTEGER, " + "\r\n" +
				"			  [i_MinTotal] INTEGER, " + "\r\n" +
				"			  [dbl_SecExpense] DOUBLE, " + "\r\n" +
				"			  [b_del] BOOLEAN, " + "\r\n" +
				"			  [s_note] TEXT(255), " + "\r\n" +
				"			  [dt_generation] DATETIME DEFAULT (datetime('now')));";
		
		db.execSQL(DB_CREATE1);
		db.execSQL(DB_CREATE2);
	      
		// заполним таблицу тестовыми данными
		ContentValues cv = new ContentValues();
		
		cv.put(COLUMN_TASK, mContext.getResources().getString(R.string.task_conference));
		cv.put(COLUMN_PLAYING, false);
		db.insert(DB_TABLE_TASKS, null, cv);

		cv.put(COLUMN_TASK, mContext.getResources().getString(R.string.task_discussion));
		cv.put(COLUMN_PLAYING, false);
		db.insert(DB_TABLE_TASKS, null, cv); 
		
		cv.put(COLUMN_TASK, mContext.getResources().getString(R.string.task_counsel));
		cv.put(COLUMN_PLAYING, false);
		db.insert(DB_TABLE_TASKS, null, cv); 
	}
	
	
	
//	// удалить таблицы в DB_TABLE_TASKS
//	public void ClearDB(SQLiteDatabase db) {
//    if (LOGD_ENABLED) Log.d(LOGTAG, "ClearDB()");
//	
//		db.execSQL("DROP TABLE t_tasks;");
//		db.execSQL("DROP TABLE s_steps;"); 
//	}
 
	
	
	// добавить запись в DB_TABLE_TASKS
	public void addRec(String txt) {
        if (LOGD_ENABLED) Log.d(LOGTAG, "DB.addRec()");
		
	    ContentValues cv = new ContentValues();
	    cv.put(COLUMN_TASK, txt);
	    cv.put(COLUMN_PLAYING, false);
	    mDatabase.insert(DB_TABLE_TASKS, null, cv);
	}
 
	
	
	// добавить запись в DB_TABLE_TASKS с текущем временем
	public void addRec2Steps(Long	l_taskid) {
        if (LOGD_ENABLED) Log.d(LOGTAG, "DB.addRec2Steps()");
        long start = Process.getElapsedCpuTime();
        
        // поставим id задачи
	    ContentValues cv = new ContentValues();
	    cv.put(COLUMN_TASKID, l_taskid);

		// вытащим текущее время
		long l_today = new java.util.Date().getTime();
		
	    cv.put(COLUMN_STEPS_START, l_today);
	    
    
	    
//db.execSQL("INSERT INTO TableName (Date) VALUES (" + (new java.util.Date()).getTime() + ");");
//java.sql.Date dt = new java.sql.Date(recivedSQLiteData);
    
	    mDatabase.insert(DB_TABLE_STEPS, null, cv);
	    
        if (LOGD_ENABLED) Log.d(LOGTAG, "addRec2Steps() used " + (Process.getElapsedCpuTime() - start) + " ms");
	}


	
	// изменить запись в DB_TABLE_TASKS
	// версия 1
	public void changeRec(long l_id, String s_column, int i_value) {
        if (LOGD_ENABLED) Log.d(LOGTAG, "DB.changeRec(...int i_value)");
        
		ContentValues cv = new ContentValues();
	    cv.put(s_column, i_value);
	    mDatabase.update(DB_TABLE_TASKS, cv, COLUMN_ID + " = " + l_id, null);
	}
	// версия 2
	public void changeRec(long id, String s_column, String s_value) {
        if (LOGD_ENABLED) Log.d(LOGTAG, "DB.changeRec(...String s_value)");
		
		ContentValues cv = new ContentValues();
	    cv.put(s_column, s_value);
	    mDatabase.update(DB_TABLE_TASKS, cv, COLUMN_ID + " = " + id, null);
	}
	// версия 3
	public void changeRec(long id, String s_column, Boolean b_value) {
        if (LOGD_ENABLED) Log.d(LOGTAG, "DB.changeRec(...Boolean b_value)");
		
		ContentValues cv = new ContentValues();
	    cv.put(s_column, b_value);
	    mDatabase.update(DB_TABLE_TASKS, cv, COLUMN_ID + " = " + id, null);
	}

	
	
	// удалить запись из DB_TABLE_TASKS
	public void delRec(long id) {
        if (LOGD_ENABLED) Log.d(LOGTAG, "DB.delRec()");
		
		mDatabase.delete(DB_TABLE_TASKS, COLUMN_ID + " = " + id, null);
	}
 
	
	
	// удалить запись из DB_TABLE_TASKS
	public void clearTable(String  table) {
        if (LOGD_ENABLED) Log.d(LOGTAG, "DB.clearTable()");
		
		// удаляем все записи
	    int clearCount = mDatabase.delete(table, null, null);
	}
	
	
	
	// класс по созданию и управлению БД
	private class DBHelper extends SQLiteOpenHelper {

	    public DBHelper(Context context, String name, CursorFactory factory,
	        int version) {
	      super(context, name, factory, version);
	        if (LOGD_ENABLED) Log.d(LOGTAG, "DB.DBHelper()");
	    }
	    
	    
	    // создаем и заполняем БД
	    @Override
	    public void onCreate(SQLiteDatabase db) {
	        if (LOGD_ENABLED) Log.d(LOGTAG, "DBHelper.onCreate()");
	    	
	    	CreateDB(db);
	    }

	    
	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        if (LOGD_ENABLED) Log.d(LOGTAG, "DBHelper.onUpgrade()");
	    }
	}
}