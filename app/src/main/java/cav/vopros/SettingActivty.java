package cav.vopros;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;


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

}
