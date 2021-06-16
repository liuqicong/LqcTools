package com.aomygod.tools.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.aomygod.tools.R;
import com.aomygod.tools.Utils.LogUtil;

public class CustomProgressDialog extends Dialog {

	private Context context = null;
	private static CustomProgressDialog customProgressDialog = null;
	private ImageView loadingImageView;
	private Animation operatingAnim;

	public CustomProgressDialog(Context context) {
		super(context);
		this.context = context;
		initView();
	}

	public CustomProgressDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
		initView();
	}

	public static CustomProgressDialog createDialog(Context context)
			throws Exception {

		if (context == null) {
			throw new Exception();
		}

		customProgressDialog = new CustomProgressDialog(context,
				R.style.tools_CustomProgressDialog);
		return customProgressDialog;
	}
	
	private void initView() {
		setContentView(R.layout.tools_customprogressdialog);
		getWindow().getAttributes().gravity = Gravity.CENTER;
		loadingImageView = (ImageView) findViewById(R.id.loadingImageView);
	}

	public void onWindowFocusChanged(boolean hasFocus) {

		if (customProgressDialog == null) {
			return;
		}

		operatingAnim = AnimationUtils.loadAnimation(context,
				R.anim.tools_progress_round_plan);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		if (operatingAnim != null && loadingImageView != null) {
			loadingImageView.startAnimation(operatingAnim);
		}

		// ImageView imageView = (ImageView) customProgressDialog
		// .findViewById(R.id.loadingImageView);
		// AnimationDrawable animationDrawable = (AnimationDrawable) imageView
		// .getBackground();
		// animationDrawable.start();
	}

	@Override
	public void dismiss() {
		try {
			super.dismiss();
			if (loadingImageView != null) {
				loadingImageView.clearAnimation();
			}
		} catch (Exception e) {
			LogUtil.e(e);
		}

	}

	/**
	 * 
	 * [Summary] setTitile ����
	 * 
	 * @param strTitle
	 * @return
	 * 
	 */
	public CustomProgressDialog setTitile(String strTitle) {
		return customProgressDialog;
	}

	/**
	 * 
	 * [Summary] setMessage ��ʾ����
	 * 
	 * @param strMessage
	 * @return
	 * 
	 */
	public CustomProgressDialog setMessage(String strMessage) {
		TextView tvMsg = (TextView) customProgressDialog
				.findViewById(R.id.id_tv_loadingmsg);

		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}

		return customProgressDialog;
	}
}
