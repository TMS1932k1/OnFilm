package com.tms.onfilm.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smarteist.autoimageslider.SliderViewAdapter;
import com.tms.onfilm.R;
import com.tms.onfilm.models.Picture;
import com.tms.onfilm.utilities.DownloadImageTask;

import java.util.List;

public class DecribePicAdapter extends SliderViewAdapter<DecribePicAdapter.Holder> {
    private List<Picture> pictureList;

    public DecribePicAdapter(List<Picture> pictureList) {
        this.pictureList = pictureList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_item_decribe_pic,
                parent,
                false)
        );
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {
        viewHolder.setData(pictureList.get(position));
    }

    @Override
    public int getCount() {
        return pictureList.size();
    }

    class Holder extends SliderViewAdapter.ViewHolder {
        public ImageView imgDescribePic;

        public Holder(View itemView) {
            super(itemView);
            imgDescribePic = itemView.findViewById(R.id.image_describe_pic);
        }

        public void setData(Picture picture) {
            new DownloadImageTask(imgDescribePic).execute(picture.getLinkPic());
        }
    }
}
