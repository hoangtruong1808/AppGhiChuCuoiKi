package com.example.appghichucuoiki.adapters.event;


public interface OnNoiDungDanhMucItemListener {
    public void onImageViewDelete(int position, int idNDDM);
    public void updateFromEditTextNoiDungDanhmuc(int positon, int idNDDM, String text);
    public void updateCheckFromCheckBox(int positon, int idNDDM);
    public void updateUnCheckFromCheckBox(int positon, int idNDDM);

}
