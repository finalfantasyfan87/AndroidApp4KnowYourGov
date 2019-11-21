package com.csc472.hw4knowyourgov.model;

import java.io.Serializable;

public class Official implements Serializable {
    private String office;
    private String name;
    private String party;

    private String address;
    private String phone;
    private String email;
    private String url;
    private String photoUrl;

    private String googlePlus;
    private String facebook;
    private String twitter;
    private String youTube;

    public Official(String office, String name, String party, String address, String phone, String email, String url, String photoUrl, String googlePlus, String facebook, String twitter, String youTube) {
        this.office = office;
        this.name = name;
        this.party = party;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.url = url;
        this.photoUrl = photoUrl;
        this.googlePlus = googlePlus;
        this.facebook = facebook;
        this.twitter = twitter;
        this.youTube = youTube;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getGooglePlus() {
        return googlePlus;
    }

    public void setGooglePlus(String googlePlus) {
        this.googlePlus = googlePlus;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getYouTube() {
        return youTube;
    }

    public void setYouTube(String youTube) {
        this.youTube = youTube;
    }

    @Override
    public String toString() {
        return "OfficialActivity{" +
                "office='" + office + '\'' +
                ", name='" + name + '\'' +
                ", party='" + party + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", url='" + url + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", googlePlus='" + googlePlus + '\'' +
                ", facebook='" + facebook + '\'' +
                ", twitter='" + twitter + '\'' +
                ", youTube='" + youTube + '\'' +
                '}';
    }
}
