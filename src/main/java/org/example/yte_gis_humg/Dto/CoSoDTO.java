package org.example.yte_gis_humg.Dto;

public record CoSoDTO(
        String id,
        String ten,
        String loai,
        Long phuongId,
        double lat,
        double lng
) {}