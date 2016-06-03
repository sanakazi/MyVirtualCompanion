package com.callndata.Others;

import java.util.HashMap;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class MaxView extends AutoCompleteTextView {

	public MaxView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/** Returns the Place Description corresponding to the selected item */
	@Override
	protected CharSequence convertSelectionToString(Object selectedItem) {
		/**
		 * Each item in the autocompetetextview suggestion list is a hashmap
		 * object
		 */
		HashMap<String, String> items = (HashMap<String, String>) selectedItem;
		return items.get("description");
	}
}