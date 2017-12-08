package com.ccm.gospelhymn.centras.activity;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.admixer.AdAdapter;
import com.admixer.AdInfo;
import com.admixer.AdMixerManager;
import com.admixer.AdView;
import com.admixer.AdViewListener;
import com.ccm.gospelhymn.centras.R;
import com.ccm.gospelhymn.centras.data.Favorite_Data;
import com.ccm.gospelhymn.centras.db.Favorite_DBopenHelper;
import com.ccm.gospelhymn.centras.mediaplayer.ContinueMediaPlayer;
import com.ccm.gospelhymn.centras.util.FadingActionBarHelperBase;
import com.ccm.gospelhymn.centras.util.ImageLoader;
import com.ccm.gospelhymn.centras.util.RoundedTransform;
import com.ccm.gospelhymn.centras.videoplayer.CustomVideoPlayer;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Favorite_Intent_Activity extends SherlockActivity implements OnItemClickListener, OnClickListener, AdViewListener{
	public Favorite_DBopenHelper favorite_mydb;
	public SQLiteDatabase mdb;
	public Cursor cursor;
	public ConnectivityManager connectivityManger;
	public NetworkInfo mobile;
	public NetworkInfo wifi;
	public static ListView listview_favorite;
	public FavoriteAdapter<Favorite_Data> adapter;
	public static LinearLayout layout_listview_favorite, layout_nodata;
	public int SDK_INT = android.os.Build.VERSION.SDK_INT;
	public static RelativeLayout ad_layout;
	public Context context;
	private ActionBar mActionBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intent_favorite);
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_TAD, "AX0004867");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_TAD_FULL, "AX0004868");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMIXER, "09moydi9");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_CAULY, "krR4Q3Rk");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMOB, "ca-app-pub-4637651494513698/5611569369");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMOB_FULL, "ca-app-pub-4637651494513698/7088302568");
		addBannerView();
		
		context = this;
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mActionBar = getSupportActionBar();
		mActionBar.setTitle(context.getString(R.string.txt_favorite_intent_activity1));
		mActionBar.setDisplayShowHomeEnabled(false);
//		mActionBar.setDisplayShowTitleEnabled(false);
		
		layout_listview_favorite = (LinearLayout)findViewById(R.id.layout_listview_favorite);
		layout_nodata = (LinearLayout)findViewById(R.id.layout_nodata);
		listview_favorite = (ListView)findViewById(R.id.listview_favorite);
		favorite_mydb = new Favorite_DBopenHelper(this);

		displayList();
		
	}	
	
	public void addBannerView() {
    	AdInfo adInfo = new AdInfo("in7glww9");
    	adInfo.setTestMode(false);
        AdView adView = new AdView(this);
        adView.setAdInfo(adInfo, this);
        adView.setAdViewListener(this);
        ad_layout = (RelativeLayout)findViewById(R.id.ad_layout);
        if(ad_layout != null){
        	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            ad_layout.addView(adView, params);	
        }
    }
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
//		displayList();
		if(adapter != null){
			adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(favorite_mydb != null){
			favorite_mydb.close();
		}
	}
	
	
	public void displayList(){
		List<Favorite_Data>contactsList = getContactsList();
		adapter = new FavoriteAdapter<Favorite_Data>(
				this, R.layout.activity_intent_favorite_listrow, contactsList);
		
		listview_favorite.setAdapter(adapter);
		listview_favorite.setFocusable(true);
		listview_favorite.setSelected(true);
		
		
		if (SDK_INT >= Build.VERSION_CODES.HONEYCOMB){ //허니콤 버전에서만 실행 가능한 API 사용}
			listview_favorite.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    	}
		listview_favorite.setOnItemClickListener(this);
		listview_favorite.setFastScrollEnabled(false);
		listview_favorite.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		if(listview_favorite.getCount() == 0){
			layout_nodata.setVisibility(View.VISIBLE);
			layout_listview_favorite.setVisibility(View.GONE);
		}else{
			layout_nodata.setVisibility(View.GONE);
			layout_listview_favorite.setVisibility(View.VISIBLE);
		}
	}
	
	public List<Favorite_Data> getContactsList() {
		List<Favorite_Data>contactsList = new ArrayList<Favorite_Data>();
		try{
			favorite_mydb = new Favorite_DBopenHelper(this);
			mdb = favorite_mydb.getWritableDatabase();
	        cursor = mdb.rawQuery("select * from favorite_list order by _id desc", null);
	        while (cursor.moveToNext()){
				addContact(contactsList,cursor.getInt(0), cursor.getString(1), cursor.getString(2), 
						cursor.getString(3),cursor.getString(4), cursor.getString(5));
	        }
		}catch (Exception e) {
		}finally{
			cursor.close();
			favorite_mydb.close();
			mdb.close();
		}
		return contactsList;
	}
	
	public void addContact(List<Favorite_Data> contactsList, int _id, String id,String num,String subject,String thumb,String portal){
		contactsList.add(new Favorite_Data(_id, id, num, subject, thumb, portal));
	}
	
	@Override
	public void onClick(View view) {
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		int selectd_count = 0;
    	SparseBooleanArray sba =  FadingActionBarHelperBase.listview_sub.getCheckedItemPositions();
		if(sba.size() != 0){
			for(int i =  FadingActionBarHelperBase.listview_sub.getCount() -1; i>=0; i--){
				if(sba.get(i)){
					sba =  FadingActionBarHelperBase.listview_sub.getCheckedItemPositions();
					selectd_count++;
				}
			}
		}
		if(selectd_count == 0){
		}else{
		}
		if(adapter != null){
			adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
//		menu.add(context.getString(R.string.txt_menu_bottom1))
//		.setIcon(R.drawable.btn_01_off)
//		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//		
//		menu.add(context.getString(R.string.txt_menu_bottom2))
//		.setIcon(R.drawable.btn_02_off)
//		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//		
//		menu.add(context.getString(R.string.txt_menu_bottom3))
//		.setIcon(R.drawable.btn_03_off)
//		.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		
		menu.add(0, 0, 0, "")
		.setIcon(R.drawable.actionbar_bt_00_off)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		
		menu.add(0, 2, 0, "")
		.setIcon(R.drawable.actionbar_bt_home)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		menu.add(0, 3, 0, "")
		.setIcon(R.drawable.actionbar_bt_06_off)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		menu.add(0, 4, 0, "")
		.setIcon(R.drawable.actionbar_bt_03_off)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		menu.add(0, 1, 0, "")
		.setIcon(R.drawable.actionbar_bt_04_off)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
//		MenuInflater inflater = getSupportMenuInflater();
//	    inflater.inflate(R.menu.menu_toggle_check, menu);
	    
//	    MenuInflater inflater2 = getSupportMenuInflater();
//	    inflater2.inflate(R.menu.menu_toggle_expand, menu);
		
			menu.findItem(0).setVisible(false);
//			menu.findItem(1).setVisible(false);
//			menu.findItem(2).setVisible(false);
//			menu.findItem(3).setVisible(false);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == 0){
		}else if(item.getItemId() == 1){
			if(item.isChecked()){
				item.setChecked(false);
				for(int i=0; i < listview_favorite.getCount(); i++){
					listview_favorite.setItemChecked(i, false);
				}
			}else{
				item.setChecked(true);
				for(int i=0; i < listview_favorite.getCount(); i++){
					listview_favorite.setItemChecked(i, true);
				}
			}
			
		}else if(item.getItemId() == 2){
			onBackPressed();
		}else if(item.getItemId() == 3){
			SparseBooleanArray sba = listview_favorite.getCheckedItemPositions();
			ArrayList<String> array_videoid = new ArrayList<String>();
			ArrayList<String> array_subject = new ArrayList<String>();
			ArrayList<String> array_portal = new ArrayList<String>();
			ArrayList<String> array_thumb = new ArrayList<String>();
			ArrayList<String> array_artist = new ArrayList<String>();
			ArrayList<String> array_playtime = new ArrayList<String>();
			if(sba.size() != 0){
				for(int i = listview_favorite.getCount(); i>=0; i--){
					if(sba.get(i)){
						Favorite_Data favorite_data = (Favorite_Data)adapter.getItem(i);
						String videoid = favorite_data.getId();
						String subject = favorite_data.getSubject();
						String portal = favorite_data.getPortal();
						String thumb = favorite_data.getThumb();
						String artist = context.getString(R.string.app_name);
						String sprit_playtime[] = subject.split("-");
						String playtime = sprit_playtime[1];
						array_videoid.add(videoid);
						array_subject.add(subject);
						array_portal.add(portal);
						array_thumb.add(thumb);
						array_artist.add(artist);
						array_playtime.add(playtime);
						sba = listview_favorite.getCheckedItemPositions();
					}
				}
				if(array_videoid.size() != 0){
					Intent intent = new Intent(context, ContinueMediaPlayer.class);
					intent.putExtra("array_videoid", array_videoid);
					intent.putExtra("array_subject", array_subject);
					intent.putExtra("array_thumb", array_thumb);
					intent.putExtra("array_artist", array_artist);
					intent.putExtra("array_playtime", array_playtime);
					intent.putExtra("array_portal", array_portal);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}else{
					Toast.makeText(context, context.getString(R.string.txt_sub_activity6), Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(context, context.getString(R.string.txt_sub_activity6), Toast.LENGTH_SHORT).show();
			}
		}else if(item.getItemId() == 4){
			SparseBooleanArray sba = listview_favorite.getCheckedItemPositions();
			ArrayList<String> array_videoid = new ArrayList<String>();
			ArrayList<String> array_subject = new ArrayList<String>();
			ArrayList<String> array_portal = new ArrayList<String>();
			if(sba.size() != 0){
				for(int i = listview_favorite.getCount(); i>=0; i--){
					if(sba.get(i)){
						Favorite_Data favorite_data = (Favorite_Data)adapter.getItem(i);
						String videoid = favorite_data.getId();
						String subject = favorite_data.getSubject();
						String portal = favorite_data.getPortal();
						array_videoid.add(videoid);
						array_subject.add(subject);
						array_portal.add(portal);
						sba = listview_favorite.getCheckedItemPositions();
					}
				}
				if(array_videoid.size() != 0){
					Intent intent = new Intent(context, CustomVideoPlayer.class);
					intent.putExtra("array_videoid", array_videoid);
					intent.putExtra("array_subject", array_subject);
					intent.putExtra("array_portal", array_portal);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}else{
					Toast.makeText(context, context.getString(R.string.txt_sub_activity6), Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(context, context.getString(R.string.txt_sub_activity6), Toast.LENGTH_SHORT).show();
			}
		}
		return true;
	}
	
	public class FavoriteAdapter<T extends Favorite_Data>extends ArrayAdapter<T>{
		public List<T> contactsList;
		ImageLoader imgLoader = new ImageLoader(context);
		public ImageButton bt_favorite_delete;
		public String num = "empty";
		public FavoriteAdapter(Context context, int textViewResourceId, List<T> items) {
			super(context, textViewResourceId, items);
			contactsList = items;
		}
		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			try{
				if(view == null){
					LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					view = vi.inflate(R.layout.activity_intent_favorite_listrow, null);
				}
				final T contacts = contactsList.get(position);
				if (contacts != null) {
					TextView txt_favorite_subject = (TextView)view.findViewById(R.id.txt_favorite_subject);
					txt_favorite_subject.setText(contacts.getSubject());
					txt_favorite_subject.setTextColor(Color.BLACK);
					
					ImageView img_favorite_thumb = (ImageView)view.findViewById(R.id.img_favorite_thumb);
					img_favorite_thumb.setFocusable(false);
					BitmapFactory.Options dimensions = new BitmapFactory.Options(); 
					dimensions.inJustDecodeBounds = true;
					Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_image, dimensions);
					        int height = dimensions.outHeight;
					        int width =  dimensions.outWidth;
					Picasso.with(context)
				    .load(contacts.getThumb())
				    .transform(new RoundedTransform())
				    .resize(width, height )
				    .placeholder(R.drawable.no_image)
				    .error(R.drawable.no_image)
				    .into(img_favorite_thumb);
				}
				
				bt_favorite_delete = (ImageButton)view.findViewById(R.id.bt_favorite_delete);
				bt_favorite_delete.setFocusable(false);
				bt_favorite_delete.setSelected(false);
				bt_favorite_delete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						try{
							favorite_mydb.getWritableDatabase().delete("favorite_list", "_id" + "=" +contacts.get_id(), null);
							favorite_mydb.close();
							displayList();
							Toast.makeText(context, context.getString(R.string.txt_favorite_activity1), Toast.LENGTH_SHORT).show();							
						}catch(Exception e){
						}
					}
				});
				
				LinearLayout layout_sublistrow = (LinearLayout)view.findViewById(R.id.layout_favoritelistrow);
				if(listview_favorite.isItemChecked(position)){
//					view.setBackgroundColor(Color.parseColor("#ddffaa"));
//					layout_sublistrow.setBackground(context.getResources().getDrawable(R.drawable.listrow_color_green));
					layout_sublistrow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.listrow_color_green));
				}else{
//					view.setBackgroundColor(Color.parseColor("#00000000"));
//					layout_sublistrow.setBackground(context.getResources().getDrawable(R.drawable.listrow_color_black));
					layout_sublistrow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.listrow_color_yellow));
				}
			}catch (Exception e) {
			}
			return view;
		}
	}
	
	//** BannerAd 이벤트들 *************
	@Override
	public void onClickedAd(String arg0, AdView arg1) {
	}

	@Override
	public void onFailedToReceiveAd(int arg0, String arg1, AdView arg2) {
	}

	@Override
	public void onReceivedAd(String arg0, AdView arg1) {
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}