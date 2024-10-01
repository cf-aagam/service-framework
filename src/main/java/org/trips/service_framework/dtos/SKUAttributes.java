package org.trips.service_framework.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SKUAttributes {
    private String spec;
    private String species;
    private String productType;
    private String freezingMethod;
    private String packing;
    private String glazingPercentage;
    private String unitWeight;
    private String shelfLife;
    private String quality;
    private String heavyMetalTest;
    private String treatment;
    private String quantityPerUnit;
    private String unitPerCarton;
    private String catchType;
    private String version;
    private String grade;
}
