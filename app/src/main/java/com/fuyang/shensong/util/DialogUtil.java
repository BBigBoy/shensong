package com.fuyang.shensong.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;

public class DialogUtil {

	/**
	 * 创建默认大小对话框
	 * 
	 * @param context
	 *            Activity上下文
	 * @param layoutID
	 *            布局文件ID
	 * @return
	 */
	public static View createDialogView(Context context, int layoutID) {
		return createDialogView(context, layoutID, 0.7f, 0.8f);
	}

	/**
	 * 创建自定义大小对话框
	 * 
	 * @param context
	 *            Activity上下文
	 * @param layoutID
	 *            布局文件ID
	 * @param scalewidth
	 *            宽度屏占比
	 * @param scaleheight
	 *            高度屏占比
	 * @return
	 */
	public static View createDialogView(Context context, int layoutID,
			float scalewidth, float scaleheight) {
		View layoutView = LayoutInflater.from(context).inflate(layoutID, null);
		Display display = ((Activity) context).getWindowManager()
				.getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);
		layoutView.setMinimumWidth((int) (outMetrics.widthPixels * scalewidth));
		layoutView
				.setMinimumHeight((int) (outMetrics.heightPixels * scaleheight));
		return layoutView;
	}

}
