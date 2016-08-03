package edu.rosehulman.rosebotics.controller1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

import edu.rosehulman.rosebotics.controller1.R;
import com.zerokol.views.JoystickView;
import com.zerokol.views.JoystickView.OnJoystickMoveListener;

public class MainActivity extends BlunoLibrary {

	private int mLastSentLeftDutyCycle = 0;
	private int mLastSentRightDutyCycle = 0;
	private Button mButtonScan;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		onCreateProcess(); // onCreate Process by BlunoLibrary

		serialBegin(115200); // set the Uart Baudrate on BLE chip to 115200

		mButtonScan = (Button) findViewById(R.id.buttonScan);
		mButtonScan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				buttonScanOnClickProcess();
			}
		});

		((ToggleButton) findViewById(R.id.armToggle)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String armCommand = String.format("CUSTOM %s\n", ((ToggleButton) v).isChecked() ? "UP" : "DOWN");
				Log.i("BLUNO", armCommand);
				MainActivity.this.serialSend(armCommand);
			}
		});

		((JoystickView) findViewById(R.id.joystickView)).setOnJoystickMoveListener(new OnJoystickMoveListener() {

			@Override
			public void onValueChanged(int x, int y) {
				x = Math.abs(x) < 30 ? 0 : x;
				y = Math.abs(y) < 30 ? 0 : y;

				int leftDutyCycle = y + x;
				int rightDutyCycle = y - x;
				leftDutyCycle = leftDutyCycle > 255 ? 255 : leftDutyCycle;
				rightDutyCycle = rightDutyCycle > 255 ? 255 : rightDutyCycle;
				leftDutyCycle = leftDutyCycle < -255 ? -255 : leftDutyCycle;
				rightDutyCycle = rightDutyCycle < -255 ? -255 : rightDutyCycle;

				if (Math.abs(leftDutyCycle - mLastSentLeftDutyCycle) > 5
						|| Math.abs(rightDutyCycle - mLastSentRightDutyCycle) > 5) {
					String driveCommand = String.format("PWM %d %d\n", leftDutyCycle, rightDutyCycle);
					MainActivity.this.serialSend(driveCommand);
					mLastSentLeftDutyCycle = leftDutyCycle;
					mLastSentRightDutyCycle = rightDutyCycle;
				}
			}
		}, JoystickView.DEFAULT_LOOP_INTERVAL);
	}

	protected void onResume() {
		super.onResume();
		System.out.println("BlUNOActivity onResume");
		onResumeProcess(); // onResume Process by BlunoLibrary
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		onActivityResultProcess(requestCode, resultCode, data); // Bluno's
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPause() {
		super.onPause();
		onPauseProcess(); // onPause Process by BlunoLibrary
	}

	protected void onStop() {
		super.onStop();
		onStopProcess(); // onStop Process by BlunoLibrary
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		onDestroyProcess(); // onDestroy Process by BlunoLibrary
	}

	@Override
	public void onConectionStateChange(connectionStateEnum theConnectionState) {
		switch (theConnectionState) {
		case isConnected:
			mButtonScan.setText("Connected");
			break;
		case isConnecting:
			mButtonScan.setText("Connecting");
			break;
		case isToScan:
			mButtonScan.setText("Scan");
			break;
		case isScanning:
			mButtonScan.setText("Scanning");
			break;
		case isDisconnecting:
			mButtonScan.setText("isDisconnecting");
			break;
		default:
			break;
		}
	}

	@Override
	public void onSerialReceived(String theString) { /* empty */}

}