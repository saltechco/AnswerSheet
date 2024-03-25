package ir.saltech.answersheet.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import ir.saltech.answersheet.R;
import ir.saltech.answersheet.intf.listener.AuthenticationChangedListener;
import ir.saltech.answersheet.view.container.MaterialFragmentShower;
import ir.saltech.answersheet.view.dialog.VisualEffectsDialog;

public class SettingsFragment extends Fragment {

    private final CollapsablePanelFragment collapsablePanel;
    private Button setupExamNotifications;
    private Button securityAndPrivacy;
    private Button setupPageWallpaper;
    private Button adjustVisualEffects;

    public SettingsFragment(CollapsablePanelFragment collapsablePanel) {
        this.collapsablePanel = collapsablePanel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        onClicks();
    }

    private void onClicks() {
        adjustVisualEffects.setOnClickListener(v -> {
            MaterialFragmentShower shower = new MaterialFragmentShower(requireContext());
            shower.setFragment(new VisualEffectsDialog());
            shower.setCancelable(true);
            shower.show(requireActivity(), shower);
        });
        setupPageWallpaper.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction().remove(collapsablePanel).add(R.id.fragment_container, new SetWallpaperFragment()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null).commit();
        });
        securityAndPrivacy.setOnClickListener(v -> {
            AuthFragment fragment = new AuthFragment(null);
            Bundle extras = new Bundle();
            extras.putBoolean(AuthFragment.LOAD_FROM_SETTINGS, true);
            fragment.setArguments(extras);
            requireActivity().getSupportFragmentManager().beginTransaction().remove(collapsablePanel).add(R.id.fragment_container, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null).commit();
        });
    }

    private void init(@NonNull View v) {
        setupExamNotifications = v.findViewById(R.id.exam_start_notifications);
        securityAndPrivacy = v.findViewById(R.id.privacy_and_security);
        setupPageWallpaper = v.findViewById(R.id.exam_page_wallpaper);
        adjustVisualEffects = v.findViewById(R.id.adjust_visual_effects);
    }
}
