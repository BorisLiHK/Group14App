package au.edu.uts.redylog.redylog.Fragments;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import au.edu.uts.redylog.redylog.DataManagers.EntryManager;
import au.edu.uts.redylog.redylog.DataManagers.JournalManager;
import au.edu.uts.redylog.redylog.DialogFragments.CreateEntryDialogFragment;
import au.edu.uts.redylog.redylog.DialogFragments.EditEntryDialogFragment;
import au.edu.uts.redylog.redylog.DialogFragments.EditJournalDialogFragment;
import au.edu.uts.redylog.redylog.DialogFragments.SearchDialogFragment;
import au.edu.uts.redylog.redylog.Helpers.FragmentEnum;
import au.edu.uts.redylog.redylog.Helpers.HelperMethods;
import au.edu.uts.redylog.redylog.Helpers.OnFragmentInteractionListener;
import au.edu.uts.redylog.redylog.Helpers.SearchFilter;
import au.edu.uts.redylog.redylog.Helpers.StatusEnum;
import au.edu.uts.redylog.redylog.Models.Entry;
import au.edu.uts.redylog.redylog.Models.Journal;
import au.edu.uts.redylog.redylog.R;
import au.edu.uts.redylog.redylog.RecyclerViewAdapters.EntryRecyclerViewAdapter;

public class EntryListFragment extends Fragment implements SearchView.OnQueryTextListener, FloatingActionButton.OnClickListener {

    private OnFragmentInteractionListener mListener;
    private TextView _tvError;
    private TextView _tvDescription;
    private TextView _tvDate;
    private LinearLayout _llStatus;
    private TextView _tvStatus;
    private TextView _tvEndDate;
    private RecyclerView mRecyclerView;
    private List<Entry> _entries = new ArrayList<>();
    private SearchView _svEntries;
    private FloatingActionButton _fabEntry;
    private ImageButton _ibFilter;
    private Menu _menu;

    private Journal _currentJournal;
    private EntryRecyclerViewAdapter _adapter;
    private SearchFilter _searchFilter = new SearchFilter(FragmentEnum.EntryListFragment);

    public EntryListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry_list, container, false);
        _currentJournal = (Journal) getArguments().getSerializable(getString(R.string.bundle_journal_key));

        setupReferences(view);
        setupRecyclerView(view);
        setupView();
        updateList();

        mListener.updateTitle(_currentJournal.get_title());

        return view;
    }

    private void setupReferences(View view) {
        _llStatus = view.findViewById(R.id.entry_list_journal_status_date);
        _tvError = view.findViewById(R.id.tv_entry_error);
        _tvDescription = view.findViewById(R.id.entry_list_journal_description);
        _tvDate = view.findViewById(R.id.entry_list_journal_date);
        _tvStatus = view.findViewById(R.id.entry_list_journal_status);
        _tvEndDate = view.findViewById(R.id.entry_list_journal_end_date);
        _svEntries = view.findViewById(R.id.sv_entries);
        _ibFilter = view.findViewById(R.id.ib_entry_list_filter);
        _fabEntry = view.findViewById(R.id.fab_entry_list);

        _svEntries.setOnQueryTextListener(this);
        _ibFilter.setOnClickListener(this);
        _fabEntry.setOnClickListener(this);
    }

    private void setupView() {

        if (_entries.size() > 0) {
            _tvError.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            _tvError.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

        if (_currentJournal.get_status() == StatusEnum.Open){
            _llStatus.setVisibility(View.GONE);
        } else {
            _tvStatus.setText(_currentJournal.get_status().toString());
            _tvEndDate.setText(HelperMethods.formatDate(_currentJournal.get_endDate()));
        }

        _tvDescription.setText(_currentJournal.get_description());
        _tvDate.setText(HelperMethods.formatDate(_currentJournal.get_startDate()));
    }

    private void setupRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.rv_entries);
        _adapter = new EntryRecyclerViewAdapter(mListener, _entries);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(_adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.entry_list_menu, menu);
        _menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
        adjustMenuVisiblity();
    }

    private void adjustMenuVisiblity() {
        StatusEnum e = _currentJournal.get_status();
        _menu.findItem(R.id.action_create_entry).setVisible(e == StatusEnum.Open);
        _menu.findItem(R.id.action_close_journal).setVisible(e == StatusEnum.Open);
        _menu.findItem(R.id.action_reopen_journal).setVisible(e == StatusEnum.Closed);
        _menu.findItem(R.id.action_edit_journal).setVisible(e == StatusEnum.Open);
        _menu.findItem(R.id.action_delete_journal).setVisible(e == StatusEnum.Open || e == StatusEnum.Closed);
        _fabEntry.setVisibility(e == StatusEnum.Open?View.VISIBLE:View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_create_entry:
                displayAddEntryDialog();
                break;
            case R.id.action_close_journal:
                displayCloseJournal();
                break;
            case R.id.action_reopen_journal:
                displayReopenJournal();
                break;
            case R.id.action_edit_journal:
                displayEditDialog();
                break;
            case R.id.action_delete_journal:
                displayDeleteJournal();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayAddEntryDialog() {
        CreateEntryDialogFragment dialogFragment = new CreateEntryDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable(getString(R.string.bundle_journal_key), _currentJournal);
        dialogFragment.setArguments(args);
        dialogFragment.setTargetFragment(this,1);
        dialogFragment.show(getFragmentManager(), "dialog");
    }

    private void displayEditDialog() {
        EditJournalDialogFragment dialogFragment = new EditJournalDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable(getString(R.string.bundle_journal_key), _currentJournal);
        dialogFragment.setArguments(args);
        dialogFragment.setTargetFragment(this,1);
        dialogFragment.show(getFragmentManager(), "dialog");
    }

    public void updateList(){
        _entries.clear();
        _entries.addAll(EntryManager.getInstance().get_entries(_currentJournal, _searchFilter));

        setupView();

        mListener.updateTitle(_currentJournal.get_title());
        _adapter.notifyDataSetChanged();
    }

    private void displayCloseJournal(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_close_journal_prompt)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        JournalManager.getInstance().closeJournal(_currentJournal);
                        mListener.displayFragment(FragmentEnum.JournalListFragment, null);
                        Toast.makeText(getContext(), R.string.journal_closed_confirmed, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
    }

    private void displayReopenJournal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_reopen_journal_prompt)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        JournalManager.getInstance().reopenJournal(_currentJournal);
                        setupView();
                        adjustMenuVisiblity();
                        Toast.makeText(getContext(), R.string.journal_reopened_confirmed, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
    }

    private void displayDeleteJournal(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_delete_journal_prompt)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        JournalManager.getInstance().deleteJournal(_currentJournal);
                        mListener.displayFragment(FragmentEnum.JournalListFragment, null);
                        Toast.makeText(getContext(), R.string.journal_deleted_confirmed, Toast.LENGTH_LONG);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
    }

    private void displayFilterDialog() {
        SearchDialogFragment dialogFragment = new SearchDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable(getString(R.string.bundle_filter_key), _searchFilter);
        dialogFragment.setArguments(args);
        dialogFragment.setTargetFragment(this,1);
        dialogFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        _searchFilter.set_query(newText);
        _entries.clear();
        _entries.addAll(EntryManager.getInstance().get_entries(_currentJournal, _searchFilter));
        _adapter.notifyDataSetChanged();
        setupView();
        return false;
    }

    @Override
    public void onClick(View view) {
        if (view == _fabEntry) {
            displayAddEntryDialog();
        } else if (view == _ibFilter) {
            displayFilterDialog();
        }
    }
}
