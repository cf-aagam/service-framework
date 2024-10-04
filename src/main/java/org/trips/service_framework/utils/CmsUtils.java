package org.trips.service_framework.utils;

import org.trips.service_framework.constants.CMSConstants;

import java.util.Objects;


public class CmsUtils {

    public static String sanitizeInput(String attribute, String value) {
        if (Objects.isNull(value) || value.isEmpty() || value.equals(CMSConstants.NOT_APPLICABLE)) {
            return CMSConstants.NOT_APPLICABLE;
        }

        if (attribute.equals("treatment")) {
            return sanitizeTreatment(value);
        }

        return value.trim();
    }

    private static String sanitizeTreatment(String treatment) {
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

    private static String parseTreatmentString(String treatment) {
        treatment = treatment.replace("[", "").replace("]", "");
        treatment = treatment.replaceAll("\"", "");
        return treatment.trim();
    }
}
