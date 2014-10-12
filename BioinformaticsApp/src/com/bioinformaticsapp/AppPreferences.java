package com.bioinformaticsapp;


import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class AppPreferences extends PreferenceActivity {

	public static final String OHBLASTIT_PREFERENCES_FILE = "ohblastit_preferences";
	
	public static final String NOTIFICATIONS_SWITCH = "notifications";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PreferenceScreen prefsScreen = setupPreferencesScreen();
		setPreferenceScreen(prefsScreen);
	}

	private PreferenceScreen setupPreferencesScreen(){	
		PreferenceManager manager = getPreferenceManager();
		manager.setSharedPreferencesName(OHBLASTIT_PREFERENCES_FILE);
		PreferenceScreen preferencesScreen = manager.createPreferenceScreen(this);

		PreferenceCategory inlinePrefCat = new PreferenceCategory(this);
        inlinePrefCat.setTitle(R.string.app_name);
        preferencesScreen.addPreference(inlinePrefCat);
        
        CheckBoxPreference notificationSwitch = new CheckBoxPreference(this);
        notificationSwitch.setKey(NOTIFICATIONS_SWITCH);
        notificationSwitch.setTitle("Notifications");
        notificationSwitch.setSummary("Notify me when BLAST sequence searches have finished");
        inlinePrefCat.addPreference(notificationSwitch);
        
        return preferencesScreen;
	}
}
