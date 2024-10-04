package org.trips.service_framework.heplers;

import org.springframework.stereotype.Service;
import org.trips.service_framework.constants.CMSConstants;
import org.trips.service_framework.dtos.SkuAttributes;
import org.apache.commons.text.StringSubstitutor;
import org.trips.service_framework.utils.CmsUtils;

import java.util.*;

@Service
public class CmsHelper {

    public Map<String, Object> createRequestMap(String name, String value, String operator, boolean isAttribute) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("name", name);
        requestMap.put("value", CmsUtils.sanitizeInput(name, value));
        requestMap.put("operator", operator);
        requestMap.put("isAttribute", isAttribute);

        return requestMap;
    }

    public Map<String, Object> getSearchQueryFromSkuCode(String code) {
        Map<String, Object> requestMap = createRequestMap("code", code, "EQ", false);
        return Map.of("searchQuery", Map.of("filters", List.of(requestMap)));
    }

    public Map<String, Object> getSearchQueryFromAttributes(SkuAttributes attributes) {
        List<Map<String, Object>> attributeList = new ArrayList<>();
        attributeList.add(createRequestMap("species", attributes.getSpecies(), "EQ", true));
        attributeList.add(createRequestMap("product_type", attributes.getProductType(), "EQ", true));
        attributeList.add(createRequestMap("spec", attributes.getSpec(), "EQ", true));
        attributeList.add(createRequestMap("catch_type", attributes.getCatchType(), "EQ", true));
        attributeList.add(createRequestMap("freezing_method", attributes.getFreezingMethod(), "EQ", true));
        attributeList.add(createRequestMap("packing", attributes.getPacking(), "EQ", true));
        attributeList.add(createRequestMap("glazing_percentage", attributes.getGlazingPercentage(), "EQ", true));
        attributeList.add(createRequestMap("unit_weight", attributes.getUnitWeight(), "EQ", true));
        attributeList.add(createRequestMap("quantity_per_unit", attributes.getQuantityPerUnit(), "EQ", true));
        attributeList.add(createRequestMap("unit_per_carton", attributes.getUnitPerCarton(), "EQ", true));
        attributeList.add(createRequestMap("treatment", attributes.getTreatment(), "EQ", true));
        attributeList.add(createRequestMap("grade", attributes.getGrade(), "EQ", true));
        attributeList.add(createRequestMap("quality", attributes.getQuality(), "EQ", true));

        return Map.of("searchQuery", Map.of("filters", attributeList));
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