package com.codeest.geeknews.ui.licenses;


import com.google.gson.annotations.SerializedName;

import java.util.List;

final class License {
	@SerializedName("name") private String mName;
	@SerializedName("description") private String mDescription;
	@SerializedName("libraries") private List<Library> mLibraries;

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String description) {
		mDescription = description;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public List<Library> getLibraries() {
		return mLibraries;
	}

	public void setLibraries(List<Library> libraries) {
		mLibraries = libraries;
	}
}
