package cav.vopros;

import android.os.Bundle;
import android.preference.PreferenceActivity;


/**
 * Created by Kotov Alexandr on 27.02.17.
 *
 */
public class SettingActivty extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO переделать потом на фрагмент
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
    }
}
