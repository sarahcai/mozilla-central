/* -*- Mode: Java; c-basic-offset: 4; tab-width: 20; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.gecko;

import org.json.JSONObject;
import org.mozilla.apache.commons.codec.digest.DigestUtils;
import org.mozilla.gecko.AwesomeBar.ContextMenuSubject;
import org.mozilla.gecko.db.BrowserDB.URLColumns;
import org.mozilla.gecko.util.UiAsyncTask;
import org.mozilla.gecko.util.ThreadUtils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Display;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.text.TextUtils;
import android.util.Log;
import android.content.res.AssetManager;
import android.content.SharedPreferences; 
import android.preference.PreferenceManager; 
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CnTopsitesTab extends AwesomeBarTab {
    public static final String LOGTAG = "CN_TOPSITES_TAB";
    public static final String TAG = "cntopsites";
    private CnTopsitesAdapter mCursorAdapter = null;
    private ExpandableListView mListView;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private String mChannelid = "firefox.mobile";
    private String mobileApiUpdate = "http://m.huohu123.com/api/release_all.json";
    private String mobileApi = "http://m.huohu123.com/api/all.json";
    private String mobileDefaultApi = "default-topsites-mobile.json";
    private String tabletApiUpdate = "http://m.huohu123.com/api/release_all_pad.json";
    private String tabletApi = "http://m.huohu123.com/api/all_pad.json";
    private String tabletDefaultApi = "default-topsites-tablet.json";
    private String mApiUpdate = mobileApiUpdate;
    private String mApi = mobileApi;
    private String mDefaultApi = mobileDefaultApi;
    private ArrayList<String> mDefaultFavicon;
   
    private class CnTitleViewHolder {
    	public TextView titleView;
    }
    
    private class CnHotEntryViewHolder {
    	public LinearLayout hotView;
    }
    
    private class CnListEntryViewHolder {
    	public LinearLayout listView;
    }
    
    public int getTitleStringId() {
        return R.string.awesomebar_cntopsites_title;
    }

    public String getTag() {
        return TAG;
    }

    public CnTopsitesTab(Context context) {
        super(context);
        if (GeckoAppShell.isTablet()) {
        	mApi = tabletApi;
        	mApiUpdate = tabletApiUpdate;
        	mDefaultApi = tabletDefaultApi;
        	mDefaultFavicon = new ArrayList<String>(Arrays.asList("baidu", "google", "weibo", "taobao",
					"tmall", "amazon", "sina", "ifeng", "renren", "douban",
					"tianya", "youku"));
        } else {
        	mApi = mobileApi;
        	mApiUpdate = mobileApiUpdate;
        	mDefaultApi = mobileDefaultApi;
        	mDefaultFavicon = new ArrayList<String>(Arrays.asList("baidu", "google", "weibo", "taobao",
					"tmall", "amazon", "sina", "ifeng", "soufun", "qzone",
					"tianya", "3g"));
        }
    }
    
    public void destroy() {
    	CnTopsitesAdapter adapter = getCursorAdapter();
        if (adapter == null) {
            return;
        }
    	return;
    }

    public boolean onBackPressed() {
    	// If the soft keyboard is visible in the bookmarks or history tab, the user
        // must have explictly brought it up, so we should try hiding it instead of
        // exiting the activity or going up a bookmarks folder level.
        View view = getView();
        if (hideSoftInput(view))
            return true;

        return false;
    }
    
    private ExpandableListView getListView() {
        if (mListView == null && mView != null) {
            mListView = (ExpandableListView) mView.findViewById(R.id.cntopsites_list);
        }
        return mListView;
    }
    
    public View getView() {
    	if (mView == null) {
            mView = (LinearLayout) (LayoutInflater.from(mContext).inflate(R.layout.awesomebar_cntopsites_list, null));
            mView.setTag(TAG);

            ExpandableListView list = getListView();
            list.setTag(TAG);
            ((Activity)mContext).registerForContextMenu(list);

            CnTopsitesAdapter adapter = getCursorAdapter();
            list.setAdapter(adapter);
            
            Display newDisplay = ((Activity)mContext).getWindowManager().getDefaultDisplay();
            int width = newDisplay.getWidth();
            int padding = (int) getResources().getDimension(R.dimen.awesomebar_list_padding);
            list.setIndicatorBounds(width-150-padding, width-padding);
            
            ViewTreeObserver vto = list.getViewTreeObserver();

            vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                	ExpandableListView list = getListView();
                	Display newDisplay = ((Activity)mContext).getWindowManager().getDefaultDisplay();
                	int width = newDisplay.getWidth();
                	int padding = (int) getResources().getDimension(R.dimen.awesomebar_list_padding);
                	list.setIndicatorBounds(width-150-padding, width-padding);
                }
            });
            
        }
    	ExpandableListView list = getListView();
    	if (getCursorAdapter().getGroupCount() != 0) {
    		list.expandGroup(0);
    		list.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                   if (groupPosition == 0) {
                	   return true;
                   }
                   return false;
               }
           });
    	}
    	
        return mView;
    }
    
    protected CnTopsitesAdapter getCursorAdapter() {
        if (mCursorAdapter == null) {
            // Load the list using a custom adapter so we can create the bitmaps
            mCursorAdapter = new CnTopsitesAdapter(mContext);
        }
        return mCursorAdapter;
    }
    
    public class CnTopsitesAdapter extends BaseExpandableListAdapter { 
    	private static final int ROW_TITLE = 0;
    	private static final int ROW_TOP = 1;
    	private static final int ROW_LIST = 2;
    	private static final int FAKE_TITLE = 3;
    	
    	private JSONArray mHotsites;
        private JSONArray mListGroup;
        private ArrayList<String> mListTitles;
        private int top_column_num = 3;
        private int list_column_num = 5;
    	private HashMap<String, Integer> mHotFavicons = new HashMap<String, Integer>();
    	
    	private void initFavicons() {
    		mHotFavicons.put("baidu", R.drawable.cntop_fav1);
			mHotFavicons.put("google", R.drawable.cntop_fav2);
			mHotFavicons.put("weibo", R.drawable.cntop_fav3);
			mHotFavicons.put("taobao", R.drawable.cntop_fav4);
			mHotFavicons.put("tmall", R.drawable.cntop_fav5);
			mHotFavicons.put("amazon", R.drawable.cntop_fav6);
			mHotFavicons.put("sina", R.drawable.cntop_fav7);
			mHotFavicons.put("ifeng", R.drawable.cntop_fav8);
			mHotFavicons.put("soufun", R.drawable.cntop_fav9);
			mHotFavicons.put("qzone", R.drawable.cntop_fav10);
			mHotFavicons.put("tianya", R.drawable.cntop_fav11);
			mHotFavicons.put("3g", R.drawable.cntop_fav12);
			mHotFavicons.put("renren", R.drawable.cntop_fav13);
			mHotFavicons.put("douban", R.drawable.cntop_fav14);
			mHotFavicons.put("youku", R.drawable.cntop_fav15);
    	}
    	
        public CnTopsitesAdapter(Context context) {
        	mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            mEditor = mPreferences.edit();
            mHotsites = new JSONArray();
            mListGroup = new JSONArray();
            mListTitles = new ArrayList<String>();
            initFavicons();
            initData();
        }
         
        public Object getChild(int groupPosition, int childPosition) {  
            return "";  
        }  
          
        public long getChildId(int groupPosition, int childPosition) {  
            return childPosition;  
        }  
          
        public int getChildrenCount(int groupPosition) {  
        	try {
        		if (groupPosition == 0) {
        			return mHotsites.length()/top_column_num;
        		}
        		return mListGroup.getJSONObject(groupPosition - 1).getJSONArray("children").length();
        	} catch(Exception e) {
        		return 0;
        	}
        }  
           
        public View getChildView(int groupPosition, int childPosition,  
            boolean isLastChild, View convertView, ViewGroup parent) {   
        	if (groupPosition == 0) {
        		return getRowView(groupPosition, childPosition, convertView, ROW_TOP);
        	}
        	return getRowView(groupPosition, childPosition, convertView, ROW_LIST);
        }  
           
        public Object getGroup(int groupPosition) {  
            return "";  
        }                 
  
        public long getGroupId(int groupPosition) {  
            return groupPosition;  
        }     
           
        public int getGroupCount() {  
            return mListGroup.length() + 1;  
        }     
          
        public View getGroupView(int groupPosition, boolean isExpanded,  
                View convertView, ViewGroup parent) {  
        	if (groupPosition == 0) {
        		return getRowView(groupPosition, 0, convertView, FAKE_TITLE); 
        	}
            return getRowView(groupPosition, 0, convertView, ROW_TITLE);  
        }   
 
        public boolean hasStableIds() {  
            return false;  
        }         
  
        public boolean isChildSelectable(int groupPosition, int childPosition) {  
            return true;  
        }  
        
        public View getRowView(int groupPosition,int childPosition, View convertView, int type) {
        	if (type == FAKE_TITLE) {
            	if (convertView == null || !convertView.getTag().equals("FAKE_TITLE")) {
	                convertView = getInflater().inflate(R.layout.cn_topsites_fake_title_row, getListView(), false);
	                convertView.setTag("FAKE_TITLE");
            	}              
            }
            if (type == ROW_TITLE) {
            	CnTitleViewHolder viewHolder = null;
            	if (convertView == null || !(convertView.getTag() instanceof CnTitleViewHolder)) {
	                convertView = getInflater().inflate(R.layout.cn_topsites_title_row, getListView(), false);
	                
	                viewHolder = new CnTitleViewHolder();
	                viewHolder.titleView = (TextView) convertView.findViewById(R.id.title);
	                convertView.setTag(viewHolder);
            	} else {
                    viewHolder = (CnTitleViewHolder) convertView.getTag();
            	}
            	SetTitle(groupPosition, viewHolder);
                
            } else if (type == ROW_TOP) {
            	CnHotEntryViewHolder viewHolder = null;
            	if (convertView == null || !(convertView.getTag() instanceof CnHotEntryViewHolder)) {
	                convertView = getInflater().inflate(R.layout.cn_topsites_hot_row, getListView(), false);
	
	                viewHolder = new CnHotEntryViewHolder();
	                viewHolder.hotView = (LinearLayout) convertView.findViewById(R.id.cn_topsites_hot_layout);
	                convertView.setTag(viewHolder);
            	} else {
            		viewHolder = (CnHotEntryViewHolder) convertView.getTag();
            	}
                SetHotItems(groupPosition, childPosition, viewHolder);
                
            } else if (type == ROW_LIST) {
            	CnListEntryViewHolder viewHolder = null;
            	if (convertView == null || !(convertView.getTag() instanceof CnListEntryViewHolder)) {
	                convertView = getInflater().inflate(R.layout.cn_topsites_list_row, getListView(), false);
	
	                viewHolder = new CnListEntryViewHolder();
	                viewHolder.listView = (LinearLayout) convertView.findViewById(R.id.cn_topsites_list_layout);
	                convertView.setTag(viewHolder);
            	} else {
            		viewHolder = (CnListEntryViewHolder) convertView.getTag();
            	}
                SetListItems(groupPosition, childPosition, viewHolder);
            }

            return convertView;
        }
        
        private void initData() {
        	if (dataIsEmpty()) {
            	String tJson = getJsonFromPreference();
            	if (tJson.length() == 0) {
            		tJson = loadDataFromFile();
            		saveJsonToPreference(tJson);
            	}
            	readDataFromJson(tJson);
            }
        	
        	if (needToCheckUpdate()) {
        		checkForUpdate();
        	}
        }
        
        private Boolean dataIsEmpty() {
        	 if (mHotsites.length() == 0 || mListGroup.length() == 0 || mListTitles.size() == 0) {
        		 return true;
        	 }
        	 return false;
        }
        
        private String getJsonFromPreference() {
        	return mPreferences.getString("cntopsites_data", "");
        }
        
        private void saveJsonToPreference(String data) {
        	mEditor.putString("cntopsites_data", data);
        	mEditor.commit();
        }
        
        private void saveFavicon(int position, String imgUrl) {
        	final int order = position;
        	final String url = imgUrl;
        	(new UiAsyncTask<Void, Void, String>(ThreadUtils.getBackgroundHandler()) {
                @Override
                public String doInBackground(Void... params) {
                	try {
                		URL myUrl = new URL(url);
                		InputStream is = (InputStream)myUrl.getContent();
                		String favicon = InputStreamToString(is);
                		return favicon;
                	} catch (Exception e) {
                    	Log.d("topsites_error", e.toString());
                    }
                	return "";
                }
                
                @Override
                public void onPostExecute(String favicon) {
                	try {
	                	mEditor.putString("cntopsites_favicon" + String.valueOf(order), favicon);
	                    mEditor.commit();
                	} catch (Exception e) {
                    	Log.d("topsites_error", e.toString());
                    }
                }

            }).execute();
        }
        
        private void readDataFromJson (String tjson) {
        	try {
        		mListGroup = new JSONArray(new ArrayList<String>());
    	    	JSONArray topsitesJson = new JSONArray(tjson);
    			// Hot sites
    			JSONObject hotsites = topsitesJson.getJSONObject(0);
    			mHotsites = hotsites.getJSONArray("children"); 
    			
    			int length = topsitesJson.length();
    			for (int i = 1; i < length; i++) {
    				mListGroup.put(topsitesJson.getJSONObject(i));
    			}
    			
    			// Init hot sites favicons.
    			// Fix me. It's better to save these in the bookmarks.
    			length = mHotsites.length();
    			for (int i = 0; i < length; i++) {
    				JSONObject hotItem = mHotsites.getJSONObject(i);
    				String imgUrl = hotItem.getString("imgurl");
    				saveFavicon(i, imgUrl);
    			}
    			mEditor.commit();
        	} catch (Exception e) {
        		Log.d("topsites_error", e.toString());
        	}
        }
        
        private String streamToString (InputStream is) {
        	try {
        		if (is != null) {
        			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        			StringBuilder sb = new StringBuilder();
        			String line = null;
        			while ((line = reader.readLine()) != null) {
        				sb.append(line);
        				sb.append('\n');
        			}
        			is.close();
        			return sb.toString();
        		}
        	} catch (IOException e) {
        		Log.d("topsites_error", e.toString());
        	} catch (Exception e) {
        		Log.d("topsites_error", e.toString());
        	}
        	return "";
        }
        
        private boolean needToCheckUpdate() {
        	int last_check = mPreferences.getInt("cntopsites_check_time", 0);
        	Date today = new Date();
        	Long current_day = (today.getTime() / (1000 * 60 * 60 * 24));
    		if (last_check < current_day.intValue()) {
    			// Need to check for update.
    			return true;
    		} 
    		return false;
        }
        
        private void checkForUpdate() {
        	final String apiUrl = mApiUpdate + "?r=" + Math.random();
        	
        	(new UiAsyncTask<Void, Void, String>(ThreadUtils.getBackgroundHandler()) {
                @Override
                public String doInBackground(Void... params) {
                	return getStringFromUrl(apiUrl);
                }
                
                @Override
                public void onPostExecute(String s) {
                	Date today = new Date();
                	Long current_day = (today.getTime() / (1000 * 60 * 60 * 24));                 
                    mEditor.putInt("cntopsites_check_time", current_day.intValue()); 
                    mEditor.commit(); 
                    
                    try {
                    	JSONObject updateJson = new JSONObject(s);
                    	String last_update = updateJson.getString("jsonTime"); 
                    	String last_update_local = mPreferences.getString("cntopsites_update_time", "0");
                    	if (last_update != last_update_local) {
                    		getDataFromApi(last_update);
                    	}
                    } catch (Exception e) {
                    	Log.d("topsites_error", e.toString());
                    }
                }

            }).execute();
        }
        
        private void getDataFromApi(String last_update) {
        	final String apiUrl = mApi + "?r=" + Math.random();
        	final String update_time = last_update;
        	(new UiAsyncTask<Void, Void, String>(ThreadUtils.getBackgroundHandler()) {
                @Override
                public String doInBackground(Void... params) {
                	return getStringFromUrl(apiUrl);
                }
                
                @Override
                public void onPostExecute(String s) {               
                    saveJsonToPreference(s);
                    readDataFromJson(s);
                    mEditor.putString("cntopsites_update_time", update_time); 
                    mEditor.commit();
                }

            }).execute();
        }
        
        private String loadDataFromFile() {
            try {
                AssetManager am = mContext.getAssets();
                InputStream stream = am.open(mDefaultApi);
                return streamToString(stream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }
        
        private void SetTitle(int groupPosition, final CnTitleViewHolder viewHolder) {
        	int titlePos = groupPosition - 1;
        	if (mListGroup.length() > titlePos) {
        		try {
        			viewHolder.titleView.setText(mListGroup.getJSONObject(titlePos).getString("title"));
    	    	} catch (Exception e) {
    	    		Log.d("topsites_error", e.toString());
    	    	}
        	}
        }
        
        private void SetHotItems(int groupPosition, int childPosition, final CnHotEntryViewHolder viewHolder) {
        	OnClickListener clickListener = new OnClickListener() {
                public void onClick(View v) {
                	onItemClick(v);
                }
            };
            
        	LinearLayout hotView = viewHolder.hotView;
        	int length = mHotsites.length();
    		if (length/top_column_num < childPosition + 1) {
    			return;
    		}
    		
    		try {
    			for (int i = 0; i < top_column_num; i++) {
    				int j = childPosition * top_column_num + i;
    				JSONObject hotsite = mHotsites.getJSONObject(j);
    				View hotItem = null;
    	
    	            hotItem = hotView.getChildAt(i * 2 + 1);
    	            String imageString = mPreferences.getString("cntopsites_favicon" + String.valueOf(j), "");
    	            if (imageString.length() == 0) {
    	            	((ImageView) hotItem.findViewById(R.id.favicon)).setImageResource(mHotFavicons.get(mDefaultFavicon.get(j)));
    	            } else {
    	            	InputStream imageStream = StringToInputStream(imageString);
    	            	Drawable drawable = Drawable.createFromStream(imageStream, null);
    	            	((ImageView) hotItem.findViewById(R.id.favicon)).setImageDrawable(drawable);
    	            }
    	            ((TextView) hotItem.findViewById(R.id.title)).setText(hotsite.getString("title"));
    	            hotItem.setTag(hotsite.getString("url"));
    	            hotItem.setOnClickListener(clickListener);
    			}
    		} catch (Exception e) {
        		Log.d("topsites_error", e.toString());
        	}
        }
        
        private void SetListItems(int groupPosition, int childPosition, final CnListEntryViewHolder viewHolder) {
        	OnClickListener clickListener = new OnClickListener() {
                public void onClick(View v) {
                	onItemClick(v);
                }
            };
            
            OnClickListener voidListener = new OnClickListener() {
                public void onClick(View v) {
                	return;
                }
            };
            
            LinearLayout listView = viewHolder.listView;
            
            if (groupPosition - 1 >= mListGroup.length()) {
            	return;
            }
            try {
            	JSONObject listSites = mListGroup.getJSONObject(groupPosition - 1);
    			JSONObject list_row = listSites.getJSONArray("children").getJSONObject(childPosition);
    			String list_title = list_row.getString("title");
    			JSONArray list_items = list_row.getJSONArray("children");
    			
    			View listItem = null;
    			listItem = listView.getChildAt(1); 
    			((TextView) listItem.findViewById(R.id.title)).setText(list_title);
    			((TextView) listItem.findViewById(R.id.title)).setTextColor(Color.BLACK);
    			listItem.setOnClickListener(voidListener);
    			
    			for (int i = 0; i < list_column_num - 1; i++) {
    				JSONObject list_item = list_items.getJSONObject(i);
    	            listItem = listView.getChildAt((i+1) * 2 + 1);
    				
    	            ((TextView) listItem.findViewById(R.id.title)).setText(list_item.getString("title"));	        
    	            listItem.setTag(list_item.getString("url"));
    	            listItem.setOnClickListener(clickListener);
    			}
    		} catch (Exception e) {
        		Log.d("topsites_error", e.toString());
        	}
        }
        
        private void onItemClick(View v) {
            AwesomeBarTabs.OnUrlOpenListener listener = getUrlListener();
            if (listener != null) {
                String title = ((TextView) v.findViewById(R.id.title)).getText().toString();
                String url = v.getTag().toString();
                listener.onUrlOpen(url, title);
            }
        }
    };
    
    private static String getStringFromUrl (String apiurl) {
    	try {
    		URL aurl = new URL(apiurl);
    		InputStream is = null;
    		HttpURLConnection conn = (HttpURLConnection) aurl.openConnection();
	    	conn.setConnectTimeout(3000);
	    	conn.setReadTimeout(5000);
	    	conn.setDoInput(true);
	    	conn.setUseCaches(false);
	    	conn.connect();
            if (conn.getResponseCode() == 200) {
	             is = conn.getInputStream();
	             if (is != null) {
		     			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		     			StringBuilder sb = new StringBuilder();
		     			String line = null;
		     			while ((line = reader.readLine()) != null) {
		     				sb.append(line);
		     				sb.append('\n');
		     			}
		     			is.close();
		     			return sb.toString();
	             }
	 		}
    	} catch (IOException e) {
    	} catch (Exception e) {
    	}
    	return "";
    }
    
    public static String InputStreamToString(InputStream in) throws Exception{  
        
    	int BUFFER_SIZE = 4096;  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] data = new byte[BUFFER_SIZE];  
        int count = -1;  
        while((count = in.read(data,0,BUFFER_SIZE)) != -1)  
            outStream.write(data, 0, count);  
          
        data = null;  
        return new String(outStream.toByteArray(),"ISO-8859-1");  
    }  
       
    public static InputStream StringToInputStream(String in) throws Exception{  
          
        ByteArrayInputStream is = new ByteArrayInputStream(in.getBytes("ISO-8859-1"));  
        return is;  
    }  
    
    public ContextMenuSubject getSubject(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        ContextMenuSubject subject = null;
        return subject;
    }

}



