package cc.zsakvo.yueduhchelper;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.AppCompatButton;
import cc.zsakvo.yueduhchelper.listener.SyncChaptersListener;
import cc.zsakvo.yueduhchelper.preference.CheckBoxPreference;
import cc.zsakvo.yueduhchelper.task.SyncChapters;
import moe.shizuku.preference.Preference;
import moe.shizuku.preference.PreferenceCategory;
import moe.shizuku.preference.PreferenceFragment;
import moe.shizuku.preference.PreferenceScreen;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class TextExportFragment extends PreferenceFragment implements SyncChaptersListener ,Preference.OnPreferenceChangeListener {

    private TextExportActivity activity;
    private String bookInfo = "";
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
        this.bookInfo = activity.getBookInfo();
        bookPath = activity.getSharedPreferences("settings",MODE_PRIVATE).getString("cachePath",Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Android/data/com.gedoor.monkeybook/cache/book_cache")+"/"+bookInfo;

    }

    public void init(){
        new SyncChapters(this).execute(bookPath);
    }

    @Override
    public void showChapters(List<String> list) {
        this.list = list;
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removeAll();
        PreferenceCategory preferenceCategory = new PreferenceCategory(activity);
        preferenceCategory.setTitle(bookInfo.split("-")[0]+"\t\t共"+list.size()+"章");
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
            checkBoxPreference.setOnPreferenceChangeListener(this);
            preferenceScreen.addPreference(checkBoxPreference);
        }
        activity.initMenuItems();
    }

    int getSize(){
        return checkedChapters.length;
    }

    void chooseChapters(int a, int b){
        for (int i=0;i<checkedChapters.length;i++){
            CheckBoxPreference cbp = (CheckBoxPreference)getPreferenceScreen().getPreference(i+1);
            cbp.setChecked(false);
            checkedChapters[i] = false;
        }
        for (int i=0;a+i<=b;i++){
            CheckBoxPreference cbp = (CheckBoxPreference)getPreferenceScreen().getPreference(a+i);
            cbp.setChecked(true);
            checkedChapters[a+i-1] = true;
        }
    }

    void chooseInvert(){
        for (int i=0;i<checkedChapters.length;i++){
            Boolean b = checkedChapters[i];
            CheckBoxPreference cbp = (CheckBoxPreference)getPreferenceScreen().getPreference(i+1);
            cbp.setChecked(!b);
            checkedChapters[i] = !b;
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
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int i = Integer.parseInt(preference.getKey());
        checkedChapters[i] = !checkedChapters[i];
        CheckBoxPreference cbp = (CheckBoxPreference)preference;
        cbp.setChecked(!cbp.isChecked());
        return false;
    }

}
