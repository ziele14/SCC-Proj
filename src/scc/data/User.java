//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package scc.data;

import java.util.Arrays;

public class User {
    private String id;
    private String name;
    private String pwd;
    private String photoId;

    public User(String id, String name, String pwd, String photoId) {
        this.id = id;
        this.name = name;
        this.pwd = pwd;
        this.photoId = photoId;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return this.pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPhotoId() {
        return this.photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String toString() {
        return "id = " + this.id + ", name = " + this.name + ", photoId = " + this.photoId;
    }
}

