package com.codeest.geeknews.ui.licenses;


import com.google.gson.annotations.SerializedName;

final class Library {
	@SerializedName("name") private String mName;
	@SerializedName("owner") private String mOwner;
	@SerializedName("copyright") private String mCopyright;

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getOwner() {
		return mOwner;
	}

	public void setOwner(String owner) {
		mOwner = owner;
	}

	public String getCopyright() {
		return mCopyright;
	}

	public void setCopyright(String copyright) {
		mCopyright = copyright;
	}
}
