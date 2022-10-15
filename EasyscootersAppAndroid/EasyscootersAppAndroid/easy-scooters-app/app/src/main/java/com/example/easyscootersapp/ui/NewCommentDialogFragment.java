package com.example.easyscootersapp.ui;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.easyscootersapp.R;
import com.example.easyscootersapp.data.User;
// ...

public class NewCommentDialogFragment extends DialogFragment {

    private EditText nbScooter;
    private EditText desc;
    private EditText contactCli;
    private ProgressBar progressBar;

    public NewCommentDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static NewCommentDialogFragment newInstance(User user) {
        NewCommentDialogFragment frag = new NewCommentDialogFragment();
        Bundle args = new Bundle();

        args.putSerializable("user", user);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_comment, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        nbScooter = view.findViewById(R.id.nbscooter);
        desc = view.findViewById(R.id.description);
        contactCli = view.findViewById(R.id.contactClient);
        Button btnNext = view.findViewById(R.id.btn_step1);
        progressBar = view.findViewById(R.id.progress);
        User user = (User) getArguments().getSerializable("user");

        btnNext.setOnClickListener(view1 -> {
            String numberSet = nbScooter.getText().toString();
            String descSet = desc.getText().toString();
            String contactSet = contactCli.getText().toString();

            if (contactSet.length()>1 ){
                if (numberSet.length()>1 ) {
                    //champs saisie
                    if (descSet.length()>1 ){
                        //champs saisie
                        //sendMail();
                        new NewCommentPresenter(this).onSendRequest(user,
                                numberSet,
                                descSet);
                    }else{
                        desc.setError("Saisie obligatoire");
                    }
                }else{
                    nbScooter.setError("Saisie obligatoire");
                }
            }else{
                contactCli.setError("Saisie obligatoire");
            }
        });

        // Fetch arguments from bundle and set title
        contactCli.setText(user.id);
        contactCli.setEnabled(false);

        //getDialog().setTitle("Hola " + user.firstname);
        // Show soft keyboard automatically and request focus to field
        //mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public void showLoading(boolean loading) {
        progressBar.setVisibility(loading? View.VISIBLE: View.GONE);
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }
}