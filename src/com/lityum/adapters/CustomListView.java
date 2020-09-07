package com.lityum.adapters;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.lityum.garaj.R;

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

public class CustomListView extends ArrayAdapter<RowItem> {

	Context context;
	ArrayList<RowItem> infoList;
	private LayoutInflater inflater = null;
	int Resource;
	ViewHolder holder;

	public CustomListView(Context context, int resourceId,
			ArrayList<RowItem> items) {
		super(context, resourceId, items);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Resource = resourceId;
		infoList = items;
	}

	/* private view holder class */
	public class ViewHolder {
		ImageView imageView;
		TextView txtTitle;
		TextView txtDesc;
		TextView txtDate;
		TextView txtPlace;
		TextView txtTime;
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
			vi = inflater.inflate(R.layout.list_item_1, parent, false);
			holder = new ViewHolder();
			holder.txtDesc = (TextView) vi.findViewById(R.id.desc1);
			holder.txtTitle = (TextView) vi.findViewById(R.id.title1);
			holder.txtDate = (TextView) vi.findViewById(R.id.date1);
			holder.txtPlace = (TextView) vi.findViewById(R.id.time1);
			holder.txtTime = (TextView) vi.findViewById(R.id.place1);
			holder.imageView = (ImageView) vi.findViewById(R.id.icon1);
			vi.setTag(holder);
		} else {
			holder = (ViewHolder) vi.getTag();
		}

		holder.txtDesc.setText(infoList.get(position).getDesc());
		holder.txtDesc.setLines(1);
		holder.txtTitle.setText(infoList.get(position).getTitle());
		holder.txtTime.setText(infoList.get(position).getTime());
		holder.txtPlace.setText(infoList.get(position).getPlace());
		holder.txtDate.setText(infoList.get(position).getDate());
		new DownloadImageTask(holder.imageView, position).execute(infoList.get(
				position).getImage());

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

		
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			String urlStr = params[0];
			Bitmap img = null;

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(urlStr);
			HttpResponse response;
			try {
				response = (HttpResponse) client.execute(request);
				HttpEntity entity = response.getEntity();
				BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(
						entity);
				InputStream inputStream = bufferedEntity.getContent();
				img = BitmapFactory.decodeStream(inputStream);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return img;
		}

		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if (result != null && (bmImage.getTag()).equals(this.position))
				bmImage.setImageBitmap(result);
		}
	}

}