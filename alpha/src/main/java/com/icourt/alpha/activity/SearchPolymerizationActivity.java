package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.SearchPolymerizationAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.convertor.ListConvertor;
import com.icourt.alpha.db.dbmodel.ContactDbModel;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.entity.bean.SearchItemEntity;
import com.icourt.alpha.entity.bean.SearchPolymerizationEntity;
import com.icourt.alpha.utils.GlobalMessageObserver;
import com.icourt.alpha.utils.SpannableUtils;
import com.icourt.alpha.view.SoftKeyboardSizeWatchLayout;
import com.icourt.alpha.widget.filter.ListFilter;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.search.model.MsgIndexRecord;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmResults;

/**
 * Description  聚合搜索
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/24
 * version 1.0.0
 */
public class SearchPolymerizationActivity extends BaseActivity {


    @BindView(R.id.et_search_name)
    EditText etSearchName;
    @BindView(R.id.tv_search_cancel)
    TextView tvSearchCancel;
    @BindView(R.id.search_project_tv)
    TextView searchProjectTv;
    @BindView(R.id.search_task_tv)
    TextView searchTaskTv;
    @BindView(R.id.search_group_tv)
    TextView searchGroupTv;
    @BindView(R.id.search_customer_tv)
    TextView searchCustomerTv;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.softKeyboardSizeWatchLayout)
    SoftKeyboardSizeWatchLayout softKeyboardSizeWatchLayout;
    SearchPolymerizationAdapter searchPolymerizationAdapter;
    @BindView(R.id.search_classfy_ll)
    LinearLayout searchClassfyLl;
    int foregroundColor = 0xFFed6c00;

    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, SearchPolymerizationActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_polymerization);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(searchPolymerizationAdapter = new SearchPolymerizationAdapter());
        etSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    searchPolymerizationAdapter.clearData();
                    recyclerView.setVisibility(View.GONE);
                    searchClassfyLl.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    searchClassfyLl.setVisibility(View.GONE);
                    getData(true);
                }
            }
        });
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        final String keyWord = etSearchName.getText().toString();
        Observable.create(new ObservableOnSubscribe<List<SearchPolymerizationEntity>>() {
            @Override
            public void subscribe(ObservableEmitter<List<SearchPolymerizationEntity>> e) throws Exception {
                if (e.isDisposed()) return;
                List<SearchPolymerizationEntity> result = new ArrayList<SearchPolymerizationEntity>();

                //查询联系人
                ContactDbService contactDbService = new ContactDbService(getLoginUserId());
                RealmResults<ContactDbModel> name = contactDbService.contains("name", keyWord);
                List<GroupContactBean> contactBeen = ListConvertor.convertList(new ArrayList<IConvertModel<GroupContactBean>>(name));
                fiterRobots(contactBeen);
                contactDbService.releaseService();

                List<SearchItemEntity> searchItemEntities = convert2SearchItem(contactBeen, keyWord);
                if (searchItemEntities != null && !searchItemEntities.isEmpty()) {
                    //添加联系人
                    result.add(new SearchPolymerizationEntity(SearchPolymerizationEntity.TYPE_CONTACT,
                            "联系人", "查看更多联系人", convert2SearchItem(contactBeen, keyWord)));
                }


                List<MsgIndexRecord> msgindexs = NIMClient.getService(MsgService.class).searchAllSessionBlock(keyWord, 4);
                if (msgindexs != null && !msgindexs.isEmpty()) {
                    List<SearchItemEntity> data = new ArrayList<>();
                    for (MsgIndexRecord item : msgindexs) {
                        if (item != null) {
                            IMMessageCustomBody imBody = GlobalMessageObserver.getIMBody(item.getRecord().content);
                            if (imBody != null
                                    && !TextUtils.isEmpty(imBody.content)
                                    && imBody.content.contains(keyWord)) {

                                CharSequence content;
                                if (item.getRecord().count > 1) {
                                    content = String.format("%s条相关聊天记录", item.getRecord().count);
                                } else {
                                    //瞄色
                                    CharSequence originalText = imBody.content;
                                    content = SpannableUtils.getTextForegroundColorSpan(originalText, keyWord, foregroundColor);
                                }
                                data.add(new SearchItemEntity("群组", content, "", keyWord));
                            }
                            log("------------>" + item);
                        }
                    }
                    if (!data.isEmpty()) {
                        //添加聊天记录
                        result.add(new SearchPolymerizationEntity(SearchPolymerizationEntity.TYPE_CHAT_HISTORTY, "聊天记录", "查看更多聊天记录", data));
                    }
                }

                e.onNext(result);
                e.onComplete();
            }
        }).compose(this.<List<SearchPolymerizationEntity>>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<SearchPolymerizationEntity>>() {
                    @Override
                    public void accept(List<SearchPolymerizationEntity> searchPolymerizationEntities) throws Exception {
                        searchPolymerizationAdapter.bindData(true, searchPolymerizationEntities);
                    }
                });
    }

    private List<SearchItemEntity> convert2SearchItem(List<GroupContactBean> contactBeen, String keyWord) {
        List<SearchItemEntity> data = new ArrayList<>();
        if (contactBeen != null) {
            for (GroupContactBean item : contactBeen) {
                if (item != null) {
                    CharSequence originalText = item.name;
                    data.add(new SearchItemEntity(SpannableUtils.getTextForegroundColorSpan(originalText, keyWord, foregroundColor), null, item.pic, keyWord));
                }
            }
        }
        return data;
    }

    /**
     * 过滤掉机器人
     *
     * @param contactBeen
     */
    private List<GroupContactBean> fiterRobots(List<GroupContactBean> contactBeen) {
        return new ListFilter<GroupContactBean>().filter(contactBeen, GroupContactBean.TYPE_ROBOT);
    }


    @OnClick({R.id.tv_search_cancel})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search_cancel:
                finish();
                break;
            default:
                super.onClick(v);
                break;
        }
    }
}
