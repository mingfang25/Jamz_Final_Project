package com.example.jamz;

import com.firebase.ui.auth.data.model.User;

public class UserInfo {
    public String altdisplayname;
    public String userbio;
    public String userinstruments;

    public UserInfo(String altdisplayname, String userbio, String userinstruments){
        this.altdisplayname = altdisplayname;
        this.userbio = userbio;
        this.userinstruments = userinstruments;
    }

    public String getAltdisplayname(){return altdisplayname;}
    public void setAltdisplayname(String altdisplayname){this.altdisplayname = altdisplayname;}
    public String getUserbio(){return userbio;}
    public void setUserbio(String userbio){this.userbio = userbio;}
    public String getUserinstruments(){return userinstruments;}
    public void setUserinstruments(String userinstruments){this.userinstruments = userinstruments;}

    public UserInfo(){

    }


}
