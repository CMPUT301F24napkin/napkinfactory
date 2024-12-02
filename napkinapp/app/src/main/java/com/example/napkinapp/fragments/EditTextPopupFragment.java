/**
 * Helper fragment which extends DialogFragment and will allow the user to
 * specify a text in an input field and press either accept or cancel.
 */

package com.example.napkinapp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.example.napkinapp.R;

public class EditTextPopupFragment extends DialogFragment {

    private ButtonCallbacks callbacks;
    private String title;
    private String hint;

    public EditTextPopupFragment(String title, String hint, ButtonCallbacks callbacks) {
        this.callbacks = callbacks;
        this.title = title;
        this.hint = hint;
    }

    /**
     * callback interface for the dialog on button press.
     */
    public interface ButtonCallbacks {
      void onPositive(String text);
      default void onNegative() {};
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.enter_text_dialog, null);
        EditText editText = view.findViewById(R.id.edit_text);
        editText.setText(this.hint);

        // Use the Builder class for convenient dialog construction.
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(this.title)
                    .setView(view)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            callbacks.onPositive(editText.getText().toString());
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            callbacks.onNegative();
                        }
                    });
            // Create the AlertDialog object and return it.
            return builder.create();
    }
}

