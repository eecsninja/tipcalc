package com.example.tipcalc;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class TipCalcMainActivity extends Activity {
	// Store references to the various view elements.
	private EditText base_field;
	private EditText num_people_field;
	private SeekBar tip_bar;
	private TextView tip_value_field;

	// Button arrays.
	private ArrayList<Button> loadButtons;
	private ArrayList<Button> saveButtons;

	// Store the preset values in this file.
	private final String PRESETS_FILENAME = "tipcalc.txt";
	// Number of preset tip values, plus an array of them.
	private final int NUM_PRESETS = 3;
	private ArrayList<Integer> presets;

	private final int MAX_TIP = 100;	// Do not let tip go over 100%.

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tip_calc_main);

		// Initialize view element handles.
		tip_bar = (SeekBar) findViewById(R.id.tipValueBar);
		base_field = (EditText) findViewById(R.id.inputValue);
		num_people_field = (EditText) findViewById(R.id.partySizeInputValue);
		tip_value_field = (TextView) findViewById(R.id.tipInputValue);

		loadButtons = new ArrayList<Button>();
		loadButtons.add((Button) findViewById(R.id.loadButton1));
		loadButtons.add((Button) findViewById(R.id.loadButton2));
		loadButtons.add((Button) findViewById(R.id.loadButton3));
		saveButtons = new ArrayList<Button>();
		saveButtons.add((Button) findViewById(R.id.saveButton1));
		saveButtons.add((Button) findViewById(R.id.saveButton2));
		saveButtons.add((Button) findViewById(R.id.saveButton3));

		// Load preset tip values from file.
		loadPresetsFromFile();

		// Initialize tip bar.
		tip_bar.setProgress(0);

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

	public void onButtonClick(View v) {
		boolean do_load = false;		// Indicates the action to take.
		int button_index = 0;		// Selects a button (0-2).
		switch (v.getId()) {
		// Three preset load buttons.
		case R.id.loadButton1:
			do_load = true;
			button_index = 0;
			break;
		case R.id.loadButton2:
			do_load = true;
			button_index = 1;
			break;
		case R.id.loadButton3:
			do_load = true;
			button_index = 2;
			break;
		// Three preset save buttons.
		case R.id.saveButton1:
			do_load = false;
			button_index = 0;
			break;
		case R.id.saveButton2:
			do_load = false;
			button_index = 1;
			break;
		case R.id.saveButton3:
			do_load = false;
			button_index = 2;
			break;
		default:
			return;
		}

		// Handle loading and saving.
		if (do_load) {
			// Load a preset.
			try {
				tip_bar.setProgress(presets.get(button_index));
			} catch (IndexOutOfBoundsException e) {
				System.err.println("Caught exception: " + e.getMessage());
			}
			computeAndDisplayTipAmount();
		} else {
			// Save as a preset.
			try {
				presets.set(button_index, tip_bar.getProgress());
			} catch (IndexOutOfBoundsException e) {
				System.err.println("Caught exception: " + e.getMessage());
			}
			savePresetsToFile();
			updatePresetButton(loadButtons.get(button_index), presets.get(button_index));
		}
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
		TextView total_per_person_text = (TextView) findViewById(R.id.totalPerPersonValue);
		if (num_people > 0) {
			tip_per_person_text.setText(format.format(tip_value / num_people));
			total_per_person_text.setText(format.format((base_value + tip_value) / num_people));
		} else {
			tip_per_person_text.setText(format.format(0));
			total_per_person_text.setText(format.format(0));
		}
	}

	private void updatePresetButton(Button button, int value) {
		button.setText("" + value + "%");
	}

	private void loadPresetsFromFile() {
		File tip_file = new File(getFilesDir(), PRESETS_FILENAME);
		presets = new ArrayList<Integer>();		// Initialize presets.
		try {
			List<String> file_lines = FileUtils.readLines(tip_file);
			for (int i = 0; i < file_lines.size() && i < NUM_PRESETS; ++i) {
				// Read in the value and fit it within a reasonable range.
				int value = Integer.parseInt(file_lines.get(i));
				if (value < 0)
					value = 0;
				else if (value > MAX_TIP)
					value = MAX_TIP;
				presets.add(value);
				// Update the button text to reflect the tip value.
				updatePresetButton(loadButtons.get(i), value);
			}
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
		// Make sure |presets| has |NUM_PRESETS| elements.
		while (presets.size() < NUM_PRESETS) {
			presets.add(0);
		}
	}

	private void savePresetsToFile() {
		File tip_file = new File(getFilesDir(), PRESETS_FILENAME);
		try {
			FileUtils.writeLines(tip_file, null, presets);
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
	}
}
