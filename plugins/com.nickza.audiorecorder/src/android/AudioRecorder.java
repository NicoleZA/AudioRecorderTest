package com.nickza.audiorecorder;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.widget.LinearLayout;
import android.os.Bundle;
import android.os.Environment;
import android.content.Context;
import android.media.MediaRecorder;
import android.media.MediaPlayer;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.io.IOException;



public class AudioRecorder extends CordovaPlugin {
	protected void pluginInitialize() {
	}

	private MediaRecorder recorder = null;
    private MediaPlayer   player = null;
    private CallStateListener listener = null;
  
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) 
	  throws JSONException {
	if (action.equals("alert")) {
	  alert(args.getString(0), args.getString(1), args.getString(2), callbackContext);
	  return true;
	}
	else if (action.equals("autoRecording")) {
	  autoRecording(callbackContext);
	  return true;
	}
	else if (action.equals("startRecording")) {
	  startRecording(args.getString(0), callbackContext);
	  return true;
	}
	else if (action.equals("stopRecording")) {
	  stopRecording(callbackContext);
	  return true;
	}
	else if (action.equals("startPlaying")) {
	  startPlaying(args.getString(0), callbackContext);
	  return true;
	}
	else if (action.equals("stopPlaying")) {
	  stopPlaying(callbackContext);
	  return true;
	}
	return false;
	}

	private synchronized void alert(final String title, 
                                  final String message, 
                                  final String buttonLabel, 
                                  final CallbackContext callbackContext) {
		new AlertDialog.Builder(cordova.getActivity())
		.setTitle(title)
		.setMessage(message)
		.setCancelable(false)
		.setNeutralButton(buttonLabel, new AlertDialog.OnClickListener() {
		  public void onClick(DialogInterface dialogInterface, int which) {
			dialogInterface.dismiss();
			callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, 0));
		  }
		})
		.create()
		.show();
	}

    private synchronized void startRecording(final String filename, 
                                    final CallbackContext callbackContext) {
		
		String file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename;
	//		Context context = cordova.getActivity().getApplicationContext();
	//		String file = context.getFilesDir().getPath().toString() + "/" + filename;
		try {
	        recorder = new MediaRecorder();
			recorder.reset();
			recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
			recorder.setOutputFile(file);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.prepare();
			recorder.start();
		} catch (IOException e) {
			alert("IO Error",e.getMessage(),"Ok", callbackContext);
		} catch (IllegalStateException e) {
			alert("State Error",e.getMessage(),"Ok", callbackContext);
    	}
	}

    private synchronized void stopRecording(final CallbackContext callbackContext) {
		recorder.stop(); 
		recorder.release(); 
        recorder = null;
	}
  
    private synchronized void startPlaying(final String filename, 
                                    final CallbackContext callbackContext) {
		
		try {
			String file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename;
			player = new MediaPlayer();
            player.setDataSource(file);
            player.prepare();
            player.start();
		} catch (IOException e) {
			alert("IO Error",e.getMessage(),"Ok", callbackContext);
		} catch (IllegalStateException e) {
			alert("State Error",e.getMessage(),"Ok", callbackContext);
    	}
	}

    private synchronized void stopPlaying(final CallbackContext callbackContext) {
		player.release(); 
        player = null;
	}

	private synchronized void autoRecording(final CallbackContext callbackContext) {
	//	alert("Message","autoRecording","Ok", callbackContext);
        if (listener == null) {
	//	alert("Message","set Listener","Ok", callbackContext);
            listener = new CallStateListener();
            TelephonyManager TelephonyMgr = (TelephonyManager) cordova.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            TelephonyMgr.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }
		listener.setCallbackContext(callbackContext);
	}

}

class CallStateListener extends PhoneStateListener {

    private CallbackContext callbackContext;

    public void setCallbackContext(CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
    }

    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);

        if (callbackContext == null) return;

        String msg = "";

        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
            msg = "IDLE";
            break;

            case TelephonyManager.CALL_STATE_OFFHOOK:
            msg = "OFFHOOK," + incomingNumber;
            break;

            case TelephonyManager.CALL_STATE_RINGING:
            msg = "RINGING";
            break;
        }

        PluginResult result = new PluginResult(PluginResult.Status.OK, msg);
        result.setKeepCallback(true);

        callbackContext.sendPluginResult(result);
    }
	

}