//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package scc.data;

import java.util.Arrays;

public class UserDAO {
    private String _rid;
    private String _ts;
    private String id;
    private String name;
    private String pwd;
    private String photoId;
    private String[] channelIds;

    public UserDAO() {
    }

    public UserDAO(User u) {
        this(u.getId(), u.getName(), u.getPwd(), u.getPhotoId(), u.getChannelIds());
    }

    public UserDAO(String id, String name, String pwd, String photoId, String[] channelIds) {
        this.id = id;
        this.name = name;
        this.pwd = pwd;
        this.photoId = photoId;
        this.channelIds = channelIds;
    }

    public String get_rid() {
        return this._rid;
    }

    public void set_rid(String _rid) {
        this._rid = _rid;
    }

    public String get_ts() {
        return this._ts;
    }

    public void set_ts(String _ts) {
        this._ts = _ts;
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

    public String[] getChannelIds() {
        return this.channelIds == null ? new String[0] : this.channelIds;
    }

    public void setChannelIds(String[] channelIds) {
        this.channelIds = channelIds;
    }

    public User toUser() {
        return new User(this.id, this.name, this.pwd, this.photoId, this.channelIds == null ? null : (String[])Arrays.copyOf(this.channelIds, this.channelIds.length));
    }

    public String toString() {
        String var10000 = this._rid;
        return "UserDAO [_rid=" + var10000 + ", _ts=" + this._ts + ", id=" + this.id + ", name=" + this.name + ", pwd=" + this.pwd + ", photoId=" + this.photoId + ", channelIds=" + Arrays.toString(this.channelIds) + "]";
    }
}
