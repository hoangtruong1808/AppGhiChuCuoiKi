package com.example.appghichucuoiki.adapters;

import android.content.Context;
import android.media.Image;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bigexcersice.R;
import com.example.bigexcersice.adapters.event.OnNoiDungDanhMucItemListener;
import com.example.bigexcersice.model.NoiDungDanhMuc;

import java.util.List;

public class NoiDungDanhMucAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<NoiDungDanhMuc> danhMucList;
    private OnNoiDungDanhMucItemListener onNoiDungDanhMucItemListener;

    public NoiDungDanhMucAdapter(Context context, int layout, List<NoiDungDanhMuc> congViecList, OnNoiDungDanhMucItemListener onNoiDungDanhMucItemListener) {
        this.context = context;
        this.layout = layout;
        this.danhMucList = congViecList;
        this.onNoiDungDanhMucItemListener = onNoiDungDanhMucItemListener;
    }


    @Override
    public int getCount() {
        return danhMucList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        EditText contentDM;
        CheckBox checkDM;
        ImageView imgDelete;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, null);
            holder.contentDM = view.findViewById(R.id.id_content_danh_muc_edt);
            holder.checkDM = view.findViewById(R.id.id_check_danh_muc_chb);
            holder.imgDelete = view.findViewById(R.id.id_delete_NDDM_imgView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        NoiDungDanhMuc cv = danhMucList.get(position);
        holder.contentDM.setText(cv.getContent());
        if(cv.isCheck()){
            holder.checkDM.setChecked(true);
        }else holder.checkDM.setChecked(false);
        //  holder.txtTen.setText(cv.getTenCV());
        //  holder.txtNgayTao.setText(cv.getNgayTao());
        // bắt sự kiện xoá - sửa
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNoiDungDanhMucItemListener.onImageViewDelete(position,danhMucList.get(position).getId());
            }
        });
        holder.checkDM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(holder.checkDM.isChecked()){
                    onNoiDungDanhMucItemListener.updateCheckFromCheckBox(position,danhMucList.get(position).getId());
                }else{
                    onNoiDungDanhMucItemListener.updateUnCheckFromCheckBox(position,danhMucList.get(position).getId());
                }
            }
        });
        holder.contentDM.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    onNoiDungDanhMucItemListener.updateFromEditTextNoiDungDanhmuc(position,danhMucList.get(position).getId(),holder.contentDM.getText().toString());
                }
            }
        });



        return view;
    }
}

