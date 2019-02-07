package cc.zsakvo.yueduhchelper.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

@SuppressWarnings("ALL")
public class DirChooseUtil extends DialogFragment implements android.content.DialogInterface.OnClickListener {
    private cc.zsakvo.yueduhchelper.utils.DirChooseUtil.SimpleFileChooserListener simpleFileChooserListener;
    private String rootPath, currentPath;
    private ArrayList<String> dirs;
    private ArrayAdapter<String> adapter;
    private ListView lvDirs;
    private boolean showHidden = true;

    public interface SimpleFileChooserListener {
        void onFileChosen(File file);
        void onDirectoryChosen(File directory);
        void onCancel();
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        currentPath = rootPath = rootPath == null ? Environment.getExternalStorageDirectory().getAbsolutePath() : rootPath;

        try {
            rootPath = new File(rootPath).getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dirs = getFilesInDirectory(rootPath);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_single_choice, dirs);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setSingleChoiceItems(adapter, -1, this).setPositiveButton(android.R.string.ok, this).setNegativeButton(android.R.string.cancel, this).setTitle(getDirectoryName());

        AlertDialog alert = builder.create();
        lvDirs = alert.getListView();

        return alert;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        File current = new File(currentPath);

        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                if (current.isDirectory())
                    simpleFileChooserListener.onDirectoryChosen(current);

                if (current.isFile())
                    simpleFileChooserListener.onFileChosen(current);
                break;
            case Dialog.BUTTON_NEGATIVE:
                simpleFileChooserListener.onCancel();
                break;
            default:
                selectFile(which);
                break;
        }
    }

    // selects new file or directory
    private void selectFile(int which) {
        File current = new File(currentPath);

        try {
            current.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String selected = dirs.get(which).replace("/", "");

        if (selected.equals("..")) {
            currentPath = currentPath.substring(0, currentPath.lastIndexOf("/"));
            refreshListView();
        } else {
            currentPath += "/" + selected;
            current = new File(currentPath);

            if (!current.isFile())
                refreshListView();
        }

        if (!current.isFile())
            getDialog().setTitle(getDirectoryName());
    }

    // updates listview's items
    private void refreshListView() {
        dirs.clear();
        dirs.addAll(getFilesInDirectory(currentPath));
        adapter.notifyDataSetChanged();

        if (!new File(currentPath).isFile())
            for (int i = 0; i < lvDirs.getCount(); i++)
                lvDirs.setItemChecked(i, false);
    }

    private String getDirectoryName() {
        return currentPath.substring(currentPath.lastIndexOf("/"));
    }

    // gets all files ascending in current directory
    private ArrayList<String> getFilesInDirectory(String dir) {
        ArrayList<String> dirs = new ArrayList<>();

        try {
            File currentDir = new File(dir);

            if (!dir.equals(rootPath))
                dirs.add("..");

            if (!currentDir.exists() || !currentDir.isDirectory())
                return dirs;

            for (File file : currentDir.listFiles()) {
                if (!showHidden && file.isHidden())
                    continue;

                if (file.isDirectory())
                    dirs.add(file.getName() + "/");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.sort(dirs, new Comparator<String>(){
            public int compare(String s1, String s2){
                return s1.toLowerCase().compareTo(s2.toLowerCase());
            }});

        return dirs;    }

    public void setOnChosenListener(cc.zsakvo.yueduhchelper.utils.DirChooseUtil.SimpleFileChooserListener sfc) {
        simpleFileChooserListener = sfc;
    }

    // show/hide hidden files/directories
    // default = true
    public void setShowHidden(boolean showHidden) {
        this.showHidden = showHidden;
    }

    // set default root directory path
    // default = external storage directory
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
}
