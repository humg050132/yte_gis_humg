package org.example.yte_gis_humg.Service;

import org.example.yte_gis_humg.Dto.*;
import org.example.yte_gis_humg.Mapper.CoSoMapper;
import org.example.yte_gis_humg.Repository.CoSoYTeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CoSoService {

    private static final Set<String> LOAI_HOP_LE = Set.of("benh_vien", "phong_kham", "nha_thuoc");
    private static final double BAN_KINH_TOI_DA = 5000;
    private static final int LIMIT_TOI_DA = 100;

    private final CoSoYTeRepository repo;
    private final CoSoMapper mapper;

    public CoSoService(CoSoYTeRepository repo, CoSoMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<CoSoGanDayDTO> timGanDay(double lat, double lng, double radius, String loai, int limit) {
        kiemTraToaDo(lat, lng);
        if (radius <= 0 || radius > BAN_KINH_TOI_DA) {
            throw loi("radius phải nằm trong (0, " + (int) BAN_KINH_TOI_DA + "]");
        }
        if (limit < 1 || limit > LIMIT_TOI_DA) {
            throw loi("limit phải nằm trong [1, " + LIMIT_TOI_DA + "]");
        }
        kiemTraLoai(loai);

        return repo.timGanDay(lat, lng, radius, loai, limit).stream()
                .map(mapper::toGanDayDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CoSoDTO layTheoId(String id) {
        return repo.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy cơ sở: " + id));
    }

    @Transactional(readOnly = true)
    public List<CoSoDTO> theoPhuong(Long phuongId, String loai) {
        kiemTraLoai(loai);
        return repo.findByPhuongId(phuongId).stream()
                .filter(c -> loai == null || loai.equals(c.getLoai()))
                .map(mapper::toDto)
                .toList();
    }

    private void kiemTraToaDo(double lat, double lng) {
        if (lat < -90 || lat > 90) throw loi("lat phải nằm trong [-90, 90]");
        if (lng < -180 || lng > 180) throw loi("lng phải nằm trong [-180, 180]");
    }

    private void kiemTraLoai(String loai) {
        if (loai != null && !LOAI_HOP_LE.contains(loai)) {
            throw loi("loai không hợp lệ, chọn một trong: " + LOAI_HOP_LE);
        }
    }

    private ResponseStatusException loi(String thongBao) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, thongBao);
    }

    private static final int LIMIT_KHUNG_TOI_DA = 1000;

    @Transactional(readOnly = true)
    public List<CoSoDTO> trongKhung(double minLng, double minLat, double maxLng, double maxLat,
                                    String loai, int limit) {
        if (minLng >= maxLng || minLat >= maxLat) {
            throw loi("minLng, minLat phải nhỏ hơn maxLng, maxLat");
        }
        kiemTraToaDo(minLat, minLng);
        kiemTraToaDo(maxLat, maxLng);
        if (limit < 1 || limit > LIMIT_KHUNG_TOI_DA) {
            throw loi("limit phải nằm trong [1, " + LIMIT_KHUNG_TOI_DA + "]");
        }
        kiemTraLoai(loai);

        return repo.timTrongKhung(minLng, minLat, maxLng, maxLat, loai, limit).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public GeoJsonDTO.FeatureCollection geoJson(double minLng, double minLat, double maxLng, double maxLat,
                                                String loai, int limit) {
        var features = trongKhung(minLng, minLat, maxLng, maxLat, loai, limit).stream()
                .map(c -> GeoJsonDTO.Feature.diem(c.lng(), c.lat(), thuocTinh(c)))
                .toList();
        return GeoJsonDTO.FeatureCollection.of(features);
    }

    @Transactional(readOnly = true)
    public List<PhuongDTO> phuongTheoQuan(String quan) {
        kiemTraQuan(quan);
        return repo.timPhuongTheoQuan(quan).stream()
                .map(p -> new PhuongDTO(p.getId(), p.getPhuong(), p.getQuan()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ThongKeDTO> thongKeTheoQuan(String quan) {
        kiemTraQuan(quan);
        return repo.thongKeTheoQuan(quan).stream()
                .map(t -> new ThongKeDTO(t.getPhuong(), t.getLoai(), t.getSoLuong()))
                .toList();
    }

    private Map<String, Object> thuocTinh(CoSoDTO c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", c.id());
        m.put("ten", c.ten());
        m.put("loai", c.loai());
        m.put("phuongId", c.phuongId());
        return m;
    }

    private void kiemTraQuan(String quan) {
        if (quan == null || quan.isBlank()) {
            throw loi("quan không được để trống");
        }
    }
}