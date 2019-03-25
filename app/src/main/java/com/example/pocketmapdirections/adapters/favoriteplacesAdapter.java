package com.example.pocketmapdirections.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pocketmapdirections.FavoritePlaces;
import com.example.pocketmapdirections.MapsActivity;
import com.example.pocketmapdirections.R;
import com.example.pocketmapdirections.SavePlaceMapActivity;
import com.example.pocketmapdirections.models.PlacesModel;
import com.example.pocketmapdirections.utils.DatabaseHelper;

import java.util.List;

public class favoriteplacesAdapter extends RecyclerView.Adapter<favoriteplacesAdapter.ViewHolder> {
    private Context context;
    private List<PlacesModel> placesModels;
    private DatabaseHelper db;

    public favoriteplacesAdapter(FavoritePlaces favoritePlaces, List<PlacesModel> placesList) {
        this.db=new DatabaseHelper(favoritePlaces.getApplicationContext());
        this.context = favoritePlaces;
        this.placesModels = placesList;

    }

    @NonNull
    @Override
    public favoriteplacesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favoriteplaces_list_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull favoriteplacesAdapter.ViewHolder viewHolder, final int i) {
        final PlacesModel placesModel = placesModels.get(i);
        viewHolder.title.setText(placesModel.getTitle());
        Log.d("idofitemis",""+placesModel.getId());
        viewHolder.subtitle.setText(placesModel.getSubtitle());
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = placesModels.get(i).getId();
                Log.d("check","positionIS"+i);
                DatabaseHelper database = new DatabaseHelper(context);
                Log.d("check","THISISID"+id);
                db.deleteEntry(id);
                placesModels.remove(i);
                notifyDataSetChanged();
            }
        });
        viewHolder.navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String location=placesModel.getSubtitle();
                if (!location.isEmpty()){
                    Intent intent=new Intent(context,MapsActivity.class);
                    intent.putExtra("location",location);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                    ((Activity)context).finish();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return placesModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        ImageButton navigate, delete;
        DatabaseHelper db;
        RelativeLayout relativeLayout;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            navigate = itemView.findViewById(R.id.navigate);
            delete = itemView.findViewById(R.id.delete);
            db = new DatabaseHelper(context);
            title = itemView.findViewById(R.id.placetitle);
            subtitle = itemView.findViewById(R.id.subtitle);
            relativeLayout = itemView.findViewById(R.id.rowlayout);

        }
    }

}
