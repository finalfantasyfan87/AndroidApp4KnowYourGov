package com.csc472.hw4knowyourgov.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.csc472.hw4knowyourgov.R;
import com.csc472.hw4knowyourgov.activity.MainActivity;
import com.csc472.hw4knowyourgov.model.Official;

import java.util.List;

public class OfficialAdapter extends RecyclerView.Adapter<OfficialViewHolder> {
    private List<Official> listOfOfficials;
    private MainActivity mainActivity;

    public OfficialAdapter (List<Official> officialList, MainActivity activity){
        this.listOfOfficials = officialList;
        mainActivity = activity;
    }

    @Override
    public OfficialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.official_list_item, parent, false);
        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);
        return new OfficialViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OfficialViewHolder holder, int position) {
        Official official = listOfOfficials.get(position);
        holder.office.setText(official.getOffice());
        holder.nameParty.setText(official.getName() + " (" + official.getParty() + ")");
    }

    @Override
    public int getItemCount() {
        return listOfOfficials.size();
    }
}
