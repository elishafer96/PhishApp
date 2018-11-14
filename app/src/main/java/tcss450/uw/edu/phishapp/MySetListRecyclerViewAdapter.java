package tcss450.uw.edu.phishapp;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tcss450.uw.edu.phishapp.SetListFragment.OnListFragmentInteractionListener;
import tcss450.uw.edu.phishapp.setlist.SetListPost;

/**
 * {@link RecyclerView.Adapter} that can display a {@link SetListPost} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MySetListRecyclerViewAdapter extends RecyclerView.Adapter<MySetListRecyclerViewAdapter.ViewHolder> {

    private final List<SetListPost> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MySetListRecyclerViewAdapter(List<SetListPost> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_setlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mLongDate.setText(mValues.get(position).getLongDate());
        holder.mLocation.setText(mValues.get(position).getLocation());
//        holder.mVenue.setText(mValues.get(position).getVenue());
        holder.mVenue.setText(Html.fromHtml(mValues.get(position).getVenue(),
                Html.FROM_HTML_MODE_COMPACT));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mLongDate;
        public final TextView mLocation;
        public final TextView mVenue;
        public SetListPost mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mLongDate = (TextView) view.findViewById(R.id.setlist_longdate);
            mLocation = (TextView) view.findViewById(R.id.setlist_location);
            mVenue = (TextView) view.findViewById(R.id.setlist_venue);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mLocation.getText() + "'";
        }
    }
}
