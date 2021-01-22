package com.example.appghichucuoiki.model;

public class Account {
    private int Id;
    private String taiKhoan;
    private String ngayTao;

    public Account(int id, String taiKhoan, String ngayTao) {
        Id = id;
        this.taiKhoan = taiKhoan;
        this.ngayTao = ngayTao;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(String taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public String getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(String ngayTao) {
        this.ngayTao = ngayTao;
    }

    @Override
    public String toString() {
        return "Account{" +
                "Id=" + Id +
                ", taiKhoan='" + taiKhoan + '\'' +
                ", ngayTao='" + ngayTao + '\'' +
                '}';
    }
}
