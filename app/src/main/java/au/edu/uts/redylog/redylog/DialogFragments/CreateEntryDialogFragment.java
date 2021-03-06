package au.edu.uts.redylog.redylog.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import au.edu.uts.redylog.redylog.DataManagers.EntryManager;
import au.edu.uts.redylog.redylog.Fragments.EntryListFragment;
import au.edu.uts.redylog.redylog.Models.Entry;
import au.edu.uts.redylog.redylog.Models.Journal;
import au.edu.uts.redylog.redylog.R;

/**
 * Created by neola on 29-Aug-17.
 */

public class CreateEntryDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private Journal _currentJournal;
    private EntryListFragment prevFragment;

    public CreateEntryDialogFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        _currentJournal = (Journal) getArguments().getSerializable(getString(R.string.bundle_journal_key));

        return new AlertDialog.Builder(getActivity())
                .setView(getActivity().getLayoutInflater().inflate(R.layout.fragment_create_entry_dialog, null))
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }
    //Todo get the journal id from previous fragment
    public void onClick(DialogInterface dialog, int whichButton) {
        EditText etTitle = ((AlertDialog) dialog).findViewById(R.id.et_create_entry_title);
        EditText etContent = ((AlertDialog) dialog).findViewById(R.id.et_create_entry_content);

        if (TextUtils.isEmpty(etTitle.getText().toString())) {
            Toast.makeText(getActivity(), "Entry title cannot be empty - failed to create entry.", Toast.LENGTH_SHORT).show();
        } else
        {
            Entry entry = new Entry(
                    etTitle.getText().toString(),
                    etContent.getText().toString(),
                    _currentJournal.get_journalId());

            EntryManager.getInstance().addEntry(entry);
            Toast.makeText(getContext(), R.string.entry_created_confirmed, Toast.LENGTH_SHORT).show();
            prevFragment = (EntryListFragment) getTargetFragment();
            prevFragment.updateList();
        }
    }
}

