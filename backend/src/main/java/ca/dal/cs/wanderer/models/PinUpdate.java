package ca.dal.cs.wanderer.models;

import java.util.Date;

public class PinUpdate {
    String document_id;
    Date last_updated;

    public PinUpdate() {
    }

    public PinUpdate(String document_id, Date timestamp) {
        this.document_id = document_id;
        this.last_updated = timestamp;
    }

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public Date getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(Date last_updated) {
        this.last_updated = last_updated;
    }

    @Override
    public String toString() {
        return "PinUpdate{" +
                "document_id='" + document_id + '\'' +
                ", last_updated=" + last_updated +
                '}';
    }
}
