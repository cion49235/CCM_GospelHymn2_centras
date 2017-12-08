package com.ccm.gospelhymn.centras.adapter;

import java.util.ArrayList;
import java.util.Collections;

import com.ccm.gospelhymn.centras.R;
import com.ccm.gospelhymn.centras.activity.SubActivity;
import com.ccm.gospelhymn.centras.data.Sub_Data;
import com.ccm.gospelhymn.centras.util.FadingActionBarHelperBase;
import com.ccm.gospelhymn.centras.util.ImageLoader;
import com.ccm.gospelhymn.centras.util.RoundedTransform;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class SubAdapter extends BaseAdapter{
	public Context context;
//	public ListView listview_sub;
	public ImageLoader imgLoader;
	public SharedPreferences settings,pref;
	public Editor edit;
	ArrayList<Sub_Data> list;
	public ImageButton bt_favorite;
	public String num = "empty";
	public Cursor cursor;
	private ViewHolder holder;
	public SubAdapter(Context context,ArrayList<Sub_Data> list, ListView listview_sub) {
		this.imgLoader = new ImageLoader(context.getApplicationContext());
		this.context = context;
		this.list = list;
//		this.listview_sub = listview_sub;
		
	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		try{
			pref = context.getSharedPreferences(context.getString(R.string.txt_pref), Activity.MODE_PRIVATE);
			String array_subject= pref.getString("array_subject", "");
			String array_videoid = pref.getString("array_videoid", "");
			
			if(view == null){	
				holder = new ViewHolder();
				LayoutInflater layoutInflater = LayoutInflater.from(context);
				view = layoutInflater.inflate(R.layout.activity_sub_listrow, parent, false);
				holder.sub_img_thumb = (ImageView)view.findViewById(R.id.sub_img_thumb);
				holder.sub_txt_subject = (TextView)view.findViewById(R.id.sub_txt_subject);
				holder.bt_favorite = (ImageButton)view.findViewById(R.id.bt_favorite);
				view.setTag(holder);
			}else{
				holder = (ViewHolder) view.getTag();
			}
			
			BitmapFactory.Options dimensions = new BitmapFactory.Options(); 
			dimensions.inJustDecodeBounds = true;
			
			Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_image, dimensions);
			        int height = dimensions.outHeight;
			        int width =  dimensions.outWidth;
			Picasso.with(context)
		    .load(list.get(position).thumb)
//		    .memoryPolicy(MemoryPolicy.NO_CACHE)
		    .transform(new RoundedTransform())
		    .resize(width, height )
		    .placeholder(R.drawable.no_image)
		    .error(R.drawable.no_image)
		    .into(holder.sub_img_thumb);
			
			Collections.sort(list, new Sub_Data());
			
			holder.sub_txt_subject.setText(list.get(position).subject);
			if(list.get(position).subject.equals(array_subject) && list.get(position).videoid.equals(array_videoid)){
				holder.sub_txt_subject.setTextColor(Color.RED);
			}else{
				holder.sub_txt_subject.setTextColor(Color.BLACK);
			}
			
			try{
				cursor = SubActivity.favorite_mydb.getReadableDatabase().rawQuery(
						"select * from favorite_list where num = '"+list.get(position).id+"'", null);
				if(null != cursor && cursor.moveToFirst()){
					num = cursor.getString(cursor.getColumnIndex("num"));
				}else{
					num = "empty";
					
				}
				if(num.equals("empty")){
					holder.bt_favorite.setImageResource(R.drawable.bt_favorite_normal);
				}else{
					holder.bt_favorite.setImageResource(R.drawable.bt_favorite_pressed);	
				}
			}catch(Exception e){
			}finally{
				if(SubActivity.favorite_mydb != null){
					SubActivity.favorite_mydb.close();
				}
				if(cursor != null){
					cursor.close();
				}
			}
			holder.bt_favorite.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					cursor = SubActivity.favorite_mydb.getReadableDatabase().rawQuery(
							"select * from favorite_list where num = '"+list.get(position).id+"'", null);
					if(null != cursor && cursor.moveToFirst()){
						num = cursor.getString(cursor.getColumnIndex("num"));
					}else{
						num = "empty";
					}
					if(num.equals("empty")){
						holder.bt_favorite.setImageResource(R.drawable.bt_favorite_pressed);
						ContentValues cv = new ContentValues();
						cv.put("id", list.get(position).videoid);
	    				cv.put("num", list.get(position).id);
	    				cv.put("subject", list.get(position).subject);
	    				cv.put("thumb", list.get(position).thumb);
	    				cv.put("portal", list.get(position).portal);
						SubActivity.favorite_mydb.getWritableDatabase().insert("favorite_list", null, cv);
						if(SubActivity.sub_adapter != null){
							SubActivity.sub_adapter.notifyDataSetChanged();
						}
						Toast.makeText(context, context.getString(R.string.txt_main_activity1), Toast.LENGTH_SHORT).show();
					}else{
						holder.bt_favorite.setImageResource(R.drawable.bt_favorite_normal);
						SubActivity.favorite_mydb.getWritableDatabase().delete("favorite_list", "num" + "=" +num, null);
						if(SubActivity.sub_adapter != null){
							SubActivity.sub_adapter.notifyDataSetChanged();
						}
						Toast.makeText(context, context.getString(R.string.txt_main_activity2), Toast.LENGTH_SHORT).show();
					}
				}
			});
			holder.bt_favorite.setFocusable(false);
			holder.bt_favorite.setSelected(false);
			LinearLayout layout_sublistrow = (LinearLayout)view.findViewById(R.id.layout_sublistrow);
			if(FadingActionBarHelperBase.listview_sub.isItemChecked(position+1)){
//				view.setBackgroundColor(Color.parseColor("#ddffaa"));
//				layout_sublistrow.setBackground(context.getResources().getDrawable(R.drawable.listrow_color_green));
				layout_sublistrow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.listrow_color_green));
			}else{
//				view.setBackgroundColor(Color.parseColor("#00000000"));
//				layout_sublistrow.setBackground(context.getResources().getDrawable(R.drawable.listrow_color_black));
				layout_sublistrow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.listrow_color_black));
			}
		
		}catch (Exception e) {
		}
		return view;
	}
	
	private class ViewHolder {
		public ImageView sub_img_thumb;
		public TextView sub_txt_subject;
		public ImageButton bt_favorite;
  }
}