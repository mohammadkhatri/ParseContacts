package com.example.contacts_on_parse;

import java.util.List;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Contact")
public class Contact extends ParseObject {

	public String getName() {
		return getString("name");
	}

	public void setName(String title) {
		put("name", title);
	}

	public List<String> getPhones() {
		return getList("phone");
	}

	public void setPhones(List<String> phones) {
		put("phone", phones);
	}

	public List<String> getEmails() {
		return getList("email");
	}

	public void setEmails(List<String> title) {
		put("email", title);
	}

	public ParseUser getAuthor() {
		return getParseUser("author");
	}

	public void setAuthor(ParseUser currentUser) {
		put("author", currentUser);
	}

	public static ParseQuery<Contact> getQuery() {
		return ParseQuery.getQuery(Contact.class);
	}
}
