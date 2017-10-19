package com.icourt.alpha.view.recyclerviewDivider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

import com.icourt.alpha.R;
import com.icourt.alpha.utils.DensityUtil;

/**
 * Description  任务列表的分割线
 * Company Beijing icourt
 * author  zhaodanyang  E-mail:zhaodanyang@icourt.cc
 * date createTime：2017/8/31
 * version 2.0.0
 */
public class TastItemDecoration extends RecyclerView.ItemDecoration {

    private Context mContext;

    private DecorationCallBack mDecorationCallBack;//返回分割线标题和要显示的数量的回调

    private int mTitleHeight;//title的高

    private TextPaint mTextPaint;//文字的画笔

    private Paint mPaint;//线的画笔

    private int mGroupTextSize;//分组标题的大小

    private int mCountTextSize;//分组数字的大小

    private int mLineWidth;//线的宽度

    private Rect mBounds;//文字所占矩形空间的大小


    public TastItemDecoration(Context context, DecorationCallBack decorationCallBack) {
        this.mContext = context;
        this.mDecorationCallBack = decorationCallBack;
        this.mTitleHeight = DensityUtil.dip2px(mContext, 46);
        this.mGroupTextSize = DensityUtil.sp2px(mContext, 15);
        this.mCountTextSize = DensityUtil.sp2px(mContext, 14);
        this.mLineWidth = DensityUtil.dip2px(mContext, 31);
        this.mBounds = new Rect();
        this.mTextPaint = new TextPaint();
        this.mTextPaint.setAntiAlias(true);
        this.mTextPaint.setTextAlign(Paint.Align.LEFT);
        this.mPaint = new Paint();
        this.mPaint.setColor(ContextCompat.getColor(mContext, R.color.task_divider_line_color));
        this.mPaint.setAntiAlias(true);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int childPosition = parent.getChildAdapterPosition(view);
        if (childPosition < 0)
            return;
        if (isFirstInGroup(childPosition)) {
            outRect.top = mTitleHeight;
        } else {
            outRect.top = 0;
        }
    }


    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
//        LogUtils.d("====------------>updata2:"+Thread.currentThread().getName());
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);
            if (isFirstInGroup(position)) {
                //绘制分割线内容的矩形区域
                int left = view.getLeft() + DensityUtil.dip2px(mContext, 5);
                int top = view.getTop() + mTitleHeight;
                int right = view.getRight() - DensityUtil.dip2px(mContext, 5);//因为CardView自带边距margin值，所以要减去5dp。
                int bottom = view.getTop();
                int centerY = bottom - mTitleHeight / 2;
                //所要画的线是个矩形，要确定矩形的上下坐标。
                int lineStartY = centerY - DensityUtil.dip2px(mContext, 0.5f);
                int lineEndY = centerY + DensityUtil.dip2px(mContext, 0.5f);
                //先画第一条线
                c.drawRect(left, lineStartY, left + mLineWidth, lineEndY, mPaint);
                //绘制分组文本
                String groupName = mDecorationCallBack.getGroupName(position);
                mTextPaint.setTextSize(mGroupTextSize);
                mTextPaint.setColor(ContextCompat.getColor(mContext, R.color.task_divider_text_color));
                mTextPaint.getTextBounds(groupName, 0, groupName.length(), mBounds);
                int txtStartX = left + mLineWidth + DensityUtil.dip2px(mContext, 10);
                int txtEndX = txtStartX + mBounds.width();
                c.drawText(groupName, txtStartX, centerY + mGroupTextSize * 0.25f, mTextPaint);
                //绘制第二条线
                c.drawRect(txtEndX + DensityUtil.dip2px(mContext, 10), lineStartY, txtEndX + DensityUtil.dip2px(mContext, 10) + mLineWidth, lineEndY, mPaint);
                //绘制数量
                mTextPaint.setTextSize(mCountTextSize);
                mTextPaint.setColor(ContextCompat.getColor(mContext, R.color.task_divider_count_color));
                String count = String.valueOf(mDecorationCallBack.getCountByGroupName(groupName));
                mTextPaint.getTextBounds(count, 0, count.length(), mBounds);
                int countStartX = right - mBounds.width();
                c.drawText(count, countStartX, centerY + mCountTextSize * 0.25f, mTextPaint);
            }
        }
    }

    /**
     * 判断是不是在这个组里的第一个
     *
     * @param position
     * @return
     */
    private boolean isFirstInGroup(int position) {
        if (TextUtils.isEmpty(mDecorationCallBack.getGroupName(position)))
            return false;
        String preGroupName = mDecorationCallBack.getGroupName(position - 1);
        String groupName = mDecorationCallBack.getGroupName(position);
        return !TextUtils.equals(preGroupName, groupName);
    }

    public interface DecorationCallBack {
        /**
         * 返回任务所在分组的名称
         *
         * @param position
         * @return
         */
        String getGroupName(int position);

        /**
         * 返回分组有多少个item
         *
         * @param groupName
         * @return
         */
        int getCountByGroupName(String groupName);

    }
}
