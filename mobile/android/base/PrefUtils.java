package org.mozilla.gecko;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import org.mozilla.gecko.GeckoApplication;

public class PrefUtils {
    private static SharedPreferences sPrefs = null;

    public static SharedPreferences getPrefs() {
	if (sPrefs == null) {
	    sPrefs = PreferenceManager.getDefaultSharedPreferences(GeckoApplication
		    .getContext());
	}
	return sPrefs;
    }

    /**
     * Retrieve a String value from the preferences.
     * 
     * @param key
     *            The name of the preference to retrieve.
     * @param defValue
     *            Value to return if this preference does not exist.
     * 
     * @return Returns the preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that
     *         is not a String.
     * 
     * @throws ClassCastException
     */
    public static String getString(String key, String defValue) {
	return getPrefs().getString(key, defValue);
    }

    /**
     * Retrieve an int value from the preferences.
     * 
     * @param key
     *            The name of the preference to retrieve.
     * @param defValue
     *            Value to return if this preference does not exist.
     * 
     * @return Returns the preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that
     *         is not an int.
     * 
     * @throws ClassCastException
     */
    public static int getInt(String key, int defValue) {
	return getPrefs().getInt(key, defValue);
    }

    /**
     * Retrieve a long value from the preferences.
     * 
     * @param key
     *            The name of the preference to retrieve.
     * @param defValue
     *            Value to return if this preference does not exist.
     * 
     * @return Returns the preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that
     *         is not a long.
     * 
     * @throws ClassCastException
     */
    public static long getLong(String key, long defValue) {
	return getPrefs().getLong(key, defValue);
    }

    /**
     * Retrieve a float value from the preferences.
     * 
     * @param key
     *            The name of the preference to retrieve.
     * @param defValue
     *            Value to return if this preference does not exist.
     * 
     * @return Returns the preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that
     *         is not a float.
     * 
     * @throws ClassCastException
     */
    public static float getFloat(String key, float defValue) {
	return getPrefs().getFloat(key, defValue);
    }

    /**
     * Retrieve a boolean value from the preferences.
     * 
     * @param key
     *            The name of the preference to retrieve.
     * @param defValue
     *            Value to return if this preference does not exist.
     * 
     * @return Returns the preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that
     *         is not a boolean.
     * 
     * @throws ClassCastException
     */
    public static boolean getBoolean(String key, boolean defValue) {
	return getPrefs().getBoolean(key, defValue);
    }

    /**
     * Write a String value to the preferences.
     * 
     * @param key
     *            The name of the preference to modify.
     * @param value
     *            The new value for the preference.
     */
    public static void putString(String key, String value) {
	Editor editor = getPrefs().edit();
	editor.putString(key, value);
	editor.commit();
    }

    /**
     * Write an int value in the preferences.
     * 
     * @param key
     *            The name of the preference to modify.
     * @param value
     *            The new value for the preference.
     */
    public static void putInt(String key, int value) {
	Editor editor = getPrefs().edit();
	editor.putInt(key, value);
	editor.commit();
    }

    /**
     * Write a long value in the preferences.
     * 
     * @param key
     *            The name of the preference to modify.
     * @param value
     *            The new value for the preference.
     */
    public static void putLong(String key, long value) {
	Editor editor = getPrefs().edit();
	editor.putLong(key, value);
	editor.commit();
    }

    /**
     * Write a float value to the preferences.
     * 
     * @param key
     *            The name of the preference to modify.
     * @param value
     *            The new value for the preference.
     */
    public static void putFloat(String key, float value) {
	Editor editor = getPrefs().edit();
	editor.putFloat(key, value);
	editor.commit();
    }

    /**
     * Write a boolean value in the preferences.
     * 
     * @param key
     *            The name of the preference to modify.
     * @param value
     *            The new value for the preference.
     */
    public static void putBoolean(String key, boolean value) {
	Editor editor = getPrefs().edit();
	editor.putBoolean(key, value);
	editor.commit();
    }
}
