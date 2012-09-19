package org.powerlabor.main;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * Activity ��� ����� ��������� ������ ������������� (�-�, �������� ����� ������)
 * ��������� layout: input_data.xml
 * 
 * TODO: �������� ���� �����-�� ���������� ������ �������� ������ �� ������������,
 * 		� �� �������� ���� �����+layout. � ����� ������ ������� ��� ����� ����, �������, ��
 * 		������� ��� ����� ��������� ������, �� ����� (� �� ����, ������ ����).
 */
public class InputDataActivity extends Activity implements OnClickListener {
	// ���������� ��� ���������� ������ (�������� ������� �� �������� ������ - PowerlaborActivity)
	private final static boolean LOGV_ENABLED = PowerlaborActivity.LOGV_ENABLED;
	private final static boolean LOGD_ENABLED = PowerlaborActivity.LOGD_ENABLED;
	private final static String LOGTAG = "myLog_InputDataActivity";
	
	// ���� ��� ����� ������
	EditText etName;
	// ������ ��
	Button btnOK;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.input_data);
    
    //	������ � ���
    if (LOGD_ENABLED) Log.d(LOGTAG, "Input_dataActivity.onCreate()"); 
    
    // ������� ���� ��� �����, ���� ����� ���� � ���� ���������� ��� ��������� ������� ������
    etName = (EditText) findViewById(R.id.etName);
    
    // �������� ������ �� ����������
    btnOK = (Button) findViewById(R.id.btnOK);
	btnOK.setOnClickListener(this);
  }
  

  @Override
  public void onClick(View v) {
	if (LOGD_ENABLED) Log.d(LOGTAG, "InputDataActivity.onClick()"); 
	// ������� ������ ����� Intent (��������� ������������ � onActivityResult)
    Intent intent = new Intent();
    intent.putExtra("name", etName.getText().toString());
    setResult(RESULT_OK, intent);
    finish();
  }
}