package org.sookmyung.weatherlook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.sookmyung.weatherlook.R;

import java.util.ArrayList;

// <희> 상위 카테고리를 표시할 vertical recyclerview
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    public ArrayList<Category> categories;

    public CategoryAdapter(Context context, ArrayList<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.recyclerView.setAdapter(new ItemAdapter(context, categories.get(position).items));
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerView.setHasFixedSize(true);
        holder.tvHeading.setText(categories.get(position).categoryName);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    // 보여줄 항목: 하위 카테고리를 표시할 horizontal recyclerview, 상위 카테고리 이름
    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        TextView tvHeading;

        public ViewHolder(View itemView) {
            super(itemView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.rvItems);
            tvHeading = (TextView) itemView.findViewById(R.id.tvCategoryName);
        }
    }
}
