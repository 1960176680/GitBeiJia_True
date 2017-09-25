package com.example.xy.demo;


import com.example.xy.demo.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import cn.com.senter.helper.ShareReferenceSaver;

public class ActServerConfig extends Activity {
	private final static String SERVER_KEY1 = "CN.COM.SENTER.SERVER_KEY1";
	private final static String PORT_KEY1 = "CN.COM.SENTER.PORT_KEY1";
	
	private final static String SERVER_KEY2 = "CN.COM.SENTER.SERVER_KEY2";
	private final static String PORT_KEY2 = "CN.COM.SENTER.PORT_KEY2";
	
	private final static String SERVER_KEY3 = "CN.COM.SENTER.SERVER_KEY3";
	private final static String PORT_KEY3 = "CN.COM.SENTER.PORT_KEY3";
	
	private final static String SERVER_KEY4 = "CN.COM.SENTER.SERVER_KEY4";
	private final static String PORT_KEY4 = "CN.COM.SENTER.PORT_KEY4";
	
	private final static String Server_Selected = "CN.COM.SENTER.SelIndex";
	
	private String server_address1;
	private int server_port1;
	
	private String server_address2;
	private int server_port2;
	
	private String server_address3;
	private int server_port3;
	
	private String server_address4;
	private int server_port4;		
	
	private String server_address;
	private int server_port;
	private String Server_sel;
	
	EditText server_address_tv1;
	EditText server_port_tv1;
	
	EditText server_address_tv2;
	EditText server_port_tv2;
	
	EditText server_address_tv3;
	EditText server_port_tv3;
	
	EditText server_address_tv4;
	EditText server_port_tv4;	
	
	Button button_ok;
	Button button_cancel;
	
	RadioGroup group;
	RadioButton mradio0;
	RadioButton mradio1;
	RadioButton mradio2;
	RadioButton mradio3;
	
	private long exitTime = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pop_server);
		
		group = (RadioGroup)this.findViewById(R.id.radioGroup1);
		mradio0 = (RadioButton) this.findViewById(R.id.radio0);
		mradio1 = (RadioButton) this.findViewById(R.id.radio1);
		mradio2 = (RadioButton) this.findViewById(R.id.radio2);
		mradio3 = (RadioButton) this.findViewById(R.id.radio3);	
		
		server_address_tv1 = (EditText) findViewById(R.id.server_address1);
		server_port_tv1 = (EditText) 	findViewById(R.id.server_port1);
		
		server_address_tv2 = (EditText) findViewById(R.id.server_address2);
		server_port_tv2 = (EditText) 	findViewById(R.id.server_port2);
		
		server_address_tv3 = (EditText) findViewById(R.id.server_address3);
		server_port_tv3 = (EditText) 	findViewById(R.id.server_port3);
	
		server_address_tv4 = (EditText) findViewById(R.id.server_address4);
		server_port_tv4 = (EditText) 	findViewById(R.id.server_port4);
		
		button_ok =  (Button) 	findViewById(R.id.buttonOk);
		button_cancel =  (Button) 	findViewById(R.id.buttonCancel);	
		
		
	
		if (ShareReferenceSaver.getData(this, Server_Selected).trim().length() < 1) {
			 this.Server_sel = "0";
		} else {
			this.Server_sel = ShareReferenceSaver.getData(this, Server_Selected);
		}

		if (Server_sel.equals("0")) mradio0.setChecked(true);
		if (Server_sel.equals("1")) mradio1.setChecked(true);
		if (Server_sel.equals("2")) mradio2.setChecked(true);
		if (Server_sel.equals("3")) mradio3.setChecked(true);
		
		if (ShareReferenceSaver.getData(this, SERVER_KEY1).trim().length() <= 0) {
			 this.server_address1 = "senter-online.cn";
		} else {
			this.server_address1 = ShareReferenceSaver.getData(this, SERVER_KEY1);
		}
		if (ShareReferenceSaver.getData(this, PORT_KEY1).trim().length() <= 0) {
			this.server_port1 = 60002;
		} else {
			this.server_port1 = Integer.valueOf(ShareReferenceSaver.getData(this, PORT_KEY1));				
		}
		
		server_address_tv1.setText(server_address1);
		server_port_tv1.setText(String.valueOf(server_port1));
		
		if (ShareReferenceSaver.getData(this, SERVER_KEY2).trim().length() <= 0) {
			 this.server_address2 = "";
		} else {
			this.server_address2 = ShareReferenceSaver.getData(this, SERVER_KEY2);
		}
		if (ShareReferenceSaver.getData(this, PORT_KEY2).trim().length()<= 0) {
			this.server_port2 = 10002;
		} else {
			this.server_port2 = Integer.valueOf(ShareReferenceSaver.getData(this, PORT_KEY2));				
		}
		
		server_address_tv2.setText(server_address2);
		server_port_tv2.setText(String.valueOf(server_port2));
				
		if (ShareReferenceSaver.getData(this, SERVER_KEY3).trim().length() <= 0) {
			 this.server_address3 = "";
		} else {
			this.server_address3 = ShareReferenceSaver.getData(this, SERVER_KEY3);
		}
		if (ShareReferenceSaver.getData(this, PORT_KEY3).trim().length() <= 0) {
			this.server_port3 = 10002;
		} else {
			this.server_port3 = Integer.valueOf(ShareReferenceSaver.getData(this, PORT_KEY3));				
		}
		
		server_address_tv3.setText(server_address3);
		server_port_tv3.setText(String.valueOf(server_port3));		
		
		if (ShareReferenceSaver.getData(this, SERVER_KEY4).trim().length() <= 0) {
			 this.server_address4 = "";
		} else {
			this.server_address4 = ShareReferenceSaver.getData(this, SERVER_KEY4);
		}
		if (ShareReferenceSaver.getData(this, PORT_KEY4).trim().length() <= 0) {
			this.server_port4 = 10002;
		} else {
			this.server_port4 = Integer.valueOf(ShareReferenceSaver.getData(this, PORT_KEY4));				
		}
		
		server_address_tv4.setText(server_address4);
		server_port_tv4.setText(String.valueOf(server_port4));			
		
		button_ok.setOnClickListener(mylistener );  
		button_cancel.setOnClickListener(mylistener );  
		
	}


	View.OnClickListener mylistener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.buttonOk:
					buttonok();
		            
					break;
				case R.id.buttonCancel:
					finish();
					break;
				default:
					break;
				}	
			}
		};
	
		
		private void buttonok(){
			saveconfig();
			
			 Log.e("MAIN", "select: "+ server_address);
			 Log.e("MAIN", "select: "+ server_port);
			 
			if (server_address.length() < 0 || server_port == 0){
				Toast.makeText(ActServerConfig.this, "请选择有效的服务器地址!", Toast.LENGTH_LONG).show();
				return;
			}
			
            Intent intent = new Intent();
            intent.putExtra("address", server_address);
            intent.putExtra("port", server_port);

            // Set result and finish this Activity
            setResult(100, intent);
            finish();
		}
		
	private void saveconfig(){
		
		server_address1 = String.valueOf(server_address_tv1.getText());	
		if (server_port_tv1.getText().length()<=0){
			server_port1 = 0;
		}else{
			server_port1 = new Integer(String.valueOf(server_port_tv1.getText()));	
		}
		
		ShareReferenceSaver.saveData(ActServerConfig.this,SERVER_KEY1, server_address1);				
		ShareReferenceSaver.saveData(ActServerConfig.this,PORT_KEY1,String.format("%d", server_port1));
			
		server_address2 = String.valueOf(server_address_tv2.getText());		
		if (server_port_tv2.getText().length() <= 0){
			server_port2 = 0;
		}else{
			server_port2 = new Integer(String.valueOf(server_port_tv2.getText()));	
		}
		
		ShareReferenceSaver.saveData(ActServerConfig.this,SERVER_KEY2, server_address2);				
		ShareReferenceSaver.saveData(ActServerConfig.this,PORT_KEY2,String.format("%d", server_port2));		

		server_address3 = String.valueOf(server_address_tv3.getText());	
		if (server_port_tv3.getText().length() <= 0){
			server_port3 = 0;
		}else{
			server_port3 = new Integer(String.valueOf(server_port_tv3.getText()));	
		}		
		ShareReferenceSaver.saveData(ActServerConfig.this,SERVER_KEY3, server_address3);				
		ShareReferenceSaver.saveData(ActServerConfig.this,PORT_KEY3,String.format("%d", server_port3));		
		
		server_address4 = String.valueOf(server_address_tv4.getText());		
		if (server_port_tv4.getText().length() <= 0){
			server_port4 = 0;
		}else{
			server_port4 = new Integer(String.valueOf(server_port_tv4.getText()));	
		}
		
		ShareReferenceSaver.saveData(ActServerConfig.this,SERVER_KEY4, server_address4);				
		ShareReferenceSaver.saveData(ActServerConfig.this,PORT_KEY4,String.format("%d", server_port4));		
		
		if (mradio0.isChecked()){
			server_address = server_address1;
			server_port = server_port1;
			Server_sel = "0";
		}
		
		if (mradio1.isChecked()){
			server_address = server_address2;
			server_port = server_port2;		
			Server_sel = "1";
			
		}
		
		if (mradio2.isChecked()){
			server_address = server_address3;
			server_port = server_port3;		
			Server_sel = "2";

		}
		
		if (mradio3.isChecked()){
			server_address = server_address4;
			server_port = server_port4;		
			Server_sel = "3";

		}
		
		ShareReferenceSaver.saveData(ActServerConfig.this,Server_Selected, Server_sel);				

		}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {

			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), getString(R.string.idPressAgainToExitTest),
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();

			} else {
				
				
				finish();

			}

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}	

}
