package com.example.tipcalc;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class TipCalcMainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tip_calc_main);
	}

	public void onButtonClick(View v) {
		// Determine which button was clicked.
		int tip_percentage = 0;
		switch (v.getId()) {
		case R.id.button10:
			tip_percentage = 10;
			break;
		case R.id.button15:
			tip_percentage = 15;
			break;
		case R.id.button20:
			tip_percentage = 20;
			break;
		default:
			tip_percentage = 0;
			break;
		}
		// Calculate tip:
		float tip_value = getInputAmount() * tip_percentage / 100;
		// TODO: Display tip later. For now, toast the amount.
		Toast.makeText(getApplicationContext(), "" + tip_value, Toast.LENGTH_SHORT)
			.show();
	}

	private float getInputAmount() {
		EditText input_field = (EditText) findViewById(R.id.inputValue);
		float value = 0.0f;
		try {
			value = Float.parseFloat(input_field.getText().toString());
		} catch (NumberFormatException e) {
			System.err.println("Caught exception: " + e.getMessage());
		}
		return value;
	}
}
