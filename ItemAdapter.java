package com.dup.tdup;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// 기존 CategoryAdapter 수정 후 ItemAdapter로 이름 변경
// CategoryAdapter 다른 내용으로 신규 생성
// <희> - 하위 카테고리를 표시할 horizontal recyclerview
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ItemInfo> items;
    //private ArrayList<Item> items;
    //private View.OnClickListener onClickItem;
    private OnClickListener mListener;

    public interface OnClickListener{
        void onClick(int position);
    }

    public void setOnClickListener(OnClickListener listener){
        mListener = listener;
    }

    public ItemAdapter(Context context, ArrayList<ItemInfo> items) {
        this.context = context;
        this.items = items;
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
        View view = LayoutInflater.from(context).inflate(R.layout.single_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ItemInfo item = items.get(position);
        holder.tvItemName.setText(item.name);
        Glide.with(context)
                .asBitmap()
                .load(items.get(position).imgSrc)
                .into(holder.ivItem);

        //상세 페이지에 정보 전달
        //holder.itemImageView.setOnClickListener(onClickItem);
        holder.ivItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = (String) view.getTag();
                Intent intent = new Intent(context.getApplicationContext(), DetailActivity.class);

                intent.putExtra("img", items.get(position).imgSrc);
                intent.putExtra("name", items.get(position).name);
                intent.putExtra("price", items.get(position).price);
                intent.putExtra("itemID", items.get(position).itemID);
                intent.putExtra("category", items.get(position).category);

                context.startActivity(intent);
            }
        });
    }

    // 보여줄 항목: 상품 사진, 이름
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivItem;
        public TextView tvItemName;

        public ViewHolder(View itemView) {
            super(itemView);
            tvItemName = (TextView) itemView.findViewById(R.id.tvItemName);
            ivItem = (ImageView) itemView.findViewById(R.id.ivItem);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
