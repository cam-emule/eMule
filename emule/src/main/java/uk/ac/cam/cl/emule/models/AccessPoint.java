package uk.ac.cam.cl.emule.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
 * Created by fergus on 10/12/2016.
 */


public class AccessPoint {

    private String id;
    private String name;
    private String description;
    private String imageurl;
    private Integer updatedOn;
    private String updatedAt;
    private Double longitude = 0.0d;
    private Double latitude = 0.0d;
    private Integer lastGatewaySync;
    private File fileFrom;
    private File fileTo;
    private String subdomain = "";
    private Double bounty = 0.50; //Thats 50p.
    private float distanceMeters = -1.0f;
    private String distanceString = "---";


    public Double getBounty() {
        return bounty;
    }

    public void setBounty(Double bounty) {
        this.bounty = bounty;
    }

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The _id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     * The imageurl
     */
    public String getImageurl() {
        return imageurl;
    }

    /**
     *
     * @param imageurl
     * The imageurl
     */
    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    /**
     *
     * @return
     * The updatedOn
     */
    public Integer getUpdatedOn() {
        return updatedOn;
    }

    /**
     *
     * @param updatedOn
     * The updated_on
     */
    public void setUpdatedOn(Integer updatedOn) {
        this.updatedOn = updatedOn;
    }

    /**
     *
     * @return
     * The updatedAt
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     *
     * @param updatedAt
     * The updated_at
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     *
     * @return
     * The loc
     */
    public List<Double> getLoc() {
        List<Double> loc = new ArrayList<Double>(2);
        loc.add(longitude);
        loc.add(latitude);

        return loc;
    }


    public File getFileFrom() {
        return fileFrom;
    }

    public void setFileFrom(File fileFrom) {
        this.fileFrom = fileFrom;
    }

    public File getFileTo() {
        return fileTo;
    }

    public void setFileTo(File fileTo) {
        this.fileTo = fileTo;
    }

    public Integer getLastGatewaySync() {
        return lastGatewaySync;
    }

    public void setLastGatewaySync(Integer lastGatewaySync) {
        this.lastGatewaySync = lastGatewaySync;
    }


    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }




    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(name).append(description).append(imageurl).append(updatedOn).append(updatedAt).append(longitude).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AccessPoint)) {
            return false;
        }
        AccessPoint rhs = ((AccessPoint) other);
        return new EqualsBuilder().append(id, rhs.id).append(name, rhs.name).append(description, rhs.description).append(imageurl, rhs.imageurl).append(updatedOn, rhs.updatedOn).append(updatedAt, rhs.updatedAt).append(longitude, rhs.longitude).isEquals();
    }


    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }


    public Float getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(Float distanceMeters) {
        this.distanceMeters = distanceMeters;
    }

    public String getDistanceString() {
        return distanceString;
    }

    public void setDistanceString(String distanceString) {
        this.distanceString = distanceString;
    }





}


