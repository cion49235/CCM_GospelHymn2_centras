package com.ccm.gospelhymn.centras.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.admixer.AdAdapter;
import com.admixer.AdInfo;
import com.admixer.AdMixerManager;
import com.admixer.AdView;
import com.admixer.AdViewListener;
import com.admixer.CustomPopup;
import com.admixer.CustomPopupListener;
import com.admixer.InterstitialAd;
import com.admixer.InterstitialAdListener;
import com.admixer.PopupInterstitialAdOption;
import com.ccm.gospelhymn.centras.R;
import com.ccm.gospelhymn.centras.adapter.SubAdapter;
import com.ccm.gospelhymn.centras.data.Sub_Data;
import com.ccm.gospelhymn.centras.db.Favorite_DBopenHelper;
import com.ccm.gospelhymn.centras.mediaplayer.ContinueMediaPlayer;
import com.ccm.gospelhymn.centras.util.Crypto;
import com.ccm.gospelhymn.centras.util.FadingActionBarHelper;
import com.ccm.gospelhymn.centras.util.FadingActionBarHelperBase;
import com.ccm.gospelhymn.centras.util.ImageLoader;
import com.ccm.gospelhymn.centras.util.KoreanTextMatch;
import com.ccm.gospelhymn.centras.util.KoreanTextMatcher;
import com.ccm.gospelhymn.centras.util.PreferenceUtil;
import com.ccm.gospelhymn.centras.util.Utils;
import com.ccm.gospelhymn.centras.videoplayer.CustomVideoPlayer;
import com.skplanet.tad.AdFloating;
import com.skplanet.tad.AdFloatingListener;
import com.skplanet.tad.AdRequest.ErrorCode;
import com.skplanet.tad.AdSlot;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import kr.co.inno.autocash.service.AutoServiceActivity;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;
public class SubActivity extends SherlockActivity implements OnItemClickListener, OnClickListener,OnCheckedChangeListener, AdViewListener, AdFloatingListener, InterstitialAdListener, CustomPopupListener, OnScrollListener{
	public static Context context;
	public String num, subject, thumb;
	public static SubAdapter sub_adapter;
//	public static ListView listview_sub;
	public static int SDK_INT = android.os.Build.VERSION.SDK_INT;
	public ArrayList<Sub_Data> list = new ArrayList<Sub_Data>();
	public Sub_ParseAsync sub_parseAsync = null;
	public boolean retry_alert = false;
	public static String i;
	public ImageView image_header;
	public FadingActionBarHelper helper;
	public ImageLoader imgLoader;
	private ActionBar mActionBar;
	public ToggleButton bt_toggle_check, bt_toggle_expand;
	public boolean list_expand = false;
	public static RelativeLayout ad_layout;
	public static AdFloating mAdFloating;
	public Handler handler = new Handler();
	public boolean flag;
	public static InterstitialAd interstialAd;
	public static Favorite_DBopenHelper favorite_mydb;
	public SharedPreferences settings,pref;
	public static AlertDialog security_alertDialog;
	public static AlertDialog re_security_alertDialog;
	public static Button btn_security01, btn_security02;
	public static CheckBox checkbox_security01, checkbox_security02, checkbox_security03;
	public static Button btn_re_security01, btn_re_security02;
	public static CheckBox checkbox_re_security01, checkbox_re_security02, checkbox_re_security03;
	public View view, view2;
	public static boolean ischeck_security01 = false;
	public static boolean ischeck_security02 = false;
	public Editor edit;
	public static AlertDialog alertDialog;
	public static int category_which = 0;
	public static LinearLayout layout_nodata;
	public static EditText edit_searcher;
	public static ImageButton bt_home, bt_search_result; 
	public String searchKeyword;
	private KoreanTextMatch match1, match2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	context = this;
	num = getIntent().getStringExtra("num");
	subject = getIntent().getStringExtra("subject");
	thumb = getIntent().getStringExtra("thumb");
	retry_alert = true;
	mActionBar = getSupportActionBar();
	mActionBar.setTitle(subject);
	mActionBar.setDisplayShowHomeEnabled(false);
//	mActionBar.setDisplayShowTitleEnabled(false);
	exit_handler();
	auto_service();
	load();
	}
	
	private void auto_service() {
        Intent intent = new Intent(context, AutoServiceActivity.class);
        context.stopService(intent);
        context.startService(intent);
    }
	
	public void load() {
		setFadingActionBar();
		image_header = (ImageView) findViewById(R.id.image_header);
		try {
			imgLoader = new ImageLoader(context.getApplicationContext());
			Picasso.with(context)
		    .load(R.drawable.icon512)
		    .placeholder(R.drawable.no_image)
		    .error(R.drawable.no_image)
		    .into(image_header);
		} catch (Exception e) {
		}
		seacher_start();
		displaylist();
	}

	public void setFadingActionBar() {
		helper = new FadingActionBarHelper()
				.actionBarBackground(R.drawable.ab_background)
				.headerLayout(R.layout.profile_image)
				.contentLayout(R.layout.activity_sub);
		setContentView(helper.createView(this));
		helper.initActionBar(this);
		helper.initContext(this);
		favorite_mydb = new Favorite_DBopenHelper(context);
		
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_TAD, "AX0004867");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_TAD_FULL, "AX0004868");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMIXER, "09moydi9");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_CAULY, "krR4Q3Rk");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMOB, "ca-app-pub-4637651494513698/5611569369");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMOB_FULL, "ca-app-pub-4637651494513698/7088302568");
		
		
		layout_nodata = (LinearLayout)findViewById(R.id.layout_nodata);
		edit_searcher = (EditText)findViewById(R.id.edit_searcher);
		bt_search_result = (ImageButton)findViewById(R.id.bt_search_result);
		bt_search_result.setOnClickListener(this);
		
		bt_home = (ImageButton)findViewById(R.id.bt_home);
		bt_home.setOnClickListener(this);
		
//	  Custom Popup 시작
	    CustomPopup.setCustomPopupListener(this);
	    CustomPopup.startCustomPopup(this, "09moydi9");

	    addBannerView();
		create_mAdFloating();
	}
	
	public void addBannerView() {
    	AdInfo adInfo = new AdInfo("09moydi9");
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
	
	public void addInterstitialView_popup() {
    	if(interstialAd != null)
			return;
		AdInfo adInfo = new AdInfo("09moydi9");
		adInfo.setInterstitialTimeout(0); // 초단위로 전면 광고 타임아웃 설정 (기본값 : 0, 0 이면 서버 지정 시간(20)으로 처리됨)
		adInfo.setUseRTBGPSInfo(false);
		adInfo.setMaxRetryCountInSlot(-1); // 리로드 시간 내에 전체 AdNetwork 반복 최대 횟수(-1 : 무한, 0 : 반복 없음, n : n번 반복)
		adInfo.setBackgroundAlpha(true); // 고수익 전면광고 노출 시 광고 외 영역 반투명처리 여부 (true: 반투명, false: 처리안함)

//		 이 주석을 제거하시면 고수익 전면광고가 팝업형으로 노출됩니다.
		// 팝업형 전면광고 세부설정을 원하시면 아래 PopupInterstitialAdOption 설정하세요
		PopupInterstitialAdOption adConfig = new PopupInterstitialAdOption();
		// 팝업형 전면광고 노출 상태에서 뒤로가기 버튼 방지 (true : 비활성화, false : 활성화)
		adConfig.setDisableBackKey(true);
		// 왼쪽버튼. 디폴트로 제공되며, 광고를 닫는 기능이 적용되는 버튼 (버튼문구, 버튼색상)
		adConfig.setButtonLeft(context.getString(R.string.txt_finish_no), "#234234");
		// 오른쪽 버튼을 사용하고자 하면 반드시 설정하세요. 앱을 종료하는 기능을 적용하는 버튼. 미설정 시 위 광고종료 버튼만 노출
		adConfig.setButtonRight(context.getString(R.string.txt_finish_yes), "#234234");
		// 버튼영역 색상지정
		adConfig.setButtonFrameColor(null);
		// 팝업형 전면광고 추가옵션 (com.admixer.AdInfo$InterstitialAdType.Basic : 일반전면, com.admixer.AdInfo$InterstitialAdType.Popup : 버튼이 있는 팝업형 전면)
		adInfo.setInterstitialAdType(AdInfo.InterstitialAdType.Popup, adConfig);
		
		interstialAd = new InterstitialAd(this);
		interstialAd.setAdInfo(adInfo, this);
		interstialAd.setInterstitialAdListener(this);
		interstialAd.startInterstitial();
    }
	
	public void addInterstitialView() {
    	if(interstialAd == null) {
        	AdInfo adInfo = new AdInfo("09moydi9");
//        	adInfo.setTestMode(false);
        	interstialAd = new InterstitialAd(this);
        	interstialAd.setAdInfo(adInfo, this);
        	interstialAd.setInterstitialAdListener(this);
        	interstialAd.startInterstitial();
    	}
    }
	
	public void create_mAdFloating(){
		// AdFloating 객체를 생성합니다. 
		mAdFloating = new AdFloating(this); 
		// AdFloating 상태를 모니터링 할 listner 를 등록합니다. listener 에 대한 내용은 아래 참조 mAdFloating.setListener(mListener);   
		// 할당받은 ClientID 를 설정합니다. 
		mAdFloating.setClientId("AX0004D7E");      
		// 할당받은 Slot 번호를 설정합니다.
		mAdFloating.setSlotNo(AdSlot.FLOATING);  
		// TestMode 여부를 설정합니다. 
		mAdFloating.setTestMode(false);  
		// 광고를 삽입할 parentView 를 설정합니다.
		mAdFloating.setParentWindow(getWindow()); 
		// 광고를 요청 합니다. 로드시 설정한 값들이 유효한지 판단한 후 광고를 수신합니다.
		// 광고 요청에 대한 결과는 설정한 listener 를 통해 알 수 있습니다.
		mAdFloating.setListener(this);
		// 일일 광고 노출 제한을 설정합니다.
//		mAdFloating.setDailyFrequency(5);
		if (mAdFloating != null) {
			try{
				mAdFloating.loadAd(null);
			}catch(Exception e){ 
				e.printStackTrace(); 
			} 
		}
		handler.postDelayed(new Runnable() {
			 @Override
			 public void run() {
				 if (mAdFloating.isReady()) {
						try {
							mAdFloating.showAd(180, 200);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
			 }
		 },3000);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		if(sub_adapter != null){
			sub_adapter.notifyDataSetChanged();	
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		retry_alert = false;
		if(sub_parseAsync != null){
			sub_parseAsync.cancel(true);
		}
		if (mAdFloating != null) {
			mAdFloating.destroyAd();
		}
		
		if(favorite_mydb != null){
			favorite_mydb.close();
		}
		// Custom Popup 종료
		CustomPopup.stopCustomPopup();
		
		settings = getSharedPreferences(context.getString(R.string.txt_pref), MODE_PRIVATE);
		edit = settings.edit();
		edit.putInt("category_which", 0);
		edit.commit();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		PreferenceUtil.setBooleanSharedData(context, PreferenceUtil.PREF_AD_VIEW, false);
	}
	
	public void onConfigurationChanged(android.content.res.Configuration newConfig) {
	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	    	if (mAdFloating != null) {
				try {
					mAdFloating.moveAd(320, 50);
				} catch (Exception e) {
				}
			}
	    }else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	    	if (mAdFloating != null) {
				try {
					mAdFloating.moveAd(180, 200);
				} catch (Exception e) {
				}
			}
	    }
	    super.onConfigurationChanged(newConfig);
	};
	
	public void displaylist(){
		sub_parseAsync = new Sub_ParseAsync();
		sub_parseAsync.execute();
		if (SDK_INT >= Build.VERSION_CODES.HONEYCOMB){ //허니콤 버전에서만 실행 가능한 API 사용}
			FadingActionBarHelperBase.listview_sub.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		FadingActionBarHelperBase.listview_sub.setOnItemClickListener(this);
		FadingActionBarHelperBase.listview_sub.setItemsCanFocus(false);
		FadingActionBarHelperBase.listview_sub.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}
	
	public void seacher_start(){
		edit_searcher.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable arg0) {
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				try {
					searchKeyword = s.toString().toLowerCase();
					Log.e("dsu", "검색어 : " + searchKeyword);
				} catch (Exception e) {
				}
			}
		});
	}
	
	
	public void MoreLoad(String number) {
		try{
			if (FadingActionBarHelperBase.listview_sub.getLastVisiblePosition() >= FadingActionBarHelperBase.listview_sub.getCount() - 1
					&& FadingActionBarHelperBase.listview_sub.getChildAt(0).getTop() != 0) {
			}	
		}catch(Exception e){
		}
	}
	
	public class Sub_ParseAsync extends AsyncTask<String, Integer, String>{
		String Response;
		Sub_Data sub_data;
		String total_row;
		String parse_id;
		String parse_subject;
		String parse_thumb;
		String parse_portal;
		ArrayList<Sub_Data> menuItems = new ArrayList<Sub_Data>();
		public Sub_ParseAsync(){
		}
			@Override
			protected String doInBackground(String... params) {
				String sTag;
				if(Integer.parseInt(num) > 0 && Integer.parseInt(num) < 301){
					i = "1";
				}else if(Integer.parseInt(num) > 300 && Integer.parseInt(num) < 601){
					i = "2";
				}else if(Integer.parseInt(num) > 600 && Integer.parseInt(num) < 901){
					i = "3";
				}else if(Integer.parseInt(num) > 900 && Integer.parseInt(num) < 1201){
					i = "4";
				}else if(Integer.parseInt(num) > 1200 && Integer.parseInt(num) < 1501){
					i = "5";
				}else if(Integer.parseInt(num) > 1500 && Integer.parseInt(num) < 1801){
					i = "6";
				}else if(Integer.parseInt(num) > 1800 && Integer.parseInt(num) < 2101){
					i = "7";
				}else if(Integer.parseInt(num) > 2100 && Integer.parseInt(num) < 2401){
					i = "8";
				}else if(Integer.parseInt(num) > 2400 && Integer.parseInt(num) < 2701){
					i = "9";
				}else if(Integer.parseInt(num) > 2700 && Integer.parseInt(num) < 3001){
					i = "10";
				}else if(Integer.parseInt(num) > 3000){
					i = "11";
				}
				try{
					Log.e("dsu", "num : " + num);
				   String data = Crypto.decrypt(Utils.data, context.getString(R.string.txt_str8));
		           String str = data+i+".php?view="+num; 
		           HttpURLConnection localHttpURLConnection = (HttpURLConnection)new URL(str).openConnection();
		           HttpURLConnection.setFollowRedirects(false);
		           localHttpURLConnection.setConnectTimeout(15000);
		           localHttpURLConnection.setReadTimeout(15000); 
		           localHttpURLConnection.setRequestMethod("GET");
		           localHttpURLConnection.connect();
		           InputStream inputStream = new URL(str).openStream(); //open Stream을 사용하여 InputStream을 생성합니다.
		           XmlPullParserFactory factory = XmlPullParserFactory.newInstance(); 
		           XmlPullParser xpp = factory.newPullParser();
		           xpp.setInput(inputStream, "EUC-KR"); //euc-kr로 언어를 설정합니다. utf-8로 하니깐 깨지더군요
		           int eventType = xpp.getEventType();
		           while (eventType != XmlPullParser.END_DOCUMENT) {
			        	if (eventType == XmlPullParser.START_DOCUMENT) {
			        	}else if (eventType == XmlPullParser.END_DOCUMENT) {
			        	}else if (eventType == XmlPullParser.START_TAG){
			        		sTag = xpp.getName();
			        		if(sTag.equals("Content")){
			        			sub_data = new Sub_Data();
			        			parse_id = xpp.getAttributeValue(null, "id") + "";
			            	}else if(sTag.equals("videoid")){
			        			Response = xpp.nextText()+"";
			            	}else if(sTag.equals("subject")){
			            		parse_subject = xpp.nextText()+"";
			            	}else if(sTag.equals("portal")){
			            		parse_portal = xpp.nextText()+"";
			            	}else if(sTag.equals("thumb")){
			            		parse_thumb = xpp.nextText()+"";
			            	}
			        	} else if (eventType == XmlPullParser.END_TAG){
			            	sTag = xpp.getName();
			            	if(sTag.equals("Content")){
			            		sub_data.id = parse_id;
			            		sub_data.videoid = Response;
			            		sub_data.subject = parse_subject;
			            		sub_data.portal = parse_portal;
			            		sub_data.thumb = parse_thumb;
			            		if(searchKeyword != null && "".equals(searchKeyword.trim()) == false){
			        				KoreanTextMatcher matcher1 = new KoreanTextMatcher(searchKeyword.toLowerCase());
			        				KoreanTextMatcher matcher2 = new KoreanTextMatcher(searchKeyword.toUpperCase());
			        				match1 = matcher1.match(sub_data.subject.toLowerCase());
			        				match2 = matcher2.match(sub_data.subject.toUpperCase());
			        				if(match1.success()){
			        					list.add(sub_data);
			        				}else if (match2.success()){
			        					list.add(sub_data);
			        				}
			        			}else{
			        				list.add(sub_data);
			        			}
			            	}
			            } else if (eventType == XmlPullParser.TEXT) {
			            }
			            eventType = xpp.next();
			        }
		         }
		         catch (SocketTimeoutException localSocketTimeoutException)
		         {
		         }
		         catch (ClientProtocolException localClientProtocolException)
		         {
		         }
		         catch (IOException localIOException)
		         {
		         }
		         catch (Resources.NotFoundException localNotFoundException)
		         {
		         }
		         catch (XmlPullParserException localXmlPullParserException)
		         {
		         }
		         catch (NullPointerException NullPointerException)
		         {
		         }
				 catch (Exception e)
		         {
		         }
		         return Response;
			}
			
			@Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            setSupportProgressBarIndeterminateVisibility(true);
	        }
			@Override
			protected void onPostExecute(String Response) {
				super.onPostExecute(Response);
				setSupportProgressBarIndeterminateVisibility(false);
				try{
					if(Response != null){
						for(int i=0;; i++){
							if(i >= list.size()){
//							while (i > list.size()-1){
								sub_adapter = new SubAdapter(context, menuItems, FadingActionBarHelperBase.listview_sub);
								FadingActionBarHelperBase.listview_sub.setAdapter(sub_adapter);
								FadingActionBarHelperBase.listview_sub.setFocusable(true);
								FadingActionBarHelperBase.listview_sub.setSelected(true);
								FadingActionBarHelperBase.listview_sub.setSelection(0);
								if(FadingActionBarHelperBase.listview_sub.getCount() == 0){
									layout_nodata.setVisibility(View.VISIBLE);
								}else{
									layout_nodata.setVisibility(View.GONE);
								}
								bt_toggle_check = (ToggleButton)findViewById(R.id.bt_toggle_check);
								bt_toggle_expand = (ToggleButton)findViewById(R.id.bt_toggle_expand);
								bt_toggle_check.setOnCheckedChangeListener(SubActivity.this);
								bt_toggle_expand.setOnCheckedChangeListener(SubActivity.this);
								return;
							}
							menuItems.add(list.get(i));
						}
					}else{
						Retry_AlertShow(context.getString(R.string.txt_sub_activity3));
					}
				}catch(NullPointerException e){
				}
			}
			@Override
			protected void onProgressUpdate(Integer... values) {
				super.onProgressUpdate(values);
			}
		}
	
	@Override
	public void onClick(View view) {
		if(view == btn_security01){
			Uri uri = Uri.parse(context.getString(R.string.txt_location_url));
			Intent intent  = new Intent(Intent.ACTION_VIEW,uri);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}else if(view == btn_security02){
			Uri uri = Uri.parse(context.getString(R.string.txt_location_url));
			Intent intent  = new Intent(Intent.ACTION_VIEW,uri);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}else if(view == checkbox_security01){
			if(checkbox_security01.isChecked()){
				checkbox_security01.setChecked(true);
			}else{
				checkbox_security01.setChecked(false);
			}
		}else if(view == checkbox_security02){
			if(checkbox_security02.isChecked()){
				checkbox_security02.setChecked(true);
			}else{
				checkbox_security02.setChecked(false);
			}
		}else if(view == checkbox_security03){
			if(checkbox_security03.isChecked()){
				checkbox_security03.setChecked(true);
				checkbox_security01.setChecked(true);
				checkbox_security02.setChecked(true);
			}else{
				checkbox_security03.setChecked(false);
				checkbox_security01.setChecked(false);
				checkbox_security02.setChecked(false);
			}
		}
		else if(view == btn_re_security01){
			Uri uri = Uri.parse(context.getString(R.string.txt_location_url));
			Intent intent  = new Intent(Intent.ACTION_VIEW,uri);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}else if(view == btn_re_security02){
			Uri uri = Uri.parse(context.getString(R.string.txt_location_url));
			Intent intent  = new Intent(Intent.ACTION_VIEW,uri);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}else if(view == checkbox_re_security01){
			if(checkbox_re_security01.isChecked()){
				checkbox_re_security01.setChecked(true);
			}else{
				checkbox_re_security01.setChecked(false);
			}
		}else if(view == checkbox_re_security02){
			if(checkbox_re_security02.isChecked()){
				checkbox_re_security02.setChecked(true);
			}else{
				checkbox_re_security02.setChecked(false);
			}
		}else if(view == checkbox_re_security03){
			if(checkbox_re_security03.isChecked()){
				checkbox_re_security03.setChecked(true);
				checkbox_re_security01.setChecked(true);
				checkbox_re_security02.setChecked(true);
			}else{
				checkbox_re_security03.setChecked(false);
				checkbox_re_security01.setChecked(false);
				checkbox_re_security02.setChecked(false);
			}
		}else if(view == bt_search_result){
			String search_text = edit_searcher.getText().toString();
			if ((search_text != null) && (search_text.length() > 0)){
				InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
	    		inputMethodManager.hideSoftInputFromWindow(edit_searcher.getWindowToken(), 0);
				
				list = new ArrayList<Sub_Data>();
				list.clear();
				load();
			}else{
				Toast.makeText(context, context.getString(R.string.txt_search_empty), Toast.LENGTH_SHORT).show();
			}
		}else if(view == bt_home){
			list = new ArrayList<Sub_Data>();
			list.clear();
			edit_searcher.setText("");
			load();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
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
//			if(mAdFloating.isShown()){
//				mAdFloating.closeAd();
//			}
		}
		if(sub_adapter != null){
			sub_adapter.notifyDataSetChanged();
		}
	}

	public void Retry_AlertShow(String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(SubActivity.this);
		builder.setTitle(context.getString(R.string.txt_network_error));
		builder.setCancelable(false);
		builder.setMessage(msg);
		builder.setInverseBackgroundForced(true);
		builder.setNeutralButton(context.getString(R.string.txt_sub_activity4), new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
             	 list = new ArrayList<Sub_Data>();
             	 list.clear();
             	 sub_parseAsync = new Sub_ParseAsync();
             	 sub_parseAsync.execute();
             	 dialog.dismiss();
			}
		});
		builder.setNegativeButton(context.getString(R.string.txt_sub_activity5), new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
             	 dialog.dismiss();
			}
		});
		AlertDialog myAlertDialog = builder.create();
		if(retry_alert) myAlertDialog.show();
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
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		menu.add(0, 1, 0, "")
		.setIcon(R.drawable.actionbar_bt_01_off)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			
		menu.add(0, 2, 0, "")
		.setIcon(R.drawable.actionbar_bt_02_off)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		menu.add(0, 3, 0, "")
		.setIcon(R.drawable.actionbar_bt_06_off)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		menu.add(0, 4, 0, "")
		.setIcon(R.drawable.actionbar_bt_03_off)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.menu_toggle_check, menu);
	    
//	    MenuInflater inflater2 = getSupportMenuInflater();
//	    inflater2.inflate(R.menu.menu_toggle_expand, menu);
		
			menu.findItem(0).setVisible(false);
			menu.findItem(1).setVisible(false);
//			menu.findItem(2).setVisible(false);
//			menu.findItem(3).setVisible(false);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == 0){
			final String channel_title[] = {
					context.getString(R.string.txt_channel_title0),
					context.getString(R.string.txt_channel_title1),
					context.getString(R.string.txt_channel_title2)
			};
			settings = getSharedPreferences(context.getString(R.string.txt_pref), MODE_PRIVATE);
			edit = settings.edit();
			pref = getSharedPreferences(context.getString(R.string.txt_pref), Activity.MODE_PRIVATE);
			category_which = pref.getInt("category_which", 0);
			
			alertDialog = new AlertDialog.Builder(context)
			.setSingleChoiceItems(channel_title, category_which, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(which == 0){
						searchKeyword = "";
						
						num = "405";
						subject = context.getString(R.string.app_name);
						thumb = "http://i.ytimg.com/vi/RHaiK-gP9-g/hqdefault.jpg";
						list = new ArrayList<Sub_Data>();
						list.clear();
						load();
						dialog.dismiss();
						edit.putInt("category_which", 0);
					}else if(which == 1){
						searchKeyword = "";
						
						num = "406";
						subject = context.getString(R.string.app_name);
						thumb = "http://i.ytimg.com/vi/E1SYL3KJGn4/hqdefault.jpg";
						list = new ArrayList<Sub_Data>();
						list.clear();
						load();
						dialog.dismiss();
						edit.putInt("category_which", 1);
					}else if(which == 2){
						searchKeyword = "";
						
						num = "407";
						subject = context.getString(R.string.app_name);
						thumb = "http://i.ytimg.com/vi/aupR9weH2IU/hqdefault.jpg";
						list = new ArrayList<Sub_Data>();
						list.clear();
						load();
						dialog.dismiss();
						edit.putInt("category_which", 2);
					}
					edit.commit();
					dialog.dismiss();
				}
			}).show();
			
		}else if(item.getItemId() == 1){
		}else if(item.getItemId() == 2){
			Intent intent = new Intent(this, Favorite_Intent_Activity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}else if(item.getItemId() == 3){
			SparseBooleanArray sba = FadingActionBarHelperBase.listview_sub.getCheckedItemPositions();
			ArrayList<String> array_videoid = new ArrayList<String>();
			ArrayList<String> array_subject = new ArrayList<String>();
			ArrayList<String> array_portal = new ArrayList<String>();
			ArrayList<String> array_thumb = new ArrayList<String>();
			ArrayList<String> array_artist = new ArrayList<String>();
			ArrayList<String> array_playtime = new ArrayList<String>();
			if(sba.size() != 0){
				for(int i = FadingActionBarHelperBase.listview_sub.getCount(); i>=0; i--){
					if(sba.get(i+1)){
						Sub_Data sub_data = (Sub_Data)sub_adapter.getItem(i);
						String videoid = sub_data.videoid;
						String subject = sub_data.subject;
						String portal = sub_data.portal;
						String thumb = sub_data.thumb;
						String artist = context.getString(R.string.app_name);
						String sprit_playtime[] = subject.split("-");
						String playtime = sprit_playtime[1];
						array_videoid.add(videoid);
						array_subject.add(subject);
						array_portal.add(portal);
						array_thumb.add(thumb);
						array_artist.add(artist);
						array_playtime.add(playtime);
						sba = FadingActionBarHelperBase.listview_sub.getCheckedItemPositions();
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
			SparseBooleanArray sba = FadingActionBarHelperBase.listview_sub.getCheckedItemPositions();
			ArrayList<String> array_videoid = new ArrayList<String>();
			ArrayList<String> array_subject = new ArrayList<String>();
			ArrayList<String> array_portal = new ArrayList<String>();
			if(sba.size() != 0){
				for(int i = FadingActionBarHelperBase.listview_sub.getCount(); i>=0; i--){
					if(sba.get(i+1)){
						Sub_Data sub_data = (Sub_Data)sub_adapter.getItem(i);
						String videoid = sub_data.videoid;
						String subject = sub_data.subject;
						String portal = sub_data.portal;
						array_videoid.add(videoid);
						array_subject.add(subject);
						array_portal.add(portal);
						sba = FadingActionBarHelperBase.listview_sub.getCheckedItemPositions();
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

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(buttonView == bt_toggle_check){
			if(isChecked == true){
				bt_toggle_check.setBackgroundResource(R.drawable.actionbar_bt_04_on);
				for(int i=0; i < FadingActionBarHelperBase.listview_sub.getCount(); i++){
					FadingActionBarHelperBase.listview_sub.setItemChecked(i, true);
				}
			}else{
				bt_toggle_check.setBackgroundResource(R.drawable.actionbar_bt_04_off);
				for(int i=0; i < FadingActionBarHelperBase.listview_sub.getCount(); i++){
					FadingActionBarHelperBase.listview_sub.setItemChecked(i, false);
				}
			}
		}else if(buttonView == bt_toggle_expand){
			list = new ArrayList<Sub_Data>();
			list.clear();
			if(isChecked == true){
				bt_toggle_expand.setBackgroundResource(R.drawable.actionbar_bt_05_off);
				list_expand = isChecked;
			}else{
				bt_toggle_expand.setBackgroundResource(R.drawable.actionbar_bt_05_on);
				list_expand = isChecked;
			}
			load();
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
	
	//** AdFloatingListener 이벤트들 *************
	@Override
	public void onAdWillLoad() {

	}

	@Override
	public void onAdResized() {
	
	}
	
	@Override
	public void onAdResizeClosed() {
	
	}
	
	@Override
	public void onAdPresentScreen() {
		Log.i("dsu", "플로팅배너onAdPresentScreen");
	}
	
	@Override
	public void onAdLoaded() {
		Log.i("dsu", "플로팅배너onAdLoaded");
	}
	
	@Override
	public void onAdLeaveApplication() {
		Log.i("dsu", "플로팅배너onAdLeaveApplication");
	}
	
	@Override
	public void onAdExpanded() {
		
	}
	
	@Override
	public void onAdExpandClosed() {
		
	}
	
	@Override
	public void onAdDismissScreen() {
		Log.i("dsu", "플로팅배너onAdDismissScreen");
	}

	@Override
	public void onAdFailed(ErrorCode arg0) {
		Log.i("dsu", "플로팅배너onAdFailed : " + arg0);
	}

	@Override
	public void onAdClicked() {
	
	}	
	
	//** InterstitialAd 이벤트들 *************
	@Override
	public void onInterstitialAdClosed(InterstitialAd arg0) {
		interstialAd = null;
		PreferenceUtil.setBooleanSharedData(context, PreferenceUtil.PREF_AD_VIEW, true);
		finish();
	}

	@Override
	public void onInterstitialAdFailedToReceive(int arg0, String arg1,
			InterstitialAd arg2) {
		interstialAd = null;
	}

	@Override
	public void onInterstitialAdReceived(String arg0, InterstitialAd arg1) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(context.getString(R.string.app_name));
		builder.setMessage(context.getString(R.string.txt_finish_ment));
		builder.setInverseBackgroundForced(true);
		builder.setNeutralButton(context.getString(R.string.txt_finish_yes), new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
				PreferenceUtil.setBooleanSharedData(context, PreferenceUtil.PREF_AD_VIEW, true);
				finish();
			}
		});
		builder.setNegativeButton(context.getString(R.string.txt_finish_no), new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
             	 dialog.dismiss();
			}
		});
		AlertDialog myAlertDialog = builder.create();
		if(retry_alert) myAlertDialog.show();
	}
	
	@Override
	public void onInterstitialAdShown(String arg0, InterstitialAd arg1) {
	}
	
	public void exit_handler(){
    	handler = new Handler(){
    		@Override
    		public void handleMessage(Message msg) {
    			if(msg.what == 0){
    				flag = false;
    			}
    		}
    	};
    }
	
	//** CustomPopup 이벤트들 *************
	@Override
	public void onCloseCustomPopup(String arg0) {
	}

	@Override
	public void onHasNoCustomPopup() {
	}

	@Override
	public void onShowCustomPopup(String arg0) {
	}

	@Override
	public void onStartedCustomPopup() {
	}

	@Override
	public void onWillCloseCustomPopup(String arg0) {
	}

	@Override
	public void onWillShowCustomPopup(String arg0) {
	}
	
	@Override
	public void onLeftClicked(String arg0, InterstitialAd arg1) {
	}

	@Override
	public void onRightClicked(String arg0, InterstitialAd arg1) {
		PreferenceUtil.setBooleanSharedData(context, PreferenceUtil.PREF_AD_VIEW, true);
		finish();
	}

	@Override
	public void onAdClosed(boolean arg0) {
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if(keyCode == KeyEvent.KEYCODE_BACK){
			 if(!flag){
				 Toast.makeText(context, context.getString(R.string.txt_finish) , Toast.LENGTH_SHORT).show();
				 flag = true;
				 handler.sendEmptyMessageDelayed(0, 2000);
				 return false;
			 }else{
				 handler.postDelayed(new Runnable() {
					 @Override
					 public void run() {
						 PreferenceUtil.setBooleanSharedData(context, PreferenceUtil.PREF_AD_VIEW, true);
						 finish();
					 }
				 },0);
			 }
			 return false;	 
		 }
		return super.onKeyDown(keyCode, event);
	}
	
	public static void setNotification_continue(Context context, ArrayList<String> array_music, ArrayList<String> array_videoid, ArrayList<String> array_playtime, ArrayList<String> array_imageurl, ArrayList<String> array_artist, int video_num) {
    	if (SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
			try{
				notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		        Intent intent = new Intent(context, ContinueMediaPlayer.class);
		    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		    	intent.putExtra("array_music", array_music);
				intent.putExtra("array_videoid", array_videoid);
				intent.putExtra("array_playtime", array_playtime);
				intent.putExtra("array_imageurl", array_imageurl);
				intent.putExtra("array_artist", array_artist);
				intent.putExtra("video_num", video_num);
				PendingIntent content = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		        Notification.Builder builder = new Notification.Builder(context)
		                .setContentIntent(content)
		                .setSmallIcon(R.drawable.icon128)
		                .setContentTitle(array_music.get(video_num))
//		                .setContentText("")
		                .setDefaults(Notification.FLAG_AUTO_CANCEL)
		                .setTicker(context.getString(R.string.app_name));
		        notification = builder.build();
		        notificationManager.notify(noti_state, notification);
			}catch(NullPointerException e){
			}
		}
    	
    }
	
	public static void setNotification_Cancel(){
    	if(notificationManager != null) notificationManager.cancel(noti_state);
    }
	
	public void Security_AlertShow(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(context.getString(R.string.txt_security_title));
		builder.setCancelable(false);
		builder.setInverseBackgroundForced(true);
		builder.setView(view);
		builder.setPositiveButton(context.getString(R.string.txt_security_ok), new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
				settings = getSharedPreferences(context.getString(R.string.txt_pref), MODE_PRIVATE);
				edit = settings.edit();
				if(checkbox_security01.isChecked() && checkbox_security02.isChecked()){
					edit.putBoolean("checkbox_security01", checkbox_security01.isChecked());
					edit.putBoolean("checkbox_security02", checkbox_security02.isChecked());
				}else{
					edit.putBoolean("checkbox_security01", checkbox_security01.isChecked());
					edit.putBoolean("checkbox_security02", checkbox_security02.isChecked());
					Re_Security_AlertShow();
					if(view != null){
						((ViewGroup)view.getParent()).removeView(view);
					}
					checkbox_security01.setChecked(false);
					checkbox_security02.setChecked(false);
					Toast.makeText(context, context.getString(R.string.txt_service_ok), Toast.LENGTH_SHORT).show();
				}
				edit.commit();
				dialog.dismiss();
				Log.i("dsu", "" + checkbox_security01.isChecked() +"\n" + checkbox_security02.isChecked());
			}
		});
		
		builder.setNegativeButton(context.getString(R.string.txt_security_cancel), new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
				finish();
				dialog.dismiss();
			}
		});
		security_alertDialog = builder.create();
		security_alertDialog.show();
	}
	
	public void Re_Security_AlertShow(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(context.getString(R.string.txt_security_title));
		builder.setCancelable(false);
		builder.setInverseBackgroundForced(true);
		builder.setView(view2);
		builder.setPositiveButton(context.getString(R.string.txt_security_ok), new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
				settings = getSharedPreferences(context.getString(R.string.txt_pref), MODE_PRIVATE);
				edit = settings.edit();
				if(checkbox_re_security01.isChecked() && checkbox_re_security02.isChecked()){
					edit.putBoolean("checkbox_security01", checkbox_re_security01.isChecked());
					edit.putBoolean("checkbox_security02", checkbox_re_security02.isChecked());
				}else{
					edit.putBoolean("checkbox_security01", checkbox_re_security01.isChecked());
					edit.putBoolean("checkbox_security02", checkbox_re_security02.isChecked());
					Security_AlertShow();
					if(view2 != null){
						((ViewGroup)view2.getParent()).removeView(view2);
					}
					checkbox_re_security01.setChecked(false);
					checkbox_re_security02.setChecked(false);
					Toast.makeText(context, context.getString(R.string.txt_service_ok), Toast.LENGTH_SHORT).show();
				}
				edit.commit();
				dialog.dismiss();
				Log.i("dsu", "" + checkbox_re_security01.isChecked() +"\n" + checkbox_re_security02.isChecked());
			}
		});
		
		builder.setNegativeButton(context.getString(R.string.txt_security_cancel), new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
				finish();
				dialog.dismiss();
			}
		});
		re_security_alertDialog = builder.create();
		re_security_alertDialog.show();
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if(scrollState == OnScrollListener.SCROLL_STATE_FLING){
			InputMethodManager inputMethodManager = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
    		inputMethodManager.hideSoftInputFromWindow(edit_searcher.getWindowToken(), 0);
    		FadingActionBarHelperBase.listview_sub.setFastScrollEnabled(true);
		}else{
			FadingActionBarHelperBase.listview_sub.setFastScrollEnabled(false);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		
	}
	
	
	public static NotificationManager notificationManager;
	public static int noti_state = 1;
	public static Notification notification;
}
