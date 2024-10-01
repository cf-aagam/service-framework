package org.trips.service_framework.heplers;

import org.springframework.stereotype.Service;
import org.trips.service_framework.constants.CMSConstants;
import org.trips.service_framework.dtos.SKUAttributes;
import org.apache.commons.text.StringSubstitutor;

import java.util.*;

@Service
public class BaseCMSHelper {

    public Map<String, Object> getRequestMap(String name, String value, String operator, boolean isAttribute) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("name", name);
        requestMap.put("value", sanitizeInput(name, value));
        requestMap.put("operator", operator);
        requestMap.put("isAttribute", isAttribute);

        return requestMap;
    }

    public Map<String, Object> getSearchBySkuCodeParams(String code) {
        Map<String, Object> requestMap = getRequestMap("code", code, "EQ", false);
        return Map.of("searchQuery", Map.of("filters", List.of(requestMap)));
    }

    public Map<String, Object> getSearchQueryForItem(SKUAttributes attributes) {
        List<Map<String, Object>> attributeList = new ArrayList<>();
        attributeList.add(getRequestMap("species", attributes.getSpecies(), "EQ", true));
        attributeList.add(getRequestMap("product_type", attributes.getProductType(), "EQ", true));
        attributeList.add(getRequestMap("spec", attributes.getSpec(), "EQ", true));
        attributeList.add(getRequestMap("catch_type", attributes.getCatchType(), "EQ", true));
        attributeList.add(getRequestMap("freezing_method", attributes.getFreezingMethod(), "EQ", true));
        attributeList.add(getRequestMap("packing", attributes.getPacking(), "EQ", true));
        attributeList.add(getRequestMap("glazing_percentage", attributes.getGlazingPercentage(), "EQ", true));
        attributeList.add(getRequestMap("unit_weight", attributes.getUnitWeight(), "EQ", true));
        attributeList.add(getRequestMap("quantity_per_unit", attributes.getQuantityPerUnit(), "EQ", true));
        attributeList.add(getRequestMap("unit_per_carton", attributes.getUnitPerCarton(), "EQ", true));
        attributeList.add(getRequestMap("treatment", attributes.getTreatment(), "EQ", true));
        attributeList.add(getRequestMap("grade", attributes.getGrade(), "EQ", true));
        attributeList.add(getRequestMap("quality", attributes.getQuality(), "EQ", true));
        return Map.of("searchQuery", Map.of("filters", attributeList));
    }

    public String sanitizeInput(String attribute, String value) {
        if (Objects.isNull(value) || value.isEmpty() || value.equals(CMSConstants.NOT_APPLICABLE)) {
            return CMSConstants.NOT_APPLICABLE;
        }

        if (attribute.equals("treatment")) {
            return sanitizeTreatment(value);
        }

        return value.trim();
    }

    public String sanitizeTreatment(String treatment) {
        String[] treatments = treatment.split(",");
        StringBuilder sanitizedTreatment = new StringBuilder();

        for (String t : treatments) {
            String parsedTreatment = parseTreatmentString(t);
            if (!parsedTreatment.isEmpty()) {
                sanitizedTreatment.append("\"").append(parsedTreatment).append("\",");
            }
        }

        if (sanitizedTreatment.length() == 0) {
            return CMSConstants.NOT_APPLICABLE;
        }

        sanitizedTreatment.setLength(sanitizedTreatment.length() - 1);
        return String.format("[%s]", sanitizedTreatment.toString());
    }

    public String parseTreatmentString(String treatment) {
        treatment = treatment.replace("[", "").replace("]", "");
        treatment = treatment.replaceAll("\"", "");
        return treatment.trim();
    }

    public String constructSkuName(List<Map<String, String>> attributes) {
        Map<String, String> attributeMap = new HashMap<>();
        attributes.forEach(x -> {
            String name = x.get("name");
            String value = x.get("value");
            attributeMap.put(name, Objects.equals(value, CMSConstants.NOT_APPLICABLE) ? "" : value.trim());
        });

        String quantityPerUnit = attributeMap.getOrDefault("quantity_per_unit", "");
        String unitPerCarton = attributeMap.getOrDefault("unit_per_carton", "");
        String unitData = (quantityPerUnit.isEmpty() || unitPerCarton.isEmpty()) ? "" : String.format("%sx%s", unitPerCarton, quantityPerUnit);
        attributeMap.put("unitData", unitData);

        String glazingPercentage = attributeMap.get("glazing_percentage");
        if (!glazingPercentage.isEmpty()) {
            attributeMap.put("glazing_percentage", glazingPercentage + "% Glazing");
        }

        String skuNameTemplate = "${species} ${catch_type} ${product_type} ${spec} ${grade} ${quality} ${freezing_method} ${treatment} ${glazing_percentage} ${packing} ${unitData} ${unit_weight}";
        return StringSubstitutor.replace(skuNameTemplate, attributeMap).replaceAll(" +", " ");
    }
}