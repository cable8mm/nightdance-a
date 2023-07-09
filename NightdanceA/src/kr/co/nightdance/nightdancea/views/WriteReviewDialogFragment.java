package kr.co.nightdance.nightdancea.views;

import kr.co.nightdance.nightdancea.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;



public class WriteReviewDialogFragment extends DialogFragment implements TextWatcher {
	public static final String TAG = "WriteReviewDialogFragment";

	public OnClickListener listener;
	private EditText mCommentText;
	
	public WriteReviewDialogFragment() {
        // Empty constructor required for DialogFragment
    }
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view	= inflater.inflate(R.layout.dialog_write_review, null);
        mCommentText	= (EditText)view.findViewById(R.id.comment_text);
        mCommentText.addTextChangedListener(this);
        
        builder.setView(view)
        	.setMessage("리뷰 쓰기")
        	.setIcon(R.drawable.ic_action_write)
            .setPositiveButton("완료", listener)
            .setNegativeButton(R.string.cancel, listener);

        return builder.create();
    }
	
	@Override
	public void onStart() {
		super.onStart();
		AlertDialog dialog	= (AlertDialog)getDialog();
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub
		Log.i(TAG, "s, start, count, after ="+s+" "+start+" "+count+" "+after);
		if(s.length() != 0) {
			AlertDialog dialog	= (AlertDialog)getDialog();
			dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
		} else {
			AlertDialog dialog	= (AlertDialog)getDialog();
			dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
		}
	}
}
