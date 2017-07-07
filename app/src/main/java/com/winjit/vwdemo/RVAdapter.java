package com.winjit.vwdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static android.view.LayoutInflater.from;

/**
 * Created by SachinR on 7/5/2017.
 */
public class RVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private RecyclerView manager;
    private Context context;
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        manager= recyclerView;
    }
    
    public RVAdapter(Context context) {
        inflater = from(context);
        this.context = context;
    }
    
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.row_rv_text, parent, false);
        return new Holder(v);
    }
    
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Holder hold = (Holder) holder;
        hold.tv.setText("Hello World " + (position+1));
        
    }
    
    @Override
    public int getItemCount() {
        return 10;
    }
   
    private class Holder extends RecyclerView.ViewHolder{
        TextView tv;
        Holder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tvText);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = getAdapterPosition();
                    manager.smoothScrollToPosition(adapterPosition);
                }
            });
        }
    }
}
