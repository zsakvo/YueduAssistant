package cc.zsakvo.yueduassistant.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.Objects;

import cc.zsakvo.yueduassistant.R;
import cc.zsakvo.yueduassistant.listener.PathListener;
import cc.zsakvo.yueduassistant.utils.DialogUtil;
import cc.zsakvo.yueduassistant.utils.SourceUtil;
import cc.zsakvo.yueduassistant.utils.SpUtil;
import cc.zsakvo.yueduassistant.view.SettingsActivity;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, PathListener {

    private SettingsActivity activity;
    private Preference cache_path;
    private Preference backup_path;
    private Preference output_path;
    private Preference update_source;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);
        activity = (SettingsActivity) getActivity();
        cache_path = findPreference("cache_path");
        cache_path.setOnPreferenceClickListener(this);
        output_path = findPreference("output_path");
        output_path.setOnPreferenceClickListener(this);
        update_source = findPreference("update_source");
        update_source.setOnPreferenceClickListener(this);
        cache_path.setSummary(SpUtil.getCacheDirPath(Objects.requireNonNull(getContext())));
        output_path.setSummary(SpUtil.getOutputPath(Objects.requireNonNull(getContext())));
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
            case "update_source":
                SourceUtil.update(activity.getSupportFragmentManager(),getContext(),activity.findViewById(R.id.toolbar));
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
