package org.brixcms.plugin.usermgmt.user;

import javax.persistence.Entity;

import org.brixcms.plugin.usermgmt.BaseEntity;

/**
 * @author dan.simko@gmail.com
 */
@Entity
@SuppressWarnings("serial")
public class User extends BaseEntity {

    private String username;
    private String password;
    private String passSalt;
    private String firstName;
    private String lastName;
    private String email;
    private boolean verified;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassSalt() {
        return passSalt;
    }

    public void setPassSalt(String passSalt) {
        this.passSalt = passSalt;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
