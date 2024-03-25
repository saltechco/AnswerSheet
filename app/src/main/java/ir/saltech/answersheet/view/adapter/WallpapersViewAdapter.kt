package ir.saltech.answersheet.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import ir.saltech.answersheet.R
import ir.saltech.answersheet.intf.listener.ExamWallpaperClickedListener
import ir.saltech.answersheet.`object`.data.ExamWallpaper
import ir.saltech.answersheet.`object`.enums.WallpaperType

class WallpapersViewAdapter(
    wallpapers: List<ExamWallpaper>,
    clickedListener: ExamWallpaperClickedListener
) : RecyclerView.Adapter<WallpapersViewAdapter.WallpaperViewHolder?>() {
    private val wallpapers: List<ExamWallpaper> = wallpapers
    private val clickedListener: ExamWallpaperClickedListener = clickedListener
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperViewHolder {
        this.context = parent.context
        return WallpaperViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_template_wallpaper, parent, false)
        )
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: WallpaperViewHolder, position: Int) {
        if (wallpapers[position].isSelected) {
            holder.wallpaperCard.setForeground(context!!.resources.getDrawable(R.drawable.selected_background_bg))
        } else {
            holder.wallpaperCard.setForeground(null)
        }
        if (wallpapers[position].getType() == WallpaperType.Picture) {
            holder.wallpaperAnim.setVisibility(View.GONE)
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
            holder.wallpaperPicture.visibility = View.VISIBLE
            holder.addPicture.visibility = View.GONE
            when (position) {
                0 -> {
                    holder.wallpaperPicture.setImageDrawable(context!!.resources.getDrawable(R.drawable.wallpaper))
                }
                1 -> {
                    holder.wallpaperPicture.setImageDrawable(context!!.resources.getDrawable(R.drawable.wallpaper2))
                }
                2 -> {
                    holder.wallpaperPicture.setImageDrawable(context!!.resources.getDrawable(R.drawable.wallpaper3))
                }
                3 -> {
                    holder.wallpaperPicture.setImageDrawable(context!!.resources.getDrawable(R.drawable.wallpaper4))
                }
                4 -> {
                    holder.wallpaperPicture.setImageDrawable(context!!.resources.getDrawable(R.drawable.wallpaper5))
                }
                5 -> {
                    holder.wallpaperPicture.setImageDrawable(context!!.resources.getDrawable(R.drawable.wallpaper6))
                }
            }
            holder.wallpaperCard.setOnClickListener {
                wallpapers[position].isSelected = true
                clickedListener.onClicked(wallpapers[position], position)
            }
        } else if (wallpapers[position].getType() == WallpaperType.Animation) {
            holder.addPicture.visibility = View.GONE
            holder.wallpaperAnim.setVisibility(View.VISIBLE)
            if (position == 0) {
                holder.wallpaperAnim.setAnimation(R.raw.turning_wave)
            } else if (position == 1) {
                holder.wallpaperAnim.setAnimation(R.raw.turning_wave2)
            } else if (position == 2) {
                holder.wallpaperAnim.setAnimation(R.raw.turning_wave3)
            } else if (position == 3) {
                holder.wallpaperAnim.setAnimation(R.raw.turning_wave4)
            } else if (position == 4) {
                holder.wallpaperAnim.setAnimation(R.raw.turning_wave5)
            } else if (position == 5) {
                holder.wallpaperAnim.setAnimation(R.raw.turning_wave6)
            } else if (position == 6) {
                holder.wallpaperAnim.setAnimation(R.raw.turning_wave7)
            } else if (position == 7) {
                holder.wallpaperAnim.setAnimation(R.raw.turning_wave8)
            } else if (position == 8) {
                holder.wallpaperAnim.setAnimation(R.raw.turning_wave9)
            }
            holder.wallpaperAnim.playAnimation()
            holder.wallpaperPicture.visibility = View.GONE
            holder.wallpaperCard.setOnClickListener {
                wallpapers[position].isSelected = true
                clickedListener.onClicked(wallpapers[position], position)
            }
        }
    }

    override fun getItemCount() = wallpapers.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class WallpaperViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var wallpaperCard: CardView = v.findViewById<CardView>(R.id.wallpaper_card)
        var wallpaperAnim: LottieAnimationView =
            v.findViewById<LottieAnimationView>(R.id.animation_background)
        var wallpaperPicture: ImageView = v.findViewById<ImageView>(R.id.picture_background)
        var addPicture: LinearLayout = v.findViewById<LinearLayout>(R.id.add_picture_wallpaper)
    }
}
