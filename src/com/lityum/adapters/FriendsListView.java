package com.lityum.adapters;

import java.io.InputStream;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lityum.garaj.R;

public class FriendsListView extends ArrayAdapter<FriendsRowItem> {
	Context context;
	ArrayList<FriendsRowItem> infoList;
	private LayoutInflater inflater = null;
	int Resource;
	ViewHolder holder;

	public FriendsListView(Context context, int resourceId,
			ArrayList<FriendsRowItem> items) {
		super(context, resourceId, items);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Resource = resourceId;
		infoList = items;
	}

	/* private view holder class */
	private class ViewHolder {
		ImageView imageView;
		TextView txtTitle;
	}

	public int getCount() {
		return infoList.size();
	}

	public RowItem getItem(RowItem position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (vi == null) {
			vi = inflater.inflate(R.layout.list_item_3, parent, false);
			holder = new ViewHolder();
			holder.txtTitle = (TextView) vi.findViewById(R.id.title3);
			holder.imageView = (ImageView) vi.findViewById(R.id.icon3);
			vi.setTag(holder);
		} else {
			holder = (ViewHolder) vi.getTag();
		}

		holder.txtTitle.setText(infoList.get(position).getTitle());
		new DownloadImageTask(holder.imageView, position).execute(infoList.get(position)
				.getImage());

		return vi;
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;
		int position;

		public DownloadImageTask(ImageView bmImage, int position) {
			this.bmImage = bmImage;
			this.position = position;
			bmImage.setTag(position);
			bmImage.setImageBitmap(null);
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon = null;
			try {
				InputStream in = (InputStream) new java.net.URL(urldisplay)
						.getContent();

				mIcon = BitmapFactory.decodeStream(in);
				in.close();
			} catch (Exception e) {
				// Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon;
		}

		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if (result != null && (bmImage.getTag()).equals(this.position))
				bmImage.setImageBitmap(result);
		}
	}
}
