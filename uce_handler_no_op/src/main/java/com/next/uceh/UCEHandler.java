package com.next.uceh;

import android.content.Context;

/**
 * <pre>
 *     author : NeXT
 *     time   : 2018/09/26
 *     desc   :
 * </pre>
 */
public class UCEHandler {

    private UCEHandler(Builder builder) {
    }

    public static class Builder {
        private Context context;
        private boolean isUCEHEnabled = true;
        private boolean isTrackActivitiesEnabled = false;
        private boolean isBackgroundModeEnabled = true;
        private UCECallback mUCECallback = null;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setUCEHCallback(UCECallback ucehCallback) {
            this.mUCECallback = ucehCallback;
            return this;
        }

        public Builder setUCEHEnabled(boolean isUCEHEnabled) {
            this.isUCEHEnabled = isUCEHEnabled;
            return this;
        }

        public Builder setTrackActivitiesEnabled(boolean isTrackActivitiesEnabled) {
            this.isTrackActivitiesEnabled = isTrackActivitiesEnabled;
            return this;
        }

        public Builder setBackgroundModeEnabled(boolean isBackgroundModeEnabled) {
            this.isBackgroundModeEnabled = isBackgroundModeEnabled;
            return this;
        }

        public UCEHandler build() {
            return new UCEHandler(this);
        }
    }

}
