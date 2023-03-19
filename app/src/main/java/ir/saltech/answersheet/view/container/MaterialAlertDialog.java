package ir.saltech.answersheet.view.container;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import ir.saltech.answersheet.R;
import ir.saltech.answersheet.object.container.Saver;

public class MaterialAlertDialog extends MaterialDialogFragment {
	private final Context context;
	private Drawable icon;
	private String title;
	private String message;
	private String primaryButtonTitle;
	private String secondaryButtonTitle;
	private String naturalButtonTitle;
	private View.OnClickListener primaryClickListener;
	private View.OnClickListener secondaryClickListener;
	private View.OnClickListener naturalClickListener;
	private boolean cancelable;
	private boolean progressbarEnabled;

	public MaterialAlertDialog(@NonNull Context context) {
		this.context = context;
		Saver.getInstance(getContext()).setDismissSide(SIDE_ALERT_DIALOG);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		super.setDismissReceiver(new BroadcastReceiver() {
			@SuppressLint("SyntheticAccessor")
			@Override
			public void onReceive(Context context, Intent intent) {
				if (Saver.getInstance(getContext()).getDismissSide() != null) {
					if (Saver.getInstance(getContext()).getDismissSide().equals(SIDE_ALERT_DIALOG)) {
						try {
							dismiss(MaterialAlertDialog.this);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		super.setIcon(icon);
		super.setTitle(title);
		super.setMessage(message);
		super.setPositiveButton(primaryButtonTitle, primaryClickListener);
		super.setNegativeButton(secondaryButtonTitle, secondaryClickListener);
		super.setNaturalButton(naturalButtonTitle, naturalClickListener);
		super.setCancelable(cancelable);
		super.showProgressBar(progressbarEnabled);
		super.show();
	}

	public void setProgressbarEnabled(boolean progressbarEnabled) {
		this.progressbarEnabled = progressbarEnabled;
	}

	public boolean isCancelable() {
		return cancelable;
	}

	@Override
	public void setCancelable(boolean cancelable) {
		this.cancelable = cancelable;
	}

	public Drawable getIcon() {
		return icon;
	}

	@Override
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	@SuppressLint("UseCompatLoadingForDrawables")
	public void setIcon(int resId) {
		this.icon = context.getResources().getDrawable(resId);
	}

	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public void setMessage(String message) {
		this.message = message;
	}

	public void setPositiveButton(String primaryButtonTitle, View.OnClickListener primaryClickListener) {
		this.primaryButtonTitle = primaryButtonTitle;
		this.primaryClickListener = primaryClickListener;
	}

	public void setNaturalButton(String naturalButtonTitle, View.OnClickListener naturalClickListener) {
		this.naturalButtonTitle = naturalButtonTitle;
		this.naturalClickListener = naturalClickListener;
	}

	public void setNegativeButton(String secondaryButtonTitle, View.OnClickListener secondaryClickListener) {
		this.secondaryButtonTitle = secondaryButtonTitle;
		this.secondaryClickListener = secondaryClickListener;
	}

	public void show(FragmentActivity activity) {
		activity.getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, MaterialAlertDialog.this).addToBackStack(DIALOG_BACKSTACK).commit();
	}
}
