package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ImUserMessageDetailAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.convertor.ListConvertor;
import com.icourt.alpha.db.dbmodel.ContactDbModel;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.ChatFileInfoEntity;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.entity.bean.MsgStatusEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.StringUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.Team;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.activity.ChatMsgClassfyActivity.MSG_CLASSFY_CHAT_DING;
import static com.icourt.alpha.activity.ChatMsgClassfyActivity.MSG_CLASSFY_CHAT_FILE;
import static com.icourt.alpha.activity.ChatMsgClassfyActivity.MSG_CLASSFY_MY_COLLECTEED;
import static com.icourt.alpha.activity.ImagePagerActivity.IMAGE_FROM_CHAT_FILE;
import static com.icourt.alpha.constants.Const.CHAT_TYPE_P2P;
import static com.icourt.alpha.constants.Const.CHAT_TYPE_TEAM;
import static com.icourt.alpha.constants.Const.MSG_TYPE_FILE;
import static com.icourt.alpha.constants.Const.MSG_TYPE_LINK;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/18
 * version 1.0.0
 */
public class FileDetailsActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener, BaseRecyclerAdapter.OnItemChildClickListener {

    private static final String KEY_FILE_INFO = "key_file_info";
    private static final String KEY_CLASSFY_TYPE = "KEY_CLASSFY_TYPE";
    IMMessageCustomBody item;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    ImUserMessageDetailAdapter imUserMessageDetailAdapter;

    //本地同步的联系人
    protected final List<GroupContactBean> localContactList = new ArrayList<>();
    private final List<Team> localTeams = new ArrayList<>();
    private boolean isRevoke;//消息是否撤回

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_details);
        ButterKnife.bind(this);
        initView();
        getData(true);
    }

    @ChatMsgClassfyActivity.MsgClassfyType
    public int getMsgClassfyType() {
        switch (getIntent().getIntExtra(KEY_CLASSFY_TYPE, 0)) {
            case MSG_CLASSFY_MY_COLLECTEED:
                return MSG_CLASSFY_MY_COLLECTEED;
            case MSG_CLASSFY_CHAT_DING:
                return MSG_CLASSFY_CHAT_DING;
            case MSG_CLASSFY_CHAT_FILE:
                return MSG_CLASSFY_CHAT_FILE;
            default:
                return MSG_CLASSFY_MY_COLLECTEED;
        }
    }

    /**
     * 获取本地联系人
     */
    private void getLocalContacts() {
        queryAllContactFromDbAsync(new Consumer<List<GroupContactBean>>() {
            @Override
            public void accept(List<GroupContactBean> groupContactBeen) throws Exception {
                if (groupContactBeen != null && !groupContactBeen.isEmpty()) {
                    localContactList.clear();
                    localContactList.addAll(groupContactBeen);
                    imUserMessageDetailAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * 异步查询本地联系人
     */
    protected final void queryAllContactFromDbAsync(@NonNull Consumer<List<GroupContactBean>> consumer) {
        if (consumer == null) return;
        Observable.create(new ObservableOnSubscribe<List<GroupContactBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<GroupContactBean>> e) throws Exception {
                ContactDbService threadContactDbService = null;
                try {
                    if (!e.isDisposed()) {
                        threadContactDbService = new ContactDbService(getLoginUserId());
                        RealmResults<ContactDbModel> contactDbModels = threadContactDbService.queryAll();
                        if (contactDbModels != null) {
                            List<GroupContactBean> contactBeen = ListConvertor.convertList(new ArrayList<IConvertModel<GroupContactBean>>(contactDbModels));
                            e.onNext(contactBeen);
                        }
                        e.onComplete();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (threadContactDbService != null) {
                        threadContactDbService.releaseService();
                    }
                }
            }
        }).compose(this.<List<GroupContactBean>>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }

    public static void launch(@NonNull Context context,
                              IMMessageCustomBody imFileEntity,
                              @ChatMsgClassfyActivity.MsgClassfyType int msgClassfyType) {
        if (context == null) return;
        if (imFileEntity == null) return;
        Intent intent = new Intent(context, FileDetailsActivity.class);
        intent.putExtra(KEY_FILE_INFO, imFileEntity);
        intent.putExtra(KEY_CLASSFY_TYPE, msgClassfyType);
        context.startActivity(intent);
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("详情");
        TextView titleActionTextView = getTitleActionTextView();
        if (titleActionTextView != null) {
            titleActionTextView.setText("跳转");
            titleActionTextView.setVisibility(View.INVISIBLE);
        }
        Serializable serializableExtra = getIntent().getSerializableExtra(KEY_FILE_INFO);
        if (serializableExtra instanceof IMMessageCustomBody) {
            item = (IMMessageCustomBody) serializableExtra;
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(imUserMessageDetailAdapter = new ImUserMessageDetailAdapter(localContactList, localTeams));
            imUserMessageDetailAdapter.setOnItemClickListener(this);
            imUserMessageDetailAdapter.setOnItemChildClickListener(this);
            imUserMessageDetailAdapter.bindData(true, Arrays.asList(item));
        }
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);

        callEnqueue(getChatApi()
                        .msgStatus(item.id),
                new SimpleCallBack<MsgStatusEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<MsgStatusEntity>> call, Response<ResEntity<MsgStatusEntity>> response) {
                        if (response.body().result == null) return;
                        isRevoke = response.body().result.recalled;
                        titleAction.setVisibility(View.VISIBLE);
                    }
                });
        getLocalContacts();
        getTeams();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleAction:
                if (isRevoke) {
                    showTopSnackBar("该消息已被撤回");
                    return;
                }
                if (item != null) {
                    switch (item.ope) {
                        case CHAT_TYPE_P2P:
                            // 我->cl  from me to:cl;
                            //cl ——>我  from cl:to:me
                            boolean isMySend = StringUtils.equalsIgnoreCase(getLoginUserId(), item.from, false);
                            ChatActivity.launchP2P(getContext(),
                                    isMySend ? item.to : item.from,
                                    item.name,
                                    item.id,
                                    0,
                                    true);
                            break;
                        case CHAT_TYPE_TEAM:
                            Team team = getTeam(item.to);
                            ChatActivity.launchTEAM(getContext(),
                                    item.to,
                                    team != null ? team.getName() : "",
                                    item.id,
                                    0,
                                    true);
                            break;
                    }
                    finish();
                }
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    private Team getTeam(String id) {
        for (Team team : localTeams) {
            if (team != null && StringUtils.equalsIgnoreCase(team.getId(), id, false)) {
                return team;
            }
        }
        return null;
    }

    public void getTeams() {
        NIMClient.getService(TeamService.class)
                .queryTeamList()
                .setCallback(new RequestCallbackWrapper<List<Team>>() {
                    @Override
                    public void onResult(int code, List<Team> result, Throwable exception) {
                        if (result != null) {
                            localTeams.clear();
                            localTeams.addAll(result);
                            imUserMessageDetailAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        IMMessageCustomBody item = imUserMessageDetailAdapter.getItem(adapter.getRealPos(position));
        if (item == null) return;
        switch (item.show_type) {
            case MSG_TYPE_LINK:
                if (item.ext != null) {
                    WebViewActivity.launch(view.getContext(), item.ext.url);
                }
                break;
            case MSG_TYPE_FILE:
                //文件
                if (item.ext != null) {
                    FileDownloadActivity.launch(
                            view.getContext(),
                            item.ext,
                            SFileConfig.FILE_FROM_IM);
                }
                break;
        }
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        switch (view.getId()) {
            case R.id.file_img:
                IMMessageCustomBody item = imUserMessageDetailAdapter.getItem(imUserMessageDetailAdapter.getRealPos(position));
                if (item != null && item.ext != null) {
                    ArrayList<ChatFileInfoEntity> chatFileInfoEntities = new ArrayList<>();

                    ChatFileInfoEntity chatFileInfoEntity = item.ext.convert2Model();
                    chatFileInfoEntity.setChatMsgId(item.id);

                    chatFileInfoEntities.add(chatFileInfoEntity);

                    @ImagePagerActivity.ImageFrom int imageFrom = IMAGE_FROM_CHAT_FILE;
                    switch (getMsgClassfyType()) {
                        case MSG_CLASSFY_CHAT_DING:
                            imageFrom = ImagePagerActivity.IMAGE_FROM_DING;
                            break;
                        case MSG_CLASSFY_CHAT_FILE:
                            imageFrom = ImagePagerActivity.IMAGE_FROM_CHAT_FILE;
                            break;
                        case MSG_CLASSFY_MY_COLLECTEED:
                            imageFrom = ImagePagerActivity.IMAGE_FROM_COLLECT;
                            break;
                    }
                    ImagePagerActivity.launch(
                            view.getContext(),
                            imageFrom,
                            chatFileInfoEntities,
                            0,
                            item.ope,
                            item.to,
                            view);
                }
                break;
        }
    }
}
