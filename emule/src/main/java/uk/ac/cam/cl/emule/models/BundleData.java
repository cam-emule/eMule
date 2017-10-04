package uk.ac.cam.cl.emule.models;


import java.util.HashMap;
import java.util.Map;

public class BundleData {

    private String vapname;
    private String url;
    private int size;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    public String getVapname() {
        return vapname;
    }


    public void setVapname(String vAPNAME) {
        this.vapname = vAPNAME;
    }


    public String getUrl() {
        return url;
    }


    public void setUrl(String uRL) {
        this.url = url;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }


    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }



    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return this.getVapname() + " URL: " + this.getUrl();
    }
}