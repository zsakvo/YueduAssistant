package cc.zsakvo.yueduhchelper.view;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import cc.zsakvo.yueduhchelper.BuildConfig;
import cc.zsakvo.yueduhchelper.R;
import cc.zsakvo.yueduhchelper.listener.ChangePathListener;
import cc.zsakvo.yueduhchelper.utils.DialogUtil;
import moe.shizuku.preference.Preference;
import moe.shizuku.preference.PreferenceFragment;

import static android.content.Context.MODE_PRIVATE;


public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, ChangePathListener {

    private SettingsActivity activity;
    private Preference cs_cache;
    private Preference cs_backup;
    private Preference cs_out;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setDefaultPackages(new String[]{BuildConfig.APPLICATION_ID + "."});
        getPreferenceManager().setSharedPreferencesName("settings");
        getPreferenceManager().setSharedPreferencesMode(MODE_PRIVATE);
        setPreferencesFromResource(R.xml.settings, null);

        activity = (SettingsActivity) getActivity();

        cs_cache = findPreference("cs_cache");
        cs_cache.setOnPreferenceClickListener(this);

        cs_backup = findPreference("cs_backup");
        cs_backup.setOnPreferenceClickListener(this);

        cs_out = findPreference("cs_out");
        cs_out.setOnPreferenceClickListener(this);

        cs_cache.setSummary(activity.getSharedPreferences("settings", MODE_PRIVATE).getString("cachePath", Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.gedoor.monkeybook/cache/book_cache"));
        cs_backup.setSummary(activity.getSharedPreferences("settings", MODE_PRIVATE).getString("backupPath", Environment.getExternalStorageDirectory().getAbsolutePath() + "/YueDu"));
        cs_out.setSummary(activity.getSharedPreferences("settings", MODE_PRIVATE).getString("outPath", Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents/YueDuTXT"));


        Preference ab_id = findPreference("ab_id");
        ab_id.setOnPreferenceClickListener(this);

        Preference ab_code = findPreference("ab_code");
        ab_code.setOnPreferenceClickListener(this);

        Preference ab_libs = findPreference("ab_libs");
        ab_libs.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "cs_cache":
                DialogUtil.fileChoose("cachePath", getContext(),activity.getSupportFragmentManager(),this);
                break;
            case "cs_out":
                DialogUtil.fileChoose("outPath", getContext(),activity.getSupportFragmentManager(),this);
                break;
            case "cs_backup":
                DialogUtil.fileChoose("backupPath", getContext(),activity.getSupportFragmentManager(),this);
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
            case "ab_libs":
                startActivity(new Intent(getActivity(),ShowLibsActivity.class));
                break;
        }
        return false;
    }

    @Override
    public void changePath(String str, String path) {
        switch (str) {
            case "cachePath":
                cs_cache.setSummary(path);
                break;
            case "backupPath":
                cs_backup.setSummary(path);
                break;
            case "outPath":
                cs_out.setSummary(path);
                break;
        }
    }

}
