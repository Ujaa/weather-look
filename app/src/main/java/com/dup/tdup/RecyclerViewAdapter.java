package com.dup.tdup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import android.widget.Toast;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>
{

    private Context mContext;
    private List<Outfit> outfitList;
    static int selectedOutfitID;

    //Constructor
    public RecyclerViewAdapter(Context mContext, List<Outfit> outfitList)
    {
        this.mContext = mContext;
        this.outfitList = outfitList;
    }//end constructor


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.card_view_item, viewGroup, false);
        return new MyViewHolder(view);
    }//end onCreateViewHolder

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i)
    {
        String category = outfitList.get(i).getCategory();
        String description = "";
        if(category.equals("top")){description = "Top";}
        if(category.equals("long_wears")){description = "Long Wears";}
        if(category.equals("trousers")){description = "Trousers";}
        if(category.equals("shorts_n_skirts")){description = "Shorts and Skirts";}

        // 추가
        if(category.equals("hats")){description = "Hats";}
        if(category.equals("scarves")){description = "Scarves";}
        if(category.equals("shoes")){description = "Shoes";}

        myViewHolder.textView_description.setText(description);
        byte[] img_byte = outfitList.get(i).getImage(); //get image as byte array
        //convert byte array image to bitmap
        Bitmap img_bitmap = BitmapFactory.decodeByteArray(img_byte, 0, img_byte.length);
        myViewHolder.img_item.setImageBitmap(img_bitmap);

        //set click listener
        myViewHolder.cardView_item.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int item_id = outfitList.get(i).getId();
                String item_category = outfitList.get(i).getCategory();
                byte[] item_image = outfitList.get(i).getImage();

                DrawView.currentOutfit = new Outfit(item_id, item_category, item_image);
                Intent intent = new Intent(mContext, FitPreviewActivity.class);
                mContext.startActivity(intent);
            }
        });

        myViewHolder.cardView_item.setOnLongClickListener(new View.OnLongClickListener()
        {
            public boolean onLongClick(View arg0)
            {
                selectedOutfitID = outfitList.get(i).getId();
                return false;
            }
        });

        //소정추가
        //set checkbox click listener
        myViewHolder.checkbox_item.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            //@Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 체크박스를 클릭해서 상태가 바꾸었을 경우 호출되는 콜백 메서드

                if (buttonView.getId() == R.id.card_view_item_checkbox) {
                    if (isChecked) {
                        Toast.makeText(mContext.getApplicationContext(),"선택", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext.getApplicationContext(), "선택 취소", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


    }//end onBindViewHolder

    @Override
    public int getItemCount() {
        return outfitList.size();
    }


    public static class MyViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener
    {
        CardView cardView_item;
        TextView textView_description;
        ImageView img_item;
        CheckBox checkbox_item;  //소정추가
        Button bt_item;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            cardView_item = (CardView) itemView.findViewById(R.id.card_view_item_frame_id);
            textView_description = (TextView) itemView.findViewById(R.id.card_view_item_description);
            img_item = (ImageView) itemView.findViewById(R.id.card_view_item_image);
            checkbox_item = (CheckBox) itemView.findViewById(R.id.card_view_item_checkbox);     //소정추가

            itemView.setOnCreateContextMenuListener(this);
        }//end Constructor


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
        {
            final int CONTEXT_OPTION_DELETE = 1;

            menu.setHeaderTitle("Choose ..");
            MenuItem menuDeleteItem = menu.add(this.getAdapterPosition(), CONTEXT_OPTION_DELETE, 0, "Delete");

            menuDeleteItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item)
                {
                    DatabaseManager databaseManager = new DatabaseManager(itemView.getContext());
                    databaseManager.deleteOutfitById(selectedOutfitID);
                    databaseManager.close();

                    return true;
                }
            });//end menuDeleteItem.setOnMenuItemClickListener
        }//end onCreateContextMenu
    }//end MyViewHolder
}//End RecyclerViewAdapter
