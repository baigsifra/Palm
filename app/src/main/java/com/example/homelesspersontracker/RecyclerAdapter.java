package com.example.homelesspersontracker;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    // TODO Figure out how to make a new RecyclerView when thing is clicked
    private ArrayList<Request> requestAL;
    private boolean isHelpRequest;

    public RecyclerAdapter(ArrayList<Request> requestAL, boolean isHelpRequest) {
        this.requestAL = requestAL;
        this.isHelpRequest = isHelpRequest;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView requestStatusTV, requestDateTV;
        private Request request;
        private TextView awaitingBubble, claimedBubble, processingBubble, inTransitBubble, requestFulfilledBubble;

        public MyViewHolder(View v) {
            super(v);
            if(isHelpRequest) {
                awaitingBubble = v.findViewById(R.id.awaitingAgencyReplyBubble);
                claimedBubble = v.findViewById(R.id.agencyClaimedHelpBubble);
                processingBubble = v.findViewById(R.id.processingBubble);
                inTransitBubble = v.findViewById(R.id.inTransitBubble);
                requestFulfilledBubble = v.findViewById(R.id.requestFulfilledBubble);
                requestStatusTV = v.findViewById(R.id.helpRequestStatusTV);
                requestDateTV = v.findViewById(R.id.helpRequestDateTV);
            } else {
                requestStatusTV = v.findViewById(R.id.requestStatusTV);
                requestDateTV = v.findViewById(R.id.requestDateTV);
            }

            if(MainActivity.userType.equals("employee")) {
                if(isHelpRequest) {
                    v.findViewById(R.id.nextStateBtn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            switch (request.getStatus()) {
                                case "Awaiting":
                                    request.setStatus("Claimed");
                                    break;
                                case "Claimed":
                                    request.setStatus("Processing");
                                    break;
                                case "Processing":
                                    request.setStatus("In Transit");
                                    break;
                                case "In Transit":
                                    request.setStatus("Fulfilled");
                                    break;
                            }

                            Log.d("LFRA", "Next State: " + request.getStatus());

                            notifyItemChanged(getAdapterPosition());
                            MainActivity.firebaseHelper.updateRequestStatus(request);
                        }
                    });
                    v.findViewById(R.id.prevStateBtn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            switch (request.getStatus()) {
                                case "Fulfilled":
                                    request.setStatus("In Transit");
                                    break;
                                case "In Transit":
                                    request.setStatus("Processing");
                                    break;
                                case "Processing":
                                    request.setStatus("Claimed");
                                    break;
                                case "Claimed":
                                    request.setStatus("Awaiting");
                            }

                            Log.d("LFRA", "Prev State: " + request.getStatus());

                            notifyItemChanged(getAdapterPosition());
                            MainActivity.firebaseHelper.updateRequestStatus(request);
                        }
                    });
                }
            }
        }
    }

    @NonNull
    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if(MainActivity.userType.equals("individual")) {
            if (isHelpRequest) {
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.help_request_status_item, parent, false);
            } else {
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.donation_request_status_item, parent, false);
            }
        } else {
            if (isHelpRequest) {
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.update_help_request_status_item, parent, false);
            } else {
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.update_donation_request_status_item, parent, false);
            }
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.MyViewHolder holder, int position) {
        Request request = requestAL.get(position);
        String requestStatus = requestAL.get(position).getStatus();
        String requestDate = requestAL.get(position).getDate();

        holder.request = request;
        holder.requestStatusTV.setText(requestStatus);
        holder.requestDateTV.setText(requestDate);

        if(isHelpRequest) {
            switch (request.getStatus()) {
                case "Awaiting":
                    holder.awaitingBubble.setBackgroundResource(R.drawable.circle_filled);
                    holder.claimedBubble.setBackgroundResource(R.drawable.circle_empty);
                    holder.processingBubble.setBackgroundResource(R.drawable.circle_empty);
                    holder.inTransitBubble.setBackgroundResource(R.drawable.circle_empty);
                    holder.requestFulfilledBubble.setBackgroundResource(R.drawable.circle_empty);
                    break;
                case "Claimed":
                    holder.awaitingBubble.setBackgroundResource(R.drawable.circle_empty);
                    holder.claimedBubble.setBackgroundResource(R.drawable.circle_filled);
                    holder.processingBubble.setBackgroundResource(R.drawable.circle_empty);
                    holder.inTransitBubble.setBackgroundResource(R.drawable.circle_empty);
                    holder.requestFulfilledBubble.setBackgroundResource(R.drawable.circle_empty);
                    break;
                case "Processing":
                    holder.awaitingBubble.setBackgroundResource(R.drawable.circle_empty);
                    holder.claimedBubble.setBackgroundResource(R.drawable.circle_empty);
                    holder.processingBubble.setBackgroundResource(R.drawable.circle_filled);
                    holder.inTransitBubble.setBackgroundResource(R.drawable.circle_empty);
                    holder.requestFulfilledBubble.setBackgroundResource(R.drawable.circle_empty);
                    break;
                case "In Transit":
                    holder.awaitingBubble.setBackgroundResource(R.drawable.circle_empty);
                    holder.claimedBubble.setBackgroundResource(R.drawable.circle_empty);
                    holder.processingBubble.setBackgroundResource(R.drawable.circle_empty);
                    holder.inTransitBubble.setBackgroundResource(R.drawable.circle_filled);
                    holder.requestFulfilledBubble.setBackgroundResource(R.drawable.circle_empty);
                    break;
                case "Fulfilled":
                    holder.awaitingBubble.setBackgroundResource(R.drawable.circle_empty);
                    holder.claimedBubble.setBackgroundResource(R.drawable.circle_empty);
                    holder.processingBubble.setBackgroundResource(R.drawable.circle_empty);
                    holder.inTransitBubble.setBackgroundResource(R.drawable.circle_empty);
                    holder.requestFulfilledBubble.setBackgroundResource(R.drawable.circle_filled);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return requestAL.size();
    }
}
