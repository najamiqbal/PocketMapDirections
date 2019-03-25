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

import com.example.pocketmapdirections.MapsActivity;
import com.example.pocketmapdirections.PlanedRoute;
import com.example.pocketmapdirections.R;
import com.example.pocketmapdirections.models.PlanedRouteModel;
import com.example.pocketmapdirections.utils.DatabaseHelper;

import java.util.List;

public class PlanedRouteAdapter extends RecyclerView.Adapter<PlanedRouteAdapter.ViewHolder> {
    Context context;
    List<PlanedRouteModel> planedRouteModelList;
    private DatabaseHelper db;

    public PlanedRouteAdapter(Context context, List<PlanedRouteModel> planedRouteModelList) {
        this.db=new DatabaseHelper(context.getApplicationContext());
        this.context = context;
        this.planedRouteModelList = planedRouteModelList;
    }

    @NonNull
    @Override
    public PlanedRouteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.planed_route_list_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanedRouteAdapter.ViewHolder viewHolder, final int i) {
        final PlanedRouteModel planedRouteModel = planedRouteModelList.get(i);
        viewHolder.startpoint.setText(planedRouteModel.getStartpoint());
        viewHolder.endpoint.setText(planedRouteModel.getEndpoint());
        viewHolder.datetime.setText(planedRouteModel.getDate()+" "+planedRouteModel.getTime());
        viewHolder.DeleteRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = planedRouteModelList.get(i).getId();
                Log.d("check","positionIS"+i);
                DatabaseHelper database = new DatabaseHelper(context);
                Log.d("check","THISISID"+id);
                db.deleteRoute(id);
                planedRouteModelList.remove(i);
                notifyDataSetChanged();
            }
        });
        viewHolder.navRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String start=planedRouteModel.getStartpoint();
                String end=planedRouteModel.getEndpoint();
                if (!start.isEmpty() && !end.isEmpty()){
                    Log.d("yeh","endPoint"+end);
                    Intent intent=new Intent(context,MapsActivity.class);
                    intent.putExtra("endpoint",end);
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
        return planedRouteModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView startpoint,endpoint,datetime;
        ImageButton navRoute,DeleteRoute;
        RelativeLayout relativeLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            startpoint=itemView.findViewById(R.id.starttv);
            endpoint=itemView.findViewById(R.id.destinationtv);
            datetime=itemView.findViewById(R.id.datetime);
            navRoute=itemView.findViewById(R.id.NavRoute);
            DeleteRoute=itemView.findViewById(R.id.deleteRoute);
            relativeLayout=itemView.findViewById(R.id.rowlayoutPlaned);
        }
    }
}
