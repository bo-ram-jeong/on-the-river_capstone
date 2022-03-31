package com.example.CapstonDesign;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SelectListAdpater extends RecyclerView.Adapter<SelectListAdpater.CustomViewHolder> {

    private ArrayList<SelectListData> arrayList;

    public SelectListAdpater(ArrayList<SelectListData> arrayList){
        this.arrayList = arrayList;
    }
    @NonNull
    @NotNull
    @Override
    public SelectListAdpater.CustomViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_tab_item,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SelectListAdpater.CustomViewHolder holder, int position) {
        holder.listImage.setImageResource(arrayList.get(position).getMarker());
        holder.tvNumber.setText(arrayList.get(position).getTvNumber());

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String curName = holder.tvNumber.getText().toString();
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != arrayList? arrayList.size():0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView listImage;
        protected TextView tvNumber;
        public CustomViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.listImage = itemView.findViewById(R.id.listImage);
            this.tvNumber = itemView.findViewById(R.id.tvNumber);
        }
    }
}
