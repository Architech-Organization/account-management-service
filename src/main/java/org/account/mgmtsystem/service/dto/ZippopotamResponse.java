package org.account.mgmtsystem.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class ZippopotamResponse {
    private String country;
    private List<Place> places;

    @Getter
    @Setter
    public static class Place {
        @JsonProperty("place name")
        private String placeName;
        @JsonProperty("state abbreviation")
        private String stateAbbreviation;
        private String longitude;
        private String latitude;
    }
}

