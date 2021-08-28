package com.appupdater;

import android.content.Context;

import androidx.annotation.NonNull;


public class VersionValidator implements Comparable<VersionValidator> {
    private String version;

    public final String get () {
        return this.version;
    }

    public VersionValidator (Context context, String version) {
        if (version == null)
            AppUpdateUtils.printLog(context,"Version can not be null");
        version = version.replaceAll("[^0-9?!\\.]", "");
        if (!version.matches("[0-9]+(\\.[0-9]+)*"))
            AppUpdateUtils.printLog(context,"Invalid version format");
        this.version = version;
    }

    @Override
    public int compareTo (@NonNull VersionValidator that) {
        String[] thisParts = this.get().split("\\.");
        String[] thatParts = that.get().split("\\.");
        int length = Math.max(thisParts.length, thatParts.length);
        for (int i = 0; i < length; i++) {
            int thisPart = i < thisParts.length ?
                    Integer.parseInt(thisParts[ i ]) : 0;
            int thatPart = i < thatParts.length ?
                    Integer.parseInt(thatParts[ i ]) : 0;
            if (thisPart < thatPart)
                return -1;
            if (thisPart > thatPart)
                return 1;
        }
        return 0;
    }

    @Override
    public boolean equals (Object that) {
        if (this == that)
            return true;
        if (that == null)
            return false;
        return this.getClass() == that.getClass() && this.compareTo((VersionValidator) that) == 0;
    }

}
