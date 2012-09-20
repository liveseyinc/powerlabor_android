package org.powerlabor.main;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ������� Activity
 * ��������� layout: main.xml
 */
public class PowerlaborActivity extends Activity {
	
	// ����
    // Set to true to enable verbose logging.
    final static boolean LOGV_ENABLED = true; //false;
    // Set to true to enable extra debug logging.
    final static boolean LOGD_ENABLED = true;
	private final static String LOGTAG = "myLog_PowerlaborActivity";
	private final static int DLG_INPUT_NAME_ACTIVITY = 0; 
	private final static int DLG_INPUT_NAME_VIEW_ID = 0;

	
	ListItemAdapter TaskItemAdapter;

	private static final int CM_DELETE_ID = 1;
	
	ListView lvData;
	DB inv_db;
	SimpleCursorAdapter scAdapter;
	Cursor cursor;

	// SharedPreferences ��� ������ � ������ ��������
	SharedPreferences sp;


	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (LOGV_ENABLED) Log.v(LOGTAG, "PowerlaborActivity.onCreate: this=" + this);
        if (LOGD_ENABLED) Log.d(LOGTAG, "PowerlaborActivity.onCreate()");
   
	    // �������� SharedPreferences, ������� �������� � ������ ��������
	    sp = PreferenceManager.getDefaultSharedPreferences(this);
	    // ������ ������� ��������
	    // sp.edit().clear().commit();
        
        
	    // ��������� ����������� � ��
	    inv_db = new DB(this);
	    inv_db.open();

	    // �������� ������
	    cursor = inv_db.getAllData();
	    startManagingCursor(cursor);
	    
	    // ��������� ������� �������������
	    String[] from = new String[] { DB.COLUMN_PLAYING, DB.COLUMN_TASK };
	    int[] to = new int[] { R.id.ivPlayPause, R.id.tvTask};
    
	    // ������� �������
	    TaskItemAdapter = new ListItemAdapter(this, R.layout.taskitem, cursor, from, to);

        // ����������� ������
        ListView lvData = (ListView) findViewById(R.id.lvData);
        lvData.setAdapter(TaskItemAdapter);
	    
	    // ��������� ����������� ���� � ������
	    registerForContextMenu(lvData);
   
	    // ������� OnItemClick �� ������ lvData
	    // TODO: ������� � ��������� ������� ���������� ������� �� ������� ������
	    lvData.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	            
	        	if (LOGD_ENABLED) Log.d(LOGTAG, "itemClick: position = " + position + ", id = " + id);
        	
	            // ������� � ������� ������
	    		cursor.moveToPosition(position);
	    		
	    		
//    			// ������� ���������
    			final Chronometer mChronometer = (Chronometer) view.findViewById(R.id.chronometer);

	    		// ������� ������� �������� COLUMN_PLAYING (������� ����, ��� ������� ������ � ������, ������������ �����, ��������� ��������)
	    		int plaing_value = cursor.getInt(cursor.getColumnIndex(DB.COLUMN_PLAYING));
	    		boolean	b_playing = false;
	    		
	    		if (plaing_value == 0) {
	    			// �������� ��������
	    			b_playing = true;
	    			
	    			// ������� ������ � t_step � ����� ������
	    			inv_db.addRec2Steps((long) position);
	    			

	    			// TODO: ����� �������!!! �������, ���� ��� ������ ������ ���������� ���� ����������� ��������� (������ ���� � ������ ������� ��� ������). ������ ��������� ��� �� ������, �.�. ��������� ����� � ����� ������, �������� �������� ���������� �� ���� ��������.
	    			mChronometer.start(); //(SystemClock.elapsedRealtime());
	    	        if (LOGD_ENABLED) Log.d(LOGTAG, "mChronometer.start() Position - " + position);
	    	
	    			
	    			String title = ((TextView)view.findViewById(R.id.tvTask)).getText().toString();
	    			Toast.makeText(getApplicationContext(), title, Toast.LENGTH_LONG).show(); 

	    		}
	    		else if (plaing_value == 1) {
	    			// �������� ��������
	    			b_playing = false;
	    			// ��������� ���������
	    			mChronometer.stop(); //(SystemClock.elapsedRealtime());
	    	        if (LOGD_ENABLED) Log.d(LOGTAG, "mChronometer.stop() Position - " + position);
	    	
	    		}
	    		
	    		// ������� � ���� ������� ������ ������
	    		inv_db.changeRec (id, DB.COLUMN_PLAYING, b_playing);
	    		
	    		// ��������� ������
	    		cursor.requery();
	    		
	        }
	    });

	    // TODO: ������� � ��������� �������
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
//// ������ ������ � ������������� �����������.
//		Boolean notif = sp.getBoolean("notif", false);
//	    String address = sp.getString("address", "");
//	    String text = "Notifications are "
//	        + ((notif) ? "enabled, address = " + address : "disabled");
//        Toast.makeText(this, text, Toast.LENGTH_LONG).show(); 
        
	    super.onResume();

        if (LOGD_ENABLED) Log.d(LOGTAG, "PowerlaborActivity.onResume()");  
	}


	

	// ��������� ����� ����� ����� ������ � ���������� ������ ��� �����
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (LOGD_ENABLED) Log.d(LOGTAG, "PowerlaborActivity.onActivityResult()");
		if (data == null) {return;}
		if (data.getStringExtra("name").length()>0) {
			inv_db.addRec(data.getStringExtra("name"));
		    // ��������� ������
		    cursor.requery();
		}
	}

	// ������ ���������� ������� ������
	public void onButtonClick(View v) {
		if (LOGD_ENABLED) Log.d(LOGTAG, "PowerlaborActivity.onButtonClick()");
		
        // ������� ��������
        switch(v.getId()){
		case R.id.button_addrow:
			//show dialog for obtain activity name
			showDialog(DLG_INPUT_NAME_ACTIVITY);
			break;
		}
	}
	
	/*
	 * This method would be called on Activity.showDialog()
	 * 
	 */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DLG_INPUT_NAME_ACTIVITY:
                return createInputNameDialog();
            default:
                return null;
        }
    }
    
    /**
     * If a dialog has already been created,
     * this is called to reset the dialog
     * before showing it a 2nd time. Optional.
     */
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
 
        switch (id) {
            case DLG_INPUT_NAME_ACTIVITY:
                // Clear the input box.
                EditText text = (EditText) dialog.findViewById( DLG_INPUT_NAME_VIEW_ID );
                text.setText("");
                break;
        }
    }
    
    /**
     * Creating and setting Dialog object
     * @return dialog
     */
    private Dialog createInputNameDialog() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle( R.string.input_task_title );
        builder.setMessage( R.string.manifest_input_task );
     
         // Use an EditText view to get user input.
        final EditText input = new EditText(this);
        input.setId(DLG_INPUT_NAME_VIEW_ID);
        //one text string only
        input.setSingleLine( true );
        builder.setView(input);
         
         //set button "Ok"
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
 
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                if (LOGD_ENABLED) Log.d(LOGTAG, "PowerlaborActivity.onActivityResult()");
        		if ( value.length()>0) {
        			inv_db.addRec( value );
        		    // ��������� ������
        		    cursor.requery();
        		}
                return;
            }
        });
        //set button "Cancel"
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
 
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
 
        return builder.create();
    }

    // �������� ����
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.mymenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	// ���������� ����
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (LOGD_ENABLED) Log.d(LOGTAG, "PowerlaborActivity.onOptionsItemSelected()");
		
		switch(item.getItemId()){
		
		case R.id.menu_clear_db:
			//����� �� ����������
			inv_db.clearTable("t_tasks");
		    // ��������� ������
		    cursor.requery();
			break;
		case R.id.menu_options:
		    item.setIntent(new Intent(this, PrefActivity.class));
			break;
		case R.id.menu_exit:
			//����� �� ����������
			finish();
			break;
		}
	
		return super.onOptionsItemSelected(item);
	}

	
    // �������� ����������� ����
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
	
	
	// ���������� ������������ ���� 
	public boolean onContextItemSelected(MenuItem item) {
        if (LOGD_ENABLED) Log.d(LOGTAG, "PowerlaborActivity.onContextItemSelected()");
	    if (item.getItemId() == CM_DELETE_ID) {
	      // �������� �� ������ ������������ ���� ������ �� ������ ������ 
	      AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
	      // ��������� id ������ � ������� ��������������� ������ � ��
	      inv_db.delRec(acmi.id);
	      // ��������� ������
	      cursor.requery();
	      return true;
	    }
	    return super.onContextItemSelected(item);
	}

	
	// ��� ������ ������ ����
	protected void onDestroy() {
		super.onDestroy();
        if (LOGD_ENABLED) Log.d(LOGTAG, "PowerlaborActivity.onDestroy()");
		// ��������� ����������� ��� ������
		inv_db.close();
	}
}