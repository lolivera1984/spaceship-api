package com.spacefleet.spaceshipapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpaceshipDTO {

    private String id;
    private String name;
    private String model;
    private String manufacturer;

}
