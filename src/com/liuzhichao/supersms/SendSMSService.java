package com.liuzhichao.supersms;

import java.util.ArrayList;
import java.util.List;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsManager;

public class SendSMSService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		//获取手机号码和短信内容
		List<String> numberList = intent.getStringArrayListExtra("numberList");
		String text = intent.getStringExtra("content");
		
		//启动发送短信线程
		sendSMS(numberList, text);
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	/**
	 * 批量发送短信
	 * @param numberList  号码
	 * @param text        短信内容
	 */
	private void sendSMS(final List<String> numberList,final String text){
		new Thread(){
			@Override
			public void run() {
				SmsManager sms = SmsManager.getDefault();
				
				for (int i = 0; i < numberList.size(); i++) {
					String number = numberList.get(i);
					Intent sendIntent = new Intent(
							AppConstants.ACTION_SMS_SEND_ACTIOIN);
					sendIntent.putExtra("number", number);
					sendIntent.putExtra("numberIndex", i);

					PendingIntent sendPendingIntent = PendingIntent
							.getBroadcast(getApplicationContext(), (int) System.currentTimeMillis(),
									sendIntent,
									PendingIntent.FLAG_UPDATE_CURRENT);

					Intent deliveredIntent = new Intent(
							AppConstants.ACTION_SMS_DELIVERED_ACTION);
					deliveredIntent.putExtra("number", number);
					PendingIntent deliveredPendingIntent = PendingIntent
							.getBroadcast(getApplicationContext(), (int) System.currentTimeMillis(),
									deliveredIntent,
									PendingIntent.FLAG_UPDATE_CURRENT);
					try {
						// 睡眠1秒
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ArrayList<String> msgs = sms.divideMessage(text);
					for (String msg : msgs) {
						sms.sendTextMessage(number, null, msg,
								sendPendingIntent, deliveredPendingIntent);
					}
					
					Intent intent = new Intent();
					intent.putExtra("number", number);
					intent.setAction(AppConstants.ACTION_SMS_SEND_NUMBER);
					sendBroadcast(intent);
				}
				super.run();
			}
		}.start();
	}

	@Override
	public void onDestroy() {
		System.out.println("send sms service stop");
		super.onDestroy();
	}

	
}
