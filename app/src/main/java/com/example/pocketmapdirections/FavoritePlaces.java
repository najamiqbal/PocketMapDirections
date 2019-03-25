package com.example.pocketmapdirections;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.pocketmapdirections.adapters.favoriteplacesAdapter;
import com.example.pocketmapdirections.models.PlacesModel;
import com.example.pocketmapdirections.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class FavoritePlaces extends AppCompatActivity {
    android.support.v7.widget.Toolbar m_toolbar;
    RecyclerView recyclerView;
    DatabaseHelper db;
    private DividerItemDecoration dividerItemDecoration;
    favoriteplacesAdapter adapter;
    List<PlacesModel> placesList;
    FloatingActionButton addplace;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_places);
        m_toolbar = findViewById(R.id.toolbar);
        m_toolbar.setTitle("Select Favorite Place");
        setSupportActionBar(m_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new DatabaseHelper(this);
        recyclerView=findViewById(R.id.recycler_view);
        addplace=findViewById(R.id.addplace);
        addplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FavoritePlaces.this,SavePlaceMapActivity.class);
                startActivity(intent);
            }
        });
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        placesList = new ArrayList<>();
        placesList.addAll(db.getAllNotes());
        adapter = new favoriteplacesAdapter(this, placesList);

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
