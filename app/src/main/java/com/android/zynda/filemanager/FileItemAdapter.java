package com.android.zynda.filemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FileItemAdapter extends ArrayAdapter {

    private final int resourceId;

    public FileItemAdapter(@android.support.annotation.NonNull Context context, int resource, List<FileData> items) {
        super(context, resource,items);
        resourceId=resource;
    }

    @android.support.annotation.NonNull
    @Override
    public View getView(int position, @android.support.annotation.Nullable View convertView, @android.support.annotation.NonNull ViewGroup parent) {
        FileData fd= (FileData) getItem(position);
        View view=convertView==null?LayoutInflater.from(getContext()).inflate(resourceId, null):convertView;
        ImageView iv=view.findViewById(R.id.imgFile);
        TextView tv=view.findViewById(R.id.filename);
        iv.setImageResource(fd.imgId);
        tv.setText(fd.fileName);
        return view;
    }
}
