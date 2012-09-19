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
 * Activity для ввода текстовой строки пользователем (н-р, название новой задачи)
 * связанный layout: input_data.xml
 * 
 * TODO: Возможно есть какой-то встроенный способ получать данные от пользователя,
 * 		и не городить свой класс+layout. Я нашёл только диалоги для ввода даты, времени, но
 * 		диалога для ввода текстовой строки, не нашёл (а он есть, должен быть).
 */
public class InputDataActivity extends Activity implements OnClickListener {
	// переменные для управления логами (значения берутся из главного класса - PowerlaborActivity)
	private final static boolean LOGV_ENABLED = PowerlaborActivity.LOGV_ENABLED;
	private final static boolean LOGD_ENABLED = PowerlaborActivity.LOGD_ENABLED;
	private final static String LOGTAG = "myLog_InputDataActivity";
	
	// поле для ввода текста
	EditText etName;
	// кнопка ОК
	Button btnOK;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.input_data);
    
    //	Запись в лог
    if (LOGD_ENABLED) Log.d(LOGTAG, "Input_dataActivity.onCreate()"); 
    
    // вытащим поле для ввода, чтоб можно было к нему обращаться при обработке нажатия кнопки
    etName = (EditText) findViewById(R.id.etName);
    
    // назначим кнопке ОК обработчик
    btnOK = (Button) findViewById(R.id.btnOK);
	btnOK.setOnClickListener(this);
  }
  

  @Override
  public void onClick(View v) {
	if (LOGD_ENABLED) Log.d(LOGTAG, "InputDataActivity.onClick()"); 
	// Передаём данные через Intent (обработка продолжается в onActivityResult)
    Intent intent = new Intent();
    intent.putExtra("name", etName.getText().toString());
    setResult(RESULT_OK, intent);
    finish();
  }
}