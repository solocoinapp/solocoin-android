package app.solocoin.solocoin.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Reward implements Parcelable {

    public static final Creator<Reward> CREATOR = new Creator<Reward>() {
        @Override
        public Reward createFromParcel(Parcel in) {
            return new Reward(in);
        }

        @Override
        public Reward[] newArray(int size) {
            return new Reward[size];
        }
    };
    private String offerName, costRupees, costCoins, offerExtraDetails, companyName;
    private ArrayList offerDetails;

    public Reward() {
    }

    public Reward(String offerName, String costRupees, String costCoins, ArrayList<String> offerDetails, String offerExtraDetails, String companyName) {
        this.offerName = offerName;
        this.offerDetails = offerDetails;
        this.costCoins = costCoins;
        this.costRupees = costRupees;
        this.offerExtraDetails = offerExtraDetails;
        this.companyName = companyName;
    }

    private Reward(Parcel in) {
        offerName = in.readString();
        costRupees = in.readString();
        costCoins = in.readString();
        offerDetails = in.readArrayList(null);
        offerExtraDetails = in.readString();
        companyName = in.readString();
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public String getCostRupees() {
        return costRupees;
    }

    public void setCostRupees(String costRupees) {
        this.costRupees = costRupees;
    }

    public String getCostCoins() {
        return costCoins;
    }

    public void setCostCoins(String costCoins) {
        this.costCoins = costCoins;
    }

    public ArrayList<String> getOfferDetails() {
        return offerDetails;
    }

    public void setOfferDetails(ArrayList<String> offerDetails) {
        this.offerDetails = offerDetails;
    }

    public String getOfferExtraDetails() {
        return offerExtraDetails;
    }

    public void setOfferExtraDetails(String offerExtraDetails) {
        this.offerExtraDetails = offerExtraDetails;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(offerName);
        parcel.writeString(costRupees);
        parcel.writeString(costCoins);
        parcel.writeList(offerDetails);
        parcel.writeString(offerExtraDetails);
        parcel.writeString(companyName);
    }
}
