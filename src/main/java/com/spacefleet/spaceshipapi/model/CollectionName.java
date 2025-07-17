package com.spacefleet.spaceshipapi.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CollectionName {

    USERS("users"),
    SPACESHIPS("spaceships");

    private final String value;

}
