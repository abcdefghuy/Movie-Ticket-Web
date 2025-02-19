package com.example.movieticketWeb.enumeration;
import java.util.ArrayList;
import java.util.List;

public enum City {
    HA_NOI("Hà Nội"),
    HAI_PHONG("Hải Phòng"),
    QUANG_NINH("Quảng Ninh"),
    BAC_NINH("Bắc Ninh"),
    HAI_DUONG("Hải Dương"),
    HUNG_YEN("Hưng Yên"),
    NAM_DINH("Nam Định"),
    NINH_BINH("Ninh Bình"),
    THAI_BINH("Thái Bình"),
    VINH_PHUC("Vĩnh Phúc"),
    PHU_THO("Phú Thọ"),
    BAC_GIANG("Bắc Giang"),
    LAO_CAI("Lào Cai"),
    YEN_BAI("Yên Bái"),
    LANG_SON("Lạng Sơn"),
    CAO_BANG("Cao Bằng"),
    BAC_KAN("Bắc Kạn"),
    HA_GIANG("Hà Giang"),
    HA_NAM("Hà Nam"),
    HOA_BINH("Hòa Bình"),
    SON_LA("Sơn La"),
    LAI_CHAU("Lai Châu"),
    DIEN_BIEN("Điện Biên"),
    DA_NANG("Đà Nẵng"),
    QUANG_NAM("Quảng Nam"),
    QUANG_NGAI("Quảng Ngãi"),
    BINH_DINH("Bình Định"),
    PHU_YEN("Phú Yên"),
    KHANH_HOA("Khánh Hòa"),
    NINH_THUAN("Ninh Thuận"),
    BINH_THUAN("Bình Thuận"),
    QUANG_BINH("Quảng Bình"),
    QUANG_TRI("Quảng Trị"),
    THUA_THIEN_HUE("Thừa Thiên Huế"),
    GIA_LAI("Gia Lai"),
    KON_TUM("Kon Tum"),
    DAK_LAK("Đắk Lắk"),
    DAK_NONG("Đắk Nông"),
    HO_CHI_MINH("Hồ Chí Minh"),
    LONG_AN("Long An"),
    TIEN_GIANG("Tiền Giang"),
    BEN_TRE("Bến Tre"),
    TRA_VINH("Trà Vinh"),
    VINH_LONG("Vĩnh Long"),
    DONG_THAP("Đồng Tháp"),
    AN_GIANG("An Giang"),
    KIEN_GIANG("Kiên Giang"),
    CAN_THO("Cần Thơ"),
    HAU_GIANG("Hậu Giang"),
    SOC_TRANG("Sóc Trăng"),
    BAC_LIEU("Bạc Liêu"),
    CA_MAU("Cà Mau"),
    PHU_QUOC("Phú Quốc"),
    BA_RIA_VUNG_TAU("Bà Rịa - Vũng Tàu"),
    BINH_DUONG("Bình Dương"),
    BINH_PHUOC("Bình Phước"),
    DONG_NAI("Đồng Nai"),
    TAY_NINH("Tây Ninh")
    ;

    private final String vietnameseName;

    City(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }

    public static List<String> getListCity(){
        List<String> listcity = new ArrayList<String>();
        for (City city : City.values()) {
            listcity.add(city.getVietnameseName());
        }
        return listcity;
    }
}

