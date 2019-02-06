package cc.zsakvo.yueduhchelper.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import cc.zsakvo.yueduhchelper.R;

public class Divider extends RecyclerView.ItemDecoration {

    private int dividerHeight;
    private Paint dividerPaint;
    private int padding;

    public Divider(Context context,int dip) {
        dividerPaint = new Paint();
        dividerPaint.setColor(context.getResources().getColor(R.color.divider));
        dividerHeight = context.getResources().getDimensionPixelSize(R.dimen.divider_height);
        this.padding = dip2px(context,dip);
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = dividerHeight;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        int left = padding;
        int right = parent.getWidth() - padding;

        for (int i = 0; i < childCount - 1; i++) {
            View view = parent.getChildAt(i);
            float top = view.getBottom();
            float bottom = view.getBottom() + dividerHeight;
            c.drawRect(left, top, right, bottom, dividerPaint);
        }
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
