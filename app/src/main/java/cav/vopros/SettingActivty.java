package cav.vopros;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import cav.vopros.utils.OpenFileDialog;


/**
 * Created by Kotov Alexandr on 27.02.17.
 *
 */
public class SettingActivty extends PreferenceActivity {
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
                Toast.makeText(getBaseContext(),
                        "The custom preference has been clicked",
                        Toast.LENGTH_LONG).show();

                OpenFileDialog fileDialog = new OpenFileDialog(mContext);
                fileDialog.show();
                return true;
            }
        });

        /*
        opendialog.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Toast.makeText(getBaseContext(),
                        "The custom preference has been clicked"+newValue,
                        Toast.LENGTH_LONG).show();
                return true;
            }
        });
        */

    }
}
