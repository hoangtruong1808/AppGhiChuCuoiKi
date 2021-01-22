package com.example.appghichucuoiki.model;

import java.util.ArrayList;
import java.util.List;

public class DanhMuc {
    private int id;
    private String tenDanhMuc;
    private List<NoiDungDanhMuc> listNoiDung = new ArrayList<>();
    private String ngayTao;

    public DanhMuc(int id, String tenDanhMuc, List<NoiDungDanhMuc> listNoiDung,String ngayTao) {
        this.id = id;
        this.tenDanhMuc = tenDanhMuc;
        this.listNoiDung = listNoiDung;
        this.ngayTao = ngayTao;
    }

    public DanhMuc(int id, String tenDanhMuc, String ngayTao) {
        this.id = id;
        this.tenDanhMuc = tenDanhMuc;
        this.ngayTao = ngayTao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTenDanhMuc() {
        return tenDanhMuc;
    }

    public void setTenDanhMuc(String tenDanhMuc) {
        this.tenDanhMuc = tenDanhMuc;
    }

    public List<NoiDungDanhMuc> getListNoiDung() {
        return listNoiDung;
    }

    public void setListNoiDung(List<NoiDungDanhMuc> listNoiDung) {
        this.listNoiDung = listNoiDung;
    }

    public String getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(String ngayTao) {
        this.ngayTao = ngayTao;
    }

    @Override
    public String toString() {
        return "DanhMuc{" +
                "id=" + id +
                ", tenDanhMuc='" + tenDanhMuc + '\'' +
                ", listNoiDung=" + listNoiDung +
                ", ngayTao='" + ngayTao + '\'' +
                '}';
    }
}
