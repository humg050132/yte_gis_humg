package org.example.yte_gis_humg.Dto;

import java.util.List;
import java.util.Map;

public final class GeoJsonDTO {

    private GeoJsonDTO() {}

    public record FeatureCollection(String type, List<Feature> features) {
        public static FeatureCollection of(List<Feature> features) {
            return new FeatureCollection("FeatureCollection", features);
        }
    }

    public record Feature(String type, Point geometry, Map<String, Object> properties) {
        public static Feature diem(double lng, double lat, Map<String, Object> properties) {
            return new Feature("Feature", new Point(lng, lat), properties);
        }
    }

    public record Point(String type, double[] coordinates) {
        public Point(double lng, double lat) {
            this("Point", new double[]{lng, lat});
        }
    }
}