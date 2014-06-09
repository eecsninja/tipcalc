package com.example.tipcalc;

import java.text.DecimalFormat;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class TipCalcMainActivity extends Activity {

	private final float NO_TIP_SELECTED = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tip_calc_main);

		// Initialize the stored tip percentage.
		tip_percentage = NO_TIP_SELECTED;

		// Listen for text input changes.
		TextWatcher text_watcher = new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				computeAndDisplayTipAmount(getInputAmount(), getNumPeople());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		};
		// Use the same listener for both text input fields.
		EditText input_field = (EditText) findViewById(R.id.inputValue);
		input_field.addTextChangedListener(text_watcher);
		EditText num_people_field = (EditText) findViewById(R.id.partySizeInputValue);
		num_people_field.addTextChangedListener(text_watcher);

		// Listen for seek bar changes.
		SeekBar.OnSeekBarChangeListener tip_listener = new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// The seek bar's position value is equivalent to the tip %age.
				tip_percentage = (float) progress;
				// Update the tip text field.
				TextView tip_value_field = (TextView) findViewById(R.id.tipInputValue);
				tip_value_field.setText("" + progress + "%");

				// Calculate and show tip.
				computeAndDisplayTipAmount(getInputAmount(), getNumPeople());
			}
		};
		SeekBar tip_bar = (SeekBar) findViewById(R.id.tipValueBar);
		tip_bar.setOnSeekBarChangeListener(tip_listener);
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

	private int getNumPeople() {
		EditText input_field = (EditText) findViewById(R.id.partySizeInputValue);
		int value = 0;
		try {
			value = Integer.parseInt(input_field.getText().toString());
		} catch (NumberFormatException e) {
			System.err.println("Caught exception: " + e.getMessage());
		}
		return value;
	}

	private void computeAndDisplayTipAmount(float base_value, float num_people) {
		// Do not display anything (not even a zero value) if the tip
		// percentage hasn't been selected.
		if (tip_percentage == NO_TIP_SELECTED) {
			return;
		}
		float tip_value = base_value * tip_percentage / 100;

		// Make sure to display the right decimal format.
		DecimalFormat format = new DecimalFormat("0.00");

		// Display the total tip.
		TextView tip_text = (TextView) findViewById(R.id.outputValue);
		tip_text.setText(format.format(tip_value));

		// Display the tip per person.
		TextView tip_per_person_text = (TextView) findViewById(R.id.numPeopleOutputValue);
		if (num_people > 0) {
			tip_per_person_text.setText(format.format(tip_value / num_people));
		} else {
			tip_per_person_text.setText(format.format(0));
		}
	}

	// Store the last selected tip percentage.
	private float tip_percentage;
}
