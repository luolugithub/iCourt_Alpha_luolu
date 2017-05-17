package com.icourt.alpha.view.recyclerviewDivider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * Description  聊天界面 时间分割线  间隔5分钟
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/29
 * version 1.0.0
 */
public class ChatItemDecoration extends RecyclerView.ItemDecoration {


    private Paint mPaint;
    private Rect mBounds;
    private float mTextSize;
    private static final int DEFAULE_COLOR_TEXT = 0xFFA6A6A6;
    private static final int DEFAULE_COLOR_LINE = 0xFFE7E7E7;
    private float lineHeight;//线条高度
    private float dividerHeight;//整个分割线高度
    private ITimeDividerInterface iTimeDividerInterface;

    private float sp2px(@NonNull Context context, int sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    private float dp2px(@NonNull Context context, int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public ChatItemDecoration(@NonNull Context context, @NonNull ITimeDividerInterface iTimeDividerInterface) {
        this.mPaint = new Paint();
        this.mPaint.setTextSize(mTextSize = sp2px(context, 13));
        this.mPaint.setColor(DEFAULE_COLOR_TEXT);
        this.mPaint.setAntiAlias(true);
        this.mBounds = new Rect();
        this.lineHeight = dp2px(context, 1);
        this.dividerHeight = mTextSize * 3;
        this.iTimeDividerInterface = iTimeDividerInterface;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (iTimeDividerInterface == null) {
            super.onDraw(c, parent, state);
            return;
        }
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) parent.getChildAt(i)
                    .getLayoutParams();
            //int position = params.getViewLayoutPosition();
            int adapterPosition = params.getViewAdapterPosition();
            if (iTimeDividerInterface.isShowTimeDivider(adapterPosition)) {

                final View child = parent.getChildAt(i);
                String tag = iTimeDividerInterface.getShowTime(adapterPosition);
                mPaint.setColor(DEFAULE_COLOR_TEXT);
                mPaint.getTextBounds(tag, 0, tag.length(), mBounds);
                if (mBounds.width() >= child.getWidth()) {
                    c.drawText(tag, 0, child.getBottom(), mPaint);
                } else {
                    float txtStartX = (child.getWidth() - mBounds.width()) / 2;
                    float txtEndX = txtStartX + mBounds.width();
                    float dividerCenterY = dividerHeight / 2 + child.getBottom();

                    //画中间文本
                    c.drawText(tag, txtStartX, dividerCenterY + mTextSize * 0.25f, mPaint);

                    float lineWidth = mTextSize * 2;
                    float lineTxtMargin = mTextSize;

                    float leftLineStartX = txtStartX - lineWidth - lineTxtMargin;
                    float leftLineEndX = leftLineStartX + lineWidth;

                    float rightLineStartX = txtEndX + lineTxtMargin;
                    float rightLineEndX = rightLineStartX + lineWidth;

                    //画两边的线条
                    if (leftLineStartX > 0 && rightLineEndX < child.getWidth()) {
                        mPaint.setColor(DEFAULE_COLOR_LINE);
                        c.drawRect(leftLineStartX, dividerCenterY - lineHeight * 0.5f, leftLineEndX, dividerCenterY + lineHeight * 0.5f, mPaint);
                        mPaint.setColor(DEFAULE_COLOR_LINE);
                        c.drawRect(rightLineStartX, dividerCenterY - lineHeight * 0.5f, rightLineEndX, dividerCenterY + lineHeight * 0.5f, mPaint);
                    }
                }
            }
        }
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        int adapterPosition = params.getViewAdapterPosition();
        if (iTimeDividerInterface != null && iTimeDividerInterface.isShowTimeDivider(adapterPosition)) {
            outRect.set(0, 0, 0, (int) dividerHeight);
            //outRect.set(0, 0, (int) dividerHeight, 0);
        }
    }
}
