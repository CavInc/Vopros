package cav.vopros.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.List;
/**
 * orginal Copyright (c) 2013-2014 Sergey Antonov
 */

public class OpenFileDialog  extends AlertDialog.Builder{
    private String currentPath = Environment.getExternalStorageDirectory().getPath();
    private TextView title;

    public OpenFileDialog(Context context) {
        super(context);
        setPositiveButton(android.R.string.ok, null)
                .setItems(getFiles(currentPath), null)
                .setNegativeButton(android.R.string.cancel, null);
    }

    private String[] getFiles(String directoryPath){
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        String[] result = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                result[i] = files[i].getName();
            }
        }
        return result;
    }

    private class FileAdapter extends ArrayAdapter<File> {
        public FileAdapter(Context context, List<File> files) {
            super(context, android.R.layout.simple_list_item_1, files);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            File file = getItem(position);
            view.setText(file.getName());
            return view;
        }
    }
}
