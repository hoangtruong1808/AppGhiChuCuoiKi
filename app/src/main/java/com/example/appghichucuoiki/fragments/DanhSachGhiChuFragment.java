package com.example.appghichucuoiki.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bigexcersice.R;
import com.example.bigexcersice.adapters.CongViecAdapter;
import com.example.bigexcersice.adapters.event.OnGhiChuItemTouchListener;
import com.example.bigexcersice.database.Database;
import com.example.bigexcersice.model.CongViec;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;

public class DanhSachGhiChuFragment extends Fragment {
    Database database;
    View danh_sach_ghi_chu_fragment;
    ListView lvCongViec;
    ArrayList<CongViec> congViecArrayList = new ArrayList<>();
    CongViecAdapter congViecAdapter;
    EditText edtTimKiem;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        danh_sach_ghi_chu_fragment = inflater.inflate(R.layout.danh_sach_ghi_chu_fragment, container, false);

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView adView = new AdView(getActivity());
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(String.valueOf(R.string.adsID));
        adView = danh_sach_ghi_chu_fragment.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        lvCongViec = danh_sach_ghi_chu_fragment.findViewById(R.id.id_list_ghichu);
        congViecAdapter = new CongViecAdapter(getActivity(),R.layout.row_congviec_items, congViecArrayList, clickCartView());
        lvCongViec.setAdapter(congViecAdapter);
        edtTimKiem = (EditText) danh_sach_ghi_chu_fragment.findViewById(R.id.edsearch);

        //tạo database
        database = new Database(getActivity(),"ghichu.sqlite",null,1);
       // database.QueryData("DROP TABLE CongViec");
        //tạo bảng công việc
        database.QueryData("CREATE TABLE IF NOT EXISTS CongViec(Id INTEGER PRIMARY KEY AUTOINCREMENT,TenCV NVARCHAR(200), NoiDungCV NVARCHAR(400), NgayTao TEXT, MaAccount INTEGER NOT NULL CONSTRAINT MaAccount REFERENCES Account(TaiKhoan) ON DELETE CASCADE)");
        //Thêm dữ liệu
        java.util.Date utilDate = new java.util.Date();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        System.out.println("Dữ liệu: "+utilDate);
        System.out.println("Dữ liệu: "+sqlDate);
        //database.QueryData("INSERT INTO CongViec VALUES(null,'Xong trang viết ghi chú','Hmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm hổng có gì hết','"+sqlDate+"')");
     //   database.QueryData("INSERT INTO CongViec VALUES(null,'Trang ghi chú công việc','Hmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm hổng có gì hết')");
      //  database.QueryData("INSERT INTO CongViec VALUES(null,'Test nếu quá dài- AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa','Hmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm hổng có gì hết')");
        System.out.println("ĐÂY LÀ ACCOUNT: "+getProfileLogin());
        GetDsGhiChu();
        timKiem();
        return danh_sach_ghi_chu_fragment;
    }
    private void timKiem() {
        edtTimKiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                congViecAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    private String getProfileLogin() {
        SharedPreferences pre = getActivity().getSharedPreferences("ID_USER", Context.MODE_PRIVATE);
        String id = pre.getString("id", "");
        return id;
    }
    private void GetDsGhiChu(){
        congViecArrayList.clear();
        //select
        System.out.println("lấy ds");
        Cursor dataCongViec = database.GetData("SELECT * FROM CongViec WHERE MaAccount = " + getProfileLogin());
        while(dataCongViec.moveToNext()){
            int idCV = dataCongViec.getInt(0);
            String tenCV = dataCongViec.getString(1);
            String ndCV = dataCongViec.getString(2);
            String dateCV = dataCongViec.getString(3);
            CongViec cv = new CongViec(idCV,tenCV,ndCV,dateCV);
            congViecArrayList.add(cv);
            System.out.println("ds lúc này: "+cv);
        }
        congViecAdapter.notifyDataSetChanged();
    }
    private OnGhiChuItemTouchListener clickCartView(){
        OnGhiChuItemTouchListener itemTouchListener = new OnGhiChuItemTouchListener() {
            @Override
            public void onButtonEditGhiChu(View view, int position, int ghiChuID) {
                Cursor dataCongViec = database.GetData("SELECT * FROM CongViec WHERE Id = "+ghiChuID+" AND MaAccount = "+ getProfileLogin());
                CongViec cv = null;
                while(dataCongViec.moveToNext()){
                    int id = dataCongViec.getInt(0);
                    String tenCV = dataCongViec.getString(1);
                    String ndCV = dataCongViec.getString(2);
                    String dateCV = dataCongViec.getString(3);
                    cv = new CongViec(id,tenCV,ndCV,dateCV);
                }
                DialogSuaGhiChu(cv);
            }

            @Override
            public void onButtonDeleteGhiChu(View view, int position, final int ghiChuID) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Xoá ghi chú");
                builder.setMessage("Bạn có muốn xoá ghi chú này?");
                builder.setCancelable(false);
                builder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNegativeButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        database.QueryData("DELETE FROM CongViec WHERE Id = '"+ghiChuID+"' AND MaAccount = "+getProfileLogin());
                        Toast.makeText(getActivity(),"Xoá thành công",Toast.LENGTH_SHORT).show();
                        GetDsGhiChu();
                    }
                });
                builder.show();
            }

            @Override
            public void onButtonLoadMore(View view, int position, int ghiChuID) {
                Cursor dataCongViec = database.GetData("SELECT * FROM CongViec WHERE Id = "+ghiChuID+" AND MaAccount ="+getProfileLogin());
                CongViec cv = null;
                while(dataCongViec.moveToNext()){
                    int id = dataCongViec.getInt(0);
                    String tenCV = dataCongViec.getString(1);
                    String ndCV = dataCongViec.getString(2);
                    String dateCV = dataCongViec.getString(3);

                    cv = new CongViec(id,tenCV,ndCV,dateCV);
                }
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setMessage(cv.getNoiDungCV());
                alert.show();
            }
        };
        return itemTouchListener;
    }
    public void DialogSuaGhiChu(final CongViec cv){
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.sua_ghichu_dialog);

        final EditText edtTitle, edtContent;
        Button btnXacNhan, btnHuy;
        edtTitle = dialog.findViewById(R.id.id_titleSuaGhiChu_edt);
        edtContent = dialog.findViewById(R.id.id_contentSuaGhiChu_edt);
        btnXacNhan = dialog.findViewById(R.id.id_xacNhanSuaGhiChu_btn);
        btnHuy = dialog.findViewById(R.id.id_huySuaGhiChu_btn);
        edtTitle.setText(cv.getTenCV());
        edtContent.setText(cv.getNoiDungCV());

        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titleMoi = edtTitle.getText().toString().trim();
                String contentMoi = edtContent.getText().toString().trim();
                database.QueryData("UPDATE CongViec SET TenCV = '"+titleMoi+"', NoiDungCV = '"+contentMoi+"' WHERE Id = '"+cv.getIdCV()+"'");
                Toast.makeText(getActivity(),"Đã cập nhật", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                GetDsGhiChu();
            }
        });
        dialog.show();
    }
}