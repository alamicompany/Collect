package com.alamicompany.collect;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends AppCompatActivity {
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    RecyclerView recyclerView;

    HelpRecyclerAdaper adapter;
    List<HelpItem> helpItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        collapsingToolbarLayout = (CollapsingToolbarLayout)     findViewById(R.id.collapsing_toolbar);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbarLayout.setTitle("Help");
        setSupportActionBar(toolbar);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        initializeData();
        adapter = new HelpRecyclerAdaper(helpItems);
        recyclerView.setAdapter(adapter);

    }
    private void initializeData() {
        helpItems = new ArrayList<>();
        helpItems.add(new HelpItem(1,"FAQ", R.drawable.ic_faq));
        helpItems.add(new HelpItem(2,"ContactUs", R.drawable.ic_contact_us));
        helpItems.add(new HelpItem(3,"Terms", R.drawable.ic_terms));
        helpItems.add(new HelpItem(4,"Licences", R.drawable.ic_licences));

    }
}
