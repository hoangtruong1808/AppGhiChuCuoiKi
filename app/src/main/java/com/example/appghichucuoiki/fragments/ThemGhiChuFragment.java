package com.example.appghichucuoiki.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bigexcersice.R;
import com.example.bigexcersice.database.Database;
import com.example.bigexcersice.database.SaveData;
import com.example.bigexcersice.model.CongViec;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.SimpleDateFormat;

public class ThemGhiChuFragment extends Fragment {
    Database database;
    EditText edtTitle, edtContent;
    Button btnThem;
    View them_ghi_chu_fragment;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        them_ghi_chu_fragment = inflater.inflate(R.layout.them_ghi_chu_fragment, container, false);
        anhxa();
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView adView = new AdView(getActivity());
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(String.valueOf(R.string.adsID));
        adView = them_ghi_chu_fragment.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        database = new Database(getActivity(), SaveData.databaseName,null,1);
        EventOnButtonThem();
        return them_ghi_chu_fragment;
    }
    private void anhxa(){
        edtTitle = them_ghi_chu_fragment.findViewById(R.id.id_titleGhiChu_edt);
        edtContent = them_ghi_chu_fragment.findViewById(R.id.id_contentGhiChu_edt);
        btnThem = them_ghi_chu_fragment.findViewById(R.id.id_themGhiChu_btn);
    }
    private void EventOnButtonThem(){
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtTitle.getText().toString().equals("")){
                    edtTitle.setError("Nhập tiêu đề");
                    edtTitle.requestFocus();
                }else if(edtContent.getText().toString().equals("")){
                    edtContent.setError("Nhập nội dung");
                    edtContent.requestFocus();
                }else{
                    String tenCV = edtTitle.getText().toString();
                    String ndCV = edtContent.getText().toString();
                    java.util.Date utilDate = new java.util.Date();
                    //java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                    SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    database.QueryData("INSERT INTO CongViec VALUES(null, '"+tenCV+"', '"+ndCV+"', '"+sf.format(utilDate)+"', "+getProfileLogin()+")");
                    Toast.makeText(getActivity(),"Thêm Thành Công",Toast.LENGTH_SHORT).show();
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new DanhSachGhiChuFragment()).commit();
                }
            }
        });
    }
    private String getProfileLogin() {
        SharedPreferences pre = getActivity().getSharedPreferences("ID_USER", Context.MODE_PRIVATE);
        String id = pre.getString("id", "");
        return id;
    }
}