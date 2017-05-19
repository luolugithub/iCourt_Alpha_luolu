package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.IMContactAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.convertor.ListConvertor;
import com.icourt.alpha.db.dbmodel.ContactDbModel;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.PinyinComparator;
import com.icourt.alpha.widget.filter.ListFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmResults;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/19
 * version 1.0.0
 */
public class ContactSelectDialogFragment extends BaseDialogFragment {

    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.bt_cancel)
    TextView btCancel;
    @BindView(R.id.bt_ok)
    TextView btOk;
    Unbinder unbinder;
    IMContactAdapter imContactAdapter;

    public static ContactSelectDialogFragment newInstance(@Nullable ArrayList<GroupContactBean> selectedList) {
        ContactSelectDialogFragment contactSelectDialogFragment = new ContactSelectDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("data", selectedList);
        contactSelectDialogFragment.setArguments(args);
        return contactSelectDialogFragment;
    }

    OnFragmentCallBackListener onFragmentCallBackListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onFragmentCallBackListener = (OnFragmentCallBackListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.dialog_fragment_contact_select, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    private ArrayList<GroupContactBean> getSelectedData() {
        return (ArrayList<GroupContactBean>) getArguments().getSerializable("data");
    }

    @Override
    protected void initView() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setGravity(Gravity.BOTTOM);
                View decorView = window.getDecorView();
                if (decorView != null) {
                    int dp20 = DensityUtil.dip2px(getContext(), 20);
                    decorView.setPadding(dp20 / 2, dp20, dp20 / 2, dp20);
                }
            }
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(imContactAdapter = new IMContactAdapter());
        imContactAdapter.setSelectable(true);
        imContactAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                imContactAdapter.toggleSelected(position);
            }
        });
        getData(true);
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        Observable.create(new ObservableOnSubscribe<ArrayList<GroupContactBean>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<GroupContactBean>> e) throws Exception {
                if (e.isDisposed()) return;
                ArrayList<GroupContactBean> contactBeen = new ArrayList<GroupContactBean>();
                try {
                    ContactDbService contactDbService = new ContactDbService(getLoginUserId());
                    RealmResults<ContactDbModel> contactDbModels = contactDbService.queryAll();
                    if (contactDbModels != null) {
                        contactBeen.addAll(ListConvertor.convertList(new ArrayList<IConvertModel<GroupContactBean>>(contactDbModels)));
                        contactBeen.removeAll(getSelectedData());
                        filterRobot(contactBeen);
                        Collections.sort(contactBeen, new PinyinComparator<GroupContactBean>());
                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
                e.onNext(contactBeen);
                e.onComplete();
            }
        }).compose(this.<ArrayList<GroupContactBean>>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<GroupContactBean>>() {
                    @Override
                    public void accept(ArrayList<GroupContactBean> groupContactBeen) throws Exception {
                        imContactAdapter.bindData(true, groupContactBeen);
                    }
                });
    }

    /**
     * 过滤掉 机器人（robot == 100）
     *
     * @param data
     * @return
     */
    private List<GroupContactBean> filterRobot(List<GroupContactBean> data) {
        return new ListFilter<GroupContactBean>().filter(data, GroupContactBean.TYPE_ROBOT);
    }

    @OnClick({R.id.bt_cancel, R.id.bt_ok})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_cancel:
                dismiss();
                break;
            case R.id.bt_ok:
                if (getParentFragment() instanceof OnFragmentCallBackListener) {
                    onFragmentCallBackListener = (OnFragmentCallBackListener) getParentFragment();
                }
                if (onFragmentCallBackListener != null) {
                    Bundle params = new Bundle();
                    params.putSerializable(KEY_FRAGMENT_RESULT, imContactAdapter.getSelectedData());
                    onFragmentCallBackListener.onFragmentCallBack(this, 0, params);
                }
                dismiss();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
