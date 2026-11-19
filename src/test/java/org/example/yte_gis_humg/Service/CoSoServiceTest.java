package org.example.yte_gis_humg.Service;

import org.example.yte_gis_humg.Entity.CoSoYTe;
import org.example.yte_gis_humg.Dto.CoSoDTO;
import org.example.yte_gis_humg.Mapper.CoSoMapperImpl;
import org.example.yte_gis_humg.Repository.CoSoYTeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoSoServiceTest {

    @Mock
    CoSoYTeRepository repo;

    CoSoService service;

    @BeforeEach
    void setUp() {
        service = new CoSoService(repo, new CoSoMapperImpl());
    }

    @Test
    void ganDay_radiusQuaLon_nemBadRequest() {
        var ex = assertThrows(ResponseStatusException.class,
                () -> service.timGanDay(21.0, 105.8, 6000, null, 10));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verifyNoInteractions(repo);
    }

    @Test
    void ganDay_loaiKhongHopLe_nemBadRequest() {
        assertThrows(ResponseStatusException.class,
                () -> service.timGanDay(21.0, 105.8, 1000, "bệnh_viện_sai", 10));
        verifyNoInteractions(repo);
    }

    @Test
    void trongKhung_bboxNguoc_nemBadRequest() {
        assertThrows(ResponseStatusException.class,
                () -> service.trongKhung(105.86, 21.02, 105.84, 21.04, null, 10));
        verifyNoInteractions(repo);
    }

    @Test
    void layTheoId_khongTonTai_nem404() {
        when(repo.findById("khong_ton_tai")).thenReturn(Optional.empty());
        var ex = assertThrows(ResponseStatusException.class,
                () -> service.layTheoId("khong_ton_tai"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void layTheoId_tenNull_dungTenMacDinhVaToaDo() {
        var geo = new GeometryFactory().createPoint(new Coordinate(105.85, 21.03));
        var entity = new CoSoYTe();
        entity.setId("node/1");
        entity.setName(null);
        entity.setLoai("nha_thuoc");
        entity.setGeom(geo);
        when(repo.findById("node/1")).thenReturn(Optional.of(entity));

        CoSoDTO dto = service.layTheoId("node/1");

        assertEquals("(không tên)", dto.ten());
        assertEquals(21.03, dto.lat(), 1e-9);
        assertEquals(105.85, dto.lng(), 1e-9);
    }
}