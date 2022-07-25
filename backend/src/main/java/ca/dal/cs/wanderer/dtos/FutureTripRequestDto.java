package ca.dal.cs.wanderer.dtos;

import ca.dal.cs.wanderer.models.FutureTrip;
import ca.dal.cs.wanderer.models.Pin;
import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;

@Getter
@Setter
// class for managing future trip request dto
public class FutureTripRequestDto {

    private String tripName;
    private String tripDescription;
    private Calendar tripDate;
    private Integer pinId;

    // method for converting future trip request dto to model
    public static FutureTrip convertToModel(FutureTripRequestDto futureTripRequestDto) {
        FutureTrip futureTrip = new FutureTrip();
        futureTrip.setTripName(futureTripRequestDto.getTripName());
        futureTrip.setTripDescription(futureTripRequestDto.getTripDescription());
        futureTrip.setTripDate(futureTripRequestDto.getTripDate());
        Pin pin = new Pin();
        pin.setPinId(futureTripRequestDto.getPinId());
        futureTrip.setPin(pin);
        return futureTrip;
    }
}
