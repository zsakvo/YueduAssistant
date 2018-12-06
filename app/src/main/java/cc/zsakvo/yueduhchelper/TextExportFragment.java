package cc.zsakvo.yueduhchelper;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.AppCompatButton;
import cc.zsakvo.yueduhchelper.listener.SyncChaptersListener;
import cc.zsakvo.yueduhchelper.task.SyncChapters;
import moe.shizuku.preference.CheckBoxPreference;
import moe.shizuku.preference.Preference;
import moe.shizuku.preference.PreferenceCategory;
import moe.shizuku.preference.PreferenceFragment;
import moe.shizuku.preference.PreferenceScreen;

import static android.content.Context.MODE_PRIVATE;

public class TextExportFragment extends PreferenceFragment implements SyncChaptersListener ,Preference.OnPreferenceClickListener {

    TextExportActivity activity;
    private PreferenceScreen preferenceScreen;
    private String bookInfo = "";
    private Boolean[] checkedChapters;
    private List<String> list;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setDefaultPackages(new String[]{BuildConfig.APPLICATION_ID + "."});
        getPreferenceManager().setSharedPreferencesName("checkbox");
        getPreferenceManager().setSharedPreferencesMode(MODE_PRIVATE);
        setPreferencesFromResource(R.xml.books_cache, null);
        activity = (TextExportActivity) getActivity();
        assert activity != null;
        this.bookInfo = activity.getBookInfo();
        String bookPath = activity.getSharedPreferences("settings",MODE_PRIVATE).getString("cachePath",Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Android/data/com.gedoor.monkeybook/cache/book_cache")+"/"+bookInfo;
        new SyncChapters(this).execute(bookPath);
    }

    @Override
    public void showChapters(List<String> list) {
        this.list = list;
        preferenceScreen = getPreferenceScreen();
        preferenceScreen.removeAll();
        PreferenceCategory preferenceCategory = new PreferenceCategory(activity);
        preferenceCategory.setTitle(bookInfo.split("-")[0]+"，共"+list.size()+"章");
        preferenceScreen.addPreference(preferenceCategory);
        checkedChapters = new Boolean[list.size()];
        for (int i=0;i<list.size();i++){
            String[] a = list.get(i).split("/");
            String[] b = a[a.length-1].split("-");
            String c = a[a.length-1].replace(b[0]+"-","").replace(".nb","");
            CheckBoxPreference checkBoxPreference = new CheckBoxPreference(activity);
            checkBoxPreference.setTitle(c);
            checkBoxPreference.setChecked(true);
            checkBoxPreference.setKey(i+"");
            checkedChapters[i] = true;
            checkBoxPreference.setOnPreferenceClickListener(this);
            preferenceScreen.addPreference(checkBoxPreference);
        }
    }

    ArrayList<String> getChapters(){
        ArrayList<String> chapters = new ArrayList<>();
        for (int i=0;i<this.list.size();i++){
            if (checkedChapters[i]) chapters.add(list.get(i));
        }
        return chapters;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        int i = Integer.parseInt(preference.getKey());
        checkedChapters[i] = !checkedChapters[i];
        return false;
    }
}
