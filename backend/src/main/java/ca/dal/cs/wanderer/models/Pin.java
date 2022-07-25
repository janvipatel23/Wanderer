package ca.dal.cs.wanderer.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "pin_information")
public class Pin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pin_id", unique = true, nullable = false)
    private Integer pinId;
    @Column(name="user_id")
    private Integer userId;
    @Column(name="pin_location_name")
    private String locationName;
    @Column(name="pin_description")
    private String description;
    @Column(name="pin_latitude")
    private Double latitude;
    @Column(name="pin_longitude")
    private Double longitude;
    // create one-to-many relationship with PinRating
    @JsonManagedReference
    @OneToMany(mappedBy = "pin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PinRating> pinRatings;
    // create one-to-many relationship with PinComment
    @JsonManagedReference
    @OneToMany(mappedBy = "pin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PinComment> pinComments;

    @JsonManagedReference
    @OneToMany(mappedBy = "pin",cascade = CascadeType.ALL,fetch =FetchType.LAZY)
    private List<PinImage> pinImages;

    // create one to many relationship with BucketListPin
    @JsonIgnore
    @OneToMany(mappedBy = "pin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BucketListPin> bucketListPins;

    public Pin() {
    }

    public Pin(Integer userId, String locationName, Double latitude, Double longitude, String description) {
        this.userId = userId;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
    }

    public Pin(Integer userId, String locationName, String description, Double latitude, Double longitude) {
        this.userId = userId;
        this.locationName = locationName;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public void setPinId(int pinId) {
        this.pinId = pinId;
    }

    public Integer getPinId() {
        return pinId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    // add pin rating
    public void addPinRating(PinRating pinRating) {
        pinRatings.add(pinRating);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    // add pin comment
    public void addPinComment(PinComment pinComment) {
        pinComments.add(pinComment);
    }

    public Pin(List<PinImage> pinImages) {
        this.pinImages = pinImages;
    }

    public void addPinImage(PinImage image){
        pinImages.add(image);
    }

}