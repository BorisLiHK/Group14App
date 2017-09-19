package au.edu.uts.redylog.redylog.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import au.edu.uts.redylog.redylog.DataManagers.JournalManager;
import au.edu.uts.redylog.redylog.Helpers.HelperMethods;
import au.edu.uts.redylog.redylog.Helpers.OnFragmentInteractionListener;
import au.edu.uts.redylog.redylog.Models.Entry;
import au.edu.uts.redylog.redylog.Models.Journal;
import au.edu.uts.redylog.redylog.R;

public class ViewEntryFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private TextView _tvDate;
    private TextView _tvContent;
    private Entry _entry;

    public ViewEntryFragment() {

    }

    public Journal getJournal() {
        return JournalManager.getInstance().get_journal(_entry.get_journalId());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_entry, container, false);

        _entry = (Entry) getArguments().getSerializable(getString(R.string.bundle_entry_key));

        setupReferences(view);
        setupView(view);

        return view;
    }

    private void setupReferences(View view){
        _tvContent = view.findViewById(R.id.view_entry_content);
        _tvDate = view.findViewById(R.id.view_entry_date);
    }

    private void setupView(View view){
        _tvContent.setText(_entry.get_contents());
        _tvDate.setText(HelperMethods.formatDate(_entry.get_createdDate()));
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
        //getActivity().getMenuInflater().inflate(R.menu.journal_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == R.id.action_add_journal) {
//            displayAddJournalDialog();
//        }

        return super.onOptionsItemSelected(item);
    }

}
