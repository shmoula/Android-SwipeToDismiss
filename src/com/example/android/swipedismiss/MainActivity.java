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

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class MainActivity extends ListActivity {
	private static final int LIST_TOP_OFFSET = 0;  // offest in px, when list is not at the top of the screen
	
	private SwipableListAdapter mAdapter;
	private RelativeLayout rlRootLayout;
	
	private int iconSize = 80;          // predefined value, which is updated at the runtime - height of list item
	private int layoutWidth;			// actual width of the screen in px

	private ImageView ivTray;			// View on left side and its parameters
	private LayoutParams trayParams;

	private ImageView ivRil;			// View on right side and its parameters
	private LayoutParams rilParams;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		rlRootLayout = (RelativeLayout) findViewById(R.id.root_layout);

		// simple list stuffing
		List<RowItem> items = new ArrayList<RowItem>();
		for (int i = 0; i < 20; i++) {
			RowItem item = new RowItem();
			item.setTextContent("Item " + (i + 1));
			items.add(item);
		}
		
		mAdapter = new SwipableListAdapter(this, items);
		setListAdapter(mAdapter);

		// ImageView for the left side
		ivTray = createImageView(Color.RED);
		trayParams = new RelativeLayout.LayoutParams(iconSize, iconSize);
		trayParams.leftMargin = 0;
		rlRootLayout.addView(ivTray, trayParams);

		// ImageView for the right side
		ivRil = createImageView(Color.GREEN);
		rilParams = new RelativeLayout.LayoutParams(iconSize, iconSize);
		rlRootLayout.addView(ivRil, rilParams);

		ListView listView = getListView();

		// observer for getting actual screen size
		rlRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				layoutWidth = rlRootLayout.getWidth();
			}
		});

		// Create a ListView-specific touch listener. ListViews are given
		// special treatment because
		// by default they handle touches for their list items... i.e. they're
		// in charge of drawing
		// the pressed state (the list selector), handling list item clicks,
		// etc.
		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
				listView,
				new SwipeDismissListViewTouchListener.DismissCallbacks() {
					@Override
					public boolean canDismiss(int position) {
						return true;
					}

					@Override
					public void onDismiss(ListView listView, int[] reverseSortedPositions) {
						for (int position : reverseSortedPositions) {
							mAdapter.remove(mAdapter.getItem(position));
						}
						mAdapter.notifyDataSetChanged();
					}

					@Override
					public void onItemMove(float deltaX, int position) {
						int itemTop = mAdapter.getVerticalItemLocationOnScreen(position) + LIST_TOP_OFFSET;
						iconSize = mAdapter.getItemHeight();

						// consideration which item to show
						// TODO: alpha based on swipe length
						if (deltaX < 0)
							showTrayIcon(itemTop, iconSize);
						else if (deltaX > 0)
							showRilIcon(itemTop, iconSize);
					}

					@Override
					public void onActionUp(int position) {
						ivTray.setVisibility(View.GONE);
						ivRil.setVisibility(View.GONE);
					}
				});
		
		listView.setOnTouchListener(touchListener);

		// Setting this scroll listener is required to ensure that during
		// ListView scrolling, we don't look for swipes.
		listView.setOnScrollListener(touchListener.makeScrollListener());
	}

	/**
	 * Creates view filled with color
	 * @param color
	 * @return
	 */
	private ImageView createImageView(int color) {
		ImageView iv = new ImageView(this);

		iv.setBackgroundColor(color);
		iv.setVisibility(View.GONE);

		return iv;
	}

	/**
	 * Shows icon on the left side
	 * @param relativeY vertical position in px
	 * @param iconSize
	 */
	private void showTrayIcon(int relativeY, int iconSize) {
		ivTray.setVisibility(View.VISIBLE);
		ivRil.setVisibility(View.GONE);
		trayParams.topMargin = relativeY - iconSize/2;
	}

	/**
	 * Shows icon on the right side
	 * @param relativeY vertical positoin in px
	 * @param iconSize
	 */
	private void showRilIcon(int relativeY, int iconSize) {
		ivRil.setVisibility(View.VISIBLE);
		ivTray.setVisibility(View.GONE);
		rilParams.leftMargin = layoutWidth - iconSize;
		rilParams.topMargin = relativeY - iconSize/2;
	}

	@Override
	protected void onListItemClick(ListView listView, View view, int position, long id) {
		Toast.makeText(this, "Clicked " + getListAdapter().getItem(position).toString(), Toast.LENGTH_SHORT).show();
	}
}
