package com.example.appghichucuoiki.adapters.event;

import android.view.View;

public interface OnDanhMucItemListener {
    public void onButtonLoadMore(View v, int position, int idDanhMuc, String titleDanhMuc);
    public void onButtonDeleteDM(int position, int idDanhMuc);
}
