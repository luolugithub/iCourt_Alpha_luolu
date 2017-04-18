package com.icourt.alpha.entity.bean;

import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.icourt.alpha.utils.JsonUtils;

import java.io.IOException;
import java.io.Serializable;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/18
 * version 1.0.0
 */
public class IMFileEntity implements Serializable {

    public String groupId;
    public String createId;
    public int open;//0 单聊 1 群聊
    public String createName;
    public String pic;
    public String groupName;
    public long createDate;
    public String tid;//云信id

    //注意服务器返回的是字符串包裹的json对象
    @JsonAdapter(PathFileInfoAdapter.class)
    public PathFileInfo content;


    public static class PathFileInfo implements Serializable {
        public String path;
        public String file;//其实就是文件名
        public long size;//kb

        @Override
        public String toString() {
            return "PathFileInfo{" +
                    "path='" + path + '\'' +
                    ", file='" + file + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "IMFileEntity{" +
                "groupId='" + groupId + '\'' +
                ", createId='" + createId + '\'' +
                ", open=" + open +
                ", createName='" + createName + '\'' +
                ", pic='" + pic + '\'' +
                ", groupName='" + groupName + '\'' +
                ", createDate=" + createDate +
                ", tid='" + tid + '\'' +
                ", content=" + content +
                '}';
    }

    public static class PathFileInfoAdapter extends TypeAdapter<PathFileInfo> {

        @Override
        public void write(JsonWriter out, PathFileInfo value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(JsonUtils.Gson2String(value));
            }
        }

        @Override
        public PathFileInfo read(JsonReader in) throws IOException {
            switch (in.peek()) {
                case STRING://带双引号的json对象
                    try {
                        return JsonUtils.Gson2Bean(new JsonPrimitive(in.nextString()).getAsString(), PathFileInfo.class);
                    } catch (JsonParseException e) {
                    }
            }
            try {
                return JsonUtils.Gson2Bean(in.nextString(), PathFileInfo.class);
            } catch (JsonParseException e) {
            }
            return null;
        }
    }
}
