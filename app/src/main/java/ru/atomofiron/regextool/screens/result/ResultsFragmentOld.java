package ru.atomofiron.regextool.screens.result;


/*
public class ResultsFragmentOld extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

	private Activity ac;
	private ListView fragmentView;
	private ResultAdapter listAdapter;
	private SnackbarHelper snackbarHelper;

	private ArrayList<Result> resultsList;
	private String startMessage = null;

	public ResultsFragmentOld() {}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		if (resultsList != null)
			inflater.inflate(R.menu.results, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.share) {
			StringBuilder data = new StringBuilder();
			for (Result result : resultsList)
				data.append(result.toMarkdown());
			String title = "regex_finder_results.md.txt";

			Intent intent = new Intent(Intent.ACTION_SEND)
					.setType("text/plain")
					.putExtra(Intent.EXTRA_SUBJECT, title)
					.putExtra(Intent.EXTRA_TITLE, title)
					.putExtra(Intent.EXTRA_TEXT, data.toString());

			if (intent.resolveActivity(ac.getPackageManager()) != null)
				startActivity(Intent.createChooser(intent, getResources().getString(R.string.app_name)));
			else
				snackbarHelper.show(R.string.no_activity);
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		View view = getView();
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		ac = getActivity();

		if (fragmentView == null) {
			fragmentView = new ListView(ac);
			resultsList = ResultsHolder.getResults();

			if (resultsList == null) {
				TextView label = new TextView(getContext());
				label.setText(R.string.data_was_lost);
				label.setGravity(Gravity.CENTER);
				label.setLayoutParams(new ViewGroup.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT
				));
				return label;
			}

			listAdapter = new ResultAdapter(ac);
			listAdapter.setResults(resultsList);

			fragmentView.setOnItemLongClickListener(this);
			fragmentView.setOnItemClickListener(this);
			fragmentView.setAdapter(listAdapter);

			snackbarHelper = new SnackbarHelper(fragmentView);
			startMessage = getString(R.string.results, resultsList.size());
		} else
			ResultsHolder.resetResults();

		return fragmentView;
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);

		ResultsHolder.setResults(resultsList);
	}

	@Override
	public void onStart() {
		super.onStart();

		if (startMessage != null) {
			snackbarHelper.show(startMessage);
			startMessage = null;
		}
	}

	private void showPathWithCopyAction(final String str) {
		snackbarHelper.show(str, R.string.copy, new View.OnClickListener() {
			public void onClick(View v) {
				((android.content.ClipboardManager) ac.getSystemService(Context.CLIPBOARD_SERVICE))
						.setPrimaryClip(android.content.ClipData.newPlainText("RegexFinder", str));
				snackbarHelper.show(R.string.copied);
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		File file = new File(listAdapter.getItem(position));
		String format = Util.getFormat(file.getName());
		String[] extra = Util.sp(getActivity()).getString(Const.PREF_TEXT_FORMATS, Const.DEFAULT_TEXT_FORMATS).trim().split("[ ]+");

		if (Util.isTextFile(format, extra)) {
			ResultsHolder.setResult(resultsList.get(position));
			*//*startActivity(
					new Intent(ac, MainActivity.class)
							.setAction(MainActivity.ACTION_SHOW_RESULT)
			);*//*
		} else
			try {
				Uri uri = Build.VERSION.SDK_INT < 24 ? Uri.fromFile(file) :
						FileProvider.getUriForFile(ac, ac.getApplicationContext().getPackageName() + ".provider", file);
				Intent intent = new Intent()
						.setAction(android.content.Intent.ACTION_VIEW)
						.setDataAndType(uri, MimeTypeMap.getSingleton().getMimeTypeFromExtension(format));

				if (intent.resolveActivity(ac.getPackageManager()) != null)
					startActivity(intent);
				else
					snackbarHelper.show(R.string.no_activity);
			} catch (Exception e) {
				if (e.getMessage().startsWith("Failed to find configured root that contains")) {
					snackbarHelper.showLong(R.string.fucking_provider);
					showPathWithCopyAction(file.getAbsolutePath());
				} else
					snackbarHelper.show(R.string.error);
			}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		showPathWithCopyAction(listAdapter.getItem(position));
		return true;
	}
}*/