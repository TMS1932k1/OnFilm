package com.tms.onfilm.models;

import java.io.Serializable;

public class Picture implements Serializable {
    private String idPic;
    private String linkPic;
    private String typePic;

    public Picture() {
    }

    public Picture(String idPic, String linkPic, String typePic) {
        this.idPic = idPic;
        this.linkPic = linkPic;
        this.typePic = typePic;
    }

    public String getIdPic() {
        return idPic;
    }

    public void setIdPic(String idPic) {
        this.idPic = idPic;
    }

    public String getLinkPic() {
        return linkPic;
    }

    public void setLinkPic(String linkPic) {
        this.linkPic = linkPic;
    }

    public String getTypePic() {
        return typePic;
    }

    public void setTypePic(String typePic) {
        this.typePic = typePic;
    }
}
