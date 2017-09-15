package com.icourt.alpha.widget.json;

import android.text.TextUtils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/14
 * version 2.1.0
 */
public class SeaFileTimeJsonAdapter extends TypeAdapter<Long> {

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
                return Long.valueOf(formaterTime(jsonReader.nextString()));
            case NUMBER:
                return Long.valueOf(jsonReader.nextLong());
        }
        return Long.valueOf(0);
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
