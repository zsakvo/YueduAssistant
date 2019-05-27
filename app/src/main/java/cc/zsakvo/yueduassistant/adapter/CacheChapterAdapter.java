package cc.zsakvo.yueduassistant.adapter;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;

import java.util.List;

import cc.zsakvo.yueduassistant.R;
import cc.zsakvo.yueduassistant.bean.CacheChapter;

public class CacheChapterAdapter extends BaseQuickAdapter<CacheChapter, BaseViewHolder> {

    private List<Boolean> flags;

    public CacheChapterAdapter(int layoutResId, List<CacheChapter> data,List<Boolean> flags) {
        super(layoutResId, data);
        this.flags = flags;
    }

    @Override
    protected void convert(BaseViewHolder helper, CacheChapter item) {
        CheckBox checkBox = helper.getView(R.id.cache_chapter_check);
        checkBox.setText(item.getName());
        checkBox.setChecked(true);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> flags.set(helper.getLayoutPosition(),!flags.get(helper.getLayoutPosition())));
    }
}
