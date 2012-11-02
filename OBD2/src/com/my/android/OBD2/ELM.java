package com.my.android.OBD2;

import android.util.Log;

public class ELM extends dongle{
	
	public ELM(BluetoothOBD _Chatthreath) {
		super(_Chatthreath);
		// TODO Auto-generated constructor stub
	}
		
	private void sendNextCommand(){
		if(init){
    		if (initialize.length <= i){
    			init = false;
    			i = 0;
    		} else {
				if(DCompute) Log.e(TAG, "sendNextCommand initialize:" + initialize[i]);
				Chatthreat.sendMessage(initialize[i]);
				ready = false;
    		}
			
		} else {
			if (sensors.length <= i) {
				i = 0;
	               try {
	                    synchronized (this) {
	                        wait(1000); 
	                    }
	                } catch (InterruptedException e) {
	                    Log.d(TAG, "Waiting didnt work!!");
	                    e.printStackTrace();
	                }
			}
    			int nsensor = sensors[i];    			
    			if(DCompute) Log.e(TAG, "sendNextCommand sensor" + i + " :" + Integer.toHexString( nsensor ));
    			Chatthreat.sendMessage( "0" + Integer.toHexString( nsensor ) );
    			ready = false;
		}
		
		i++;
	}
	
		
}
