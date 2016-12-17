package com.codeest.geeknews.ui.licenses;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.codeest.geeknews.R;
import com.codeest.geeknews.databinding.ChildBinding;
import com.codeest.geeknews.databinding.FragmentLicencesBinding;
import com.codeest.geeknews.databinding.GroupBinding;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static android.databinding.DataBindingUtil.inflate;


public final class LicensesFragment extends AppCompatDialogFragment {
	private static final int LAYOUT = R.layout.fragment_licences;
	private static final int ID_LOAD_LICENCES_TASK = 0x54;
	private static final String LICENCES_LIST_JSON = "licenses-list.json";
	private FragmentLicencesBinding mBinding;
	private static final Gson gson = new Gson();

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (hidden) {
			collapseExpandedGroup();
		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mBinding = DataBindingUtil.inflate(inflater, LAYOUT, container, getShowsDialog());
		return mBinding.getRoot();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		loadLicences();
	}

	private void collapseExpandedGroup() {
		ExpandableListAdapter adapter = mBinding.licencesList.getExpandableListAdapter();
		if (adapter == null) {
			return;
		}
		for (int groupPos = 0, cnt = adapter.getGroupCount();
				groupPos < cnt;
				groupPos++) {
			if (mBinding.licencesList.isGroupExpanded(groupPos)) {
				mBinding.licencesList.collapseGroup(groupPos);
			}
		}
	}

	private void loadLicences() {
		getLoaderManager().initLoader(ID_LOAD_LICENCES_TASK, Bundle.EMPTY, new LoaderManager.LoaderCallbacks<Licenses>() {
			@Override
			public Loader<Licenses> onCreateLoader(int id, Bundle args) {
				return new AsyncTaskLoader<Licenses>(getContext()) {
					@Override
					public Licenses loadInBackground() {
						Licenses licenses;
						try {
							licenses = gson.fromJson(new InputStreamReader(getContext().getAssets()
							                                                                          .open(LICENCES_LIST_JSON)), Licenses.class);
						} catch (IOException e) {
							licenses = null;
						}
						return licenses;
					}
				};
			}

			@Override
			public void onLoadFinished(Loader<Licenses> loader, Licenses licenses) {
				if (licenses != null) {
					LicencesListAdapter adapter = new LicencesListAdapter(licenses);
					mBinding.licensesCountTv.setText(getString(R.string.software_licenses_count, licenses.getLicenses().size(), adapter.getGroupCount()));
					ExpandableListView expListView = mBinding.licencesList;
					if (expListView != null) {
						expListView.setAdapter(adapter);
					}
				}
			}

			@Override
			public void onLoaderReset(Loader<Licenses> loader) {

			}
		}).forceLoad();
	}


	private static final class LicencesListAdapter extends BaseExpandableListAdapter {
		private static final String LICENCES_BOX = "licenses-box";
		private static final String COPYRIGHT_HOLDERS = "<copyright holders>";
		private static final String YEAR = "<year>";
		private static final String LICENCE_BOX_LOCATION_FORMAT = "%s/%s.txt";
		private static final int LAYOUT_GROUP = R.layout.list_licences_item_group;
		private static final int LAYOUT_CHILD = R.layout.list_licences_item_child;
		private final Licenses mLicenses;
		private final int mLicencesCount;
		private final ArrayMap<Library, Pair<String, String>> mLibraryList = new ArrayMap<>();//Pair contains licence's name and description.
		private final ArrayMap<String, String> mLicenceContentList = new ArrayMap<>();

		public LicencesListAdapter(Licenses licenses) {
			mLicenses = licenses;
			List<License> licencesList = mLicenses.getLicenses();
			for (License license : licencesList) {
				List<Library> libraries = license.getLibraries();
				for (Library library : libraries) {
					mLibraryList.put(library, new Pair<>(license.getName(), license.getDescription()));
				}
			}
			mLicencesCount = mLibraryList.size();
		}

		private static String loadLicencesContent(Context cxt, @NonNull final String licenceName) {
			String licenceLocation = String.format(LICENCE_BOX_LOCATION_FORMAT, LICENCES_BOX, licenceName);
			String licenceContent;
			try {
				licenceContent = LicensesUtils.readTextFile(cxt.getAssets()
				                                       .open(licenceLocation));
			} catch (IOException e) {
				licenceContent = null;
			}
			return licenceContent;
		}

		public int getGroupCount() {
			return mLicencesCount;
		}

		public int getChildrenCount(int groupPosition) {
			return 1;
		}

		public Object getGroup(int groupPosition) {
			throw new UnsupportedOperationException("This adapter doesn't need group.");
		}

		public Object getChild(int groupPosition, int childPosition) {
			throw new UnsupportedOperationException("This adapter doesn't need children.");
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition * childPosition;
		}

		public boolean hasStableIds() {
			return true;
		}

		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			GroupBinding binding;
			if (convertView == null) {
				binding = inflate(LayoutInflater.from(parent.getContext()), LAYOUT_GROUP, parent, false);
				convertView = binding.getRoot();
				convertView.setTag(binding);
			} else {
				binding = (GroupBinding) convertView.getTag();
			}
			Library library = mLibraryList.keyAt(groupPosition);
			Pair<String, String> nameDesc = mLibraryList.get(library);

			binding.setTitle(library.getName());
			binding.setDescription(nameDesc.second);
			binding.executePendingBindings();
			return convertView;
		}

		public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			ChildBinding binding;
			if (convertView == null) {
				binding = inflate(LayoutInflater.from(parent.getContext()), LAYOUT_CHILD, parent, false);
				convertView = binding.getRoot();
				convertView.setTag(binding);
			} else {
				binding = (ChildBinding) convertView.getTag();
			}

			String content;
			Library library = mLibraryList.keyAt(groupPosition);
			Pair<String, String> nameDesc = mLibraryList.get(library);
			//Licence content will be read from disk firstly and from memory next time.
			if (mLicenceContentList.get(nameDesc.first) == null) {
				content = loadLicencesContent(parent.getContext(), nameDesc.first);
				mLicenceContentList.put(nameDesc.first, content);
			} else {
				content = mLicenceContentList.get(nameDesc.first);
			}

			if (content.contains(YEAR) && content.contains(COPYRIGHT_HOLDERS)) {
				content = content.replace(YEAR,
				                          TextUtils.isEmpty(library.getCopyright()) ?
				                          "" :
				                          library.getCopyright())
				                 .replace(COPYRIGHT_HOLDERS,
				                          TextUtils.isEmpty(library.getOwner()) ?
				                          "" :
				                          library.getOwner());
			}
			binding.getRoot()
			       .setOnClickListener(new View.OnClickListener() {
				       @Override
				       public void onClick(View view) {
//					       EventBus.getDefault()
//					               .post(new CloseExpandableListGroupEvent(groupPosition));
				       }
			       });
			binding.setContent(content);
			binding.executePendingBindings();
			return convertView;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}


	}
}
