package com.liuzhichao.supersms;

import java.io.File;
import java.util.ArrayList;

import com.huzhiyi.supersms.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements OnClickListener {

	private EditText et_status;
	private Button btn_start;
	private ArrayList<String> numbers;
	private Intent service;
	private SMSStatusReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//防止手机休眠
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  
		
		setContentView(R.layout.activity_main);
		et_status = (EditText) findViewById(R.id.et_status);
		btn_start = (Button) findViewById(R.id.btn_start);

		btn_start.setOnClickListener(this);
		receiver = new SMSStatusReceiver();
	}

	@Override
	public void onClick(View v) {
		// step1 read the phone number form file

		et_status.setText("----Start----\n");

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File path = Environment.getExternalStorageDirectory();
			et_status.append("开始读取手机号码.\n");
			numbers = FileUtils.readNumbers(new File(path, "numbers.txt"));

			if (numbers == null || numbers.size() == 0) {
				et_status
						.append("读取到 0 条手机号.请检查SD卡根目录numbers.txt文件是否存在\n 程序结束. \n ----End----");
				return;
			}
			et_status.append("读取到 " + numbers.size() + " 条手机号.\n");

			et_status.append("开始读取短信.\n");
			String sms = FileUtils.readSMSContent(new File(path, "content.txt"));
			if (StringUtils.isNullOrEmpty(sms)) {
				et_status
						.append("读取短信内容为空.请检查SD卡根目录content.txt文件是否存在\n 程序结束. \n ----End----");
				return;
			}
			et_status.append("读取到短信内容为: " + sms + "\n启动短信群发服务...\n");

			// 注册广播
			IntentFilter filter = new IntentFilter();
			filter.addAction(AppConstants.ACTION_SMS_SEND_NUMBER);
			filter.addAction(AppConstants.ACTION_SMS_DELIVERED_ACTION);
			filter.addAction(AppConstants.ACTION_SMS_SEND_ACTIOIN);
			registerReceiver(receiver, filter);

			// step2 send sms
			// 启动发送短信的服务
			service = new Intent(this, SendSMSService.class);
			service.putStringArrayListExtra("numberList", numbers);
			service.putExtra("content", sms);
			startService(service);

		} else {
			et_status.append("SD卡不存在. \n 程序结束. \n ----End---- ");
		}

	}

	
	//短信发送状态广播
	public class SMSStatusReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (AppConstants.ACTION_SMS_SEND_NUMBER.equals(action)) {
				String number = intent.getStringExtra("number");
			
				et_status.append("开始向 " + number + " 发送短信\n");

			} else if (AppConstants.ACTION_SMS_SEND_ACTIOIN.equals(action)) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					et_status.append(Html.fromHtml("向 "
							+ intent.getStringExtra("number")
							+ " 发送短信<font color='green'>成功</font><br>"));
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
				case SmsManager.RESULT_ERROR_RADIO_OFF:
				case SmsManager.RESULT_ERROR_NULL_PDU:
				default:
					et_status.append(Html.fromHtml("向 "
							+ intent.getStringExtra("number")
							+ " 发送短信<font color='red'>失败</font><br>"));
					break;
				}
				
				int numberIndex = intent.getIntExtra("numberIndex", 0);
				if (numberIndex == numbers.size() - 1) {
					et_status.append("所有号码已发送完毕\n");
					stopService(service);
					et_status.append("关闭短信群发服务. \n ----End----\n");
				}

			} else if (AppConstants.ACTION_SMS_DELIVERED_ACTION.equals(action)) {
				et_status.append(Html.fromHtml(intent.getStringExtra("number")
						+ " 接收短信<font color='green'>成功</font><br>"));
			}

		}

	}




	@Override
	protected void onDestroy() {
		if (receiver != null) {
			try {
				getApplicationContext().unregisterReceiver(receiver);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		super.onDestroy();
	}

}
