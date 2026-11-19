package org.example.yte_gis_humg.Mapper;

import org.example.yte_gis_humg.Dto.CoSoDTO;
import org.example.yte_gis_humg.Dto.CoSoGanDayDTO;
import org.example.yte_gis_humg.Dto.CoSoGanDayProjection;
import org.example.yte_gis_humg.Entity.CoSoYTe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CoSoMapper {

    @Mapping(target = "ten", source = "name", defaultValue = "(không tên)")
    @Mapping(target = "lat", source = "geom.y")
    @Mapping(target = "lng", source = "geom.x")
    CoSoDTO toDto(CoSoYTe entity);

    CoSoGanDayDTO toGanDayDto(CoSoGanDayProjection projection);
}