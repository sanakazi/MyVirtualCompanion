package com.callndata.adapter;

import java.util.ArrayList;

import com.callndata.item.MyFriendListItem;
import statnor.callndata.com.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyFriendsListAdapter extends BaseAdapter {

	Context context;
	ArrayList<MyFriendListItem> MyFriendListAL = new ArrayList<MyFriendListItem>();

	public MyFriendsListAdapter(Context context, ArrayList<MyFriendListItem> MyFriendListAL) {
		this.context = context;
		this.MyFriendListAL = MyFriendListAL;
	}

	@Override
	public int getCount() {
		return MyFriendListAL.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view;

		if (convertView == null) {
			view = View.inflate(context, R.layout.adapter_friend_list, null);
		} else {
			view = convertView;
		}

		Holder holder = new Holder();

		holder.txtName = (TextView) view.findViewById(R.id.txtName);
		holder.txtNumber = (TextView) view.findViewById(R.id.txtNumber);

		holder.txtName.setText(MyFriendListAL.get(position).getName());
		holder.txtNumber.setText(MyFriendListAL.get(position).getNumber());

		return view;
	}

	class Holder {
		TextView txtName, txtNumber;
	}

}
