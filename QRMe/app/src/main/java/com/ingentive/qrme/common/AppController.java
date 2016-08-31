package com.ingentive.qrme.common;


import android.app.Application;
import android.text.TextUtils;

import com.activeandroid.ActiveAndroid;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

//
//@ReportsCrashes(formKey = "",
//		formUri = "http://www.yourbrand.pk/yourbrand/junaid_khan/crashes/crashscript.php",
//		mode = ReportingInteractionMode.TOAST,
//		resToastText = R.string.crash_toast_text)
public class AppController extends android.app.Application  {

	public static final String TAG = AppController.class.getSimpleName();

//	private RequestQueue mRequestQueue;
//
//	private static AppController mInstance;

	@Override
	public void onCreate() {
		super.onCreate();
		ActiveAndroid.initialize(this);
		//mInstance = this;
//		ACRA.init(this);
	}

//	public static synchronized AppController getInstance() {
//		return mInstance;
//	}
//
//	public RequestQueue getRequestQueue() {
//		if (mRequestQueue == null) {
//			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
//		}
//
//		return mRequestQueue;
//	}
//
//	public <T> void addToRequestQueue(Request<T> req, String tag) {
//		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
//		getRequestQueue().add(req);
//	}
//
//	public <T> void addToRequestQueue(Request<T> req) {
//		req.setTag(TAG);
//		getRequestQueue().add(req);
//	}
//
//	public void cancelPendingRequests(Object tag) {
//		if (mRequestQueue != null) {
//			mRequestQueue.cancelAll(tag);
//		}
//	}

}
