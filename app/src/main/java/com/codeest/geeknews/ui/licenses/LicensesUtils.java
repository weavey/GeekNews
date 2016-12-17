package com.codeest.geeknews.ui.licenses;

import android.content.Context;
import android.provider.Settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

final class LicensesUtils {

	private LicensesUtils() {

	}

	public static String readTextFile(InputStream inputStream) {
		InputStreamReader inputreader = new InputStreamReader(inputStream);
		BufferedReader buffreader = new BufferedReader(inputreader);
		StringBuilder text = new StringBuilder();

		String line;
		try {
			while ((line = buffreader.readLine()) != null) {
				text.append(line);
				text.append('\n');
			}
		} catch (IOException var6) {
			return null;
		}

		return text.toString();
	}

	public static boolean isAirplaneModeOn(Context context) {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
		        /* API 17 and above */
			return Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
		} else {
                /* below */
			return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
		}
	}
}
