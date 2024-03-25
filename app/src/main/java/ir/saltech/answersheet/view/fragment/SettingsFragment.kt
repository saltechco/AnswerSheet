package ir.saltech.answersheet.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import ir.saltech.answersheet.R
import ir.saltech.answersheet.view.container.MaterialFragmentShower
import ir.saltech.answersheet.view.dialog.VisualEffectsDialog

class SettingsFragment(private val collapsablePanel: CollapsablePanelFragment) : Fragment() {
    private var setupExamNotifications: Button? = null
    private var securityAndPrivacy: Button? = null
    private var setupPageWallpaper: Button? = null
    private var adjustVisualEffects: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        onClicks()
    }

    private fun onClicks() {
        adjustVisualEffects!!.setOnClickListener { v: View? ->
            val shower: MaterialFragmentShower = MaterialFragmentShower(requireContext())
            shower.fragment = VisualEffectsDialog()
            shower.setCancelable(true)
            shower.show(requireActivity(), shower)
        }
        setupPageWallpaper!!.setOnClickListener { v: View? ->
            requireActivity().supportFragmentManager.beginTransaction().remove(
                collapsablePanel
            ).add(R.id.fragment_container, SetWallpaperFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null)
                .commit()
        }
        securityAndPrivacy!!.setOnClickListener { v: View? ->
            val fragment = AuthFragment(null)
            val extras: Bundle = Bundle()
            extras.putBoolean(AuthFragment.Companion.LOAD_FROM_SETTINGS, true)
            fragment.arguments = extras
            requireActivity().supportFragmentManager.beginTransaction().remove(collapsablePanel)
                .add(R.id.fragment_container, fragment).setTransition(
                FragmentTransaction.TRANSIT_FRAGMENT_FADE
            ).addToBackStack(null).commit()
        }
    }

    private fun init(v: View) {
        setupExamNotifications = v.findViewById<Button>(R.id.exam_start_notifications)
        securityAndPrivacy = v.findViewById<Button>(R.id.privacy_and_security)
        setupPageWallpaper = v.findViewById<Button>(R.id.exam_page_wallpaper)
        adjustVisualEffects = v.findViewById<Button>(R.id.adjust_visual_effects)
    }
}
