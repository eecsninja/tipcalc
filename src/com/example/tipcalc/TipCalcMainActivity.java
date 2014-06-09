package com.example.tipcalc;

import java.text.DecimalFormat;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TipCalcMainActivity extends Activity {

	private final float NO_TIP_SELECTED = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tip_calc_main);

		// Initialize the stored tip percentage.
		tip_percentage = NO_TIP_SELECTED;

		// Listen for text input changes.
		EditText input_field = (EditText) findViewById(R.id.inputValue);
		input_field.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				computeAndDisplayTipAmount(getInputAmount());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	public void onButtonClick(View v) {
		// Determine which button was clicked.
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
		// Calculate and show tip.
		computeAndDisplayTipAmount(getInputAmount());
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

	private void computeAndDisplayTipAmount(float base_value) {
		// Do not display anything (not even a zero value) if the tip
		// percentage hasn't been selected.
		if (tip_percentage == NO_TIP_SELECTED) {
			return;
		}
		float value = base_value * tip_percentage / 100;

		TextView text = (TextView) findViewById(R.id.outputValue);
		// Make sure to display the right decimal format.
		text.setText((new DecimalFormat("0.00")).format(value));
	}

	// Store the last selected tip percentage.
	private float tip_percentage;
}
