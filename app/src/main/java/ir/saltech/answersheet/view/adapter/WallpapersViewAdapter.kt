package ir.saltech.answersheet.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.List;

import ir.saltech.answersheet.R;
import ir.saltech.answersheet.intf.listener.ExamWallpaperClickedListener;
import ir.saltech.answersheet.object.data.ExamWallpaper;
import ir.saltech.answersheet.object.enums.WallpaperType;

public class WallpapersViewAdapter extends RecyclerView.Adapter<WallpapersViewAdapter.WallpaperViewHolder> {

    private final List<ExamWallpaper> wallpapers;
    private final ExamWallpaperClickedListener clickedListener;
    private Context context;

    public WallpapersViewAdapter(List<ExamWallpaper> wallpapers, ExamWallpaperClickedListener clickedListener) {
        this.wallpapers = wallpapers;
        this.clickedListener = clickedListener;
    }

    @NonNull
    @Override
    public WallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new WallpaperViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_template_wallpaper, parent, false));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull WallpaperViewHolder holder, int position) {
        if (wallpapers.get(position).isSelected()) {
            holder.wallpaperCard.setForeground(context.getResources().getDrawable(R.drawable.selected_background_bg));
        } else {
            holder.wallpaperCard.setForeground(null);
        }
        if (wallpapers.get(position).getType() == WallpaperType.Picture) {
            holder.wallpaperAnim.setVisibility(View.GONE);
            /*if (wallpapers.get(position).getDrawable() != null) {
                holder.wallpaperCard.setElevation(15f);
                holder.wallpaperPicture.setVisibility(View.VISIBLE);
                holder.addPicture.setVisibility(View.GONE);
                holder.wallpaperPicture.setImageDrawable(wallpapers.get(position).getDrawable());
                holder.wallpaperCard.setOnClickListener(v -> {
                    wallpapers.get(position).setSelected(true);
                    clickedListener.onClicked(wallpapers.get(position), position);
                });
            } else {
                holder.wallpaperCard.setElevation(0);
                holder.wallpaperPicture.setVisibility(View.GONE);
                holder.addPicture.setVisibility(View.VISIBLE);
                holder.wallpaperCard.setOnClickListener(v -> {
                    Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show();
                });
            }*/
            holder.wallpaperPicture.setVisibility(View.VISIBLE);
            holder.addPicture.setVisibility(View.GONE);
            if (position == 0) {
                holder.wallpaperPicture.setImageDrawable(context.getResources().getDrawable(R.drawable.wallpaper));
            } else if (position == 1) {
                holder.wallpaperPicture.setImageDrawable(context.getResources().getDrawable(R.drawable.wallpaper2));
            } else if (position == 2) {
                holder.wallpaperPicture.setImageDrawable(context.getResources().getDrawable(R.drawable.wallpaper3));
            } else if (position == 3) {
                holder.wallpaperPicture.setImageDrawable(context.getResources().getDrawable(R.drawable.wallpaper4));
            } else if (position == 4) {
                holder.wallpaperPicture.setImageDrawable(context.getResources().getDrawable(R.drawable.wallpaper5));
            } else if (position == 5) {
                holder.wallpaperPicture.setImageDrawable(context.getResources().getDrawable(R.drawable.wallpaper6));
            }
            holder.wallpaperCard.setOnClickListener(v -> {
                wallpapers.get(position).setSelected(true);
                clickedListener.onClicked(wallpapers.get(position), position);
            });
        } else if (wallpapers.get(position).getType() == WallpaperType.Animation) {
            holder.addPicture.setVisibility(View.GONE);
            holder.wallpaperAnim.setVisibility(View.VISIBLE);
            if (position == 0) {
                holder.wallpaperAnim.setAnimation(R.raw.turning_wave);
            } else if (position == 1) {
                holder.wallpaperAnim.setAnimation(R.raw.turning_wave2);
            } else if (position == 2) {
                holder.wallpaperAnim.setAnimation(R.raw.turning_wave3);
            } else if (position == 3) {
                holder.wallpaperAnim.setAnimation(R.raw.turning_wave4);
            } else if (position == 4) {
                holder.wallpaperAnim.setAnimation(R.raw.turning_wave5);
            } else if (position == 5) {
                holder.wallpaperAnim.setAnimation(R.raw.turning_wave6);
            } else if (position == 6) {
                holder.wallpaperAnim.setAnimation(R.raw.turning_wave7);
            } else if (position == 7) {
                holder.wallpaperAnim.setAnimation(R.raw.turning_wave8);
            } else if (position == 8) {
                holder.wallpaperAnim.setAnimation(R.raw.turning_wave9);
            }
            holder.wallpaperAnim.playAnimation();
            holder.wallpaperPicture.setVisibility(View.GONE);
            holder.wallpaperCard.setOnClickListener(v -> {
                wallpapers.get(position).setSelected(true);
                clickedListener.onClicked(wallpapers.get(position), position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return wallpapers.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class WallpaperViewHolder extends RecyclerView.ViewHolder {
        public CardView wallpaperCard;
        public LottieAnimationView wallpaperAnim;
        public ImageView wallpaperPicture;
        public LinearLayout addPicture;

        public WallpaperViewHolder(@NonNull View v) {
            super(v);
            wallpaperCard = v.findViewById(R.id.wallpaper_card);
            wallpaperAnim = v.findViewById(R.id.animation_background);
            wallpaperPicture = v.findViewById(R.id.picture_background);
            addPicture = v.findViewById(R.id.add_picture_wallpaper);
        }
    }
}
