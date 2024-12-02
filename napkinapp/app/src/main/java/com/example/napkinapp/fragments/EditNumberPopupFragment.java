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
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.example.napkinapp.R;


public class EditNumberPopupFragment extends DialogFragment {

    private ButtonCallbacks callbacks;
    private String title;
    private String hint;

    public EditNumberPopupFragment(String title, String hint, ButtonCallbacks callbacks) {
        this.callbacks = callbacks;
        this.title = title;
        this.hint = hint;
    }

    /**
     * callback interface for the dialog on button press.
     */
    public interface ButtonCallbacks {
        void onPositive(Integer number);
        default void onNegative() {};
        default Pair<Boolean, String> checkValid(Integer number) { return new Pair(Boolean.TRUE, ""); }
    };


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.enter_text_dialog, null);
        EditText editText = view.findViewById(R.id.edit_text);
        editText.setText(this.hint);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER); // make it numbers only!

        // Use the Builder class for convenient dialog construction.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(this.title)
                .setView(view)
                .setPositiveButton("OK", null )
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        callbacks.onNegative();
                    }
                });
        // Create the AlertDialog object and return it.
        AlertDialog dialog = builder.create();
        // add the custom positive button handler
        dialog.setOnShowListener(d -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                Integer number = Integer.parseInt(editText.getText().toString());
                if(callbacks.checkValid(number).first) {
                    callbacks.onPositive(number);
                    dialog.dismiss();
                } else {
                    event
                }
            });
        });

        return dialog;
    }
}

