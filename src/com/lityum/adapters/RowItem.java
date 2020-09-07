package com.lityum.adapters;


public class RowItem {
	private String image;
	private String title;
	private String desc;
	private String date;
	private String time;
	private String place;

	public RowItem() {

	}

	public RowItem(String image, String title, String desc, String date,
			String time, String place) {
		this.image = image;
		this.title = title;
		this.desc = desc;
		this.date = date;
		this.time = time;
		this.place = place;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return title + "\n" + date + "\n" + desc + "\n" + time + "\n" + place
				+ "\n" + image;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

}
