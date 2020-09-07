package com.lityum.adapters;

public class FriendsRowItem {
	private String image;
	private String title;

	public FriendsRowItem() {

	}

	public FriendsRowItem(String image, String title) {
		this.image = image;
		this.title = title;

	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return title+ "\n" + image;
	}

}
