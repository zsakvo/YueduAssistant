package cc.zsakvo.yueduassistant.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import java.util.Objects;

import cc.zsakvo.yueduassistant.R;
import cc.zsakvo.yueduassistant.listener.PathListener;
import cc.zsakvo.yueduassistant.utils.DialogUtil;
import cc.zsakvo.yueduassistant.utils.SpUtil;
import cc.zsakvo.yueduassistant.view.SettingsActivity;
import moe.shizuku.preference.BuildConfig;
import moe.shizuku.preference.Preference;
import moe.shizuku.preference.PreferenceFragment;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, PathListener {

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

//        backup_path = findPreference("backup_path");
//        backup_path.setOnPreferenceClickListener(this);

        output_path = findPreference("output_path");
        output_path.setOnPreferenceClickListener(this);

        cache_path.setSummary(SpUtil.getCacheDirPath(Objects.requireNonNull(getContext())));
//        backup_path.setSummary(SpUtil.getBackupPath(Objects.requireNonNull(getContext())));
        output_path.setSummary(SpUtil.getOutputPath(Objects.requireNonNull(getContext())));

//        Preference author_id = findPreference("author_id");
//        author_id.setOnPreferenceClickListener(this);
//
//        Preference project_git = findPreference("project_git");
//        project_git.setOnPreferenceClickListener(this);
//
//        Preference libs = findPreference("libs");
//        libs.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "cache_path":
                DialogUtil.fileChoose("cache_path", getContext(),activity.getSupportFragmentManager(),this,SpUtil.getCacheDirPath(Objects.requireNonNull(getContext())));
                break;
            case "backup_path":
                DialogUtil.fileChoose("backup_path", getContext(),activity.getSupportFragmentManager(),this,null);
                break;
            case "output_path":
                DialogUtil.fileChoose("output_path", getContext(),activity.getSupportFragmentManager(),this,SpUtil.getOutputPath(Objects.requireNonNull(getContext())));
                break;
            case "author_id":
                Intent intent_id = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.coolapk.com/u/522069"));
                startActivity(intent_id);
                break;
            case "project_git":
                Intent intent_code = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/zsakvo/YueDuHcHelper"));
                startActivity(intent_code);
                break;
            case "libs":
//                startActivity(new Intent(getActivity(),ShowLibsActivity.class));
                break;
        }
        return false;
    }

    @Override
    public void changePath(String str, String path) {
        switch (str) {
            case "cache_path":
                cache_path.setSummary(path);
                break;
            case "backup_path":
                backup_path.setSummary(path);
                break;
            case "output_path":
                output_path.setSummary(path);
                break;
        }
    }
}
