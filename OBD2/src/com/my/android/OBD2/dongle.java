package com.my.android.OBD2;

import android.util.Log;


public class dongle {
	
    // Debugging
	protected static final String TAG = "dongle";
    protected static final boolean D = true;
    protected static final boolean DCompute = true;
	
    private static final String ELM_Detected = "ELM Detected";
	
    
    protected dongle Dongle;
    
    
    private String message;
    //private Handler mHandler;
    protected BluetoothOBD Chatthreat;
		
    protected String[] initialize;
    protected int[] sensors;
	
    protected int i = 0;
    protected boolean init = true;
    protected boolean ready = true;
    protected boolean ELM = false;
    
	public dongle(BluetoothOBD _Chatthreat){
		Chatthreat = _Chatthreat;
		
		initialize = new String[3];
		initialize[0] = "AT Z";
		initialize[1] = "AT L0";
		initialize[2] = "AT E0";
		//initialize[3] = "01 00";
		
		sensors = new int[2];
		sensors[0] = 0x010C;
		sensors[1] = 0x010D;
		
		
		
		if(DCompute) Log.e(TAG, "dongle() :" + Integer.toHexString( sensors[1] ));
	}
	
	public void toggle(String read){
		if(DCompute) Log.e(TAG, "toggle :" + message);
		if(read.contains(">")) ready = true;
		message += read;
		
		if (message.contains("\r")) 
		{
			message = message.replaceAll("( )|(>)|(\r)", "");
			computMessage(message);	
			if (message.contains("ELM"))
			{
				Chatthreat.getmConversationArrayAdapter().add(ELM_Detected);
				if(D) Log.e(TAG, ELM_Detected);
				ELM = true;
			} else if (message.contains("AT")) {
				Chatthreat.getmConversationArrayAdapter().add(message);
			}
			message = "";
		}
		if(ready) sendNextCommand();
	}
	
	private void computMessage( String message ){
		if(DCompute) Log.e(TAG, "Compute :" + message);
		//Chatthreat.getmConversationArrayAdapter().add("Message :" + message );
        if (message.matches( "(410C).*" )){
        	if(DCompute) Log.e(TAG, "Speed matches :" + message);
        	String krukas = message.replaceAll( "(410C)", "");
        	double pkrukas = Integer.parseInt(krukas, 16 );
        	double dkrukas = (pkrukas/65535)*16383.75;
        	Chatthreat.getmConversationArrayAdapter().add("krukas :" + dkrukas + " TPM" );
        	//ready = true;
        }
        
        else if (message.matches( "(410D).*" )){
        	if(DCompute) Log.e(TAG, "TMP matches :" + message);
        	String snelheid = message.replaceAll( "(410D)", "" );
        	Chatthreat.getmConversationArrayAdapter().add("snelheid :" + Integer.parseInt( snelheid, 16 ) + " KM/H" );
        	//ready = true;
        }
        else {
        	//Chatthreat.getmConversationArrayAdapter().add( "compute :" + message );
        }
        
	}

	private void sendNextCommand(){
		if(!ELM){
			i = 0;
			init = true;
		}
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
