package cn.antke.bct.update.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.common.utils.LogUtil;
import com.common.utils.PreferencesUtils;
import com.common.utils.StringUtil;

import java.io.File;

import cn.antke.bct.R;
import cn.antke.bct.update.controller.UpdateActiviy;

/**
 * 新版本下载安装服务
 */
public class UpdateService extends Service {

	public static final String DOWNLOAD_FOLDER_NAME = "fengyimall";
	public static final String DOWNLOAD_FILE_NAME = "fengyimall_client_new.apk";
	public static final String APK_DOWNLOAD_ID = "apkDownloadId";
	private static final String TAG = "UpdateService";
	private String ApkUrl;
	private String notificationTitle;
	private String notificationDescription;
	private DownloadManager downloadManager;
	private CompleteReceiver completeReceiver;

	public UpdateService() {
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			ApkUrl = intent.getStringExtra(UpdateActiviy.KEY_UPDATE_URL);
			if (!StringUtil.isEmpty(ApkUrl)) {
				new DowdloadApkHelper().execute();
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	class DowdloadApkHelper {

		DowdloadApkHelper() {
			super();
			downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
			completeReceiver = new CompleteReceiver();
		}

		void execute() {
			//清除已下载的内容重新下载
			long downloadId = PreferencesUtils.getLong(UpdateService.this, APK_DOWNLOAD_ID);
			if (downloadId != -1) {
				downloadManager.remove(downloadId);
				PreferencesUtils.removeSharedPreferenceByKey(UpdateService.this, APK_DOWNLOAD_ID);
			}

			DownloadManager.Request request = new DownloadManager.Request(Uri.parse(ApkUrl));

			//设置Notification中显示的文字
			request.setTitle(notificationTitle);
			request.setDescription(notificationDescription);
			request.setNotificationVisibility(
					(!StringUtil.isEmpty(notificationTitle) || !StringUtil.isEmpty(notificationDescription))
							? View.VISIBLE : View.GONE);
			//设置可用的网络类型
			request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
			//设置状态栏中显示Notification
			request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
			//不显示下载界面
			request.setVisibleInDownloadsUi(false);
			//设置下载后文件存放的位置
			File folder = Environment.getExternalStoragePublicDirectory(DOWNLOAD_FOLDER_NAME);
			if (!folder.exists() || !folder.isDirectory()) {
				folder.mkdirs();
			}
			File apkFile = new File(Environment.getExternalStoragePublicDirectory(DOWNLOAD_FOLDER_NAME) + File.separator + DOWNLOAD_FILE_NAME);
			if (apkFile.exists()) {
				apkFile.delete();
			}
			request.setDestinationInExternalPublicDir(DOWNLOAD_FOLDER_NAME, DOWNLOAD_FILE_NAME);
			//设置文件类型
			MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
			String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(ApkUrl));
			request.setMimeType(mimeString);
			long longId = downloadManager.enqueue(request);
			//保存返回唯一的downloadId
			PreferencesUtils.putLong(UpdateService.this, APK_DOWNLOAD_ID, longId);
			Toast.makeText(UpdateService.this, R.string.update_started, Toast.LENGTH_LONG).show();

			/** register download success broadcast **/
			UpdateService.this.registerReceiver(completeReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

			//download percent observer
//            mDownloadObserver = new DownloadObserver(null, UpdateService.this, longId);
//            UpdateService.this.getContentResolver().registerContentObserver(Uri.parse("content://downloads/"), true, mDownloadObserver);
		}
	}

	class CompleteReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			/**
			 * get the id of download which have download success, if the id is my id and it's status is successful,
			 * then install it
			 **/
			long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
			long downloadId = PreferencesUtils.getLong(context, APK_DOWNLOAD_ID);
			if (completeDownloadId == downloadId) {

				// if download successful
				if (queryDownloadStatus(downloadManager, downloadId) == DownloadManager.STATUS_SUCCESSFUL) {

					//clear downloadId
					PreferencesUtils.removeSharedPreferenceByKey(context, APK_DOWNLOAD_ID);

					//unregisterReceiver
					//do it onDestory
//                    context.unregisterReceiver(completeReceiver);

					//install apk
					String apkFilePath = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath())
							.append(File.separator).append(DOWNLOAD_FOLDER_NAME).append(File.separator)
							.append(DOWNLOAD_FILE_NAME).toString();
					if (install(context, apkFilePath)) {
						stopSelf();
					}

				}
			}
		}
	}

	class DownloadObserver extends ContentObserver {
		private long downid;
		private Handler handler;
		private Context context;

		public DownloadObserver(Handler handler, Context context, long downid) {
			super(handler);
			this.handler = handler;
			this.downid = downid;
			this.context = context;
		}

		@Override
		public void onChange(boolean selfChange) {
			//每当/data/data/com.android.providers.download/database/database.db变化后，触发onCHANGE，开始具体查询
			super.onChange(selfChange);
			//实例化查询类，这里需要一个刚刚的downid
			DownloadManager.Query query = new DownloadManager.Query().setFilterById(downid);
			DownloadManager downManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
			//这个就是数据库查询啦

			//handler.sendMessage(xxxx),这样就可以更新UI了
			try {
				Cursor cursor = downManager.query(query);
				if (cursor == null) return;
				while (cursor.moveToNext()) {
					int mDownload_so_far = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
					int mDownload_all = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
					int mProgress = (int) (mDownload_so_far >= mDownload_all ? 100 : ((long) mDownload_so_far * 100 / (long) mDownload_all));
					LogUtil.e(TAG, mDownload_so_far + " " + mDownload_all + " " + String.valueOf(mProgress));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 查询下载状态
	 */
	public static int queryDownloadStatus(DownloadManager downloadManager, long downloadId) {
		int result = -1;
		DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
		Cursor c = null;
		try {
			c = downloadManager.query(query);
			if (c != null && c.moveToFirst()) {
				result = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return result;
	}

	/**
	 * install app
	 * @return whether apk exist
	 */
	public static boolean install(Context context, String filePath) {
		File file = new File(filePath);
		if (file.length() > 0 && file.exists() && file.isFile()) {
			if(Build.VERSION.SDK_INT>=24) {//判读版本是否在7.0以上
				Uri apkUri = FileProvider.getUriForFile(context, "cn.antke.bct.fileprovider",file);//在AndroidManifest中的android:authorities值
				Intent install = new Intent(Intent.ACTION_VIEW);
				install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
				install.setDataAndType(apkUri, "application/vnd.android.package-archive");
				context.startActivity(install);
			} else{
				Intent install = new Intent(Intent.ACTION_VIEW);
				install.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
				install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(install);
			}
			return true;
		}
		return false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(completeReceiver);
//        getContentResolver().unregisterContentObserver(mDownloadObserver);
	}
}
