package com.example.xy.mpdemo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.senter.helper.ShareReferenceSaver;
import cn.com.senter.sdkdefault.mediator.impl.s;

import com.example.xy.mpdemo.MainActivity;
import com.example.xy.mpdemo.R;
import com.xy.bluetooth.BluetoothUtils;
import com.xy.bluetooth.StateUtils;
import com.xy.bluetooth.TBlueReader;

public class BtDemoActivity extends Activity {

	private BluetoothAdapter bluetoothAdapter;
	private String v;
	private TextView receiveText;
	private TextView dcdyText;
	private TextView fddlText;
	private TextView cddlText;
	private TextView ssghText;
	private EditText dateText;
	private ListView mListView;
	private Button scanningButton;
	private Button scanning1Button;
	private Button queryDCDLButton;
	private Button scanStateButton;
	private Button scanDeviceStateButton;
	private SimpleAdapter listItemAdapter;
	ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
	private StringBuffer buffer = new StringBuffer();

	private Date date;
	// 计时器

	private TextView countText;
	private EditText timeEdit;
	int count = 0;
	boolean js = false;
	int dateCount = 0;
	Timer timer = new Timer();
	//计时扫码次数
	TimerTask task = new TTimerTask();
	class TTimerTask extends TimerTask {
		
		@Override
		public void run() {
			dateCount--;
			if(dateCount==0){
				js = false;
//				Toast.makeText(BtDemoActivity.this, "计时结束"+count, Toast.LENGTH_LONG).show();
				jsHandler.obtainMessage(1, count).sendToTarget();
				task.cancel();
			}else
				jsHandler.obtainMessage(1, dateCount).sendToTarget();
		}

		@Override
		public boolean cancel() {
			// TODO Auto-generated method stub
			Log.i("关闭", String.valueOf(count));
			
			return super.cancel();
		}
	};

	Timer timer2 = new Timer();
	//延时执行（长按连扫）
	TimerTask task1 = new CTimerTask();
	
	class CTimerTask extends TimerTask {
		
		@Override
		public void run() {
			//修改按钮状态 并关闭连扫
			jsHandler.obtainMessage(2, true).sendToTarget();
			task1.cancel();
		}
		@Override
		public boolean cancel() {
			// TODO Auto-generated method stub
			Log.i("关闭", String.valueOf(count));
			
			return super.cancel();
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initData();

		countText = (TextView) findViewById(R.id.countText);
		timeEdit = (EditText) findViewById(R.id.timeEdit);
		// 计时
		findViewById(R.id.timeStartButton).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						countText.setText("开始计数");
						String t = timeEdit.getText().toString();
						Log.i("关闭t", t);
						int time = Integer.valueOf(t);
						Log.i("time闭t", String.valueOf(time));
						dateCount = time;
						count = 0;
						js = true;
						//每1s执行一次
						task = new TTimerTask();
						timer.schedule(task,0,1000);
					}
				});
		
		receiveText = (TextView) findViewById(R.id.receiveText);
		dcdyText = (TextView) findViewById(R.id.dcdyText);
		ssghText = (TextView) findViewById(R.id.ssghText);
		fddlText = (TextView) findViewById(R.id.fddlText);
		cddlText = (TextView) findViewById(R.id.cddlText);
		dateText = (EditText) findViewById(R.id.settingDatetText);
		mListView = (ListView) findViewById(R.id.ListView01);
		scanningButton = (Button) findViewById(R.id.scanningButton);
		scanning1Button = (Button) findViewById(R.id.scanning1Button);
		queryDCDLButton = (Button) findViewById(R.id.queryDCDLButton);
		scanStateButton = (Button) findViewById(R.id.scanStateButton);
		scanDeviceStateButton = (Button) findViewById(R.id.scanDeviceStateButton);
		// 清空
		findViewById(R.id.cleanButton).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						buffer = new StringBuffer();
						receiveText.setText("");
						listItem = new ArrayList<HashMap<String, String>>();
						// 添加并且显示
						mListView.setAdapter(listItemAdapter);
					}
				});
		/**
		 * 连接
		 */
		findViewById(R.id.lableButton).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// 启动蓝牙数据连接服务
						String macAddress = ShareReferenceSaver
								.getData(BtDemoActivity.this,
										StateUtils.BLUE_ADDRESSKEY);
						if (macAddress == "") {
							Toast.makeText(BtDemoActivity.this, "请选择蓝牙设备!",
									Toast.LENGTH_LONG).show();
							return;
						}
						BluetoothDevice device = bluetoothAdapter
								.getRemoteDevice(macAddress);
						blueReader.connect(device, true,
								StateUtils.TK_TYPE_READ);
					}
				});
		// 断开
		findViewById(R.id.emptyButton).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						blueReader.stop();
					}
				});
		// 返回
		findViewById(R.id.returnButton).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						upPage();
					}
				});
		// 下一界面
		findViewById(R.id.bbhButton).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// nextPage();
				nextIdCardPage();
			}
		});
		// 切换扫描模式
				findViewById(R.id.switchButton).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// nextPage();
								if(scanningButton.getVisibility() == View.GONE){
									scanning1Button.setVisibility(View.GONE);
									scanningButton.setVisibility(View.VISIBLE);
								}else{
									scanningButton.setVisibility(View.GONE);
									scanning1Button.setVisibility(View.VISIBLE);
								}
							}
						});
		// 单次扫码
		scanningButton.setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// nextPage();
						blueReader.scanSingle();
					}
				});
		// 点按连续扫码
		scanning1Button.setOnTouchListener(
				new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							scanning1Button.setEnabled(true);
							date = new Date();
							// 开始连续扫描
							blueReader.scanDouble();
						}
						if (event.getAction() == MotionEvent.ACTION_UP) {
							// 关闭连续扫描-需要关闭扫描头的指令
							Date d = new Date();
							long t = d.getTime()-date.getTime();
							Log.i("shijian",""+t);
							task1 = new CTimerTask();
							if(t<500){
//								scanning1Button.setEnabled(false);
								//延时500毫秒
								timer2.schedule(task1,500-t);
							}else{
								timer2.schedule(task1,0);
							}
						}
						return true;
					}

				});
		// 扫码头休眠
		findViewById(R.id.scanStandbyButton).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// nextPage();
						blueReader.scanStandby();
					}
				});
		// 连续扫码
		findViewById(R.id.scanning2Button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// nextPage();
						blueReader.scanDouble();
					}
				});
		// 身份证
		findViewById(R.id.scanningCardButton).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// nextPage();
						blueReader.idCard();
					}
				});
		// 设置时间
		findViewById(R.id.settingDateButton).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// nextPage();
						String number = dateText.getText().toString();
						if (number.equals("")) {
							Toast.makeText(BtDemoActivity.this, "请正确输入时间!",
									Toast.LENGTH_LONG).show();
							return;
						}
						int num = Integer.valueOf(number);
						if (num == 0) {
							Toast.makeText(BtDemoActivity.this, "请正确输入时间!",
									Toast.LENGTH_LONG).show();
							return;
						}
						if (num > 255) {
							Toast.makeText(BtDemoActivity.this, "请正确输入时间!",
									Toast.LENGTH_LONG).show();
							return;
						}
						blueReader.settingScan(num);
					}
				});
		// 电池电量
		queryDCDLButton.setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						buffer = new StringBuffer();
						receiveText.setText("");
						blueReader.queryElectricity();
					}
				});
		// 扫码头状态
		scanStateButton.setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						buffer = new StringBuffer();
						receiveText.setText("");
						blueReader.queryScanState();
					}
				});

		// 设备状态
		scanDeviceStateButton.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						buffer = new StringBuffer();
						receiveText.setText("");
						blueReader.queryDeviceState();
					}
				});
		
	}

	private void upPage() {
		stopAll();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	private void nextIdCardPage() {
		stopAll();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	private void stopAll() {
		// TODO Auto-generated method stub
		blueReader.stop();
	}

	private TBlueReader blueReader;

	private void initData() {

		if (bluetoothAdapter == null) {
			bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		}
		String macAddress = ShareReferenceSaver.getData(this,
				StateUtils.BLUE_ADDRESSKEY);
		if (macAddress == "") {
			Toast.makeText(BtDemoActivity.this, "请选择蓝牙设备!", Toast.LENGTH_LONG)
					.show();
			return;
		}
		Log.i("macAddress", macAddress);
		// 启动蓝牙数据连接服务
		BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);
		blueReader = new TBlueReader(mHandler, device);
		blueReader.initData();

	}

	private final Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			//记录数目
			if(js)
				count++;
			switch (msg.what) {
			case StateUtils.T_BARCODE:
				receiveText.append("\n数据： " + msg.obj.toString());
				break;
			case StateUtils.T_DCDY:
				dcdyText.setText("电池电压" + msg.obj.toString());
				break;
			case StateUtils.T_CDDL:
				cddlText.setText("充电电流" + msg.obj.toString());
				break;
			case StateUtils.T_FDDL:
				fddlText.setText("放电电流" + msg.obj.toString());
				break;
			case StateUtils.T_SSGH:
				ssghText.setText("瞬时功耗" + msg.obj.toString());
				break;
			case StateUtils.T_DCDL:
				queryDCDLButton.setText("电池电量"+msg.obj.toString());
				break;
			case StateUtils.T_BARSTATE:
				scanStateButton.setText("扫码头状态"+msg.obj.toString());
			case StateUtils.T_DEVICE:
				//00 休眠，02 身份证，01 连续扫码，03单次扫描（手机指令状态）
				switch (msg.obj.toString()) {
				case "00":
					scanDeviceStateButton.setText("设备状态:休眠");
					break;

				case "01":
					scanDeviceStateButton.setText("设备状态:连续扫码");
					break;
				case "02":
					scanDeviceStateButton.setText("设备状态:身份证");
					break;
				case "03":
					scanDeviceStateButton.setText("设备状态:单次扫码");
					break;
				default:
					scanDeviceStateButton.setText("设备状态");
					break;
				}
				scanStateButton.setText("设备状态"+msg.obj.toString());
				break;
			case StateUtils.MESSAGE_STATE_CHANGE:
				switch (Integer.valueOf(msg.obj.toString())) {
				case StateUtils.STATE_CONNECTED:
					receiveText.append("\n已连接..");
					// notifyConnectionSuccess();
					break;
				case StateUtils.STATE_CONNECTING:
					receiveText.append("\n启动连接..");
					break;
				case StateUtils.STATE_LISTEN:
					receiveText.append("\n监听连接..");
					break;
				case StateUtils.STATE_NONE:
					receiveText.append("\n无连接..");
					break;
				}
				break;
			case StateUtils.MESSAGE_DEVICE_NAME:
				Log.i("shebei", msg.obj.toString());
				break;
			}
		}
	};
	private final Handler jsHandler = new Handler() {

		public void handleMessage(Message msg) {
			//记录数目
			switch (msg.what) {
			case 1:
				if(dateCount==0)
					countText.setText(msg.obj.toString());
				else
					countText.setText(msg.obj.toString());
				break;
			case 2:
				//关闭扫描
				blueReader.scanStandby();
				//判断按钮状态
				if(!scanning1Button.isEnabled())
					scanning1Button.setEnabled(true);
				break;
			}
		}
	};
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (blueReader != null) {
			blueReader.stop();
		}
	}

	public static class Utils {   
	      private static long lastClickTime;   
	      public static boolean isFastDoubleClick() {   
	          long time = System.currentTimeMillis();   
	          long timeD = time - lastClickTime;   
	          if ( 0 < timeD && timeD < 500) {       //500毫秒内按钮无效，这样可以控制快速点击，自己调整频率
	              return true;      
	          }      
	          lastClickTime = time;      
	          return false;      
	      }   
	  } 
}
