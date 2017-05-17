package com.icourt.alpha.widget.manager;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.event.TimingEvent;
import com.icourt.alpha.http.RetrofitServiceFactory;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.SpUtils;
import com.icourt.api.RequestUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

import static com.bugtags.library.Bugtags.log;

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
    private Runnable timingRunnable = new Runnable() {
        public void run() {
            base++;
            broadTiming();
        }
    };
    private String globalTimingId;
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledFuture;
    private long base;

    public void setBase(long seconds) {
        this.base = seconds;
    }

    private void broadTiming() {
        TimingEvent timingSingle = TimingEvent.timingSingle;
        timingSingle.action = TimingEvent.TIMING_UPDATE_PROGRESS;
        timingSingle.timingId = globalTimingId;
        timingSingle.timingSecond = base;
        log("------------>timing:" + timingSingle.timingSecond);
        EventBus.getDefault().post(timingSingle);
    }

    public static TimerManager getInstance() {
        if (timerManager == null) {
            synchronized (TimerManager.class) {
                timerManager = new TimerManager();
            }
        }
        return timerManager;
    }


    /**
     * 1秒一次
     */
    private synchronized void startTimingTask() {
        stopTimingTask();
        scheduledFuture =
                scheduledExecutorService.scheduleAtFixedRate(
                        timingRunnable,
                        0,
                        1,
                        TimeUnit.SECONDS);
    }

    private synchronized void stopTimingTask() {
        if (scheduledFuture != null) {
            try {
                if (scheduledFuture.isCancelled()) {
                    scheduledFuture.cancel(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

        TimeEntity.ItemEntity itemEntityCopy = new TimeEntity.ItemEntity();
        try {
            itemEntityCopy = (TimeEntity.ItemEntity) itemEntity.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        itemEntityCopy.pkId = "";
        itemEntityCopy.createUserId = getUid();
        itemEntityCopy.startTime = System.currentTimeMillis();
        itemEntityCopy.useTime = 0;
        itemEntityCopy.state = 0;
        JsonObject jsonObject = null;
        try {
            jsonObject = JsonUtils.object2JsonObject(itemEntityCopy);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        if (jsonObject != null) {
            jsonObject.addProperty("workDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            if (jsonObject.has("matterName")) {
                jsonObject.remove("matterName");
            }
            if (jsonObject.has("endTime")) {
                jsonObject.remove("endTime");
            }
            if (jsonObject.has("createTime")) {
                jsonObject.remove("createTime");
            }
            if (jsonObject.has("timingCount")) {
                jsonObject.remove("timingCount");
            }

            final TimeEntity.ItemEntity finalItemEntityCopy = itemEntityCopy;
            RetrofitServiceFactory.getAlphaApiService()
                    .timingAdd(RequestUtils.createJsonBody(jsonObject.toString()))
                    .enqueue(new SimpleCallBack<String>() {
                        @Override
                        public void onSuccess(Call<ResEntity<String>> call, Response<ResEntity<String>> response) {
                            finalItemEntityCopy.pkId = response.body().result;
                            globalTimingId = finalItemEntityCopy.pkId;
                            SpUtils.getInstance().putData(String.format(KEY_TIMER, getUid()), finalItemEntityCopy);
                            broadTimingEvent(finalItemEntityCopy.pkId, TimingEvent.TIMING_ADD);
                            startTimingTask();
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
     * 是否正在计时
     *
     * @param itemEntity
     * @return
     */
    public boolean isTimer(TimeEntity.ItemEntity itemEntity) {
        return getTimer() == itemEntity;
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
        final TimeEntity.ItemEntity timer = getTimer();
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
                                broadTimingEvent(timer.pkId, TimingEvent.TIMING_STOP);
                                stopTimingTask();
                            }
                        });
            }


        }
    }

    private void broadTimingEvent(String id, @TimingEvent.TIMING_ACTION int action) {
        EventBus.getDefault().post(new TimingEvent(id, action));
    }


}
