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

import org.mozilla.gecko.R;
import org.mozilla.gecko.zxing.client.result.AddressBookParsedResult;
import org.mozilla.gecko.zxing.client.result.ParsedResult;

import android.app.Activity;
import android.telephony.PhoneNumberUtils;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Handles address book entries.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class AddressBookResultHandler extends ResultHandler {

  private static final DateFormat[] DATE_FORMATS = {
    new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH),
    new SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.ENGLISH),
    new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH),
    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH),
  };
  static {
    for (DateFormat format : DATE_FORMATS) {
      format.setLenient(false);
    }
  }

  private static final int[] buttons = {
    R.string.button_add_contact,
  };

  public AddressBookResultHandler(Activity activity, ParsedResult result) {
    super(activity, result);
  }

  @Override
  public int getButtonCount() {
	return buttons.length;
  }

  @Override
  public int getButtonText(int index) {
    return buttons[index];
  }

  @Override
  public void handleButtonPress(int index) {
    AddressBookParsedResult addressResult = (AddressBookParsedResult) getResult();
    String[] addresses = addressResult.getAddresses();
    String address1 = addresses == null || addresses.length < 1 ? null : addresses[0];
    String[] addressTypes = addressResult.getAddressTypes();
    String address1Type = addressTypes == null || addressTypes.length < 1 ? null : addressTypes[0];
    addContact(addressResult.getNames(),
               addressResult.getNicknames(),
               addressResult.getPronunciation(),
               addressResult.getPhoneNumbers(),
               addressResult.getPhoneTypes(),
               addressResult.getEmails(),
               addressResult.getEmailTypes(),
               addressResult.getNote(),
               addressResult.getInstantMessenger(),
               address1,
               address1Type,
               addressResult.getOrg(),
               addressResult.getTitle(),
               addressResult.getURLs(),
               addressResult.getBirthday(),
               addressResult.getGeo());
  }

  private static Date parseDate(String s) {
    for (DateFormat currentFormat : DATE_FORMATS) {
      try {
        return currentFormat.parse(s);
      } catch (ParseException e) {
        // continue
      }
    }
    return null;
  }

  // Overriden so we can hyphenate phone numbers, format birthdays, and bold the name.
  @Override
  public CharSequence getDisplayContents() {
    AddressBookParsedResult result = (AddressBookParsedResult) getResult();
    StringBuilder contents = new StringBuilder(100);
    ParsedResult.maybeAppend(result.getNames(), contents);
    int namesLength = contents.length();

    String pronunciation = result.getPronunciation();
    if (pronunciation != null && pronunciation.length() > 0) {
      contents.append("\n(");
      contents.append(pronunciation);
      contents.append(')');
    }

    ParsedResult.maybeAppend(result.getTitle(), contents);
    ParsedResult.maybeAppend(result.getOrg(), contents);
    ParsedResult.maybeAppend(result.getAddresses(), contents);
    String[] numbers = result.getPhoneNumbers();
    if (numbers != null) {
      for (String number : numbers) {
        ParsedResult.maybeAppend(PhoneNumberUtils.formatNumber(number), contents);
      }
    }
    ParsedResult.maybeAppend(result.getEmails(), contents);
    ParsedResult.maybeAppend(result.getURLs(), contents);

    String birthday = result.getBirthday();
    if (birthday != null && birthday.length() > 0) {
      Date date = parseDate(birthday);
      if (date != null) {
        ParsedResult.maybeAppend(DateFormat.getDateInstance(DateFormat.MEDIUM).format(date.getTime()), contents);
      }
    }
    ParsedResult.maybeAppend(result.getNote(), contents);

    if (namesLength > 0) {
      // Bold the full name to make it stand out a bit.
      Spannable styled = new SpannableString(contents.toString());
      styled.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, namesLength, 0);
      return styled;
    } else {
      return contents.toString();
    }
  }

  @Override
  public int getDisplayTitle() {
    return R.string.result_address_book;
  }
}
