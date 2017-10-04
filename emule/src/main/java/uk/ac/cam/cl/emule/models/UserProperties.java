package uk.ac.cam.cl.emule.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Fergus Leen Jan 2017
 * This object builds on the Firebase user to offer stats. These will be based in a firebase database? Or local Db?
 * It's preferred that calculations  are done on the server side.
 */

public class UserProperties {

    String mUid;
    String mEmail; //Links to firebase email
    Integer totalDownloads = 0;
    Integer successfulTransfers = 0;
    Double successRate = 0.0; //Calculate
    Long averageDeliveryTimeInSeconds = 0L;
    Integer numberOfUniqueRoutes = 0;
    Double earnings = 0.0;
    List<Delivery> deliveries = new ArrayList<Delivery>();

    public String getmUid() {
        return mUid;
    }

    public void setmUid(String mUid) {
        this.mUid = mUid;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public Integer getTotalDownloads() {
        return totalDownloads;
    }

    public void setTotalDownloads(Integer totalDownloads) {
        this.totalDownloads = totalDownloads;
    }

    public Integer getSuccessfulTransfers() {
        return successfulTransfers;
    }

    public void setSuccessfulTransfers(Integer successfulTransfers) {
        this.successfulTransfers = successfulTransfers;
    }

    public Double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(Double successRate) {
        this.successRate = successRate;
    }

    public Long getAverageDeliveryTimeInSeconds() {
        return averageDeliveryTimeInSeconds;
    }

    public void setAverageDeliveryTimeInSeconds(Long averageDeliveryTimeInSeconds) {
        this.averageDeliveryTimeInSeconds = averageDeliveryTimeInSeconds;
    }

    public Integer getNumberOfUniqueRoutes() {
        return numberOfUniqueRoutes;
    }

    public void setNumberOfUniqueRoutes(Integer numberOfUniqueRoutes) {
        this.numberOfUniqueRoutes = numberOfUniqueRoutes;
    }

    public Double getEarnings() {
        return earnings;
    }

    public void setEarnings(Double earnings) {
        this.earnings = earnings;
    }

    public List<Delivery> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(List<Delivery> deliveries) {
        this.deliveries = deliveries;
    }

    public void addDelivery(Delivery delivery) {
        deliveries.add(delivery);
    }

    @Override
    public String toString() {
        return " totalDownloads = " + totalDownloads +
                ", \nsuccessfulTransfers = " + successfulTransfers +
                ", \nsuccessRate=" + successRate +
                ", \naverageDeliveryTimeInSeconds=" + averageDeliveryTimeInSeconds +
                ", \nnumberOfUniqueRoutes=" + numberOfUniqueRoutes +
                ", \nearnings=" + earnings;
    }
}
