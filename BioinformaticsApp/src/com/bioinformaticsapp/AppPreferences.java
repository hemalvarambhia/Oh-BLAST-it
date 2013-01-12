package com.bioinformaticsapp;


import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

/**
 * The preferences Activity class enables the app user to update their e-mail as
 * is required by some BLAST web apps
 * @author Hemal N Varambhia
 *
 */
public class AppPreferences extends PreferenceActivity {

	public static final String OHBLASTIT_PREFERENCES_FILE = "ohblastit_preferences";
	
	public static final String EMAIL_PREF = "email";
	public static final String NOTIFICATIONS_SWITCH = "notifications";
	/* (non-Javadoc)
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		PreferenceScreen prefsScreen = setupPreferencesScreen();
		setPreferenceScreen(prefsScreen);
	}

	/**
	 * Creates the preferences screen
	 * @return the preferences screen that we set up
	 */
	private PreferenceScreen setupPreferencesScreen(){
		
		PreferenceManager manager = getPreferenceManager();
		manager.setSharedPreferencesName(OHBLASTIT_PREFERENCES_FILE);
		PreferenceScreen preferencesScreen = manager.createPreferenceScreen(this);

		PreferenceCategory inlinePrefCat = new PreferenceCategory(this);
        inlinePrefCat.setTitle(R.string.app_name);
        preferencesScreen.addPreference(inlinePrefCat);
        
        EditTextPreference emailPref = new EditTextPreference(this);
        emailPref.setKey(EMAIL_PREF);
        emailPref.setTitle("E-mail");
        emailPref.setSummary("Please provide an e-mail address. This is required and used by some of the webservices to notify you when results are ready");
        inlinePrefCat.addPreference(emailPref);
        
        CheckBoxPreference notificationSwitch = new CheckBoxPreference(this);
        notificationSwitch.setKey(NOTIFICATIONS_SWITCH);
        notificationSwitch.setTitle("Notifications");
        notificationSwitch.setSummary("Notify me when BLAST sequence searches have finished");
        inlinePrefCat.addPreference(notificationSwitch);
        

        return preferencesScreen;
		
	}
	
}
