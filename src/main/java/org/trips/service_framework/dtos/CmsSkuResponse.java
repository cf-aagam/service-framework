package org.trips.service_framework.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
@Builder
public class CmsSkuResponse {
    public ResponseBody data;

    @Data
    public static class SkuAttribute {
        public String value;
        public Attribute attribute;
    }

    @Data
    public static class Attribute {
        public String name;
    }

    @Data
    @Builder
    public static class ResponseBody {
        public List<Sku> searchSkus;
    }

    @Data
    public static class ProductType {
        public String id;
        public String name;
    }

    @Data
    public static class Sku {
        public List<SkuAttribute> attributes;
        public String id;
        public String name;
        public String code;

        public Optional<String> getShelfLife() {
            for (SkuAttribute skuAttribute : this.attributes) {
                if (skuAttribute.getAttribute().getName().equals("shelf_life")) {
                    return Optional.ofNullable(skuAttribute.getValue());
                }
            }

            return Optional.empty();
        }
    }
}