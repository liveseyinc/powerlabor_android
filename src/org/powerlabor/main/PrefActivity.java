package org.powerlabor.main;


import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Блок настроек приложения (пока просто зарезервировано)
 */
public class PrefActivity extends PreferenceActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.preferences);
  }
}