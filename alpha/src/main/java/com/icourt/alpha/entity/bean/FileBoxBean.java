package com.icourt.alpha.entity.bean;

import java.io.Serializable;

/**
 * @author 创建人:lu.zhao
 *         <p>
 *         文档boxbean
 * @data 创建时间:17/3/24
 */

public class FileBoxBean implements Serializable {


    /**
     * lock_time : 0
     * name : 1.png
     * permission : rw
     * is_locked : false
     * lock_owner : null
     * mtime : 1490347705
     * locked_by_me : false
     * type : file
     * id : 19dc323a95e37518c5926db2754767af9414a340
     * size : 3365
     */

    private long lock_time;
    private String name;
    private String permission;
    private boolean is_locked;
    private String lock_owner;
    private long mtime;
    private boolean locked_by_me;
    private String type;  // file:文件 ;  dir:文件夹
    private String id;
    private int size;

    private String sortLetters;  //显示数据拼音的首字母

    public long getLock_time() {
        return lock_time;
    }

    public void setLock_time(long lock_time) {
        this.lock_time = lock_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public boolean isIs_locked() {
        return is_locked;
    }

    public void setIs_locked(boolean is_locked) {
        this.is_locked = is_locked;
    }

    public Object getLock_owner() {
        return lock_owner;
    }

    public void setLock_owner(String lock_owner) {
        this.lock_owner = lock_owner;
    }

    public long getMtime() {
        return mtime;
    }

    public void setMtime(long mtime) {
        this.mtime = mtime;
    }

    public boolean isLocked_by_me() {
        return locked_by_me;
    }

    public void setLocked_by_me(boolean locked_by_me) {
        this.locked_by_me = locked_by_me;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }
}
