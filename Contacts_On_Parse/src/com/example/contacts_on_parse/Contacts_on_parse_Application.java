package com.example.contacts_on_parse;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseTwitterUtils;

public class Contacts_on_parse_Application extends Application {

	public static final String GROUP_NAME = "ALL_CONTACTS";

	@Override
	public void onCreate() {
		super.onCreate();

		ParseObject.registerSubclass(Contact.class);

		// Required - Initialize the Parse SDK
		Parse.initialize(this, getString(R.string.parse_app_id),
				getString(R.string.parse_client_key));

		Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

		// Optional - If you don't want to allow Facebook login, you can
		// remove this line (and other related ParseFacebookUtils calls)
		ParseFacebookUtils.initialize(getString(R.string.facebook_app_id));

		// Optional - If you don't want to allow Twitter login, you can
		// remove this line (and other related ParseTwitterUtils calls)
		ParseTwitterUtils.initialize(getString(R.string.twitter_consumer_key),
				getString(R.string.twitter_consumer_secret));
	}

}
