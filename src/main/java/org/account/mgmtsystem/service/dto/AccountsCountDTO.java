package org.account.mgmtsystem.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class AccountsCountDTO {

    private String country;

    private long count;

    private List<StateCounts> states;

    @AllArgsConstructor
    @Getter
    @Setter
    public static class CountryCountsResponse {
        private String country;
        private long count;
        private List<StateCounts> states;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class StateCounts {
        private String state;
        private long count;
        private List<PlaceCounts> places;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class PlaceCounts {
        private String place;
        private long count;
    }

}
