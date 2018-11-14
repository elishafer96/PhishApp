package tcss450.uw.edu.phishapp;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class SuccessFragment extends Fragment {

    private String mEmail;
    private OnFragmentInteractionListener mListener;

    public SuccessFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SuccessFragment.OnFragmentInteractionListener) {
            mListener = (SuccessFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            // Retrieve and set credentials
            mEmail = getArguments().getString(getString(R.string.keys_email));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_success, container, false);

        Button logOutBtn = (Button) view.findViewById(R.id.successLogoutButton);
        logOutBtn.setOnClickListener(v -> mListener.onLogOutClicked());

        return view;
    }

    public interface OnFragmentInteractionListener
            extends WaitFragment.OnFragmentInteractionListener {
        void onLogOutClicked();
    }

}
