package org.account.mgmtsystem.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class StatePlaceCount {
    private String state;
    private String place;
    private long count;
}

