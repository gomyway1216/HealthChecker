package com.yudaiyaguchi.HealthChecker.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.yudaiyaguchi.HealthChecker.R;
import com.yudaiyaguchi.HealthChecker.Settings.Setting;
import com.yudaiyaguchi.HealthChecker.Settings.SettingsPrefActivity;

/**
 * This Activity is responsible to set top bar and inherited by activity that needs to set top bar
 */
public class BaseActivity extends AppCompatActivity {

    /**
     * This add xml file to top bar.
     *
     * @param savedInstanceState : contains setting data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_notification, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_data_sync, false);
    }


    /**
     * Set the top bar.
     *
     * @param layoutResID : ID of layout resource
     */
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * Inflate menu.
     *
     * @param menu : menu on the top bar
     * @return success of inflating
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.quiz_menu, menu);
        return true;
    }

    /**
     * Lister for selected item on top bar.
     * @param item : selected item
     * @return success of lister for selected menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.home:
                Intent homeIntent = new Intent(BaseActivity.this, WelcomeActivity.class);
                startActivity(homeIntent);
                return true;

            // send email
            case R.id.contact:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","info@kinetikos.io", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "From application");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
                return true;

            case R.id.setting:
                Intent settingIntent = new Intent(BaseActivity.this, Setting.class);
                startActivity(settingIntent);
                return true;

            case R.id.action_settings:
                Intent settingPrefIntent = new Intent(BaseActivity.this, SettingsPrefActivity.class);
                startActivity(settingPrefIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}