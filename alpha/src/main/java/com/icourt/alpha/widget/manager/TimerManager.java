package com.icourt.alpha.widget.manager;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.http.RetrofitServiceFactory;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.SpUtils;
import com.icourt.api.RequestUtils;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  计时管理器
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/11
 * version 1.0.0
 */
public class TimerManager {

    private static final String KEY_TIMER = "key_timer_entity_%s";

    private TimerManager() {

    }

    private static TimerManager timerManager;

    public static TimerManager getInstance() {
        if (timerManager == null) {
            synchronized (TimerManager.class) {
                timerManager = new TimerManager();
            }
        }
        return timerManager;
    }


    /**
     * 获取登陆用户id
     *
     * @return
     */
    private String getUid() {
        return LoginInfoUtils.getLoginUserId();
    }

    /**
     * 添加计时
     *
     * @param itemEntity
     */
    public void addTimer(@NonNull final TimeEntity.ItemEntity itemEntity) {
        if (itemEntity == null) return;
        itemEntity.pkId = "";
        itemEntity.createUserId = getUid();
        itemEntity.endTime = 0;
        itemEntity.startTime = System.currentTimeMillis();
        itemEntity.useTime = 0;
        itemEntity.state = 0;
        //itemEntity.workDate=DateUtils.formatDayDatetime();
        JsonObject jsonObject = null;
        try {
            jsonObject = JsonUtils.object2JsonObject(itemEntity);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        if (jsonObject != null) {
            if (jsonObject.has("matterName")) {
                jsonObject.remove("matterName");
            }
            if (jsonObject.has("timingCount")) {
                jsonObject.remove("timingCount");
            }
            SpUtils.getInstance().putData(String.format(KEY_TIMER, getUid()), itemEntity);
            RetrofitServiceFactory.getAlphaApiService()
                    .timingAdd(RequestUtils.createJsonBody(jsonObject.toString()))
                    .enqueue(new SimpleCallBack<JsonElement>() {
                        @Override
                        public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        }
                    });
        }
    }


    /**
     * 获取计时对象
     *
     * @return
     */
    @CheckResult
    @Nullable
    public TimeEntity.ItemEntity getTimer() {
        return (TimeEntity.ItemEntity) SpUtils.getInstance().getSerializableData(String.format(KEY_TIMER, getUid()));
    }

    /**
     * 是否包含正在计时
     *
     * @return
     */
    @CheckResult
    @Nullable
    public boolean hasTimer() {
        return getTimer() != null;
    }

    /**
     * 更新计时对象
     *
     * @return
     */
    public void updateTimer(TimeEntity.ItemEntity itemEntity) {
        SpUtils.getInstance().putData(String.format(KEY_TIMER, getUid()), itemEntity);
    }

    /**
     * 获取计时id
     *
     * @return
     */
    public String getTimerId() {
        TimeEntity.ItemEntity timer = getTimer();
        if (timer != null) {
            return timer.pkId;
        }
        return null;
    }

    /**
     * 获取计时状态
     * -1 表示没有计时对象
     *
     * @return
     */
    public int getTimerState() {
        TimeEntity.ItemEntity timer = getTimer();
        if (timer != null) {
            return timer.state;
        }
        return -1;
    }

    /**
     * 停止计时
     */
    public void stopTimer() {
        //TDDO 接口
        TimeEntity.ItemEntity timer = getTimer();
        if (timer != null) {

            JsonObject jsonObject = null;
            try {
                jsonObject = JsonUtils.object2JsonObject(timer);
            } catch (JsonParseException e) {
                e.printStackTrace();
            }
            if (jsonObject != null) {
                if (jsonObject.has("matterName")) {
                    jsonObject.remove("matterName");
                }
                if (jsonObject.has("timingCount")) {
                    jsonObject.remove("timingCount");
                }
                if (jsonObject.has("workTypeName")) {
                    jsonObject.remove("workTypeName");
                }
                RetrofitServiceFactory
                        .getAlphaApiService()
                        .timingUpdate(RequestUtils.createJsonBody(jsonObject.toString()))
                        .enqueue(new SimpleCallBack<JsonElement>() {
                            @Override
                            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                                SpUtils.getInstance().putData(String.format(KEY_TIMER, getUid()), "");
                            }
                        });
            }
        }
    }

}
