package com.example.routefinderke;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {
    private Context context;
    private List<Route> routeList;

    public RouteAdapter(Context context, List<Route> routeList) {
        this.context = context;
        this.routeList = routeList;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_route, parent, false);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        Route route = routeList.get(position);
        MainActivity mainActivity = (MainActivity) context;

        if (mainActivity.isShowingCounties()) {
            // MODE 1: Showing County List
            holder.tvRouteNumber.setText(route.getCounty());
            holder.tvStartDestination.setText("Explore all routes in " + route.getCounty());
            holder.tvFareRange.setVisibility(View.GONE);
            holder.tvCounty.setVisibility(View.GONE);
            
            holder.itemView.setOnClickListener(v -> {
                mainActivity.onCountyTapped(route.getCounty());
            });
        } else {
            // MODE 2: Showing specific routes for a county
            holder.tvRouteNumber.setText("Route " + route.getRouteNumber());
            holder.tvStartDestination.setText(route.getStartPoint() + " -> " + route.getDestination());
            holder.tvFareRange.setVisibility(View.VISIBLE);
            holder.tvFareRange.setText("Ksh " + route.getFareRange());
            holder.tvCounty.setVisibility(View.VISIBLE);
            holder.tvCounty.setText(route.getCounty());

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("route_object", route);
                
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        (Activity) context, holder.ivRouteImage, "bus_image_transition");
                
                context.startActivity(intent, options.toBundle());
            });
        }
        
        holder.ivRouteImage.setImageResource(route.getImageResourceId());
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    public static class RouteViewHolder extends RecyclerView.ViewHolder {
        TextView tvRouteNumber, tvStartDestination, tvFareRange, tvCounty;
        ImageView ivRouteImage;

        public RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRouteNumber = itemView.findViewById(R.id.tvRouteNumber);
            tvStartDestination = itemView.findViewById(R.id.tvStartDestination);
            tvFareRange = itemView.findViewById(R.id.tvFareRange);
            tvCounty = itemView.findViewById(R.id.tvCounty);
            ivRouteImage = itemView.findViewById(R.id.ivRouteImage);
        }
    }
}
