package com.example.appghichucuoiki.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.TooltipCompat;
import androidx.fragment.app.Fragment;

import com.example.bigexcersice.R;
import com.example.bigexcersice.adapters.NoiDungDanhMucAdapter;
import com.example.bigexcersice.adapters.event.OnNoiDungDanhMucItemListener;
import com.example.bigexcersice.database.Database;
import com.example.bigexcersice.database.SaveData;
import com.example.bigexcersice.model.NoiDungDanhMuc;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.tooltip.Tooltip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ThemDanhMucFragment extends Fragment {
    View them_danh_muc_fragment;
    ListView lvDanhMuc;
    Button btnThemNoiDung, btnThemDanhMuc;
    ArrayList<NoiDungDanhMuc> listNDDM = new ArrayList<>();
    NoiDungDanhMucAdapter danhMucAdapter;
    EditText edtNoiDungDM, edtTieuDeDM;
    Database database;
    CheckBox chbCheck;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        them_danh_muc_fragment = inflater.inflate(R.layout.them_danh_muc_fragment, container, false);
        anhxa();
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView adView = new AdView(getActivity());
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(String.valueOf(R.string.adsID));
        adView = them_danh_muc_fragment.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        database = new Database(getActivity(), SaveData.databaseName,null,1);
        //database.QueryData("DROP TABLE DSDanhMuc");
       // database.QueryData("DROP TABLE DanhMuc");
        database.QueryData("CREATE TABLE IF NOT EXISTS DanhMuc(Id INTEGER PRIMARY KEY AUTOINCREMENT,TieuDeDM NVARCHAR(200), NgayTao TEXT, MaAccount INTEGER NOT NULL CONSTRAINT MaAccount REFERENCES Account(TaiKhoan) ON DELETE CASCADE)");
        database.QueryData("CREATE TABLE IF NOT EXISTS DSDanhMuc(Id INTEGER PRIMARY KEY AUTOINCREMENT,NoiDung NVARCHAR(200), CheckOption INTEGER, NgayTao TEXT, MaDanhMuc INTEGER NOT NULL CONSTRAINT MaDanhMuc REFERENCES DanhMuc(Id) ON DELETE CASCADE)");
        SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date utilDate = new Date();
        //database.QueryData("INSERT INTO DanhMuc VALUES (null, 'TieuDe1','"+sf.format(utilDate)+"')");
        //database.QueryData("INSERT INTO DSDanhMuc VALUES (null, 'haha',1, '"+sf.format(utilDate)+"', 1)");

        lvDanhMuc = them_danh_muc_fragment.findViewById(R.id.id_danhMuc_listView);
        danhMucAdapter = new NoiDungDanhMucAdapter(getActivity(),R.layout.row_them_danh_muc_items, listNDDM,onNoiDungDanhMucItemListener());
        lvDanhMuc.setAdapter(danhMucAdapter);
        EventOnBtnThemNoiDungDanhMuc();
        EventOnThemDanhMuc();
       // GetDS();
        return them_danh_muc_fragment;
    }
    private void GetDS(){
        Cursor dataCongViec = database.GetData("SELECT * FROM DSDanhMuc where MaDanhMuc = 5");
        while(dataCongViec.moveToNext()){
            int idNoiDung = dataCongViec.getInt(0);
            String tenNoiDung = dataCongViec.getString(1);
            int check = dataCongViec.getInt(2);
            String dateNoiDung = dataCongViec.getString(3);
            int idDM = dataCongViec.getInt(4);
            boolean flag = true;
            if(check ==1 ) flag = true; else if(check==0) flag = false;
            NoiDungDanhMuc nd = new NoiDungDanhMuc(idNoiDung,flag,tenNoiDung, dateNoiDung);
        }
    }
    private OnNoiDungDanhMucItemListener onNoiDungDanhMucItemListener(){
        OnNoiDungDanhMucItemListener onDanhMucItemListener = new OnNoiDungDanhMucItemListener() {

            @Override
            public void onImageViewDelete(int position, int idNDDM) {
                boolean flag = true;
                for (NoiDungDanhMuc noiDungDanhMuc : listNDDM) {
                    if (noiDungDanhMuc.getId() == 0) {
                        Toast.makeText(getActivity(), "Xác nhận nội dung mới!", Toast.LENGTH_LONG).show();
                        flag = false;
                    }
                }
                if (flag == true) {
                    listNDDM.remove(position);
                    danhMucAdapter.notifyDataSetChanged();
                }

            }
            @Override
            public void updateFromEditTextNoiDungDanhmuc(int positon, int idNDDM, String text) {
                NoiDungDanhMuc nddm = listNDDM.get(positon);
                nddm.setContent(text);
            }

            @Override
            public void updateCheckFromCheckBox(int positon, int idNDDM) {
                NoiDungDanhMuc nddm = listNDDM.get(positon);
                nddm.setCheck(true);
            }

            @Override
            public void updateUnCheckFromCheckBox(int positon, int idNDDM) {
                NoiDungDanhMuc nddm = listNDDM.get(positon);
                nddm.setCheck(false);
            }
        };
        return  onDanhMucItemListener;
    }
    private void anhxa(){
        btnThemNoiDung = them_danh_muc_fragment.findViewById(R.id.id_themND_btn);
        btnThemDanhMuc = them_danh_muc_fragment.findViewById(R.id.id_themDM_btn);
        edtNoiDungDM = them_danh_muc_fragment.findViewById(R.id.id_content_danhMuc_edt);
        edtTieuDeDM = them_danh_muc_fragment.findViewById(R.id.id_title_danhMuc_edt);
        chbCheck = them_danh_muc_fragment.findViewById(R.id.id_content_danh_muc_chb);
    }
    private void EventOnBtnThemNoiDungDanhMuc(){
        btnThemNoiDung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtNoiDungDM.getText().toString().equals("")){
                    edtNoiDungDM.requestFocus();
                    edtNoiDungDM.setError("Nhập nội dung");

                }else{
                    Date utilDate = new Date();
                    SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    boolean flag = false;
                    if(chbCheck.isChecked()) flag = true; else flag = false;
                    NoiDungDanhMuc noiDungDanhMuc = new NoiDungDanhMuc(0,flag,edtNoiDungDM.getText().toString(),sf.format(utilDate));
                    listNDDM.add(noiDungDanhMuc);
                    danhMucAdapter.notifyDataSetChanged();
                }
            }
        });
    }
    private String getProfileLogin() {
        SharedPreferences pre = getActivity().getSharedPreferences("ID_USER", Context.MODE_PRIVATE);
        String id = pre.getString("id", "");
        return id;
    }
    private void EventOnThemDanhMuc(){
        btnThemDanhMuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtTieuDeDM.getText().toString().equals("")){
                    edtTieuDeDM.requestFocus();
                    edtTieuDeDM.setError("Nhập tiêu đề");
                }else if(edtNoiDungDM.getText().toString().equals("")){
                    edtNoiDungDM.requestFocus();
                    edtNoiDungDM.setError("Nhập nội dung");
                }else if(listNDDM.size()==0){
                     // Toast.makeText(getActivity(),"Vui lòng chọn thêm nội dung", Toast.LENGTH_LONG).show();
                    Tooltip tooltip = new Tooltip.Builder(btnThemNoiDung).setText("Chọn thêm nội dung")
                            .setTextColor(Color.WHITE).setGravity(Gravity.START).setCornerRadius(8f).setDismissOnClick(true).show();
                } else{
                    SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    Date utilDate = new Date();
                    database.QueryData("INSERT INTO DanhMuc VALUES (null, '"+edtTieuDeDM.getText().toString()+"','"+sf.format(utilDate)+"', "+getProfileLogin()+")");
                    
                    Cursor getLastID = database.GetData("SELECT Id asLastID FROM DanhMuc WHERE MaAccount = "+getProfileLogin()+" ORDER BY Id DESC LIMIT 1");
                    int lastID = 0;
                    while(getLastID.moveToNext()){
                        lastID = getLastID.getInt(0);
                    }
                    for(NoiDungDanhMuc nd : listNDDM){
                        int flag = 0;
                        if(nd.isCheck())  flag = 1; else flag = 0;
                        database.QueryData("INSERT INTO DSDanhMuc VALUES (null, '"+nd.getContent()+"',"+flag+", '"+nd.getNgayTao()+"', "+lastID+")");
                    }
                    Toast.makeText(getActivity(),"Thêm thành công", Toast.LENGTH_LONG).show();
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new DanhSachDanhMucFragment()).commit();
                }
            }
        });
    }
}