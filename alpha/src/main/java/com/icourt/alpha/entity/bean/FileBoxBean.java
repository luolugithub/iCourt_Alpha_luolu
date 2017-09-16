package com.icourt.alpha.entity.bean;

import java.io.Serializable;

/**
 * @author 创建人:lu.zhao
 *         <p>
 *         文档boxbean
 * @data 创建时间:17/3/24
 */

@Deprecated
public class FileBoxBean implements Serializable {

    public String id;
    public String name;
    public String type;
    public String permission;
    public long mtime;
    public long lock_time;
    public boolean locked_by_me;
    public boolean is_locked;
    public long size;
}
