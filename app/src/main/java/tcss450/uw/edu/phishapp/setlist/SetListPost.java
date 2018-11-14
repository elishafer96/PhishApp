package tcss450.uw.edu.phishapp.setlist;

import java.io.Serializable;

public class SetListPost implements Serializable {

    private final String mLongDate;
    private final String mLocation;
    private final String mVenue;
    private final String mURL;
    private final String mSetListData;
    private final String mSetListNotes;

    public static class Builder {
        private final String mLongDate;
        private final String location;
        private final String venue;
        private String url = "";
        private String setListData = "";
        private String setListNotes = "";

        public Builder(String location, String longDate, String venue) {
            this.location = location;
            this.mLongDate = longDate;
            this.venue = venue;
        }

        public Builder addURL(final String url) {
            this.url = url;
            return this;
        }

        public Builder addSetListData(final String setListData) {
            this.setListData = setListData;
            return this;
        }

        public Builder addSetListNotes(final String setListNotes) {
            this.setListNotes = setListNotes;
            return this;
        }

        public SetListPost build() {
            return new SetListPost(this);
        }
    }

    public SetListPost(final Builder builder) {
        this.mLongDate = builder.mLongDate;
        this.mURL = builder.url;
        this.mVenue = builder.venue;
        this.mLocation = builder.location;
        this.mSetListData = builder.setListData;
        this.mSetListNotes = builder.setListNotes;
    }

    public String getLongDate() {
        return this.mLongDate;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getVenue() {
        return mVenue;
    }

    public String getURL() {
        return mURL;
    }

    public String getSetListData() {
        return mSetListData;
    }

    public String getSetListNotes() {
        return mSetListNotes;
    }
}
