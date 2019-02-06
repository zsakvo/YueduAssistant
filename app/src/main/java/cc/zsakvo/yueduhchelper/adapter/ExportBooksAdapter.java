package cc.zsakvo.yueduhchelper.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cc.zsakvo.yueduhchelper.R;
import cc.zsakvo.yueduhchelper.bean.ExportChapter;

public class ExportBooksAdapter extends RecyclerView.Adapter{

    private List<ExportChapter> mExport = new ArrayList<>();
    private List<Boolean> flag;

    public ExportBooksAdapter(List<ExportChapter> mExport,List<Boolean> flag){
        this.mExport = mExport;
        this.flag = flag;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.export_list, parent, false);
        return new ExportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ExportViewHolder myViewHolder = (ExportViewHolder) holder;
        myViewHolder.checkBox.setText(mExport.get(position).getChapterInfo());
        myViewHolder.checkBox.setOnCheckedChangeListener(null);
        myViewHolder.checkBox.setChecked(flag.get(position));
        myViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                flag.set(position,b);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mExport.size();
    }

    private class ExportViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkBox;

        public ExportViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.export_check);
        }
    }
}
