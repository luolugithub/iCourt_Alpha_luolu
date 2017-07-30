package com.icourt.alpha.widget.manager;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.PageEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.event.TimingEvent;
import com.icourt.alpha.http.RetrofitServiceFactory;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.SpUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.api.RequestUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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

    /**
     * 设置器起始时间
     *
     * @param seconds
     */
    public void setBase(long seconds) {
        this.base = seconds;
    }

    /**
     * 返回已经计时的秒
     *
     * @return
     */
    public long getTimingSeconds() {
        return this.base;
    }

    /**
     * 通知其它页面
     */
    private void broadTiming() {
        TimingEvent timingSingle = TimingEvent.timingSingle;
        timingSingle.action = TimingEvent.TIMING_UPDATE_PROGRESS;
        timingSingle.timingId = globalTimingId;
        timingSingle.timingSecond = base;
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
                        1,
                        1,
                        TimeUnit.SECONDS);
    }

    /**
     * 停止计时任务
     */
    private synchronized void stopTimingTask() {
        if (scheduledFuture != null) {
            try {
                if (!scheduledFuture.isCancelled()) {
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
     * 获取本地唯一id
     *
     * @return
     */
    private String getlocalUniqueId() {
        AlphaUserInfo loginUserInfo = LoginInfoUtils.getLoginUserInfo();
        if (loginUserInfo != null) {
            return loginUserInfo.localUniqueId;
        }
        return null;
    }

    /**
     * 添加计时
     *
     * @param itemEntity
     */
    public void addTimer(@NonNull final TimeEntity.ItemEntity itemEntity) {
        if (itemEntity == null) return;
        addTimer(itemEntity, null);
    }

    /**
     * 添加计时
     *
     * @param itemEntity
     * @param callBack
     */
    public void addTimer(@NonNull final TimeEntity.ItemEntity itemEntity,
                         @Nullable final retrofit2.Callback<TimeEntity.ItemEntity> callBack) {
        if (itemEntity == null) return;
        RetrofitServiceFactory.getAlphaApiService()
                .timingStart(itemEntity.name,
                        itemEntity.matterPkId,
                        itemEntity.taskPkId,
                        itemEntity.workTypeId,
                        getlocalUniqueId(),
                        0,
                        0)
                .enqueue(new SimpleCallBack<TimeEntity.ItemEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<TimeEntity.ItemEntity>> call, Response<ResEntity<TimeEntity.ItemEntity>> response) {
                        if (response.body().result == null) return;
                        globalTimingId = response.body().result.pkId;
                        SpUtils.getInstance().putData(String.format(KEY_TIMER, getUid()), response.body().result);
                        broadTimingEvent(response.body().result.pkId, TimingEvent.TIMING_ADD);
                        setBase(0);
                        startTimingTask();
                        if (callBack != null) {
                            callBack.onResponse(null, Response.success(response.body().result));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<TimeEntity.ItemEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        if (callBack != null) {
                            callBack.onFailure(null, t);
                        }
                    }
                });
    }

    /**
     * 恢复原计时
     */
    public void resumeTimer() {
        TimeEntity.ItemEntity timer = TimerManager.getInstance().getTimer();
        if (timer != null) {
            globalTimingId = timer.pkId;
            long timedLength = (System.currentTimeMillis() - timer.startTime) / 1000;
            if (timedLength < 0) {
                timedLength = 0;
            }
            setBase(timedLength);
            startTimingTask();
        }
    }

    /**
     * 恢复一个新的计时
     *
     * @param timer
     */
    public void resumeTimer(TimeEntity.ItemEntity timer) {
        if (timer == null) return;
        if (timer.useTime < 0) {
            timer.useTime = 0;
        }
        SpUtils.getInstance().putData(String.format(KEY_TIMER, getUid()), timer);
        globalTimingId = timer.pkId;
        broadTimingEvent(globalTimingId, TimingEvent.TIMING_ADD);
        //resumeTimer();

        globalTimingId = timer.pkId;
        setBase(timer.useTime / 1_000);
        startTimingTask();
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
        if (itemEntity == null) return false;
        return itemEntity.equals(getTimer());
    }

    /**
     * 是否是正在计时
     *
     * @param id
     * @return
     */
    public boolean isTimer(String id) {
        TimeEntity.ItemEntity timer = getTimer();
        return timer != null && StringUtils.equalsIgnoreCase(id, timer.pkId, false);
    }


    /**
     * 同步网络计时
     */
    public void timerQuerySync() {
        RetrofitServiceFactory
                .getAlphaApiService()
                .timerRunningQuery()
                .enqueue(new SimpleCallBack<TimeEntity.ItemEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<TimeEntity.ItemEntity>> call, Response<ResEntity<TimeEntity.ItemEntity>> response) {
                        if (response.body().result == null) {
                            TimerManager.getInstance().clearTimer();
                        } else {
                            if (isTimer(response.body().result.pkId)) {
                                TimerManager.getInstance().updateTimer(response.body().result);
                            } else {
                                TimerManager.getInstance().resumeTimer(response.body().result);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<TimeEntity.ItemEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        TimerManager.getInstance().resumeTimer();
                    }
                });
    }

    /**
     * 更新原计时对象
     *
     * @return
     */
    public void updateTimer(TimeEntity.ItemEntity itemEntity) {
        if (itemEntity == null) return;
        TimeEntity.ItemEntity timer = getTimer();
        if (timer != null && timer.equals(itemEntity)) {
            SpUtils.getInstance().putData(String.format(KEY_TIMER, getUid()), itemEntity);
            resumeTimer(itemEntity);
        }
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
     * 清除timer 并发停止的广播
     */
    public void clearTimer() {
        final TimeEntity.ItemEntity timer = getTimer();
        if (timer != null) {
            TimingEvent timingSingle = TimingEvent.timingSingle;
            timingSingle.action = TimingEvent.TIMING_STOP;
            timingSingle.timingId = timer.pkId;

            stopTimingTask();

            EventBus.getDefault().post(timingSingle);

            SpUtils.getInstance().putData(String.format(KEY_TIMER, getUid()), "");
        }
    }

    /**
     * 停止计时
     */
    public void stopTimer() {
        stopTimer(null);
    }

    /**
     * 停止计时
     */
    public void stopTimer(@Nullable final SimpleCallBack<TimeEntity.ItemEntity> callBack) {
        final TimeEntity.ItemEntity timer = getTimer();
        if (timer != null) {
            RetrofitServiceFactory
                    .getAlphaApiService()
                    .timingStop(timer.pkId)
                    .enqueue(new SimpleCallBack<TimeEntity.ItemEntity>() {
                        @Override
                        public void onSuccess(Call<ResEntity<TimeEntity.ItemEntity>> call, Response<ResEntity<TimeEntity.ItemEntity>> response) {
                            stopTimingTask();
                            SpUtils.getInstance().putData(String.format(KEY_TIMER, getUid()), "");

                            TimingEvent timingSingle = TimingEvent.timingSingle;
                            timingSingle.action = TimingEvent.TIMING_STOP;
                            timingSingle.timingId = timer.pkId;
                            timingSingle.timingSecond = base;
                            EventBus.getDefault().post(timingSingle);

                            if (callBack != null) {
                                callBack.onSuccess(call, response);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResEntity<TimeEntity.ItemEntity>> call, Throwable t) {
                            super.onFailure(call, t);
                            if (callBack != null) {
                                callBack.onFailure(call, t);
                            }
                            SpUtils.getInstance().putData(String.format(KEY_TIMER, getUid()), "");
                        }
                    });
        }
    }


    private void broadTimingEvent(String id, @TimingEvent.TIMING_ACTION int action) {
        EventBus.getDefault().post(new TimingEvent(id, action));
    }
}
