/**
 * 
 */
package cn.antke.bct.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * @author taofh
 *
 */
public class SafeSharePreferenceUtil {
	public static Context mContext;
	private static String mShareName = "safe_info";
	public static SharedPreferences sharePre;

	public static void init(Context context) {
		sharePre = context.getSharedPreferences(mShareName, Context.MODE_PRIVATE);		
	}

	public static String getString(String key, String defValue) {
		try {
			String strKey = MD5Encoder.encode(key);
			String dsKey = MD5Encoder.encode(strKey);
			String strDef = DESEncoder.encrypt(defValue, dsKey);
			String vl = sharePre.getString(strKey, strDef);
			String str = DESEncoder.decrypt(vl, dsKey);
			return str;
		} catch (Exception e) {
			return defValue;
		}
	}

	public static boolean saveString(String key, String vl) {
		try {
			String strKey = MD5Encoder.encode(key);
			String dsKey = MD5Encoder.encode(strKey);
			String strDef = DESEncoder.encrypt(vl, dsKey);
			return sharePre.edit().putString(strKey, strDef).commit();
		} catch (Exception e) {
			return false;
		}
	}

	public static int getInt(String key, int defValue) {
		try {
			String str=getString(key, String.valueOf(defValue));
			int result = Integer.parseInt(str);
			return result;
		} catch (Exception e) {
			return defValue;
		}
	}

	public static boolean saveInt(String key, int vl) {
		try {
			return saveString(key, String.valueOf(vl));
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean getBoolean(String key, boolean defValue) {
		try {
			String str=getString(key, String.valueOf(defValue));
			boolean result = Boolean.parseBoolean(str);
			return result;
		} catch (Exception e) {
			return defValue;
		}
	}

	public static boolean saveBoolean(String key, boolean vl) {
		try {
			return saveString(key, String.valueOf(vl));
		} catch (Exception e) {
			return false;
		}
	}

	public static float getFloat(String key, float defValue) {
		try {
			String str=getString(key, String.valueOf(defValue));
			float result = Float.parseFloat(str);
			return result;
		} catch (Exception e) {
			return defValue;
		}
	}

	public static boolean saveFloat(String key, float vl) {
		try {
			return saveString(key, String.valueOf(vl));
		} catch (Exception e) {
			return false;
		}
	}

	public static long getLong(String key, long defValue) {
		try {
			String str=getString(key, String.valueOf(defValue));
			long result = Long.parseLong(str);
			return result;
		} catch (Exception e) {
			return defValue;
		}
	}

	public static boolean saveLong(String key, long vl) {
		try {
			return saveString(key, String.valueOf(vl));
		} catch (Exception e) {
			return false;
		}
	}
	
	public static void clearDataByKey(final Context context,String... keys) {
		if (keys == null || keys.length == 0) return;
		SharedPreferences.Editor editor = sharePre.edit();
		for(String key:keys){
			if (!TextUtils.isEmpty(key)) {
				String encoderKey = MD5Encoder.encode(key);
				editor.remove(encoderKey);
			}
		}
	    editor.commit();
	}

}
