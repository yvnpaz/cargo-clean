package com.github.cargoclean.core.model.location;

import com.github.cargoclean.core.model.Assert;
import com.github.cargoclean.core.model.InvalidDomainObjectError;

public enum Region {
    UNKNOWN("Unknown"),
    AFRICA("Africa"),
    ASIA("Asia"),
    OCEANIA("Oceania"),
    EUROPE("Europe"),
    NORTH_AMERICA("North America"),
    SOUTH_AMERICA("South America");

    public static Region of(String regionName) {
        try {
            return Region.valueOf(Assert.notBlank(regionName));
        } catch (IllegalArgumentException e) {
            throw new InvalidDomainObjectError("Cannot parse region from %s".formatted(regionName));
        }
    }

    private final String region;

    Region(String region) {
        this.region = region;
    }


    @Override
    public String toString() {
        return region;
    }
}
