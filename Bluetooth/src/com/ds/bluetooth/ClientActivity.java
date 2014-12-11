package com.ds.bluetooth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import java.io.IOException;

import com.ds.bluetooth.MyChartView.Mstyle;
import com.ds.bluetoothUtil.BluetoothClientConnThread;
import com.ds.bluetoothUtil.BluetoothClientService;
import com.ds.bluetoothUtil.BluetoothTools;
import com.ds.bluetoothUtil.TransmitBean;
import com.ds.bluetooth.Tools;

public class ClientActivity extends Activity {
	MyChartView tu;
	Timer mTimer =new Timer();
	HashMap<Double, Double> map;
	Double key=8.0;
	Double value=0.0;
	Tools tool=new Tools();
    private static final int REQUEST_CONNECT_DEVICE = 1;
	private double mainnum=1;
	private TextView serversText;
	private EditText chatEditText;
	private Button drawBtn;
	private Button startSearchBtn;
	private Button selectDeviceBtn;
	private EditText mOutEditText;
	private Button mSendButton;
	private TextView sum;
	private int count=0;
	private int number;
	public static String message;
	private StringBuffer mOutStringBuffer;
	
	private BluetoothDevice device;
	
	//广播接收器
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		
		@SuppressLint("NewApi") @Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			 if (BluetoothTools.ACTION_CONNECT_SUCCESS.equals(action)) {
				//连接成功
				serversText.setText("连接成功");
				drawBtn.setEnabled(true);
				
			} else if (BluetoothTools.ACTION_DATA_TO_GAME.equals(action)) {
				//接收数据
				String msg =message;
	               try{  
	                 number=Integer.parseInt(msg);
	                 msg = device.getName()+": "+msg + "毫升\r\n";
	               }  
	               catch(Exception ex){
	            	   number=0;
	            	   msg = device.getName()+": "+msg+"\r\n";} 
				count +=number;
				drawmessage((double)number);
				chatEditText.append(msg);
			    sum.setText("用水量总和： "+count+"毫升");
			} 
		}
	};
	
	
	@Override
	protected void onStart() {
		//清空设备列表
		
		//开启后台service
		Intent startService = new Intent(ClientActivity.this, BluetoothClientService.class);
		startService(startService);
		
		//注册BoradcasrReceiver
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothTools.ACTION_DATA_TO_GAME);
		intentFilter.addAction(BluetoothTools.ACTION_CONNECT_SUCCESS);
		registerReceiver(broadcastReceiver, intentFilter);
		
		super.onStart();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client);
		
		tu= (MyChartView)findViewById(R.id.menulist);
		tu.SetTuView(map,50,10,"min","ml",true);
		map=new HashMap<Double, Double>();
		map.put(0.0, (double) 0);

    	tu.setTotalvalue(50);
    	tu.setPjvalue(10);
    	tu.setMap(map);
		tu.setMargint(20);
		tu.setMarginb(50);
		tu.setMstyle(Mstyle.Line);
		mOutStringBuffer = new StringBuffer("");
		serversText = (TextView)findViewById(R.id.clientServersText);
		chatEditText = (EditText)findViewById(R.id.clientChatEditText);
		drawBtn = (Button)findViewById(R.id.clientDrawMsgBtn);
		startSearchBtn = (Button)findViewById(R.id.startSearchBtn);
		selectDeviceBtn = (Button)findViewById(R.id.selectDeviceBtn);
		sum=(TextView)findViewById(R.id.sum);
        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton = (Button) findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                TextView view = (TextView) findViewById(R.id.edit_text_out);
                String message = view.getText().toString();
                sendMessage(message);
            }
        });
		drawBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					map.clear();
					mainnum=1;
					tu.morenum=0;
					map.put(0.0,0.0);
					tu.postInvalidate();
			}	
		});
		startSearchBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//开始搜索
	            Intent serverIntent = new Intent(ClientActivity.this, DeviceListActivity.class);
	            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			}
		});
		
		selectDeviceBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					try {
						BluetoothClientConnThread.socket.close();
						serversText.setText("断开成功");
			} catch (IOException e) {
				e.printStackTrace();
			}
				
			}
		});
	}

	@Override
	protected void onStop() {
		//关闭后台Service
		Intent startService = new Intent(BluetoothTools.ACTION_STOP_SERVICE);
		sendBroadcast(startService);
		
		unregisterReceiver(broadcastReceiver);
		super.onStop();
	}
	private void drawmessage(double x){
		mainnum++;
		if(mainnum<8)
		{	map.put(mainnum, x);
			tu.postInvalidate();
		}
		else					
		{
		tu.morenum ++;
		randmap(map, x);}
	}
	private void randmap(HashMap<Double, Double> mp,Double d)
	{
		ArrayList<Double> dz=tool.getintfrommap(mp);
		Double[] dvz=new Double[mp.size()];
		int t=0;
		@SuppressWarnings("rawtypes")
		Set set= mp.entrySet();   
        @SuppressWarnings("rawtypes")
		Iterator iterator = set.iterator();
		 while(iterator.hasNext())
		{  
			@SuppressWarnings("rawtypes")
			Map.Entry mapentry  = (Map.Entry)iterator.next();   
			dvz[t]=(Double)mapentry.getValue();
			t+=1;
		} 
		 for(int j=0;j<dz.size()-1;j++)
		 {
			 mp.put(dz.get(j), mp.get(dz.get(j+1)));
		 }
		 mp.put((Double)dz.get(mp.size()-1), d);
		 tu.postInvalidate();
	}
    private TextView.OnEditorActionListener mWriteListener =
            new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                // If the action is a key-up event on the return key, send the message
                if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                    String message = view.getText().toString();
                    sendMessage(message);
                }
                return true;
            }
        };
        private void sendMessage(String message) {
            // Check that there's actually something to send
            if (message.length() > 0) {
                // Get the message bytes and tell the BluetoothChatService to write
				Intent sendDataIntent = new Intent(BluetoothTools.ACTION_DATA_TO_SERVICE);
				sendDataIntent.putExtra(BluetoothTools.DATA, message);
				sendBroadcast(sendDataIntent);
	            mOutStringBuffer.setLength(0);
	            mOutEditText.setText(mOutStringBuffer);
				message="我: "+message+"\r\n";
                chatEditText.append(message);
            }
        }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                // Get the device MAC address
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                // Get the BLuetoothDevice object
                device =BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
				Intent selectDeviceIntent = new Intent(BluetoothTools.ACTION_SELECTED_DEVICE);//选择动作
				selectDeviceIntent.putExtra(BluetoothTools.DEVICE, device);
				sendBroadcast(selectDeviceIntent);
            }
            break;
        }
    }
    
}
