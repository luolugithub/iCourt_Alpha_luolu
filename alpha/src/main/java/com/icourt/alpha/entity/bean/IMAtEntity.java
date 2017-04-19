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

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/19
 * version 1.0.0
 */
public class IMAtEntity extends BaseImEntity {

    @JsonAdapter(IMAtInfoAdapter.class)
    public IMAtInfo content;

    public static class IMAtInfo {
        public int atAll;
        public String content;
    }

    public static class IMAtInfoAdapter extends TypeAdapter<IMAtInfo> {

        @Override
        public void write(JsonWriter out, IMAtInfo value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(JsonUtils.Gson2String(value));
            }
        }

        @Override
        public IMAtInfo read(JsonReader in) throws IOException {
            switch (in.peek()) {
                case STRING://带双引号的json对象
                    try {
                        return JsonUtils.Gson2Bean(new JsonPrimitive(in.nextString()).getAsString(), IMAtInfo.class);
                    } catch (JsonParseException e) {
                    }
            }
            try {
                return JsonUtils.Gson2Bean(in.nextString(), IMAtInfo.class);
            } catch (JsonParseException e) {
            }
            return null;
        }
    }
}
