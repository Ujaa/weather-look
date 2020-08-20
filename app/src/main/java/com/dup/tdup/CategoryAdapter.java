package com.dup.tdup;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private ItemInfo[] itemInfoArr;
    private Context context;
    //private View.OnClickListener onClickItem;
    private OnClickListener mListener;

    public interface OnClickListener{
        void onClick(int position);
    }

    public void setOnClickListener(OnClickListener listener){
        mListener = listener;
    }

    public CategoryAdapter(final Context _context, final ItemInfo[] _itemArr) {
        this.context = _context;
        this.itemInfoArr = _itemArr;
        /*
        onClickItem = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = (String) view.getTag();
                int position = getAdapterPosition();
                Intent intent = new Intent(context.getApplicationContext(), DetailActivity.class);

                intent.putExtra("img", itemInfoList.get(position).imgSrc);
                intent.putExtra("name", itemInfoList.get(position).name);
                intent.putExtra("price", itemInfoList.get(position).price);

                context.startActivity(intent);
            }
        };

         */
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Glide.with(context)
                .asBitmap()
                .load(itemInfoArr[position].imgSrc)
                .into(holder.itemImageView);

        //holder.itemImageView.setOnClickListener(onClickItem);
        holder.itemImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = (String) view.getTag();
                Intent intent = new Intent(context.getApplicationContext(), DetailActivity.class);

                intent.putExtra("img", itemInfoArr[position].imgSrc);
                intent.putExtra("name", itemInfoArr[position].name);
                intent.putExtra("price", itemInfoArr[position].price);
                intent.putExtra("itemID", itemInfoArr[position].itemID);
                intent.putExtra("category", itemInfoArr[position].category);

                context.startActivity(intent);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView itemImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemImageView = itemView.findViewById(R.id.category_item_img);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
