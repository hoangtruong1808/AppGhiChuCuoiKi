package com.example.appghichucuoiki.model;

public class NoiDungDanhMuc {
    private int id;
    private boolean check;
    private String content;
    private String ngayTao;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(String ngayTao) {
        this.ngayTao = ngayTao;
    }

    public NoiDungDanhMuc(int id, boolean check, String content, String ngayTao) {
        this.id = id;
        this.check = check;
        this.content = content;
        this.ngayTao = ngayTao;
    }

    public NoiDungDanhMuc(int id) {
        this.id = id;
    }

    public NoiDungDanhMuc() {
    }

    @Override
    public String toString() {
        return "NoiDungDanhMuc{" +
                "id=" + id +
                ", check=" + check +
                ", content='" + content + '\'' +
                ", ngayTao='" + ngayTao + '\'' +
                '}';
    }
}
