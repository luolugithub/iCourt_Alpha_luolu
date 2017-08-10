package com.icourt.alpha.entity.bean;

import android.text.TextUtils;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.icourt.alpha.widget.comparators.ILongFieldEntity;
import com.icourt.alpha.widget.json.BooleanTypeAdapter;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Description  文档资料库
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/10
 * version 2.1.0
 */
public class DocumentRootEntity implements Serializable, ILongFieldEntity {

    @Override
    public Long getCompareLongField() {
        return last_modified;
    }

    private static class TimeJsonAdapter extends TypeAdapter<Long> {

        @Override
        public void write(JsonWriter jsonWriter, Long s) throws IOException {
            if (null == s) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(s);
            }
        }

        @Override
        public Long read(JsonReader jsonReader) throws IOException {
            JsonToken peek = jsonReader.peek();
            switch (peek) {
                case STRING:
                    return new Long(formaterTime(jsonReader.nextString()));
            }
            return new Long(0);
        }

        /**
         * @param time 2017-04-20T01:43:45+08:00
         * @return 时间戳
         */
        private long formaterTime(String time) {
            if (!TextUtils.isEmpty(time)) {
                try {
                    time = time.replaceAll("T", " ");
                    time = time.replaceAll("\\+08:00", "");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date parse = simpleDateFormat.parse(time);
                    return parse.getTime();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            return 0;
        }

    }

    @JsonAdapter(BooleanTypeAdapter.class)
    public boolean encrypted;
    public String repo_id;
    @SerializedName(value = "repo_name", alternate = {"name"})
    public String repo_name;

    @JsonAdapter(TimeJsonAdapter.class)
    public long last_modified;
    public long size;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null) return false;
        if (getClass() != o.getClass())
            return false;
        final DocumentRootEntity other = (DocumentRootEntity) o;
        return TextUtils.equals(this.repo_id, other.repo_id);
    }

}
