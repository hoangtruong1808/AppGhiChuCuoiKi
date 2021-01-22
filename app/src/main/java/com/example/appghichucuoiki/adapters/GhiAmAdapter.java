package com.example.appghichucuoiki.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bigexcersice.R;
import com.example.bigexcersice.adapters.event.OnGhiAmItemListener;
import com.example.bigexcersice.adapters.event.TimeAgo;

import java.io.File;

public class GhiAmAdapter extends BaseAdapter {
    private Context context;
    private File[] allFiles;
    private int layout;
    private OnGhiAmItemListener onGhiAmItemListener;
    private TimeAgo timeAgo;


    public GhiAmAdapter(Context context, File[] allFiles, int layout, OnGhiAmItemListener onGhiAmItemListener) {
        this.context = context;
        this.allFiles = allFiles;
        this.layout = layout;
        this.onGhiAmItemListener = onGhiAmItemListener;
    }

    @Override
    public int getCount() {
        return allFiles.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class ViewHolder {
        ImageView list_image;
        TextView list_title, list_date;
        ImageButton delete_file;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, null);
            timeAgo= new TimeAgo();
            holder.list_image = view.findViewById(R.id.list_image_view);
            holder.list_title = view.findViewById(R.id.list_title);
            holder.list_date = view.findViewById(R.id.list_date);
            holder.delete_file = view.findViewById(R.id.id_delete_file_imgBtn);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.list_title.setText(allFiles[position].getName());
        holder.list_date.setText(timeAgo.getTimeAgo(allFiles[position].lastModified()));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGhiAmItemListener.onClickListener(allFiles[position], position);
            }
        });
        holder.delete_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGhiAmItemListener.onDeleteFileRecord(allFiles[position],position);
            }
        });

        /*holder.list_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });*/
       /* holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDanhMucItemListener.onButtonDeleteDM(position, listDanhMuc.get(position).getId());
            }
        });*/
        return view;
    }
}
