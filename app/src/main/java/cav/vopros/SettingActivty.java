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
                OpenFileDialog fileDialog = new OpenFileDialog(mContext);
                fileDialog.show();
                return true;
            }
        });

    }
    //http://1-vopros-1-otvet.ru/how-to-get-time-from-timepicker-dialog-to-preference-activity-33969293/
    //http://ru.androids.help/q21986
}
