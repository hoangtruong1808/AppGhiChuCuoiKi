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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bigexcersice.R;
import com.example.bigexcersice.adapters.DanhMucAdapter;
import com.example.bigexcersice.adapters.NoiDungDanhMucAdapter;
import com.example.bigexcersice.adapters.event.OnDanhMucItemListener;
import com.example.bigexcersice.adapters.event.OnNoiDungDanhMucItemListener;
import com.example.bigexcersice.database.Database;
import com.example.bigexcersice.model.DanhMuc;
import com.example.bigexcersice.model.NoiDungDanhMuc;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DanhSachDanhMucFragment extends Fragment {
    View danh_sach_danh_muc_fragment;
    ListView listViewDanhMuc;
    List<DanhMuc> listDanhMuc = new ArrayList<>();
    Database database;
    DanhMucAdapter danhMucAdapter;
    List<NoiDungDanhMuc> listNDDM ;
    List<NoiDungDanhMuc> listNDDMInDatabase = new ArrayList<>();
    NoiDungDanhMucAdapter noiDungDanhMucAdapter;
    EditText edtTimKiem;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        danh_sach_danh_muc_fragment = inflater.inflate(R.layout.danh_sach_danh_muc_fragment, container, false);
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView adView = new AdView(getActivity());
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(String.valueOf(R.string.adsID));
        adView = danh_sach_danh_muc_fragment.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        database = new Database(getActivity(), "ghichu.sqlite", null, 1);
        anhxa();
        danhMucAdapter = new DanhMucAdapter(getActivity(), listDanhMuc, R.layout.row_danhmuc_items, onDanhMucItemListener());
        listViewDanhMuc.setAdapter(danhMucAdapter);
        GetDS();
        timKiem();
        return danh_sach_danh_muc_fragment;
    }
    private void timKiem() {
        edtTimKiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                danhMucAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    private void anhxa() {
        listViewDanhMuc = danh_sach_danh_muc_fragment.findViewById(R.id.id_list_danhMuc);
        edtTimKiem = (EditText) danh_sach_danh_muc_fragment.findViewById(R.id.edsearch_danhmuc);
    }

    private void GetDS() {
        listDanhMuc.clear();
        Cursor dataCongViec = database.GetData("SELECT * FROM DanhMuc WHERE MaAccount = "+getProfileLogin());
        while (dataCongViec.moveToNext()) {
            int idDm = dataCongViec.getInt(0);
            String tenDm = dataCongViec.getString(1);
            String ngayTaoDM = dataCongViec.getString(2);
            DanhMuc dm = new DanhMuc(idDm, tenDm, ngayTaoDM);
            listDanhMuc.add(dm);
        }
        danhMucAdapter.notifyDataSetChanged();
    }
    private String getProfileLogin() {
        SharedPreferences pre = getActivity().getSharedPreferences("ID_USER", Context.MODE_PRIVATE);
        String id = pre.getString("id", "");
        return id;
    }
    private OnDanhMucItemListener onDanhMucItemListener() {
        OnDanhMucItemListener onDanhMucItemListener = new OnDanhMucItemListener() {
            @Override
            public void onButtonLoadMore(View v, int position, int idDanhMuc, String tenDM) {
                Cursor dsNDDM = database.GetData("SELECT * FROM DSDanhMuc WHERE MaDanhMuc = " + idDanhMuc + "");
                listNDDM = new ArrayList<>();
                while (dsNDDM.moveToNext()) {
                    int id = dsNDDM.getInt(0);
                    String nddm = dsNDDM.getString(1);
                    int checkInt = dsNDDM.getInt(2);
                    String dateCV = dsNDDM.getString(3);
                    boolean checkOption = false;
                    if (checkInt == 1) {
                        checkOption = true;
                    } else if (checkInt == 0) {
                        checkOption = false;
                    }
                    //int id, boolean check, String content, String ngayTao
                    //Id INTEGER PRIMARY KEY AUTOINCREMENT,NoiDung NVARCHAR(200), CheckOption INTEGER, NgayTao TEXT, MaDanhMuc
                    NoiDungDanhMuc ndDM = new NoiDungDanhMuc(id, checkOption, nddm, dateCV);
                    listNDDM.add(ndDM);
                    System.out.println("không hiểu: "+listNDDM);
                }
                DialogSuaDanhMuc(listNDDM, tenDM, idDanhMuc);
            }

            @Override
            public void onButtonDeleteDM(int position,final int idDanhMuc) {
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
                        database.QueryData("DELETE FROM DanhMuc WHERE Id = '"+idDanhMuc+"' AND MaAccount = " +getProfileLogin());
                        Toast.makeText(getActivity(),"Xoá thành công",Toast.LENGTH_SHORT).show();
                        GetDS();
                    }
                });
                builder.show();
            }
        };
        return onDanhMucItemListener;
    }

    private void GetNDDMInDatabase(int idDanhMuc) {
        listNDDMInDatabase.clear();
        Cursor DSNDDM = database.GetData("SELECT * FROM DSDanhMuc WHERE MaDanhMuc = "+idDanhMuc);
        while (DSNDDM.moveToNext()) {
            int idnd = DSNDDM.getInt(0);
            String nd = DSNDDM.getString(1);
            int check = DSNDDM.getInt(2);
            boolean flag = false;
            if (check == 1) {
                flag = true;
            } else if (check == 0) flag = false;
            String ngayTaoNDDM = DSNDDM.getString(3);
            NoiDungDanhMuc nddm = new NoiDungDanhMuc(idnd, flag, nd, ngayTaoNDDM);
            listNDDMInDatabase.add(nddm);
            // database.QueryData("CREATE TABLE IF NOT EXISTS DSDanhMuc(Id INTEGER PRIMARY KEY AUTOINCREMENT,NoiDung NVARCHAR(200), CheckOption INTEGER, NgayTao TEXT, MaDanhMuc INTEGER NOT NULL CONSTRAINT MaDanhMuc REFERENCES DanhMuc(Id) ON DELETE CASCADE)");
        }
    }

    private boolean FindNDDMInDatabase(int idDTB) {
        for (NoiDungDanhMuc nddm : listNDDM) {
            if (nddm.getId() == idDTB) {
                return true;
            }
        }
        return false;
    }


    private void DialogSuaDanhMuc(List<NoiDungDanhMuc> listNoiDungDM, final String tenDM, final int idDanhMuc) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.danh_muc_dialog_custom);

        final EditText edtTitle;
        Button btnXacNhan, btnHuy;
        ImageButton btnThemNDDM;
        CheckBox chbCheck;
        ListView listViewNDDM;
        edtTitle = dialog.findViewById(R.id.id_titleSuaDanhMuc_edt);
        listViewNDDM = dialog.findViewById(R.id.id_noiDungDanhMuc_listView);
        btnXacNhan = dialog.findViewById(R.id.id_xacNhanSuaDanhMuc_btn);
        btnHuy = dialog.findViewById(R.id.id_huySuaDanhMuc_btn);
        chbCheck = dialog.findViewById(R.id.id_check_danh_muc_chb);
        btnThemNDDM = dialog.findViewById(R.id.id_themNDDM_btn);

        edtTitle.setText(tenDM);
        noiDungDanhMucAdapter = new NoiDungDanhMucAdapter(getActivity(), R.layout.row_them_danh_muc_items, listNoiDungDM, onNoiDungDanhMucItemListener());
        listViewNDDM.setAdapter(noiDungDanhMucAdapter);

        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println("testdtb: "+GetNDDMInDatabase());
                GetNDDMInDatabase(idDanhMuc);
                for (NoiDungDanhMuc nddm : listNDDMInDatabase) {
                    if (FindNDDMInDatabase(nddm.getId())) {
                    } else {
                        System.out.println("check false: " + nddm.getId());
                        database.QueryData("DELETE FROM DSDanhMuc WHERE Id = '" + nddm.getId() + "'");
                    }
                }
                for (NoiDungDanhMuc nddm : listNDDM) {
                    int flag = 0;
                    if (nddm.isCheck()) {
                        flag = 1;
                    } else flag = 0;
                    if (nddm.getId() == 0) {
                        if (nddm.getContent() == null || nddm.getContent().equals("")) {
                        } else {
                            SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            Date utilDate = new Date();
                            database.QueryData("INSERT INTO DSDanhMuc VALUES (null, '" + nddm.getContent() + "'," + flag + ", '" + sf.format(utilDate) + "', " + idDanhMuc + ")");
                        }
                    } else {
                        database.QueryData("UPDATE DSDanhMuc SET NoiDung = '" + nddm.getContent() + "', CheckOption = " + flag + " WHERE Id = '" + nddm.getId() + "'");
                    }
                }
                database.QueryData("UPDATE DanhMuc SET TieuDeDM = '" + edtTitle.getText().toString() + "' WHERE Id = " + idDanhMuc + " AND MaAccount = "+getProfileLogin());

                GetDS();
                dialog.cancel();
            }
        });
        btnThemNDDM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoiDungDanhMuc noiDungDanhMuc = new NoiDungDanhMuc();
                listNDDM.add(noiDungDanhMuc);
                noiDungDanhMucAdapter.notifyDataSetChanged();
            }
        });
        dialog.show();
    }

    public OnNoiDungDanhMucItemListener onNoiDungDanhMucItemListener() {
        OnNoiDungDanhMucItemListener onNoiDungDanhMucItemListener = new OnNoiDungDanhMucItemListener() {
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
                    noiDungDanhMucAdapter.notifyDataSetChanged();
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
        return onNoiDungDanhMucItemListener;
    }
}