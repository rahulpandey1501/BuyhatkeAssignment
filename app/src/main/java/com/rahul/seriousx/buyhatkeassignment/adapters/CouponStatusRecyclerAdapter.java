package com.rahul.seriousx.buyhatkeassignment.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rahul.seriousx.buyhatkeassignment.R;
import com.rahul.seriousx.buyhatkeassignment.model.ContentModel;

import java.util.List;

/**
 * Created by root on 5/27/17.
 */

public class CouponStatusRecyclerAdapter extends RecyclerView.Adapter<CouponStatusRecyclerAdapter.CustomViewHolder> {
    private Context mContext;
    private List<ContentModel> couponStatusList;

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView couponNameTextView, couponDescriptionTextView, couponAppliedTextView;
        ImageView statusImageView;
        View rowView;

        CustomViewHolder(View rowView) {
            super(rowView);
            this.rowView = rowView;
            couponNameTextView = (TextView) rowView.findViewById(R.id.coupon_name_TV);
            couponDescriptionTextView = (TextView) rowView.findViewById(R.id.coupon_description_TV);
            couponAppliedTextView = (TextView) rowView.findViewById(R.id.coupon_applied_TV);
            statusImageView = (ImageView) rowView.findViewById(R.id.current_coupon_status);
        }
    }

    public CouponStatusRecyclerAdapter(Context context, List<ContentModel> couponStatusList) {
        mContext = context;
        this.couponStatusList = couponStatusList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.coupon_status_item, viewGroup, false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int position) {
        final ContentModel model = couponStatusList.get(position);
        holder.couponNameTextView.setText(model.getCoupon());
        holder.couponDescriptionTextView.setText(model.getDescription());
        if (model.isInProgress()) {
            holder.couponAppliedTextView.setText("Applying...");
            holder.couponAppliedTextView.setTextColor(mContext.getResources().getColor(R.color.green));
            holder.statusImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.process));
        } else if (model.isValid()) {
            holder.couponAppliedTextView.setText("₹"+(int)(model.getOriginalPrice()-model.getDiscountedPrice())+" OFF!");
            holder.couponAppliedTextView.setTextColor(mContext.getResources().getColor(R.color.green));
            holder.statusImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.success));
        } else {
            holder.couponAppliedTextView.setText("N/A");
            holder.couponAppliedTextView.setTextColor(mContext.getResources().getColor(R.color.red));
            holder.statusImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.cross));
        }
    }

    @Override
    public int getItemCount() {
        return couponStatusList.size();
    }
}
