package com.icourt.alpha.entity.bean;

import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.icourt.alpha.base.BaseImEntity;
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
@Deprecated
public class IMStringWrapEntity extends BaseImEntity implements Serializable {

    //注意服务器返回的是字符串包裹的json对象
    @JsonAdapter(StringWrapIMAdapter.class)
    public StringWrapIMContent content;


    @Deprecated
    public static class StringWrapIMContent implements Serializable {
        public static final int MSG_TYPE_TEXT = 0;
        public static final int MSG_TYPE_FILE = 1;
        public static final int MSG_TYPE_DING = 2;
        public static final int MSG_TYPE_AT = 3;
        public static final int MSG_TYPE_SYS = 4;
        public int show_type;// 0: 文本消息; 1:文件 ; 2: 钉消息;  3:@消息  4:通知消息

        public String path;
        public String file;//其实就是文件名
        public long size;//kb

        public int atAll;
        public String content;

    }

    /**
     * 字符串包裹的json对象
     */
    public static class StringWrapIMAdapter extends TypeAdapter<StringWrapIMContent> {

        @Override
        public void write(JsonWriter out, StringWrapIMContent value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(JsonUtils.Gson2String(value));
            }
        }

        @Override
        public StringWrapIMContent read(JsonReader in) throws IOException {
            switch (in.peek()) {
                case STRING://带双引号的json对象
                    try {
                        return JsonUtils.Gson2Bean(new JsonPrimitive(in.nextString()).getAsString(), StringWrapIMContent.class);
                    } catch (JsonParseException e) {
                    }
            }
            try {
                return JsonUtils.Gson2Bean(in.nextString(), StringWrapIMContent.class);
            } catch (JsonParseException e) {
            }
            return null;
        }
    }

}
