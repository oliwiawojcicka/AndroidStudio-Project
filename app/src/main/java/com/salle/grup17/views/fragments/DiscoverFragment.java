package com.salle.grup17.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.salle.grup17.R;
import com.salle.grup17.views.adapters.LocationAdapter;
import com.salle.grup17.api.RetrofitClient;
import com.salle.grup17.api.RickAndMortyApi;
import com.salle.grup17.models.ApiResponse;
import com.salle.grup17.models.Location;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscoverFragment extends Fragment {

    private RecyclerView recyclerView;
    private LocationAdapter adapter;
    private ProgressBar progressBar;
    private RickAndMortyApi api;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        recyclerView = view.findViewById(R.id.rvDiscover);
        progressBar = view.findViewById(R.id.pbLoading);

        adapter = new LocationAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        api = RetrofitClient.getApi();
        fetchLocations();

        return view;
    }

    private void fetchLocations() {
        progressBar.setVisibility(View.VISIBLE);
        api.getLocations(1).enqueue(new Callback<ApiResponse<Location>>() {
            @Override
            public void onResponse(Call<ApiResponse<Location>> call, Response<ApiResponse<Location>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setLocations(response.body().getResults());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Location>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error loading locations", Toast.LENGTH_SHORT).show();
            }
        });
    }
}