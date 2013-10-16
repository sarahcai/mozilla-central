/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mozilla.gecko.zxing.client.android.result;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.mozilla.apache.commons.codec.binary.Base64;
import org.mozilla.gecko.R;
import org.mozilla.gecko.Tabs;
import org.mozilla.gecko.util.ThreadUtils;
import org.mozilla.gecko.util.UiAsyncTask;
import org.mozilla.gecko.CnLocalUtils;
import org.mozilla.gecko.zxing.Result;
import org.mozilla.gecko.zxing.client.android.Contents;
import org.mozilla.gecko.zxing.client.android.LocaleManager;
import org.mozilla.gecko.zxing.client.result.ParsedResult;
import org.mozilla.gecko.zxing.client.result.ParsedResultType;
import org.mozilla.gecko.zxing.client.result.ResultParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import ch.boye.httpclientandroidlib.util.EncodingUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;

//import org.apache.commons.codec.binary.Base64;

/**
 * A base class for the Android-specific barcode handlers. These allow the app to polymorphically
 * suggest the appropriate actions for each data type.
 *
 * This class also contains a bunch of utility methods to take common actions like opening a URL.
 * They could easily be moved into a helper object, but it can't be static because the Activity
 * instance is needed to launch an intent.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public abstract class ResultHandler {

  private static final String TAG = ResultHandler.class.getSimpleName();

  private static final String GOOGLE_SHOPPER_PACKAGE = "com.google.android.apps.shopper";
  private static final String GOOGLE_SHOPPER_ACTIVITY = GOOGLE_SHOPPER_PACKAGE +
      ".results.SearchResultsActivity";
  private static final String MARKET_URI_PREFIX = "market://details?id=";
  private static final String MARKET_REFERRER_SUFFIX =
      "&referrer=utm_source%3Dbarcodescanner%26utm_medium%3Dapps%26utm_campaign%3Dscan";

  private static final String[] EMAIL_TYPE_STRINGS = {"home", "work", "mobile"};
  private static final String[] PHONE_TYPE_STRINGS = {"home", "work", "mobile", "fax", "pager", "main"};
  private static final String[] ADDRESS_TYPE_STRINGS = {"home", "work"};
  private static final int[] EMAIL_TYPE_VALUES = {
      ContactsContract.CommonDataKinds.Email.TYPE_HOME,
      ContactsContract.CommonDataKinds.Email.TYPE_WORK,
      ContactsContract.CommonDataKinds.Email.TYPE_MOBILE,
  };
  private static final int[] PHONE_TYPE_VALUES = {
      ContactsContract.CommonDataKinds.Phone.TYPE_HOME,
      ContactsContract.CommonDataKinds.Phone.TYPE_WORK,
      ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
      ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK,
      ContactsContract.CommonDataKinds.Phone.TYPE_PAGER,
      ContactsContract.CommonDataKinds.Phone.TYPE_MAIN,
  };
  private static final int[] ADDRESS_TYPE_VALUES = {
      ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME,
      ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK,
  };
  private static final int NO_TYPE = -1;

  public static final int MAX_BUTTON_COUNT = 2;

  // Result codes.
  // If SEARCH_CODE is used, only the search contents will be sent back.
  // If URL_CODE is used, the whole url will be sent back.
  public static final int SEARCH_CODE = 2000;
  public static final int URL_CODE = 2001;

  // Content keys.
  public static final String SEARCH_KEY = "SEARCH_KEY";
  public static final String URL_KEY = "URL_KEY";

  // Book search website.
  public static final String DOUBAN_BOOK_SEARCH_WEBSITE =
    "http://book.douban.com/subject_search?search_text=";

  private final ParsedResult result;
  private final Activity activity;
  private final Result rawResult;
  private final String customProductSearch;

  // Amazon product search.
  private static final String UTF8_CHARSET = "UTF-8";
  private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
  private static final String REQUEST_URI = "/onca/xml";
  private static final String REQUEST_METHOD = "GET";
  private String endpoint = "webservices.amazon.cn"; // must be lowercase
  private String awsAccessKeyId = "AKIAJWQ43TKF7GPMBQWQ";
  private ArrayList<String> titleList;
  private ArrayList<String> detailList;
  private ProgressBar pBar;
  private ViewGroup buttonView;
  private TextView pButtonWeb;
  private TextView pButtonProduct;
  private boolean netConnectFailure;
  private View resultView;
  private View expressSelect;

  private final DialogInterface.OnClickListener shopperMarketListener =
      new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
      launchIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URI_PREFIX +
          GOOGLE_SHOPPER_PACKAGE + MARKET_REFERRER_SUFFIX)));
    }
  };

  ResultHandler(Activity activity, ParsedResult result) {
    this(activity, result, null);
  }

  ResultHandler(Activity activity, ParsedResult result, Result rawResult) {
    this.result = result;
    this.activity = activity;
    this.rawResult = rawResult;
    this.customProductSearch = parseCustomSearchURL();

    // Make sure the Shopper button is hidden by default. Without this, scanning a product followed
    // by a QR Code would leave the button on screen among the QR Code actions.
    //View shopperButton = activity.findViewById(R.id.shopper_button);
    //shopperButton.setVisibility(View.GONE);
  }

  public final ParsedResult getResult() {
    return result;
  }

  final boolean hasCustomProductSearch() {
    return customProductSearch != null;
  }

  final Activity getActivity() {
    return activity;
  }

  /**
   * Indicates how many buttons the derived class wants shown.
   *
   * @return The integer button count.
   */
  public abstract int getButtonCount();

  /**
   * The text of the nth action button.
   *
   * @param index From 0 to getButtonCount() - 1
   * @return The button text as a resource ID
   */
  public abstract int getButtonText(int index);


  /**
   * Execute the action which corresponds to the nth button.
   *
   * @param index The button that was clicked.
   */
  public abstract void handleButtonPress(int index);

  /**
   * Some barcode contents are considered secure, and should not be saved to history, copied to
   * the clipboard, or otherwise persisted.
   *
   * @return If true, do not create any permanent record of these contents.
   */
  public boolean areContentsSecure() {
    return false;
  }

  /**
   * The Google Shopper button is special and is not handled by the abstract button methods above.
   *
   * @param listener The on click listener to install for this button.
   */
  final void showGoogleShopperButton(View.OnClickListener listener) {
    /*View shopperButton = activity.findViewById(R.id.shopper_button);
    shopperButton.setVisibility(View.VISIBLE);
    shopperButton.setOnClickListener(listener);*/
  }

  /**
   * Create a possibly styled string for the contents of the current barcode.
   *
   * @return The text to be displayed.
   */
  public CharSequence getDisplayContents() {
    String contents = result.getDisplayResult();
    return contents.replace("\r", "");
  }

  /**
   * A string describing the kind of barcode that was found, e.g. "Found contact info".
   *
   * @return The resource ID of the string.
   */
  public abstract int getDisplayTitle();

  /**
   * A convenience method to get the parsed type. Should not be overridden.
   *
   * @return The parsed type, e.g. URI or ISBN
   */
  public final ParsedResultType getType() {
    return result.getType();
  }

  final void addPhoneOnlyContact(String[] phoneNumbers,String[] phoneTypes) {
    addContact(null, null, null, phoneNumbers, phoneTypes, null, null, null, null, null, null, null, null, null, null, null);
  }

  final void addEmailOnlyContact(String[] emails, String[] emailTypes) {
    addContact(null, null, null, null, null, emails, emailTypes, null, null, null, null, null, null, null, null, null);
  }

  final void addContact(String[] names,
                        String[] nicknames,
                        String pronunciation,
                        String[] phoneNumbers,
                        String[] phoneTypes,
                        String[] emails,
                        String[] emailTypes,
                        String note,
                        String instantMessenger,
                        String address,
                        String addressType,
                        String org,
                        String title,
                        String[] urls,
                        String birthday,
                        String[] geo) {

    // Only use the first name in the array, if present.
    Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT, ContactsContract.Contacts.CONTENT_URI);
    intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
    putExtra(intent, ContactsContract.Intents.Insert.NAME, names != null ? names[0] : null);

    putExtra(intent, ContactsContract.Intents.Insert.PHONETIC_NAME, pronunciation);

    int phoneCount = Math.min(phoneNumbers != null ? phoneNumbers.length : 0, Contents.PHONE_KEYS.length);
    for (int x = 0; x < phoneCount; x++) {
      putExtra(intent, Contents.PHONE_KEYS[x], phoneNumbers[x]);
      if (phoneTypes != null && x < phoneTypes.length) {
        int type = toPhoneContractType(phoneTypes[x]);
        if (type >= 0) {
          intent.putExtra(Contents.PHONE_TYPE_KEYS[x], type);
        }
      }
    }

    int emailCount = Math.min(emails != null ? emails.length : 0, Contents.EMAIL_KEYS.length);
    for (int x = 0; x < emailCount; x++) {
      putExtra(intent, Contents.EMAIL_KEYS[x], emails[x]);
      if (emailTypes != null && x < emailTypes.length) {
        int type = toEmailContractType(emailTypes[x]);
        if (type >= 0) {
          intent.putExtra(Contents.EMAIL_TYPE_KEYS[x], type);
        }
      }
    }

    // No field for URL, birthday; use notes
    StringBuilder aggregatedNotes = new StringBuilder();
    if (urls != null) {
      for (String url : urls) {
        if (url != null && url.length() > 0) {
          aggregatedNotes.append('\n').append(url);
        }
      }
    }
    for (String aNote : new String[] { birthday, note }) {
      if (aNote != null) {
        aggregatedNotes.append('\n').append(aNote);
      }
    }
    if (nicknames != null) {
      for (String nickname : nicknames) {
        if (nickname != null && nickname.length() > 0) {
          aggregatedNotes.append('\n').append(nickname);
        }
      }
    }
    if (geo != null) {
      aggregatedNotes.append('\n').append(geo[0]).append(',').append(geo[1]);
    }

    if (aggregatedNotes.length() > 0) {
      // Remove extra leading '\n'
      putExtra(intent, ContactsContract.Intents.Insert.NOTES, aggregatedNotes.substring(1));
    }

    putExtra(intent, ContactsContract.Intents.Insert.IM_HANDLE, instantMessenger);
    putExtra(intent, ContactsContract.Intents.Insert.POSTAL, address);
    if (addressType != null) {
      int type = toAddressContractType(addressType);
      if (type >= 0) {
        intent.putExtra(ContactsContract.Intents.Insert.POSTAL_TYPE, type);
      }
    }
    putExtra(intent, ContactsContract.Intents.Insert.COMPANY, org);
    putExtra(intent, ContactsContract.Intents.Insert.JOB_TITLE, title);
    launchIntent(intent);
  }

  private static int toEmailContractType(String typeString) {
    return doToContractType(typeString, EMAIL_TYPE_STRINGS, EMAIL_TYPE_VALUES);
  }

  private static int toPhoneContractType(String typeString) {
    return doToContractType(typeString, PHONE_TYPE_STRINGS, PHONE_TYPE_VALUES);
  }

  private static int toAddressContractType(String typeString) {
    return doToContractType(typeString, ADDRESS_TYPE_STRINGS, ADDRESS_TYPE_VALUES);
  }

  private static int doToContractType(String typeString, String[] types, int[] values) {
    if (typeString == null) {
      return NO_TYPE;
    }
    for (int i = 0; i < types.length; i++) {
      String type = types[i];
      if (typeString.startsWith(type) || typeString.startsWith(type.toUpperCase(Locale.ENGLISH))) {
        return values[i];
      }
    }
    return NO_TYPE;
  }

  final void shareByEmail(String contents) {
    sendEmailFromUri("mailto:", null, activity.getString(R.string.msg_share_subject_line),
        contents);
  }

  final void sendEmail(String address, String subject, String body) {
    sendEmailFromUri("mailto:" + address, address, subject, body);
  }

  // Use public Intent fields rather than private GMail app fields to specify subject and body.
  final void sendEmailFromUri(String uri, String email, String subject, String body) {
    Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse(uri));
    if (email != null) {
      intent.putExtra(Intent.EXTRA_EMAIL, new String[] {email});
    }
    putExtra(intent, Intent.EXTRA_SUBJECT, subject);
    putExtra(intent, Intent.EXTRA_TEXT, body);
    intent.setType("text/plain");
    launchIntent(intent);
  }

  final void shareBySMS(String contents) {
    sendSMSFromUri("smsto:", activity.getString(R.string.msg_share_subject_line) + ":\n" +
        contents);
  }

  final void sendSMS(String phoneNumber, String body) {
    sendSMSFromUri("smsto:" + phoneNumber, body);
  }

  final void sendSMSFromUri(String uri, String body) {
    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
    putExtra(intent, "sms_body", body);
    // Exit the app once the SMS is sent
    intent.putExtra("compose_mode", true);
    launchIntent(intent);
  }

  final void sendMMS(String phoneNumber, String subject, String body) {
    sendMMSFromUri("mmsto:" + phoneNumber, subject, body);
  }

  final void sendMMSFromUri(String uri, String subject, String body) {
    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
    // The Messaging app needs to see a valid subject or else it will treat this an an SMS.
    if (subject == null || subject.length() == 0) {
      putExtra(intent, "subject", activity.getString(R.string.msg_default_mms_subject));
    } else {
      putExtra(intent, "subject", subject);
    }
    putExtra(intent, "sms_body", body);
    intent.putExtra("compose_mode", true);
    launchIntent(intent);
  }

  final void dialPhone(String phoneNumber) {
    launchIntent(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));
  }

  final void dialPhoneFromUri(String uri) {
    launchIntent(new Intent(Intent.ACTION_DIAL, Uri.parse(uri)));
  }

  final void openMap(String geoURI) {
    launchIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(geoURI)));
  }

  /**
   * Do a geo search using the address as the query.
   *
   * @param address The address to find
   * @param title An optional title, e.g. the name of the business at this address
   */
  final void searchMap(String address, CharSequence title) {
    String query = address;
    if (title != null && title.length() > 0) {
      query += " (" + title + ')';
    }
    launchIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + Uri.encode(query))));
  }

  final void getDirections(double latitude, double longitude) {
    launchIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google." +
        LocaleManager.getCountryTLD(activity) + "/maps?f=d&daddr=" + latitude + ',' + longitude)));
  }

  // Uses the mobile-specific version of Product Search, which is formatted for small screens.
  final void openProductSearch(String upc) {
    Uri uri = Uri.parse("http://www.google." + LocaleManager.getProductSearchCountryTLD(activity) +
        "/m/products?q=" + upc + "&source=zxing");
    launchIntent(new Intent(Intent.ACTION_VIEW, uri));
  }

  final void openBookSearch(String isbn) {
    Uri uri = Uri.parse("http://books.google." + LocaleManager.getBookSearchCountryTLD(activity) +
        "/books?vid=isbn" + isbn);
    launchIntent(new Intent(Intent.ACTION_VIEW, uri));
  }

  final void openURL(String url) {
    // Strangely, some Android browsers don't seem to register to handle HTTP:// or HTTPS://.
    // Lower-case these as it should always be OK to lower-case these schemes.
    if (url.startsWith("HTTP://")) {
      url = "http" + url.substring(4);
    } else if (url.startsWith("HTTPS://")) {
      url = "https" + url.substring(5);
    }
    Intent intent = new Intent();
    Bundle bundle = new Bundle();
    bundle.putString(URL_KEY, url);
    intent.putExtras(bundle);
    activity.setResult(URL_CODE, intent);
    activity.finish();
  }

  final void webSearch(String query) {
    Intent intent = new Intent();
    Bundle bundle = new Bundle();
    bundle.putString(SEARCH_KEY, query);
    intent.putExtras(bundle);
    activity.setResult(SEARCH_CODE, intent);
    activity.finish();
  }
  
  final void expressSearch(String query) {
	expressSelect = activity.findViewById(R.id.express_select_view);
	resultView = activity.findViewById(R.id.result_view);
	resultView.setVisibility(View.GONE);
	expressSelect.setVisibility(View.VISIBLE);
  }

  final void bookSearch(String query) {
    Intent intent = new Intent();
    Bundle bundle = new Bundle();
    bundle.putString(URL_KEY, DOUBAN_BOOK_SEARCH_WEBSITE + query);
    intent.putExtras(bundle);
    activity.setResult(URL_CODE, intent);
    activity.finish();
  }

  final void productSearch(String query) {
    pBar = (ProgressBar) activity.findViewById(R.id.progress_bar_view);
    pBar.setVisibility(ProgressBar.VISIBLE);
    buttonView = (ViewGroup) activity.findViewById(R.id.result_button_view);
    pButtonWeb = (TextView) buttonView.getChildAt(0);
    pButtonProduct = (TextView) buttonView.getChildAt(1);
    pButtonWeb.setEnabled(false);
    pButtonProduct.setEnabled(false);
    try {
      // Generate Amazon signed request.
      String signedRequest = amazonSignedRequest(query);

      (new AsyncTask<String, Integer, String>() {
        @Override
        protected String doInBackground(String... url) {
          return getAmazonProductInfo(url[0]);
        }

        @Override
        protected void onPostExecute(String amazonProductInfo) {
          pBar.setVisibility(ProgressBar.GONE);
          if(netConnectFailure) {
            showNetConnectFailure();
          }
          if (amazonProductInfo != null && amazonProductInfo.length() != 0) {
            showAmazonProductInfo(amazonProductInfo);
          }
          pButtonWeb.setEnabled(true);
          pButtonProduct.setEnabled(true);
        }

        private String getAmazonProductInfo(String xmlURL) {
          try {
        	Log.d("amazon_request", xmlURL);
            URL url = new URL(xmlURL);
            InputStream is = null;
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(5000);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.connect();
            if (conn.getResponseCode() == 200) {
              setNetConnectFailure(false);
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
            } else {
              Log.d("amazon_response", String.valueOf(conn.getResponseCode()));
              setNetConnectFailure(true);
            }
          } catch (IOException e) {
            setNetConnectFailure(true);
          } catch (Exception e) {
            setNetConnectFailure(true);
          }
          return "";
        }

        private void setNetConnectFailure(boolean failure) {
          netConnectFailure = failure;
        }

        private void showNetConnectFailure() {
          Toast.makeText(activity.getApplicationContext(), R.string.amazon_product_info_net_failure, Toast.LENGTH_SHORT).show();
        }

        private void showAmazonProductInfo(String xml) {
          try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new StringReader(xml));
            int eventType = xpp.getEventType();

            titleList = new ArrayList<String>();
            detailList = new ArrayList<String>();
            String currentKey = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
              if (eventType == XmlPullParser.START_TAG){
                  currentKey = xpp.getName();
              } else if (eventType == XmlPullParser.END_TAG) {
                  currentKey = null;
              } else if (eventType == XmlPullParser.TEXT) {
                // DetailPageURL.
                if (currentKey.equals("DetailPageURL")) {
                  detailList.add(xpp.getText());
                }
                // Title.
                if (currentKey.equals("Title")) {
                  titleList.add(xpp.getText());
                }
              }
              eventType = xpp.next();
            }

            for(int i = 0; i != titleList.size(); i++) {
              titleList.set(i, String.valueOf(i+1) + ". " + titleList.get(i));
            }

            if(titleList.size() == 0 || detailList.size() == 0) {
              new AlertDialog.Builder(activity, AlertDialog.THEME_HOLO_DARK)
                .setTitle(R.string.amazon_product_info_title)
                .setPositiveButton(R.string.amazon_product_info_button_close, null)
                .setMessage(R.string.amazon_product_info_none)
                .show();
            }
            else {
              new AlertDialog.Builder(activity, AlertDialog.THEME_HOLO_DARK)
                .setTitle(R.string.amazon_product_info_title)
                .setPositiveButton(R.string.amazon_product_info_button_close, null)
                .setItems(titleList.toArray(new String[1]), new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString(URL_KEY, detailList.get(which));
                    intent.putExtras(bundle);
                    activity.setResult(URL_CODE, intent);
                    activity.finish();
                  }
                })
                .show();
            }
          } catch (XmlPullParserException e) {
          } catch (IOException e) {
          }
        }
      }).execute(signedRequest);

      //activity.finish();
    } catch (Exception ex) {
      return;
    }
  }

  final void openGoogleShopper(String query) {

    // Construct Intent to launch Shopper
    Intent intent = new Intent(Intent.ACTION_SEARCH);
    intent.setClassName(GOOGLE_SHOPPER_PACKAGE, GOOGLE_SHOPPER_ACTIVITY);
    intent.putExtra(SearchManager.QUERY, query);

    // Is it available?
    PackageManager pm = activity.getPackageManager();
    Collection<?> availableApps = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

    if (availableApps != null && !availableApps.isEmpty()) {
      // If something can handle it, start it
      activity.startActivity(intent);
    } else {
      // Otherwise offer to install it from Market.
      AlertDialog.Builder builder = new AlertDialog.Builder(activity);
      builder.setTitle(R.string.msg_google_shopper_missing);
      builder.setMessage(R.string.msg_install_google_shopper);
      //builder.setIcon(R.drawable.shopper_icon);
      builder.setPositiveButton(R.string.button_ok, shopperMarketListener);
      builder.setNegativeButton(R.string.button_cancel, null);
      builder.show();
    }
  }

  /**
   * Like {@link #launchIntent(Intent)} but will tell you if it is not handle-able
   * via {@link ActivityNotFoundException}.
   *
   * @throws ActivityNotFoundException
   */
  final void rawLaunchIntent(Intent intent) {
    if (intent != null) {
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
      Log.d(TAG, "Launching intent: " + intent + " with extras: " + intent.getExtras());
      activity.startActivity(intent);
    }
  }

  /**
   * Like {@link #rawLaunchIntent(Intent)} but will show a user dialog if nothing is available to handle.
   */
  final void launchIntent(Intent intent) {
    try {
      rawLaunchIntent(intent);
    } catch (ActivityNotFoundException ignored) {
      AlertDialog.Builder builder = new AlertDialog.Builder(activity);
      //builder.setTitle(R.string.app_name);
      builder.setMessage(R.string.msg_intent_failed);
      builder.setPositiveButton(R.string.button_ok, null);
      builder.show();
    }
  }

  private static void putExtra(Intent intent, String key, String value) {
    if (value != null && value.length() > 0) {
      intent.putExtra(key, value);
    }
  }

  private String parseCustomSearchURL() {
    return null;
  }

  final String fillInCustomSearchURL(String text) {
    if (customProductSearch == null) {
      return text; // ?
    }
    try {
      text = URLEncoder.encode(text, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      // can't happen; UTF-8 is always supported. Continue, I guess, without encoding
    }
    String url = customProductSearch.replace("%s", text);
    if (rawResult != null) {
      url = url.replace("%f", rawResult.getBarcodeFormat().toString());
      if (url.contains("%t")) {
        ParsedResult parsedResultAgain = ResultParser.parseResult(rawResult);
        url = url.replace("%t", parsedResultAgain.getType().toString());
      }
    }
    return url;
  }

  private String amazonSignedRequest(String query) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException {

    String endpoint = "webservices.amazon.cn"; // must be lowercase
    String awsAccessKeyId = "AKIAJWQ43TKF7GPMBQWQ";
    String awsSecretKey = "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";
    String associateTag = "mozillafire05-20";
    String contentType = "text";
    String operation = "ItemLookup";
    String service = "AWSECommerceService";
    String version = "2011-08-01";
    String idType = "EAN";
    String searchIndex = "All";

    // Initial sign params.
    Map<String, String> params = new HashMap<String, String>();
    params.put("AWSAccessKeyId", awsAccessKeyId);
    params.put("AssociateTag", associateTag);
    //params.put("ContentType", contentType);
    params.put("Operation", operation);
    params.put("Service", service);
    params.put("Version", version);
    params.put("Timestamp", timestamp());
    params.put("ItemId", query);
    params.put("IdType", idType);
    params.put("SearchIndex", searchIndex);

    SortedMap<String, String> sortedParamMap = new TreeMap<String, String>(params);
    String canonicalQS = canonicalize(sortedParamMap);

    String toSign =
        REQUEST_METHOD + "\n"
        + endpoint + "\n"
        + REQUEST_URI + "\n"
        + canonicalQS;

    byte[] secretyKeyBytes = awsSecretKey.getBytes(UTF8_CHARSET);
    SecretKeySpec secretKeySpec = new SecretKeySpec(secretyKeyBytes, HMAC_SHA256_ALGORITHM);

    String hmac = hmac(toSign, secretKeySpec);
    String sig = percentEncodeRfc3986(hmac);
    String url = "http://" + endpoint + REQUEST_URI + "?" +
    canonicalQS + "&Signature=" + sig;

    return url;
  }

  private String canonicalize(SortedMap<String, String> sortedParamMap)
  {
    if (sortedParamMap.isEmpty()) {
      return "";
    }

    StringBuffer buffer = new StringBuffer();
    Iterator<Map.Entry<String, String>> iter = sortedParamMap.entrySet().iterator();

    while (iter.hasNext()) {
      Map.Entry<String, String> kvpair = iter.next();
      buffer.append(percentEncodeRfc3986(kvpair.getKey()));
      buffer.append("=");
      buffer.append(percentEncodeRfc3986(kvpair.getValue()));
      if (iter.hasNext()) {
        buffer.append("&");
      }
    }
    String canonical = buffer.toString();
      return canonical;
  }

  private String percentEncodeRfc3986(String s) {
    final String UTF8_CHARSET = "UTF-8";
    String out;
    try {
      out = URLEncoder.encode(s, UTF8_CHARSET)
      .replace("+", "%20")
      .replace("*", "%2A")
      .replace("%7E", "~");
    } catch (UnsupportedEncodingException e) {
      out = s;
    }
    return out;
  }

  private String hmac(String stringToSign, SecretKeySpec secretKeySpec) throws NoSuchAlgorithmException, InvalidKeyException {
    final String UTF8_CHARSET = "UTF-8";
    Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
    mac.init(secretKeySpec);
    String signature = null;
    byte[] data;
    byte[] rawHmac;
    try {
      data = stringToSign.getBytes(UTF8_CHARSET);
      rawHmac = mac.doFinal(data);
      Base64 encoder = new Base64();
      signature = new String(encoder.encode(rawHmac));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(UTF8_CHARSET + " is unsupported!", e);
    }
    return signature;
  }

  private String timestamp() {
    String timestamp = null;
    Calendar cal = Calendar.getInstance();
    DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    dfm.setTimeZone(TimeZone.getTimeZone("GMT"));
    timestamp = dfm.format(cal.getTime());
    return timestamp;
  }

}
