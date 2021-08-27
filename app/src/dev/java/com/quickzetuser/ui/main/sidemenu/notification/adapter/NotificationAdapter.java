package com.quickzetuser.ui.main.sidemenu.notification.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.BaseRecycleAdapter;
import com.customviews.ReadMoreTextView;
import com.models.DeviceScreenModel;
import com.quickzetuser.R;
import com.quickzetuser.model.NotificationModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by Sunil kumar yadav on 6/3/18.
 */

public class NotificationAdapter extends BaseRecycleAdapter {
    private Context context;
    private List<NotificationModel> list;
    private boolean loadMore;

    private int imageHeight;

    public NotificationAdapter (Context context, List<NotificationModel> list) {
        isForDesign = false;
        this.context = context;
        this.list = list;
        imageHeight = Math.round(((float) DeviceScreenModel.getInstance().getWidth(1.0f)) /
                2.0f);
    }

    public void setLoadMore (boolean loadMore) {
        this.loadMore = loadMore;
        if (list == null) return;
        if (loadMore) {
            notifyItemInserted(list.size());
        } else {
            notifyItemRemoved(list.size());
        }
    }

    @Override
    public BaseViewHolder getViewHolder () {
        return null;
    }

    @Override
    public int getViewType (int position) {
        if (list == null) {
            return VIEW_TYPE_DATA;
        } else if (loadMore && position == list.size()) {
            return VIEW_TYPE_LOAD_MORE;
        }
        return VIEW_TYPE_DATA;

    }

    @Override
    public BaseViewHolder getViewHolder (ViewGroup parent, int viewType) {
        if (VIEW_TYPE_DATA == viewType) {
            return new ViewHolder(inflateLayout(R.layout.item_notification));
        }
        return new LoadMoreViewHolder(inflateLayout(R.layout.item_load_more));
    }


    @Override
    public int getDataCount () {
        return list == null ? 0 : (loadMore ? list.size() + 1 : list.size());
    }

    private class ViewHolder extends BaseViewHolder implements ReadMoreTextView.ReadMoreTextViewListener {
        private TextView tv_title;
        private ReadMoreTextView tv_message;
        private TextView tv_time;
        private TextView tv_date;
        private ImageView iv_image;
        private RelativeLayout rl_image;
        private ProgressBar pb_image;

        public ViewHolder (View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_message = itemView.findViewById(R.id.tv_message);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_date = itemView.findViewById(R.id.tv_date);
            iv_image = itemView.findViewById(R.id.iv_image);
            rl_image = itemView.findViewById(R.id.rl_image);
            pb_image = itemView.findViewById(R.id.pb_image);

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) iv_image.getLayoutParams();
            layoutParams.height = imageHeight;
            iv_image.setLayoutParams(layoutParams);
        }

        @Override
        public void setData (int position) {
            if (list == null) return;
            iv_image.setTag(R.id.notification_image_tag, position);
            iv_image.setOnClickListener(this);

            tv_message.setTag(position);
            tv_message.setReadMoreTextViewListener(this);
            NotificationModel notificationModel = list.get(position);
            tv_title.setText(notificationModel.getTitle());
            tv_message.readMore = notificationModel.isReadMore();
            tv_message.setText(notificationModel.getNotification(), TextView.BufferType.NORMAL);
            tv_time.setText(notificationModel.getFormattedBookingStartTime(4)); //HH:MM a
            tv_date.setText(notificationModel.getFormattedBookingStartTime(5)); //dd MMM yyyy

            if (isValidString(notificationModel.getLarge_image())) {
                pb_image.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(notificationModel.getLarge_image())
                        .placeholder(R.mipmap.ic_riksya)
                        .error(R.mipmap.ic_riksya)
                        .into(iv_image, new Callback() {
                            @Override
                            public void onSuccess () {
                                pb_image.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError (Exception e) {
                                pb_image.setVisibility(View.GONE);
                            }
                        });

            } else {
                rl_image.setVisibility(View.GONE);
                pb_image.setVisibility(View.GONE);
                iv_image.setImageResource(R.mipmap.ic_riksya);
            }
        }

        @Override
        public void onReadMoreChange (ReadMoreTextView textView) {
            int position = Integer.parseInt(textView.getTag().toString());
            list.get(position).setReadMore(textView.readMore);
            if (textView.readMore && getRecyclerView() != null) {
                getRecyclerView().scrollToPosition(position);
            }
        }

        @Override
        public void onClick (View v) {
            if (v.getId() == R.id.iv_image) {
                performItemClick((Integer) v.getTag(R.id.notification_image_tag), v);
            }

        }

    }

}
