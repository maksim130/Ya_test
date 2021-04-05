package com.example.nested;

public class Ticker {
    private String id;
    private String pic;
    private String tickerName;
    private String companyName;
    private String price;
    private String deltaPrice;
    private String isFavourite;
    private String oldprice;
    private String currency;
    private String ipo;
    private String phone;
    private String industry;
    private String site;

    public Ticker() {
    }


    public Ticker(String id, String pic, String tickerName, String companyName, String price, String deltaPrice, String isFavourite,
                  String oldprice, String currency, String ipo, String phone, String industry, String site) {
        this.id = id;
        this.pic = pic;
        this.tickerName = tickerName;
        this.companyName = companyName;
        this.price = price;
        this.deltaPrice = deltaPrice;
        this.isFavourite = isFavourite;
        this.oldprice = oldprice;
        this.currency = currency;
        this.ipo = ipo;
        this.phone = phone;
        this.industry = industry;
        this.site = site;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(String isFavourite) {
        this.isFavourite = isFavourite;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getTickerName() {
        return tickerName;
    }

    public void setTickerName(String tickerName) {
        this.tickerName = tickerName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDeltaPrice() {
        return deltaPrice;
    }

    public void setDeltaPrice(String deltaPrice) {
        this.deltaPrice = deltaPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getOldprice() {
        return oldprice;
    }

    public void setOldprice(String oldprice) {
        this.oldprice = oldprice;
    }

    public String getIpo() {
        return ipo;
    }

    public void setIpo(String ipo) {
        this.ipo = ipo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}
