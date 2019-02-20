package cc.zsakvo.yueduhchelper.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import cc.zsakvo.yueduhchelper.R;
import moe.shizuku.preference.Preference;
import moe.shizuku.preference.PreferenceFragment;

public class ShowLibsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.libs, null);

        Preference appCompat = findPreference("appCompat");
        appCompat.setOnPreferenceClickListener(this);
        Preference material = findPreference("material");
        material.setOnPreferenceClickListener(this);
        Preference supportV4 = findPreference("supportV4");
        supportV4.setOnPreferenceClickListener(this);
        Preference materialPreference = findPreference("materialPreference");
        materialPreference.setOnPreferenceClickListener(this);
        Preference simpleMenu = findPreference("simpleMenu");
        simpleMenu.setOnPreferenceClickListener(this);
        Preference fastJson = findPreference("fastJson");
        fastJson.setOnPreferenceClickListener(this);
        Preference whatsNew = findPreference("whatsNew");
        whatsNew.setOnPreferenceClickListener(this);
        Preference rxAndroid = findPreference("rxAndroid");
        rxAndroid.setOnPreferenceClickListener(this);
        Preference rxJava = findPreference("rxJava");
        rxJava.setOnPreferenceClickListener(this);
        Preference glide = findPreference("glide");
        glide.setOnPreferenceClickListener(this);
        Preference andPermission = findPreference("andPermission");
        andPermission.setOnPreferenceClickListener(this);
        Preference fab = findPreference("fab");
        fab.setOnPreferenceClickListener(this);
        Preference jsoup = findPreference("jsoup");
        jsoup.setOnPreferenceClickListener(this);
        Preference ztZip = findPreference("zt-zip");
        ztZip.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "appCompat":
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://developer.android.com/topic/libraries/support-library/packages")));
                break;
            case "material":
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/material-components/material-components-android")));
                break;
            case "supportV4":
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://developer.android.com/topic/libraries/support-library/packages")));
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
            case "whatsNew":
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/BalestraPatrick/WhatsNew")));
                break;
            case "rxAndroid":
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/ReactiveX/RxAndroid")));
                break;
            case "rxJava":
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/ReactiveX/RxJava")));
                break;
            case "glide":
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/bumptech/glide")));
                break;
            case "andPermission":
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/yanzhenjie/AndPermission")));
                break;
            case "fab":
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/leinardi/FloatingActionButtonSpeedDial")));
                break;
            case "jsoup":
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://jsoup.org/")));
                break;
            case "zt-zip":
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/zeroturnaround/zt-zip")));
                break;
            default:
                break;

        }
        return false;
    }
}
