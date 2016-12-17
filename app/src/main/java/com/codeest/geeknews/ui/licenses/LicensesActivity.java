package com.codeest.geeknews.ui.licenses;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.codeest.geeknews.R;
import com.codeest.geeknews.databinding.ActivityLicencesBinding;


public final class LicensesActivity extends AppCompatActivity {

	private static final int LAYOUT = R.layout.activity_licences;

	public static void showInstance(Activity cxt) {
		Intent intent = new Intent(cxt, LicensesActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		ActivityCompat.startActivity(cxt, intent, Bundle.EMPTY);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityLicencesBinding binding = DataBindingUtil.setContentView(this, LAYOUT);
		setSupportActionBar(binding.toolbar);
		final ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			ActivityCompat.finishAfterTransition(this);
		}
		return super.onOptionsItemSelected(item);
	}
}
