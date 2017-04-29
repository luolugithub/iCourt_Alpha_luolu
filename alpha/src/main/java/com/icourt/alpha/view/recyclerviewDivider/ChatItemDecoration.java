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

    private float sp2px(@NonNull Context context, int sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    private float dp2px(@NonNull Context context, int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public ChatItemDecoration(@NonNull Context context) {
        mPaint = new Paint();
        mPaint.setTextSize(mTextSize = sp2px(context, 13));
        mPaint.setColor(DEFAULE_COLOR_TEXT);
        mPaint.setAntiAlias(true);
        mBounds = new Rect();
        lineHeight = dp2px(context, 1);
        dividerHeight = mTextSize * 3;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        //super.onDraw(c, parent, state);
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) parent.getChildAt(i)
                    .getLayoutParams();
            int position = params.getViewLayoutPosition();
            if (position % 3 != 0) continue;//注意 这里为模拟 找到你adapter中的时间分割点
            final View child = parent.getChildAt(i);
            String tag = "上午 10:36";//模拟数据
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


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0, 0, 0, (int) dividerHeight);
    }
}
