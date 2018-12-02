package cc.zsakvo.yueduhchelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.util.Objects;

import moe.shizuku.preference.Preference;
import moe.shizuku.preference.PreferenceFragment;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    SettingsActivity activity;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setDefaultPackages(new String[]{BuildConfig.APPLICATION_ID + "."});
        getPreferenceManager().setSharedPreferencesName("settings");
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);
        setPreferencesFromResource(R.xml.settings, null);

        activity = (SettingsActivity) getActivity();

        Preference cs_cache = (Preference) findPreference("cs_cache");
        cs_cache.setOnPreferenceClickListener(this);

        Preference cs_out = (Preference) findPreference("cs_out");
        cs_out.setOnPreferenceClickListener(this);

        Preference ab_id = (Preference) findPreference("ab_id");
        ab_id.setOnPreferenceClickListener(this);

        Preference ab_code = (Preference) findPreference("ab_code");
        ab_code.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()){
            case "cs_cache":
                assert activity != null;
                activity.fileChoose("cachePath");
                break;
            case "cs_out":
                assert activity != null;
                activity.fileChoose("outPath");
                break;
            case "ab_id":
                Intent intent_id = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.coolapk.com/u/522069"));
                startActivity(intent_id);
                break;
            case "ab_code":
                Intent intent_code = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/zsakvo/YueDuHcHelper"));
                startActivity(intent_code);
                break;
        }
        return false;
    }
}
