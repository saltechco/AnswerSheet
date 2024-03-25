package ir.saltech.answersheet.view.container;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import ir.saltech.answersheet.R;
import ir.saltech.answersheet.object.container.Saver;

public class MaterialFragmentShower extends MaterialDialogFragment {
    private Context context;
    private Fragment wanted;
    private boolean hasContent;
    private boolean layoutMatchParent;
    private boolean cancelable;
    private Fragment parent;
    private Fragment contentFragment;

    public MaterialFragmentShower() {
        super();
    }

    public MaterialFragmentShower(@NonNull Context context) {
        super();
        this.context = context;
        Saver.getInstance(context).setDismissSide(SIDE_FRAGMENT_SHOWER);
    }

    @SuppressLint("LogConditional")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("TAG", "Fragment: " + wanted + " Match Parent Wanted? " + layoutMatchParent);
        super.setDismissReceiver(new BroadcastReceiver() {
            @SuppressLint("SyntheticAccessor")
            @Override
            public void onReceive(Context context2, Intent intent) {
                if (Saver.getInstance(context2).getDismissSide() != null) {
                    if (Saver.getInstance(context2).getDismissSide().equals(SIDE_FRAGMENT_SHOWER)) {
                        if (hasContent) {
                            if (parent != null && contentFragment != null)
                                getActivity().getSupportFragmentManager().beginTransaction().remove(contentFragment).remove(parent).add(R.id.dialog_content_frame, parent).addToBackStack(DIALOG_CONTENT_BACKSTACK).commit();
                            //requireActivity().getSupportFragmentManager().popBackStack(DIALOG_CONTENT_BACKSTACK, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        } else {
                            // TODO: Setup this.......
							/*if (!wanted.toString().contains("SharingRequestDialog") && !wanted.toString().contains("EditCategoryDialog")) {
								sendAddingCategoryBroadcast(MainActivity.CONTINUE_TIME);
								if (!Saver.getInstance(getContext()).getBackupRestoringStatus() && !Saver.getInstance(getContext()).getBackupCreatingStatus()) {
									dismiss(MaterialFragmentShower.this);
								} else {
									Toast.makeText(getContext(), "امکان بازگشت، به دلیل انجام یک عملیات حساس، وجود ندارد!", Toast.LENGTH_LONG).show();
								}
							}*/
                        }
                    }
                }
            }
        });
        super.setContentView(wanted, layoutMatchParent);
        super.setCancelable(cancelable);
        show();
    }

    private void sendAddingCategoryBroadcast(String status) {
        // TODO: Setup this.........
		/*Intent intent = new Intent(MainActivity.CATEGORY_ADDING_RECEIVER_INTENT);
		intent.putExtra(MainActivity.CATEGORY_ADDING_RECEIVER_RESULT, status);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);*/
    }

    public boolean isCancelable() {
        return cancelable;
    }

    @Override
    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public boolean isLayoutMatchParent() {
        return layoutMatchParent;
    }

    public void setLayoutMatchParent(boolean layoutMatchParent) {
        this.layoutMatchParent = layoutMatchParent;
    }

    public Fragment getFragment() {
        return wanted;
    }

    public void setFragment(@NonNull Fragment wanted) {
        this.wanted = wanted;
    }

    public boolean isHasContent() {
        return hasContent;
    }

    public void setHasContent(boolean hasContent) {
        this.hasContent = hasContent;
    }

    public void show(FragmentActivity activity, MaterialFragmentShower shower) {
        activity.getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, shower).addToBackStack(DIALOG_BACKSTACK).commit();
    }

    /**
     * Uses for set Nested Dialog Pages
     * @param activity FragmentActivity
     * @param fragment Wanted Dialog
     * @param currentFragment Current Dialog
     */
    public void setContentFragment(FragmentActivity activity, Fragment fragment, Fragment currentFragment) {
        this.parent = currentFragment;
        this.contentFragment = fragment;
        activity.getSupportFragmentManager().beginTransaction().add(R.id.dialog_content_frame, fragment).addToBackStack(DIALOG_CONTENT_BACKSTACK).commit();
    }
}
