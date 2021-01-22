package com.example.appghichucuoiki.model;


public class CongViec {
    private int idCV;
    private String tenCV;
    private String noiDungCV;
    private String ngayTao;

    public int getIdCV() {
        return idCV;
    }

    public void setIdCV(int idCV) {
        this.idCV = idCV;
    }

    public String getTenCV() {
        return tenCV;
    }

    public void setTenCV(String tenCV) {
        this.tenCV = tenCV;
    }

    public String getNoiDungCV() {
        return noiDungCV;
    }

    public void setNoiDungCV(String noiDungCV) {
        this.noiDungCV = noiDungCV;
    }

    public String getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(String ngayTao) {
        this.ngayTao = ngayTao;
    }

    public CongViec(int idCV, String tenCV, String noiDungCV, String ngayTao) {
        this.idCV = idCV;
        this.tenCV = tenCV;
        this.noiDungCV = noiDungCV;
        this.ngayTao = ngayTao;
    }

    @Override
    public String toString() {
        return "CongViec{" +
                "idCV=" + idCV +
                ", tenCV='" + tenCV + '\'' +
                ", noiDungCV='" + noiDungCV + '\'' +
                ", ngayTao=" + ngayTao +
                '}';
    }
}
