package org.trips.service_framework.utils;

import org.springframework.stereotype.Component;

@Component
public class GraphQLUtils {

    private final String queryFolderPath = "queries";

    public String getQueryFilePath(String filename) {
        return String.join("/", queryFolderPath, filename);
    }
}

