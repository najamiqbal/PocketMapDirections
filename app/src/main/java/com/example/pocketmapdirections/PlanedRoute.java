package com.example.pocketmapdirections;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.pocketmapdirections.adapters.PlanedRouteAdapter;
import com.example.pocketmapdirections.adapters.favoriteplacesAdapter;
import com.example.pocketmapdirections.models.PlanedRouteModel;
import com.example.pocketmapdirections.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class PlanedRoute extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    DatabaseHelper db;
    private DividerItemDecoration dividerItemDecoration;
    List<PlanedRouteModel> planedRouteModelList;
    PlanedRouteAdapter adapter;
    FloatingActionButton addroute;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.planed_route);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Select Favorite Place");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new DatabaseHelper(PlanedRoute.this);
        recyclerView = findViewById(R.id.recycler_view_planedroute);
        addroute=findViewById(R.id.addrouteplan);
        addroute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent=new Intent(PlanedRoute.this,MapsActivity.class);
               // startActivity(intent);
                finish();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        planedRouteModelList = new ArrayList<>();
        planedRouteModelList.addAll(db.getAllRoutes());
/*
        planedRouteModelList.add(new PlanedRouteModel(1, "ugoki", "sialkot", "20-12-2014", "12:23"));
        planedRouteModelList.add(new PlanedRouteModel(2, "ugoki", "sialkot", "20-12-2014", "12:23"));
        planedRouteModelList.add(new PlanedRouteModel(3, "ugoki", "sialkot", "20-12-2014", "12:23"));
*/

        adapter = new PlanedRouteAdapter(this, planedRouteModelList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //menu item selected
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
