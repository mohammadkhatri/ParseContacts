package com.example.contacts_on_parse;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

public class MainActivity extends Activity {
	ParseObject testObject;
	EditText et;
	// private Contact contact;
	private int contactsCountOnParse;
	private ProgressDialog progress;
	private ListView lv_contacts;
	private ContactListAdapter adapter;
	private TextView tv_full_contact;
	Animation showAnim, hideAnim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button btnstore = (Button) findViewById(R.id.button1);
		btnstore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				DotaskOfStoringContactsOnParse();

			}

		});

		Button btnfetch = (Button) findViewById(R.id.button2);
		btnfetch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				DotaskOfFechingContactsFromParse();

			}

		});

		lv_contacts = (ListView) findViewById(R.id.lv_contacts);
		adapter = new ContactListAdapter(MainActivity.this);
		tv_full_contact = (TextView) findViewById(R.id.tv_full_contact);
		showAnim = AnimationUtils.loadAnimation(this, R.anim.scale_from_center);
		hideAnim = AnimationUtils.loadAnimation(this, R.anim.scale_to_center);
	}

	/**
	 * Start Storing Contacts On Parse
	 */
	private void DotaskOfStoringContactsOnParse() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				showProgress();

				if (countHowManyContactsOnParseCurrently() == 0) {
					StoreContacts();
					dismissProgress();
				} else {

					showDialog();

				}

			}
		}).start();

	}

	/**
	 * Start Fetching Contacts On Parse
	 */
	private void DotaskOfFechingContactsFromParse() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				showProgress();

				if (countHowManyContactsOnParseCurrently() == 0) {
					showDialog2();

				} else {

					fillList();

				}

			}
		}).start();
	}

	/**
	 * 
	 * @return int Number Of Contacts On Parse Api
	 */
	int countHowManyContactsOnParseCurrently() {
		ParseUser currentUser = ParseUser.getCurrentUser();
		ParseRelation<Contact> relation = currentUser.getRelation("contacts");
		ParseQuery<Contact> ContactsQuery = relation.getQuery();
		contactsCountOnParse = 0;

		try {
			contactsCountOnParse = ContactsQuery.count();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return contactsCountOnParse;
	}

	/**
	 * Main Task Of Getting Contacts From Phone And Storing On Parse Api
	 */
	void StoreContacts() {

		ContentResolver cr = getContentResolver();
		Cursor cursor = cr.query(Contacts.CONTENT_URI, null, null, null, null);
		if (cursor.getCount() != 0) {
			Log.i("contact", "started sending");
			while (cursor.moveToNext()) {

				final Contact contact = new Contact();
				String contactId = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts._ID));

				String DisplayName = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

				//
				// Get all phone numbers.
				//
				Cursor phones = cr.query(Phone.CONTENT_URI, null,
						Phone.CONTACT_ID + " = " + contactId, null, null);
				List<String> phonesarry = new ArrayList<String>();
				while (phones.moveToNext()) {
					String number = phones.getString(phones
							.getColumnIndex(Phone.NUMBER));
					phonesarry.add(number);

				}
				phones.close();
				//
				// Get all email addresses.
				//
				Cursor emails = cr.query(Email.CONTENT_URI, null,
						Email.CONTACT_ID + " = " + contactId, null, null);
				String email = null;
				List<String> emailarry = new ArrayList<String>();
				while (emails.moveToNext()) {
					email = emails.getString(emails.getColumnIndex(Email.DATA));
					emailarry.add(email);

				}
				emails.close();

				contact.setAuthor(ParseUser.getCurrentUser());
				contact.setEmails(emailarry);
				contact.setName(DisplayName);
				contact.setPhones(phonesarry);
				// asynchroniuos
				// contact.saveInBackground(new SaveCallback() {
				//
				// @Override
				// public void done(ParseException e) {
				// if (e == null) {
				// ParseUser.getCurrentUser().getRelation("contacts")
				// .add(contact);
				// ParseUser.getCurrentUser().saveInBackground();
				// } else {
				// e.printStackTrace();
				// }
				//
				// }
				// });

				// synchroniuos but no need to worry because all things here
				// outside main thread :)
				try {
					contact.save();
					ParseUser.getCurrentUser().getRelation("contacts")
							.add(contact);
					ParseUser.getCurrentUser().saveInBackground();
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}

			cursor.close();
			Log.i("contact", "ended sending");
		} else {
			Log.i("contact", "no contacts");
			Toast.makeText(MainActivity.this, "No contacts in phone",
					Toast.LENGTH_LONG).show();
		}

	}

	void fillList() {
		adapter.loadObjects();
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				lv_contacts.setAdapter(adapter);

			}
		});

		dismissProgress();
	}

	void showDialog() {

		runOnUiThread(new Runnable() {
			public void run() {
				AlertDialog.Builder adb = new AlertDialog.Builder(
						MainActivity.this);

				adb.setTitle("Alert");

				adb.setMessage("You have already " + contactsCountOnParse
						+ " contacts on parse.\nWould you like to continue ?");

				adb.setIcon(android.R.drawable.ic_dialog_alert);

				adb.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								StoreContacts();
								dismissProgress();
							}
						});

				adb.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								dismissProgress();
							}
						});
				adb.show();
			}
		});

	}

	void showDialog2() {

		runOnUiThread(new Runnable() {
			public void run() {
				AlertDialog.Builder adb = new AlertDialog.Builder(
						MainActivity.this);

				adb.setTitle("Alert");

				adb.setMessage("You have No contacts on parse.");

				adb.setIcon(android.R.drawable.ic_dialog_alert);

				adb.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								dismissProgress();
							}
						});

				adb.show();
			}
		});

	}

	void dismissProgress() {
		runOnUiThread(new Runnable() {

			public void run() {
				if (progress.isShowing())
					progress.dismiss();
			}
		});
	}

	void showProgress() {
		runOnUiThread(new Runnable() {

			public void run() {
				progress = ProgressDialog.show(MainActivity.this,
						"Please Wait", "Performing Operation", true);
			}
		});
	}

	public void showFullContact(Contact contact) {
		prepareTextView(contact);
		tv_full_contact.startAnimation(showAnim);
		tv_full_contact.setVisibility(View.VISIBLE);
	}

	private void prepareTextView(Contact contact) {

		String name = "Name : \n" + "\t" + contact.getName() + "\n";
		String emails = "Email : \n";
		for (String email : contact.getEmails()) {
			emails += "\t" + email + "\n";
		}
		String phones = "Phone : \n";
		for (String phone : contact.getPhones()) {
			phones += "\t" + phone + "\n";
		}
		tv_full_contact.setText(name + emails + phones);

	}

	@Override
	public void onBackPressed() {
		if (tv_full_contact.getVisibility() == View.VISIBLE) {
			tv_full_contact.startAnimation(hideAnim);
			tv_full_contact.setVisibility(View.GONE);
		} else
			super.onBackPressed();
	}
}