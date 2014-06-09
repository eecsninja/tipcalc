package com.example.tipcalc;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import org.apache.commons.io.FileUtils;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class TipCalcMainActivity extends Activity {
	// Store references to the various view elements.
	private EditText base_field;
	private EditText num_people_field;
	private SeekBar tip_bar;
	private TextView tip_value_field;

	private final String TIP_FILENAME = "tip.txt";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tip_calc_main);

		// Initialize view element handles.
		tip_bar = (SeekBar) findViewById(R.id.tipValueBar);
		base_field = (EditText) findViewById(R.id.inputValue);
		num_people_field = (EditText) findViewById(R.id.partySizeInputValue);
		tip_value_field = (TextView) findViewById(R.id.tipInputValue);

		// Load the tip value from file if it exists.
		tip_bar.setProgress(loadTipPercentageFromFile());

		// Initialize the stored tip percentage.
		updateTipPercentage();

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
				updateTipPercentage();
				saveTipPercentageToFile(progress);
			}
		};
		tip_bar.setOnSeekBarChangeListener(tip_listener);

		// Listen for text input changes.
		TextWatcher text_watcher = new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				computeAndDisplayTipAmount();
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
		num_people_field.addTextChangedListener(text_watcher);
		base_field.addTextChangedListener(text_watcher);
	}

	private void updateTipPercentage() {
		// The seek bar's position value is equivalent to the tip %age.
		// Update the tip text field.
		tip_value_field.setText("" + tip_bar.getProgress() + "%");

		// Calculate and show tip.
		computeAndDisplayTipAmount();
	}

	private float getInputAmount() {
		float value = 0.0f;
		try {
			value = Float.parseFloat(base_field.getText().toString());
		} catch (NumberFormatException e) {
			System.err.println("Caught exception: " + e.getMessage());
		}
		return value;
	}

	private int getNumPeople() {
		int value = 0;
		try {
			value = Integer.parseInt(num_people_field.getText().toString());
		} catch (NumberFormatException e) {
			System.err.println("Caught exception: " + e.getMessage());
		}
		return value;
	}

	private void computeAndDisplayTipAmount() {
		float base_value = getInputAmount();
		float tip_percentage = tip_bar.getProgress();
		float num_people = getNumPeople();
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

	private int loadTipPercentageFromFile() {
		File tip_file = new File(getFilesDir(), TIP_FILENAME);
		int tip_value = 0;
		try {
			tip_value = Integer.parseInt(FileUtils.readLines(tip_file).get(0));
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
		return tip_value;
	}

	private void saveTipPercentageToFile(int tip_value) {
		File tip_file = new File(getFilesDir(), TIP_FILENAME);
		try {
			FileUtils.writeStringToFile(tip_file, Integer.toString(tip_value));
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
	}
}
