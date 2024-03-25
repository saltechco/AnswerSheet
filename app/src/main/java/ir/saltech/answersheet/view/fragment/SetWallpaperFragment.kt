package ir.saltech.answersheet.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import eightbitlab.com.blurview.BlurView
import ir.saltech.answersheet.R
import ir.saltech.answersheet.intf.listener.ExamWallpaperClickedListener
import ir.saltech.answersheet.`object`.container.Saver
import ir.saltech.answersheet.`object`.data.ExamWallpaper
import ir.saltech.answersheet.`object`.data.ExamWallpapers
import ir.saltech.answersheet.`object`.enums.WallpaperType
import ir.saltech.answersheet.view.activity.MainActivity
import ir.saltech.answersheet.view.adapter.WallpapersViewAdapter
import ir.saltech.answersheet.view.container.BlurViewHolder
import ir.saltech.answersheet.view.container.Toast

class SetWallpaperFragment : Fragment() {
    private var parent: BlurView? = null
    private var picturesLayout: RecyclerView? = null
    private var animationsLayout: RecyclerView? = null
    private var wallpapers: List<ExamWallpaper>? = null
    private var picturesAdapter: WallpapersViewAdapter? = null
    private var animationsAdapter: WallpapersViewAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_set_wallpaper, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        MainActivity.setStatusBarTheme(
            requireActivity(),
            !MainActivity.checkDarkModeEnabled(requireContext())
        )
        BlurViewHolder.setBlurView(requireActivity(), parent!!)
        setupLayouts()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupLayouts() {
        wallpapers =
            Saver.Companion.getInstance(requireContext()).loadExamWallpapers().getWallpapers()
        val pictureWallpapers: List<ExamWallpaper> = getWantedWallpapers(WallpaperType.Picture)
        val animationWallpapers: List<ExamWallpaper> = getWantedWallpapers(WallpaperType.Animation)
        picturesAdapter = WallpapersViewAdapter(
            pictureWallpapers,
            object : ExamWallpaperClickedListener {
                override fun onClicked(wallpaper: ExamWallpaper?, position: Int) {
                    setupThisWallpaper(wallpaper!!, position)
                    Toast.makeText(
                        requireContext(),
                        "تصویر زمینه تصویری اعمال شد.",
                        Toast.WARNING_SIGN,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        animationsAdapter = WallpapersViewAdapter(
            animationWallpapers,
            object : ExamWallpaperClickedListener {
                override fun onClicked(wallpaper: ExamWallpaper?, position: Int) {
                    setupThisWallpaper(wallpaper!!, position)
                    Toast.makeText(
                        requireContext(),
                        "تصویر زمینه متحرک اعمال شد.",
                        Toast.WARNING_SIGN,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        picturesLayout!!.setLayoutManager(
            GridLayoutManager(
                requireContext(),
                3,
                LinearLayoutManager.VERTICAL,
                false
            )
        )
        picturesLayout!!.setAdapter(picturesAdapter)
        animationsLayout!!.setLayoutManager(
            GridLayoutManager(
                requireContext(),
                3,
                LinearLayoutManager.VERTICAL,
                false
            )
        )
        animationsLayout!!.setAdapter(animationsAdapter)
        if (picturesAdapter != null) {
            picturesAdapter!!.notifyDataSetChanged()
        }
        if (animationsAdapter != null) {
            animationsAdapter!!.notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    private fun setupThisWallpaper(wallpaper: ExamWallpaper, position: Int) {
        val examWallpapers = ExamWallpapers()
        if (wallpaper.getType() == WallpaperType.Picture) {
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Picture, position == 0))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Picture, position == 1))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Picture, position == 2))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Picture, position == 3))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Picture, position == 4))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Picture, position == 5))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Animation, false))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Animation, false))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Animation, false))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Animation, false))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Animation, false))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Animation, false))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Animation, false))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Animation, false))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Animation, false))
        } else if (wallpaper.getType() == WallpaperType.Animation) {
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Picture, false))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Picture, false))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Picture, false))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Picture, false))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Picture, false))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Picture, false))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Animation, position == 0))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Animation, position == 1))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Animation, position == 2))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Animation, position == 3))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Animation, position == 4))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Animation, position == 5))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Animation, position == 6))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Animation, position == 7))
            examWallpapers.addWallpaper(ExamWallpaper(WallpaperType.Animation, position == 8))
        }
        Saver.getInstance(requireContext()).saveExamWallpapers(examWallpapers)
        setupLayouts()
    }

    private fun getWantedWallpapers(type: WallpaperType): List<ExamWallpaper> {
        val filteredWallpapers: MutableList<ExamWallpaper> = ArrayList<ExamWallpaper>()
        for (wallpaper in wallpapers!!) {
            if (wallpaper.getType() == type) {
                filteredWallpapers.add(wallpaper)
            }
        }
        return filteredWallpapers
    }

    private fun init(v: View) {
        parent = v.findViewById<BlurView>(R.id.background_picker_parent)
        picturesLayout = v.findViewById<RecyclerView>(R.id.pictures_background)
        animationsLayout = v.findViewById<RecyclerView>(R.id.animations_background)
    }
}
