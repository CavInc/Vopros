package cav.vopros;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;


import cav.vopros.utils.ConstantManager;
import cav.vopros.utils.OpenFileDialog;


/**
 * Created by Kotov Alexandr on 27.02.17.
 *
 */

public class SettingActivty extends PreferenceActivity  {
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO переделать потом на фрагмент
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);

        mContext = (Context) this;

        Preference opendialog = (Preference) findPreference("path_to_img");

        opendialog.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                OpenFileDialog fileDialog = new OpenFileDialog(mContext)
                        .setFilter(".*\\.jpg")
                        .setOpenDialogListener(new OpenFileDialog.OpenDialogListener(){
                            @Override
                            public void OnSelectedFile(String fileName) {
                                /*
                                File x = new File(fileName);
                                if (x.isFile()){
                                    Log.d("SETTING",x.getParent());
                                    SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(mContext);
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString(ConstantManager.PREF_IMAGE_PATH,x.getParent());
                                    editor.apply();
                                }
                                */

                            }

                            @Override
                            public void OnSelectedDirectory(String directoryName) {
                                //Toast.makeText(getApplicationContext(), directoryName, Toast.LENGTH_LONG).show();
                                SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(mContext);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString(ConstantManager.PREF_IMAGE_PATH,directoryName);
                                editor.apply();
                            }
                        });
                fileDialog.show();
                return true;
            }
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
