package com.example.appghichucuoiki.adapters.event;

import android.view.View;

public interface OnGhiChuItemTouchListener {
    public void onButtonEditGhiChu(View view, int position, int ghiChuID);
    public void onButtonDeleteGhiChu(View view, int position, int ghiChuID);
    public void onButtonLoadMore(View view, int position, int ghiChuID);
}
