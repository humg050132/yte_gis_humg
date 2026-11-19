package org.example.yte_gis_humg.Controller;

import org.example.yte_gis_humg.Dto.CoSoDTO;
import org.example.yte_gis_humg.Dto.CoSoGanDayDTO;
import org.example.yte_gis_humg.Dto.GeoJsonDTO;
import org.example.yte_gis_humg.Service.CoSoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/co-so")
public class CoSoController {

    private final CoSoService service;

    public CoSoController(CoSoService service) {
        this.service = service;
    }

    @GetMapping("/gan-day")
    public List<CoSoGanDayDTO> ganDay(@RequestParam double lat,
                                      @RequestParam double lng,
                                      @RequestParam(defaultValue = "1000") double radius,
                                      @RequestParam(required = false) String loai,
                                      @RequestParam(defaultValue = "20") int limit) {
        return service.timGanDay(lat, lng, radius, chuanHoa(loai), limit);
    }

    @GetMapping
    public CoSoDTO theoId(@RequestParam String id) {
        return service.layTheoId(id);
    }

    @GetMapping("/phuong/{phuongId}")
    public List<CoSoDTO> theoPhuong(@PathVariable Long phuongId,
                                    @RequestParam(required = false) String loai) {
        return service.theoPhuong(phuongId, chuanHoa(loai));
    }

    // Chuỗi rỗng từ client (ví dụ ?loai=) được coi như không lọc
    private String chuanHoa(String loai) {
        return (loai == null || loai.isBlank()) ? null : loai.trim();
    }
    @GetMapping("/khung-nhin")
    public List<CoSoDTO> khungNhin(@RequestParam double minLng,
                                   @RequestParam double minLat,
                                   @RequestParam double maxLng,
                                   @RequestParam double maxLat,
                                   @RequestParam(required = false) String loai,
                                   @RequestParam(defaultValue = "500") int limit) {
        return service.trongKhung(minLng, minLat, maxLng, maxLat, chuanHoa(loai), limit);
    }

    @GetMapping("/geojson")
    public GeoJsonDTO.FeatureCollection geoJson(@RequestParam double minLng,
                                                @RequestParam double minLat,
                                                @RequestParam double maxLng,
                                                @RequestParam double maxLat,
                                                @RequestParam(required = false) String loai,
                                                @RequestParam(defaultValue = "500") int limit) {
        return service.geoJson(minLng, minLat, maxLng, maxLat, chuanHoa(loai), limit);
    }
}