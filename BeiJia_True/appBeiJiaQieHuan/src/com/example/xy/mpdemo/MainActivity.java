package com.example.xy.mpdemo;

import java.util.concurrent.Executors;

import org.codehaus.jackson.map.ObjectMapper;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.senter.helper.ConsantHelper;
import cn.com.senter.helper.ShareReferenceSaver;
import cn.com.senter.model.IdentityCardZ;
import cn.com.senter.sdkdefault.helper.Error;

import com.xy.bluetooth.StateUtils;
import com.xy.bluetooth.TBlueReader;

public class MainActivity extends Activity {
	private final static String SERVER_KEY1 = "CN.COM.SENTER.SERVER_KEY1";
	private final static String PORT_KEY1 = "CN.COM.SENTER.PORT_KEY1";
	private final static String BLUE_ADDRESSKEY = "CN.COM.SENTER.BLUEADDRESS";

	
	private Button connectButton;
	private Button cardReadButton;
	private Button codeReadButton;
	private TextView macText;
	private TextView tv_info;
	private TextView cardReadText;
	private TextView codeReadText;
	private String server_address = "senter-online.cn";
	private int server_port = 60002;

	public static Handler uiHandler;

	private TBlueReader mBlueReaderHelper;


	// ----蓝牙功能有关的变量----
	private BluetoothAdapter mBluetoothAdapter = null; // /蓝牙适配器

	private int iselectNowtype = 0;
	private String Blueaddress = null;

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		initViews();
		uiHandler = new MyHandler(this);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		
		
		Log.i("蓝牙", "lanya");
		String macAddress = ShareReferenceSaver.getData(this, StateUtils.BLUE_ADDRESSKEY);
		if (macAddress == "") {
			Toast.makeText(MainActivity.this, "请选择蓝牙设备!", Toast.LENGTH_LONG)
					.show();
			macText.setText("设备地址:" + macAddress
					+ " is wrong, length = " + macAddress.length());
			return;
		}else
			macText.setText("设备地址:" + macAddress);
		// 启动蓝牙数据连接服务
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(macAddress);

//		mBlueReaderHelper = new TBlueReader(this, uiHandler);
		mBlueReaderHelper = new TBlueReader(this, uiHandler,mHandler,device);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		Blueaddress = ShareReferenceSaver.getData(this, BLUE_ADDRESSKEY);
		initShareReference();

		LogcatFileManager.getInstance().startLogcatManager(MainActivity.this);
	}

	@Override
	public void onStart() {
		super.onStart();

		// If BT is not on, request that it be enabled.
		Log.e("blue", "activity onStart");
		if (!mBluetoothAdapter.isEnabled()) {
			Log.e("blue", "activity isEnabled");
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, 2);
			// Otherwise, setup the chat session
		} else {
			// 此处应添加一个toast提示用户打开蓝牙功能---
		}
	}

	@Override
	public void onStop() {
		Log.e("blue", "activity onStop");
		super.onStop();
	}

	@Override
	public void onPause() {
		Log.e("blue", "onPause");

		super.onPause();
	}

	private static final int REQUEST_CONNECT_DEVICE = 1;

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("MAIN", "onActivityResult: requestCode=" + requestCode
				+ ", resultCode=" + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			if (resultCode == Activity.RESULT_OK) {

				Blueaddress = data.getExtras().getString(
						DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				macText.setText("address:" + Blueaddress
						+ " is wrong, length = " + Blueaddress.length());
				Log.i("MAC", Blueaddress);
				if (!Blueaddress
						.matches("([0-9a-fA-F][0-9a-fA-F]:){5}([0-9a-fA-F][0-9a-fA-F])")) {
					macText.setText("设备地址:" + Blueaddress
							+ " is wrong, length = " + Blueaddress.length());
					return;
				}else
					macText.setText("设备地址:" + Blueaddress);
				tv_info.setText("设备地址:" + Blueaddress);
				ShareReferenceSaver.saveData(MainActivity.this,
						BLUE_ADDRESSKEY, Blueaddress);

			}
			break;
		case 2:
			if (resultCode == 100) {

				this.server_address = data.getExtras().getString("address");
				this.server_port = data.getExtras().getInt("port");

				Log.e("MAIN", "onActivityResult: " + server_address);
				Log.e("MAIN", "onActivityResult: " + server_port);

				initShareReference();

			}
			break;
		}
	}
	//自动运行
	private void initShareReference() {

		if (this.server_address.length() <= 0) {
			if (ShareReferenceSaver.getData(this, SERVER_KEY1).trim().length() <= 0) {
				this.server_address = "senter-online.cn";
			}
			if (ShareReferenceSaver.getData(this, PORT_KEY1).trim().length() <= 0) {
				this.server_port = 60002;
			} else {
				this.server_port = Integer.valueOf(ShareReferenceSaver.getData(
						this, PORT_KEY1));
			}
		}

		// ----实例化help类---
		mBlueReaderHelper.setServerAddress(this.server_address);
		mBlueReaderHelper.setServerPort(this.server_port);

	}

	private void initViews() {
		Log.i("点击1int", "v");

		tv_info = (TextView) findViewById(R.id.tv_info);
		connectButton = (Button) findViewById(R.id.connectButton);
		cardReadButton = (Button) findViewById(R.id.cardReadButton);
		codeReadButton = (Button) findViewById(R.id.codeReadButton);
		macText = (TextView) findViewById(R.id.macText);
		cardReadText = (TextView) findViewById(R.id.cardReadText);
		codeReadText = (TextView) findViewById(R.id.codeReadText);
		//选择设备
		connectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent serverIntent = new Intent(MainActivity.this, DeviceListActivity.class);
				startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			}

		});
		//读取身份证
		cardReadButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				readIdCard();
			}
		});
		//读取条码
		codeReadButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				idCardClose();
				codeReadText.setText("");
				
				readBarCode();
				
			}
		});
		
	}
	
	private void readIdCard() {
		// TODO Auto-generated method stub
		if(mBlueReaderHelper.barCodeConnectState() == StateUtils.STATE_CONNECTED){
			mBlueReaderHelper.idCard();
			mBlueReaderHelper.stop();
		}
		readCardBlueTooth();
	}
	private void readBarCode() {
		// TODO Auto-generated method stub
		if(mBlueReaderHelper.barCodeConnectState() != StateUtils.STATE_CONNECTED)
			mBlueReaderHelper.initData();
		else
			mBlueReaderHelper.scanSingle();
	}
	private void idCardClose() {
		if(mBlueReaderHelper!=null){
			mBlueReaderHelper.close();
			mBlueReaderHelper.stop();
		}
	}

	public void ButtondefDrawable() {

		cardReadButton.setBackgroundResource(R.drawable.frame_button_d);

		switch (iselectNowtype) {
		case 1:
			break;
		case 2:
			break;
		case 3:
			cardReadButton.setBackgroundResource(R.drawable.frame_button_p);
			break;
		}

	}

	/**
	 * 蓝牙读卡方式
	 */
	private class BlueReadTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPostExecute(String strCardInfo) {

			cardReadButton.setEnabled(true);
			Log.i("读卡", strCardInfo);

			if (TextUtils.isEmpty(strCardInfo)) {
				Log.i("判断", "1");
				uiHandler.sendEmptyMessage(ConsantHelper.READ_CARD_FAILED);
				ButtondefDrawable();
				mBlueReaderHelper.close();

				return;
			}

			if (strCardInfo.length() <= 2) {
				Log.i("判断", "2");
				readCardFailed(strCardInfo);
				ButtondefDrawable();
				mBlueReaderHelper.close();

				return;
			}

			ObjectMapper objectMapper = new ObjectMapper();
			IdentityCardZ mIdentityCardZ = new IdentityCardZ();

			try {
				mIdentityCardZ = (IdentityCardZ) objectMapper.readValue(
						strCardInfo, IdentityCardZ.class);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(ConsantHelper.STAGE_LOG, "mIdentityCardZ failed");
				mBlueReaderHelper.close();

				return;
			}

			readCardSuccess(mIdentityCardZ);

			ButtondefDrawable();
			mBlueReaderHelper.close();

			super.onPostExecute(strCardInfo);

		}

		@Override
		protected String doInBackground(Void... params) {

			String strCardInfo = mBlueReaderHelper.read();
			return strCardInfo;
		}
	};

	/**
	 * 蓝牙读卡方式
	 */
	protected void readCardBlueTooth() {
		Log.e("", Blueaddress);
		if (Blueaddress == null) {
			Toast.makeText(this, "请选择蓝牙设备，再读卡!", Toast.LENGTH_LONG).show();
			return;
		}

		if (Blueaddress.length() <= 0) {
			Toast.makeText(this, "请选择蓝牙设备，再读卡!", Toast.LENGTH_LONG).show();
			return;
		}
		
		if (mBlueReaderHelper.open(Blueaddress) == true) {
//		if (true) {
			cardReadButton.setEnabled(false);

			mBlueReaderHelper.registerBlueCard();

			new BlueReadTask().executeOnExecutor(Executors
					.newCachedThreadPool());
		} else {
			Log.e("", "close ok");
			Toast.makeText(this, "请确认蓝牙设备已经连接，再读卡!", Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_clear:
			resetUI();
			break;
		case R.id.action_server:
			Intent intents = new Intent();
			intents.setClass(MainActivity.this, ActServerConfig.class);
			startActivityForResult(intents, 2);
			break;
		case R.id.action_barcode:
			idCardClose();
			Intent barcodeIntent = new Intent(this, BtDemoActivity.class);
			startActivityForResult(barcodeIntent, REQUEST_CONNECT_DEVICE);
			break;
		case R.id.action_blue:
			Intent serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			break;

		}
		return super.onOptionsItemSelected(item);
	}

	public void resetUI() {
		this.cardReadText.setText("");
//		this.codeReadText.setText("");
//		this.codeEdit.setText("");
//		this.cardEdit.setText("");
	}
	@Override
	public void onDestroy() {
		super.onDestroy();

		// mSerialPortHelper.close();
		LogcatFileManager.getInstance().stopLogcatManager();
		finish();
	}

	class MyHandler extends Handler {
		private MainActivity activity;

		MyHandler(MainActivity activity) {
			this.activity = activity;
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ConsantHelper.READ_CARD_SUCCESS:
				cardReadButton.setEnabled(true);
				ButtondefDrawable();
				break;

			case ConsantHelper.SERVER_CANNOT_CONNECT:
				activity.tv_info.setText("服务器连接失败! 请检查网络。");
				cardReadButton.setEnabled(true);
				ButtondefDrawable();
				break;

			case ConsantHelper.READ_CARD_FAILED:
				activity.tv_info.setText("无法读取信息请重试!");
				cardReadButton.setEnabled(true);
				ButtondefDrawable();
				break;

			case ConsantHelper.READ_CARD_WARNING:
				String str = (String) msg.obj;

				if (str.indexOf("card") > -1) {
					activity.tv_info.setText("读卡失败: 卡片丢失,或读取错误!");
				} else {
					String[] datas = str.split(":");

					activity.tv_info.setText("网络超时 错误码: "
							+ Integer.toHexString(new Integer(datas[1])));
				}

				cardReadButton.setEnabled(true);
				ButtondefDrawable();
				break;

			case ConsantHelper.READ_CARD_PROGRESS:

				int progress_value = (Integer) msg.obj;
				activity.tv_info.setText("正在读卡......,进度：" + progress_value
						+ "%");
				break;

			case ConsantHelper.READ_CARD_START:
				activity.resetUI();
				activity.tv_info.setText("开始读卡......");
				break;
			case Error.ERR_CONNECT_SUCCESS:
				String devname = (String) msg.obj;
				activity.tv_info.setText(devname + "连接成功!");
				break;
			case Error.ERR_CONNECT_FAILD:
				String devname1 = (String) msg.obj;
				activity.tv_info.setText(devname1 + "连接失败!");
				break;
			case Error.ERR_CLOSE_SUCCESS:
				activity.tv_info.setText((String) msg.obj + "断开连接成功");
				break;
			case Error.ERR_CLOSE_FAILD:
				activity.tv_info.setText((String) msg.obj + "断开连接失败");
				break;
			case Error.RC_SUCCESS:
				String devname12 = (String) msg.obj;
				activity.tv_info.setText(devname12 + "连接成功!");
				break;

			}
		}

	}

	private void readCardFailed(String strcardinfo) {

		int bret = Integer.parseInt(strcardinfo);
		switch (bret) {
		case -1:
			tv_info.setText("服务器连接失败!");
			break;
		case 1:
			tv_info.setText("读卡失败!");
			break;
		case 2:
			tv_info.setText("读卡失败!");
			break;
		case 3:
			tv_info.setText("网络超时!");
			break;
		case 4:
			tv_info.setText("读卡失败!");
			break;
		case -2:
			tv_info.setText("读卡失败!");
			break;
		case 5:
			tv_info.setText("照片解码失败!");
			break;
		}
	}

	private void readCardSuccess(IdentityCardZ identityCard) {

		if (identityCard != null) {
			/*nameTextView.setText(identityCard.name);
			sexTextView.setText(identityCard.sex);
			folkTextView.setText(identityCard.ethnicity);
			birthTextView.setText(identityCard.birth);
			policyTextView.setText(identityCard.authority);
			addrTextView.setText(identityCard.address);
			validDateTextView.setText(identityCard.period);*/
			cardReadText.setText(identityCard.cardNo);
		}
		tv_info.setText("读取成功!");

	}
	
	private final Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case StateUtils.T_BARCODE:
				tv_info.setText("一维数据： "
						+ msg.obj.toString());
					codeReadText.setText(msg.obj.toString());
				break;
			case StateUtils.T_DCDY:
//				dcdyText.setText("电池电压"+ msg.obj.toString());
				break;
			case StateUtils.T_CDDL:
//				cddlText.setText("充电电流"+ msg.obj.toString());
				break;
			case StateUtils.T_FDDL:
//				fddlText.setText("放电电流"+ msg.obj.toString());
				break;
			case StateUtils.T_SSGH:
//				ssghText.setText("瞬时功耗"+ msg.obj.toString());
				break;
			case StateUtils.T_DEVICE:
//				ssghText.setText("瞬时功耗"+ msg.obj.toString());
				break;
			case StateUtils.MESSAGE_STATE_CHANGE:
				switch (Integer.valueOf(msg.obj.toString())) {
				case StateUtils.STATE_CONNECTED:
					Log.i("","\n已连接..");
					tv_info.setText("一维已连接..");
					mBlueReaderHelper.scanSingle();
					// notifyConnectionSuccess();
					break;
				case StateUtils.STATE_CONNECTING:
					tv_info.setText("启动连接..");
					Log.i("","\n启动连接..");
					break;
				case StateUtils.STATE_LISTEN:
					tv_info.setText("监听连接..");
					Log.i("","\n监听连接..");
					break;
				case StateUtils.STATE_NONE:
					tv_info.setText("无连接..");
					Log.i("","\n无连接..");
					break;
				}
				break;
			case StateUtils.MESSAGE_DEVICE_NAME:
				Log.i("shebei", msg.obj.toString());
				break;
			}
		}
	};
}
