package com.example.nested;

public class FavouriteTicker {

    private String id;
    private String pic;
    private String tickerName;
    private String companyName;
    private String price;
    private String deltaPrice;


    public FavouriteTicker() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public FavouriteTicker(String id, String pic, String tickerName, String companyName, String price, String deltaPrice) {
        this.id = id;
        this.pic = pic;
        this.tickerName = tickerName;
        this.companyName = companyName;
        this.price = price;
        this.deltaPrice = deltaPrice;

    }

}

