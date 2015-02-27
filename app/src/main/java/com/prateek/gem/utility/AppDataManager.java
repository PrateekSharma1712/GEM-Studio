package com.prateek.gem.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneNumberUtils;
import android.widget.EditText;

import com.prateek.gem.AppConstants;
import com.prateek.gem.AppSharedPreference;
import com.prateek.gem.R;
import com.prateek.gem.model.Group;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.model.Users;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AppDataManager {

	// single instance for AppDataManager
	private static AppDataManager instanceManager = null;
	// it is to hold the application baseActivity
	public static Context appContext = null;
	// to keep a reference of current activity
	public static ActionBarActivity currentScreen = null;


    private static Group mGroup = null;
    private static ArrayList<Group> mGroups = new ArrayList<Group>();
    private static Users user = new Users();

	public static AppDataManager sharedManager() {
		if (instanceManager == null) {
			instanceManager = new AppDataManager();
		}
		return instanceManager;
	}

	/**
	 * Method to check whether the edit field has valid text or not
	 *
	 * @param text
	 * @return
	 */
	public static boolean isValidField(EditText text) {
		if (text == null || text.getText().toString() == null
				|| text.getText().toString().trim().length() == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Method to check whether the email address is valid or not
	 * 
	 * @param emailAddress
	 * @return
	 */
	public static boolean isValidEmail(EditText emailAddress) {
		if (isValidField(emailAddress)) {
			String data = emailAddress.getText().toString();
			if (!android.util.Patterns.EMAIL_ADDRESS.matcher(data).matches())
				return false;
		}
		return true;
	}

	/**
	 * Method to check whether the phone number is valid or not
	 * 
	 * @param number
	 * @return
	 */
	public static boolean isValidPhone(EditText number) {
		if (isValidField(number)) {
			String data = number.getText().toString().trim();
			if (data.length() < 10
					|| !PhoneNumberUtils.isGlobalPhoneNumber(data))
				return false;
		}
		return true;
	}

	/**
	 * 
	 * @param email
	 * @param subject
	 * @param text
	 */
	public static void sendEmail(Activity activity, String email,
			String subject, String text) {
		DebugLogger.method("DashboardScreen :: sendEmail");

		Intent intentEmail = new Intent(Intent.ACTION_SEND);
		intentEmail.putExtra(Intent.EXTRA_EMAIL, new String[] { email });
		intentEmail.putExtra(Intent.EXTRA_SUBJECT, subject);
		intentEmail.putExtra(Intent.EXTRA_TEXT, text);
		intentEmail.setType("message/rfc822");
		try {
			activity.startActivityForResult(Intent.createChooser(intentEmail,
					"Choose an email provider :"),
					AppConstants.ACTION_REQUEST_EMAIL);
		} catch (android.content.ActivityNotFoundException ex) {
			/*Utils.displayToast(activity.getApplicationContext(),
					"There are no email clients installed.");*/
		}
	}

	/**
	 * Method to make phone call with given number
	 * 
	 * @param phoneNumber
	 */
	public static void call(String phoneNumber) {
		if (appContext.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_TELEPHONY)) {
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel:" + phoneNumber));
			currentScreen.startActivity(intent);
		} else {
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					currentScreen);
			builder.setMessage("As your device does not seem to have call feature, Please call " + phoneNumber + " from another phone");
			builder.setTitle("Call");
			builder.show();
		}
	}

	/**
	 * Method to return the current date in "yyyyMMdd'T'HHmmss" format
	 * 
	 * @param
	 * @return String; it returns the current date
	 */
	public static String getCurrentDate() {
		DebugLogger.method("BBDataManager :: getCurrentDate");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss",
				getDefault());
		String timeStamp = dateFormat.format(new Date());
		return timeStamp;
	}

	public static Locale getDefault() {
		DebugLogger.method("BBDataManager :: getDefault");
		return Locale.getDefault();
	}

	public static Bitmap getRoundedCornerBitmap(int id) {
		Bitmap icon = BitmapFactory.decodeResource(
				currentScreen.getResources(), id);
		return getRoundedCornerBitmap(icon);
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 75;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static boolean isSDPresent() {
		Boolean status = android.os.Environment.getExternalStorageState()
				.equals(android.os.Environment.MEDIA_MOUNTED);
		return status;
	}

    public static void setCurrentGroup(Group group) {
        mGroup = group;
    }

    public static void setGroups(ArrayList<Group> groups) {
        mGroups = groups;
    }

    public static ArrayList<Group> getGroups() {
        return mGroups;
    }

    public static Group getCurrentGroup() {
        return mGroup;
    }

    public static Users getUser() {
        return user;
    }

    public static void setUser() {
        user = new Users();
        user.setEmail(AppSharedPreference.getPreferenceString(AppConstants.ADMIN_EMAIL));
        user.setGcmRegId(AppSharedPreference.getPreferenceString(AppConstants.ADMIN_ID));
        user.setUserName(AppSharedPreference.getPreferenceString(AppConstants.ADMIN_NAME));
        user.setPhoneNumber(AppSharedPreference.getPreferenceString(AppConstants.ADMIN_PHONE));
        user.setPassword(AppSharedPreference.getPreferenceString(AppConstants.ADMIN_PASSWORD));
    }

    public static int getThemePrimaryColor() {
        return appContext.getResources().getColor(R.color.theme_default_primary);
    }

    public static int getThemePrimaryDarkColor() {
        return appContext.getResources().getColor(R.color.theme_default_primary_dark);
    }

    public static int getThemePrimaryLightColor() {
        return appContext.getResources().getColor(R.color.theme_default_primary_light);
    }

    public static int getThemePrimaryTextColor() {
        return appContext.getResources().getColor(R.color.theme_default_text_primary);
    }

    public static int getThemeSecondaryTextColor() {
        return appContext.getResources().getColor(R.color.theme_default_text_secondary);
    }

    public static int getThemeBackground() {
        return appContext.getResources().getColor(R.color.theme_background);
    }

    public static int getThemeButtonMaterialLight() {
        return appContext.getResources().getColor(R.color.button_material_light);
    }
}
