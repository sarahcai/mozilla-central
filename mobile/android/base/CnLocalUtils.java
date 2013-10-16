/* -*- Mode: Java; c-basic-offset: 4; tab-width: 20; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.gecko;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.mozilla.gecko.util.ThreadUtils;
import org.mozilla.gecko.util.UiAsyncTask;
import org.mozilla.gecko.sync.setup.SyncAccounts;
import org.mozilla.gecko.GeckoApplication;

import android.text.TextUtils;
import android.content.SharedPreferences; 
import android.preference.PreferenceManager;  
import android.util.Log;

public class CnLocalUtils {
    
    public static boolean isBaiduUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            Pattern p = Pattern.compile("(www|m)\\.baidu\\.com/(s|baidu)");
            Matcher m = p.matcher(url);
            if (m.find()) {
                return true;
            }
        }
        return false;
    }
    
}
