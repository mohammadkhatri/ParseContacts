package com.example.contacts_on_parse;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;

/*
 * The ContactListAdapter is an extension of ParseQueryAdapter
 * that has a custom layout for contacts.
 * 
 */

public class ContactListAdapter extends ParseQueryAdapter<Contact> {
	MainActivity context;

	public ContactListAdapter(Context context) {
		super(context, new ParseQueryAdapter.QueryFactory<Contact>() {
			public ParseQuery<Contact> create() {
				ParseUser currentUser = ParseUser.getCurrentUser();
				ParseRelation<Contact> relation = currentUser
						.getRelation("contacts");
				ParseQuery<Contact> ContactsQuery = relation.getQuery();
				ContactsQuery.addAscendingOrder("name");

				return ContactsQuery;
			}
		});
		this.context = (MainActivity) context;
	}

	@Override
	public View getItemView(Contact contact, View v, ViewGroup parent) {

		if (v == null) {
			v = View.inflate(getContext(), R.layout.list_item, null);
		}

		super.getItemView(contact, v, parent);

		TextView titleTextView = (TextView) v
				.findViewById(R.id.tv_contact_name);
		titleTextView.setText(contact.getName());
		v.setTag(contact);
		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v.getTag() instanceof Contact)
					context.showFullContact((Contact) v.getTag());
			}
		});

		return v;
	}

}
