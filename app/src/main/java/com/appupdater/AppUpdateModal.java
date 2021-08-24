package com.appupdater;

public class AppUpdateModal {
    private String versionName;
    private Integer versionCode;
    private String releaseNotes;

    public AppUpdateModal() {
    }

    public AppUpdateModal(String latestVersionName) {
        this.versionName = latestVersionName;
    }

    public String getVersionName () {
        return versionName == null ? "" : versionName;
    }


    public Integer getVersionNameInteger () {
        try {
            return new Integer(versionName);
        } catch (Exception e) {

        }
        return null;

    }

    public String getFormatedVersionName () {
        StringBuilder stringBuilder = new StringBuilder();

        for (char cs : versionName.toCharArray()) {
            if (stringBuilder.length() == 0) {
                stringBuilder.append(cs);
            } else {
                stringBuilder.append(".").append(cs);
            }
        }
        return stringBuilder.toString();
    }

    public void setVersionName (String versionName) {
        this.versionName = versionName;
    }

    public Integer getVersionCode () {
        return versionCode;
    }

    public void setVersionCode (Integer versionCode) {
        this.versionCode = versionCode;
    }

    public String getReleaseNotes () {
        return releaseNotes;
    }

    public void setReleaseNotes (String releaseNotes) {
        this.releaseNotes = releaseNotes;
    }

    @Override
    public String toString () {
        StringBuffer stringBuffer = new StringBuffer();
        if (versionName != null) {
            stringBuffer.append("VersionName=" + versionName);
        }
        if (versionCode != null) {
            stringBuffer.append("Versioncode=" + String.valueOf(versionCode));
        }
        if (releaseNotes != null) {
            stringBuffer.append("ReleaseNotes=" + releaseNotes);
        }

        return stringBuffer.toString();
    }
}
