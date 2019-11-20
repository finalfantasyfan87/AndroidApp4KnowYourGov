package com.csc472.hw4knowyourgov.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.csc472.hw4knowyourgov.R;

public class OfficialViewHolder extends RecyclerView.ViewHolder {
    public TextView office;
    public TextView nameParty;

    public OfficialViewHolder(View view){
        super(view);
        office = view.findViewById(R.id.Office);
        nameParty = view.findViewById(R.id.Official);
    }
}
