package DTO;

import java.util.Date;

public class longBietOnDTO {
    int Id;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    Date Ngay;
    String dieuBietOn;
    public String toString(){
        return "Ngay =" + Ngay + ", dieuBietOn =" +dieuBietOn ;
    }

    public Date getNgay() {
        return Ngay;
    }

    public void setNgay(Date ngay) {
        Ngay = ngay;
    }

    public String getDieuBietOn() {
        return dieuBietOn;
    }

    public void setDieuBietOn(String dieuBietOn) {
        this.dieuBietOn = dieuBietOn;
    }
}
