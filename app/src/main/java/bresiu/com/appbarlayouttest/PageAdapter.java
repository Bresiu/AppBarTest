/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package bresiu.com.appbarlayouttest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PageAdapter extends RecyclerView.Adapter<PageViewHolder> {

	private final int numItems;

	public PageAdapter(int numItems) {
		this.numItems = numItems;
	}

	@Override public PageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

		View itemView = LayoutInflater.from(viewGroup.getContext())
				.inflate(R.layout.list_item_card, viewGroup, false);

		return new PageViewHolder(itemView);
	}

	@Override public void onBindViewHolder(PageViewHolder fakePageVH, int i) {
		// do nothing
	}

	@Override public int getItemCount() {
		return numItems;
	}
}

