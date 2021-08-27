package com.quickzetuser.model;


import com.models.BaseModel;

/**
 * Created by ubuntu on 29/3/18.
 */

public class UserModel extends BaseModel {

    private String AWS_KEY;
    private String AWS_SECRET;
    private String AWS_REGION;
    private String AWS_BUCKET;

    private long id;
    private String firstname;
    private String lastname;
    private String password;
    private String phone;
    private String country_mobile_code;
    private String email;

    private String support_email;
    private String support_phone;
    private String support_web;

    private String referral_code;
    private float avg_rating;
    private String image;
    private String status;

    private CountryModel country;
    private StateModel state;
    private CityModel city;
    private WalletModel wallet;

    public String getAWS_KEY() {
        return getValidString(AWS_KEY);
    }

    public void setAWS_KEY(String AWS_KEY) {
        this.AWS_KEY = AWS_KEY;
    }

    public String getAWS_SECRET() {
        return getValidString(AWS_SECRET);
    }

    public void setAWS_SECRET(String AWS_SECRET) {
        this.AWS_SECRET = AWS_SECRET;
    }

    public String getAWS_REGION() {
        return getValidString(AWS_REGION);
    }

    public void setAWS_REGION(String AWS_REGION) {
        this.AWS_REGION = AWS_REGION;
    }

    public String getAWS_BUCKET() {
        return getValidString(AWS_BUCKET);
    }

    public void setAWS_BUCKET(String AWS_BUCKET) {
        this.AWS_BUCKET = AWS_BUCKET;
    }

    public String getSupport_email() {
        return support_email;
    }

    public void setSupport_email(String support_email) {
        this.support_email = support_email;
    }

    public String getSupport_phone() {
        return support_phone;
    }

    public void setSupport_phone(String support_phone) {
        this.support_phone = support_phone;
    }

    public String getSupport_web() {
        return support_web;
    }

    public void setSupport_web(String support_web) {
        this.support_web = support_web;
    }




    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstname() {
        return getValidString(firstname);
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return getValidString(lastname);
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return getValidString(password);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return getValidString(phone);
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile_code() {
        return getValidString(country_mobile_code);
    }

    public void setMobile_code(String mobile_code) {
        this.country_mobile_code = mobile_code;
    }

    public String getEmail() {
        return getValidString(email);
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getReferral_code() {
        return getValidString(referral_code);
    }

    public void setReferral_code(String referral_code) {
        this.referral_code = referral_code;
    }


    public float getAvg_rating() {
        return avg_rating;
    }

    public void setAvg_rating(float avg_rating) {
        this.avg_rating = avg_rating;
    }

    public String getStatus() {
        return getValidString(status);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CountryModel getCountry() {
        return country;
    }

    public void setCountry(CountryModel country) {
        this.country = country;
    }

    public StateModel getState() {
        return state;
    }

    public void setState(StateModel state) {
        this.state = state;
    }

    public CityModel getCity() {
        return city;
    }

    public void setCity(CityModel city) {
        this.city = city;
    }

    public String getImage() {
        return getValidString(image);
    }

    public void setImage(String image) {
        this.image = image;
    }

    public WalletModel getWallet() {
        return wallet;
    }

    public void setWallet(WalletModel wallet) {
        this.wallet = wallet;
    }

    public String getFullName () {
        return getFirstname() + " " + getLastname();
    }

    public String getFullMobile () {
        if (isValidString(getMobile_code())) {
            return getMobile_code() + "-" + getPhone();
        }
        return getPhone();
    }
    public String getMobile () {
        if (isValidString(getMobile_code())) {
            return getMobile_code() + "" + getPhone();
        }
        return getPhone();
    }

    public String getFullBucketPath(String subBucket) {
        return getAWS_BUCKET() + "/" + subBucket;
    }
}
