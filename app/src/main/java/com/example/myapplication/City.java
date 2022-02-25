package com.example.myapplication;

public class City {

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getProvince() {
    return province;
  }

  public void setProvince(String province) {
    this.province = province;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getDistrict() {
    return district;
  }

  public void setDistrict(String district) {
    this.district = district;
  }

  /** id : 1 province : 北京 city : 北京 district : 北京 */
  private String id;

  private String province;
  private String city;
  private String district;

  @Override
  public String toString() {
    return "City{"
        + "id='"
        + id
        + '\''
        + ", province='"
        + province
        + '\''
        + ", city='"
        + city
        + '\''
        + ", district='"
        + district
        + '\''
        + '}';
  }
}
