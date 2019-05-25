package cc.zsakvo.yueduassistant.fragment;

import android.os.Bundle;

import cc.zsakvo.yueduassistant.R;
import cc.zsakvo.yueduassistant.view.SettingsActivity;
import moe.shizuku.preference.BuildConfig;
import moe.shizuku.preference.Preference;
import moe.shizuku.preference.PreferenceFragment;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private SettingsActivity activity;
    private Preference cache_path;
    private Preference backup_path;
    private Preference output_path;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setDefaultPackages(new String[]{BuildConfig.APPLICATION_ID + "."});
        getPreferenceManager().setSharedPreferencesName("settings");
        getPreferenceManager().setSharedPreferencesMode(MODE_PRIVATE);
        setPreferencesFromResource(R.xml.settings, null);

        activity = (SettingsActivity) getActivity();

        cache_path = findPreference("cache_path");
        cache_path.setOnPreferenceClickListener(this);

        backup_path = findPreference("backup_path");
        backup_path.setOnPreferenceClickListener(this);

        output_path = findPreference("output_path");
        output_path.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }
}
