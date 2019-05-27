package cc.zsakvo.yueduassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cc.zsakvo.yueduassistant.R;
import cc.zsakvo.yueduassistant.bean.CacheBook;


public class CacheBookAdapter extends BaseQuickAdapter<CacheBook, BaseViewHolder>{

    public CacheBookAdapter(int layoutResId, List<CacheBook> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CacheBook item) {
        helper.setText(R.id.card_cache_book_name,"《"+item.getName()+"》");
        helper.setText(R.id.card_cache_book_source,"\t共"+item.getChapterNum()+"章\t\t\t来源："+item.getSource());
    }
}


