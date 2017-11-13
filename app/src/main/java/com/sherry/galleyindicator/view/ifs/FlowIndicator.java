package com.sherry.galleyindicator.view.ifs;

import com.sherry.galleyindicator.view.PicGallery;

/**
 * An interface which defines the contract between a ViewFlow and a
 * FlowIndicator.<br/>
 * A FlowIndicator is responsible to show an visual indicator on the total views
 * number and the current visible view.<br/>
 * 
 */
public interface FlowIndicator extends PicGallery.ViewSwitchListener {

	/**
	 * Set the current ViewFlow. This method is called by the ViewFlow when the
	 * FlowIndicator is attached to it.
	 * 
	 * @param view
	 *            控件
	 */
	void setViewFlow(PicGallery view);

	/**
	 * The scroll position has been changed. A FlowIndicator may implement this
	 * method to reflect the current position
	 * 
	 * @param h
	 *            宽度
	 * @param v
	 *            参数
	 * @param oldh
	 *            参数
	 * @param oldv
	 *            参数
	 */
	void onScrolled(int h, int v, int oldh, int oldv);
}
