package com.ingentive.qrme.activeandroid;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by PC on 07-06-2016.
 */
@Table(name = "UserInfoTable")
public class UserInfoTable extends Model {

    @Column(name = "user_id")
    public int userId;

    @Column(name = "user_name")
    public String userName;

    @Column(name = "user_facebook")
    public String userFacebook;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserFacebook() {
        return userFacebook;
    }

    public void setUserFacebook(String userFacebook) {
        this.userFacebook = userFacebook;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserTwitter() {
        return userTwitter;
    }

    public void setUserTwitter(String userTwitter) {
        this.userTwitter = userTwitter;
    }

    public String getUserLinkedin() {
        return userLinkedin;
    }

    public void setUserLinkedin(String userLinkedin) {
        this.userLinkedin = userLinkedin;
    }

    @Column(name = "user_phone")
    public String userPhone;

    @Column(name = "user_email")
    public String userEmail;

    @Column(name = "user_twitter")
    public String userTwitter;

    @Column(name = "user_linkedin")
    public String userLinkedin;

    public UserInfoTable() {
        super();
        this.userId = 0;
        this.userName = "";
        this.userFacebook = "";
        this.userPhone = "";
        this.userEmail = "";
        this.userTwitter = "";
        this.userLinkedin = "";
    }
}
