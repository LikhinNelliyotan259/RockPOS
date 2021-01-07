package com.rockchipme.app.activities;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rockchipme.app.R;
import com.rockchipme.app.adapters.RecommendCategoriesAdapter;
import com.rockchipme.app.adapters.SearchAdapter;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.models.Products;
import com.rockchipme.app.models.SearchResponse;
import com.rockchipme.app.network.ApiCallRequest;
import com.rockchipme.app.network.ApiCallResponse;
import com.rockchipme.app.network.ApiCallServiceTask;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class SearchActivity extends AppCompatActivity implements ApiCallServiceTask.onApiFinish{

    private ApiCallServiceTask apiCallServiceTask;
    private String searchKey = "", apiKey;
    private List<Products> searchReultsList = new ArrayList<>();

    SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setUpToolBar();

        RecyclerView rvSarchResult = findViewById(R.id.rvItems);
        rvSarchResult.setHasFixedSize(true);
        RecommendCategoriesAdapter.CURRENT_POSITION = 0;
        rvSarchResult.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        searchAdapter = new SearchAdapter(this);
        // set the adapter object to the Recyclerview
        rvSarchResult.setAdapter(searchAdapter);

        apiCallServiceTask = new ApiCallServiceTask(this);

        if (!apiCallServiceTask.isNetworkAvailable(this)){
            Toast.makeText(this, "No internet connection available", Toast.LENGTH_SHORT).show();
        }

        apiKey = getSharedPreferences(Constants.PREF_LOGIN, MODE_PRIVATE).getString(Constants.api_key,"");
    }


    void setUpToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();

//        getSupportActionBar().setTitle("");

        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.mipmap.back_btn));//(helper.resize(getResources().getDrawable(R.mipmap.ic_settings)));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) SearchActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(SearchActivity.this.getComponentName()));
            searchView.onActionViewExpanded();
//            searchView.setFocusable(true);
            searchView.setQueryHint("Search...");

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    //Do something here
//                Toast.makeText(SearchActivity.this, "Search: " + query, Toast.LENGTH_SHORT ).show();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchKey = newText;
//                    Toast.makeText(SearchActivity.this, "search:" + searchKey, Toast.LENGTH_SHORT).show();
                    if (searchKey != null && searchKey.length() > 1) {
                        searchProductsApi();
                    } else {
                        setADapterData();
                    }
                    return false;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }


    public static final String FROM_SEARCH = "FROM_SEARCH";
    private void searchProductsApi(){
//        @POST("/products")
        if (searchKey!=null && !searchKey.equals("")) {
            Log.e(Constants.APP_TAG,"search product api body searchQuery: "+ searchKey);
            RequestBody body = new FormBody.Builder()
                    .add("rest_key", Constants.REST_KEY)
                    .add("api_key", apiKey)
                    .add("searchQuery", searchKey)
                    .build();
            apiCallServiceTask.requestApi(new ApiCallRequest(FROM_SEARCH, Constants.search_api, body, false, "Please wait..."));
        }
    }


    protected void setEmptyScreen(boolean isShow){
        int Visibility = isShow ? View.VISIBLE : View.GONE;
        LinearLayout lnEmptyScreen = findViewById(R.id.layoutEmptyScreen);
        if (lnEmptyScreen!=null)
            lnEmptyScreen.setVisibility(Visibility);

    }

    private void setADapterData(){
        if (searchKey==null || searchKey.trim().length()<1 || searchReultsList == null){
            searchReultsList = new ArrayList<>();
        }
        if (searchKey!=null && searchKey.trim().length()>1 && searchReultsList.size()<1){
            setEmptyScreen(true);
        } else {
            setEmptyScreen(false);
        }
        if (searchAdapter!=null){
            searchAdapter.setAdapterDatas(searchReultsList, searchKey);
        }
    }

    @Override
    public void onApiFinished(ApiCallResponse responseBus) {
        try {
            Log.e(Constants.APP_TAG, "Search response:" + responseBus.response);

            SearchResponse searchResponse = new Gson().fromJson(responseBus.response, SearchResponse.class);
            if (searchResponse != null && searchResponse.responseText != null && searchResponse.responseText.searchReultsList != null) {
                searchReultsList = searchResponse.responseText.searchReultsList;
            }
        } catch (Exception e){e.printStackTrace();}
        setADapterData();
    }

}
