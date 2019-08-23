package cc.zsakvo.yueduassistant.adapter;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;

import java.util.List;

import cc.zsakvo.yueduassistant.R;
import cc.zsakvo.yueduassistant.bean.CacheBook;


public class CacheBookAdapter extends BaseQuickAdapter<CacheBook, BaseViewHolder>{

    public CacheBookAdapter(int layoutResId, List<CacheBook> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CacheBook item) {
        helper.getView(R.id.card_cache_book).setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ObjectAnimator downAnimator = ObjectAnimator.ofFloat(view, "translationZ", 16);
                        downAnimator.setDuration(200);
                        downAnimator.setInterpolator(new DecelerateInterpolator());
                        downAnimator.start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        ObjectAnimator upAnimator = ObjectAnimator.ofFloat(view, "translationZ", 0);
                        upAnimator.setDuration(200);
                        upAnimator.setInterpolator(new AccelerateInterpolator());
                        upAnimator.start();
                        break;
                }
                return false;
            }
        });
        helper.setText(R.id.card_cache_book_name,"《"+item.getName()+"》");
        helper.setText(R.id.card_cache_book_source,"\t共"+item.getChapterNum()+"章\n\t来源："+item.getSource());
    }
}


