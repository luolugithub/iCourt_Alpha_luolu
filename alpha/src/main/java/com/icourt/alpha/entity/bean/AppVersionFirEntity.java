package com.icourt.alpha.entity.bean;

import java.io.Serializable;

/**
 * @author lu.zhao  E-mail:zhaolu@icourt.cc
 * @version 2.2.1
 * @Description  fir版本更新
 * @Company Beijing icourt
 * @date createTime：17/11/3
 */

public class AppVersionFirEntity implements Serializable {

    public int version;
    public int build;
    public String changelog;
    public String install_url;
    public String versionShort;
    public BinaryEntity binary;

    public static class BinaryEntity {
        public long fsize;
    }

}
