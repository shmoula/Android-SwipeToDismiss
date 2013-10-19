/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.swipedismiss;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Simple list adapter providing ability to view icons on edges of list item -
 * depending on swipe direction
 * 
 * @author shmoula
 * 
 */
public class SwipableListAdapter extends ArrayAdapter<RowItem> {
	private final Activity context;
	private List<RowItem> items;
	private int itemHeight;

	public SwipableListAdapter(Activity context, List<RowItem> items) {
		super(context, R.layout.rowlayout_list_item, items);

		this.context = context;
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflator = context.getLayoutInflater();
		final View view = inflator.inflate(R.layout.rowlayout_list_item, null);

		view.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				itemHeight = view.getHeight();
			}
		});

		RowItem item = items.get(position);
		item.setBoundView(view);

		repaintComponents(item);

		return view;
	}

	/**
	 * Repaints component of desired view - fills text and show/hide appropriate
	 * edge icon
	 * 
	 * @param item
	 * @param edgeButton
	 */
	private void repaintComponents(RowItem item) {
		View view = item.getBoundView();

		((TextView) view.findViewById(R.id.text_content)).setText(item.getTextContent());
	}

	/**
	 * Returns vertical position of list item on position
	 * @param position	Index of item in list
	 * @return
	 */
	public int getVerticalItemLocationOnScreen(int position) {
		RowItem item = items.get(position);

		int[] location = new int[2];
		item.getBoundView().getLocationOnScreen(location);

		return location[1];
	}

	/**
	 * Returns height of list item
	 * @return
	 */
	public int getItemHeight() {
		return itemHeight;
	}
}
