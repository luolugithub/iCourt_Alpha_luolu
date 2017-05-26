package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.view.audiowave.CustomVisualizer;
import com.icourt.alpha.widget.parser.AudioJsonParser;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Description
 * Company Beijing icourt
 * author  zhaolu  E-mail:zhaolu@icourt.cc
 * date createTime：2017/5/26
 * version 2.0.0
 */
public class AudioWaveDialogFragment extends BaseDialogFragment {

    Unbinder unbinder;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.visualizer)
    CustomVisualizer visualizer;
    @BindView(R.id.cancle_tv)
    TextView cancleTv;
    private StringBuffer voiceResultBuffer;

    public static AudioWaveDialogFragment newInstance() {
        return new AudioWaveDialogFragment();
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
        View view = super.onCreateView(R.layout.dialog_fragment_audio_wave_layout, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setGravity(Gravity.CENTER);
                View decorView = window.getDecorView();
                if (decorView != null) {
                    int dp20 = DensityUtil.dip2px(getContext(), 20);
                    decorView.setPadding(dp20 / 2, dp20, dp20 / 2, dp20);
                }
            }
        }
        voiceResultBuffer = new StringBuffer();
        showVoiceDialog();
    }

    private void showVoiceDialog() {
        //1.创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
        SpeechRecognizer mIat = SpeechRecognizer.createRecognizer(getContext(), null);
        //2.设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
        //3.开始听写
        mIat.startListening(mRecoListener);
    }

    private RecognizerListener mRecoListener = new RecognizerListener() {
        //听写结果回调接口(返回Json格式结果，用户可参见附录12.1)；
        //一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
        //关于解析Json的代码可参见MscDemo中JsonParser类；
        //isLast等于true时会话结束。
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.e("onResult:", results.getResultString());
            voiceResultBuffer.append(AudioJsonParser.parseIatResult(results.getResultString()));
            if (isLast) {
                String s2 = AudioJsonParser.parseIatResult(results.getResultString());
                voiceResultBuffer.append(s2);
                if (getParentFragment() instanceof OnFragmentCallBackListener) {
                    onFragmentCallBackListener = (OnFragmentCallBackListener) getParentFragment();
                }
                if (onFragmentCallBackListener != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_FRAGMENT_RESULT, voiceResultBuffer.toString());
                    onFragmentCallBackListener.onFragmentCallBack(AudioWaveDialogFragment.this, 0, bundle);
                }
                dismiss();
            }

        }

        //会话发生错误回调接口
        public void onError(SpeechError error) {
            error.getPlainDescription(true);//获取错误码描述

        }

        //开始录音
        public void onBeginOfSpeech() {
        }

        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            visualizer.prepare(i);
        }

        //结束录音
        public void onEndOfSpeech() {

        }

        //扩展用接口
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }
    };

    @OnClick({R.id.cancle_tv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancle_tv:
                dismiss();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
