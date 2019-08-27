package cc.zsakvo.yueduassistant.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import cc.zsakvo.yueduassistant.R;

public class AboutFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener{

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.about);

        findPreference("author_id").setOnPreferenceClickListener(this);
        findPreference("project_git").setOnPreferenceClickListener(this);
        findPreference("andPermission").setOnPreferenceClickListener(this);
        findPreference("materialPreference").setOnPreferenceClickListener(this);
        findPreference("simpleMenu").setOnPreferenceClickListener(this);
        findPreference("fastJson").setOnPreferenceClickListener(this);
        findPreference("rxAndroid").setOnPreferenceClickListener(this);
        findPreference("rxJava").setOnPreferenceClickListener(this);
        findPreference("fab").setOnPreferenceClickListener(this);
        findPreference("zt-zip").setOnPreferenceClickListener(this);
        findPreference("logger").setOnPreferenceClickListener(this);
        findPreference("brvah").setOnPreferenceClickListener(this);
        findPreference("fastScroll").setOnPreferenceClickListener(this);

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "author_id":
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.coolapk.com/u/522069")));
            break;
            case "project_git":
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/zsakvo/YueduAssistant")));
                break;
            case "materialPreference":
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/RikkaW/MaterialPreference")));
                break;
            case "simpleMenu":
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/RikkaW/MaterialPreference")));
                break;
            case "fastJson":
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/alibaba/fastjson")));
                break;
            case "rxAndroid":
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/ReactiveX/RxAndroid")));
                break;
            case "rxJava":
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/ReactiveX/RxJava")));
                break;
            case "andPermission":
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/yanzhenjie/AndPermission")));
                break;
            case "fab":
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/leinardi/FloatingActionButtonSpeedDial")));
                break;
            case "zt-zip":
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/zeroturnaround/zt-zip")));
                break;
            case "brvah":
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/orhanobut/logger")));
                break;
            case "logger":
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/CymChad/BaseRecyclerViewAdapterHelper")));
                break;
            case "fastScroll":
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/timusus/RecyclerView-FastScroll")));
                break;
        }
        return false;
    }

}
