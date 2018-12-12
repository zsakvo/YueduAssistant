package cc.zsakvo.yueduhchelper;

import android.os.Bundle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.zsakvo.yueduhchelper.listener.SyncChaptersListener;
import cc.zsakvo.yueduhchelper.preference.CheckBoxPreference;
import cc.zsakvo.yueduhchelper.task.SyncChapters;
import moe.shizuku.preference.Preference;
import moe.shizuku.preference.PreferenceCategory;
import moe.shizuku.preference.PreferenceFragment;
import moe.shizuku.preference.PreferenceScreen;

import static android.content.Context.MODE_PRIVATE;

public class TextExportFragment extends PreferenceFragment implements SyncChaptersListener, Preference.OnPreferenceChangeListener {

    private TextExportActivity activity;
    private List<String> cacheFilePath;
    private Boolean[] checkedChapters;
    private List<String> list;
    private String bookPath;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setDefaultPackages(new String[]{BuildConfig.APPLICATION_ID + "."});
        getPreferenceManager().setSharedPreferencesName("checkbox");
        getPreferenceManager().setSharedPreferencesMode(MODE_PRIVATE);
        setPreferencesFromResource(R.xml.books_cache, null);
        activity = (TextExportActivity) getActivity();
        assert activity != null;
        this.cacheFilePath = activity.getBookInfo();
    }

    void init() {
        new SyncChapters(this).execute(cacheFilePath);
    }

    @Override
    public void showChapters(List<File> cacheFiles, List<Boolean> isDum) {
//        this.list = list;
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removeAll();
        PreferenceCategory preferenceCategory = new PreferenceCategory(activity);
        preferenceCategory.setTitle(activity.getBookName() + "\t\t共" + cacheFiles.size() + "章");
        preferenceScreen.addPreference(preferenceCategory);
        checkedChapters = new Boolean[cacheFiles.size()];
        for (int i = 0; i < cacheFiles.size(); i++) {
            String[] a = cacheFiles.get(i).getName().split("/");
            String[] b = a[a.length - 1].split("-");
            String c = a[a.length - 1].replace(b[0] + "-", "").replace(".nb", "");
            CheckBoxPreference checkBoxPreference = new CheckBoxPreference(activity);
            checkBoxPreference.setTitle(c);
            checkBoxPreference.setChecked(isDum.get(i));
            checkBoxPreference.setKey(i + "");
            checkedChapters[i] = true;
            checkBoxPreference.setOnPreferenceChangeListener(this);
            preferenceScreen.addPreference(checkBoxPreference);
        }
        activity.initMenuItems();
    }

    int getSize() {
        return checkedChapters.length;
    }

    void chooseChapters(int a, int b) {
        for (int i = 0; i < checkedChapters.length; i++) {
            CheckBoxPreference cbp = (CheckBoxPreference) getPreferenceScreen().getPreference(i + 1);
            cbp.setChecked(false);
            checkedChapters[i] = false;
        }
        for (int i = 0; a + i <= b; i++) {
            CheckBoxPreference cbp = (CheckBoxPreference) getPreferenceScreen().getPreference(a + i);
            cbp.setChecked(true);
            checkedChapters[a + i - 1] = true;
        }
    }

    void chooseInvert() {
        for (int i = 0; i < checkedChapters.length; i++) {
            Boolean b = checkedChapters[i];
            CheckBoxPreference cbp = (CheckBoxPreference) getPreferenceScreen().getPreference(i + 1);
            cbp.setChecked(!b);
            checkedChapters[i] = !b;
        }
    }

    ArrayList<String> getChapters() {
        ArrayList<String> chapters = new ArrayList<>();
        for (int i = 0; i < this.list.size(); i++) {
            if (checkedChapters[i]) chapters.add(list.get(i));
        }
        return chapters;
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int i = Integer.parseInt(preference.getKey());
        checkedChapters[i] = !checkedChapters[i];
        CheckBoxPreference cbp = (CheckBoxPreference) preference;
        cbp.setChecked(!cbp.isChecked());
        return false;
    }

}
