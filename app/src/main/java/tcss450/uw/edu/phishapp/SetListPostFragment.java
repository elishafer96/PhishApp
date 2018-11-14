package tcss450.uw.edu.phishapp;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import tcss450.uw.edu.phishapp.setlist.SetListPost;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SetListPostFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SetListPostFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private SetListPost setListPost;

    public SetListPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_set_list_post, container, false);

        if (getArguments() != null) {
            setListPost = (SetListPost) getArguments().getSerializable("setlistpost");
            TextView text = (TextView) view.findViewById(R.id.setlistpost_longdate);
            text.setText(setListPost.getLongDate());

            text = (TextView) view.findViewById(R.id.setlistpost_location);
            text.setText(setListPost.getLocation());

            text = (TextView) view.findViewById(R.id.setlistpost_notes);
            text.setText(Html.fromHtml(setListPost.getSetListNotes(),
                    Html.FROM_HTML_MODE_COMPACT));

            text = (TextView) view.findViewById(R.id.setlistpost_data);
            text.setText(Html.fromHtml(setListPost.getSetListData(),
                    Html.FROM_HTML_MODE_COMPACT));
        }

        Button button = (Button) view.findViewById(R.id.button_setlistpost_fullpost);
        button.setOnClickListener(v -> onFullPostClicked());

        return view;

    }

    public void onFullPostClicked() {
        if (mListener != null) {
            mListener.onFullPostClicked(setListPost);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFullPostClicked(SetListPost setListPost);
    }

}
