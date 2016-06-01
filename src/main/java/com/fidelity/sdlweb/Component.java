package com.fidelity.sdlweb;

import java.util.Map;

/**
 * Created by pmedcraft on 01/06/2016.
 */
public class Component {
    private String id;
    private Map<String,String> fields;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }
}
