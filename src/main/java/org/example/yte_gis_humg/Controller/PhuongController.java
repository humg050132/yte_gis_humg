package org.example.yte_gis_humg.Controller;

import org.example.yte_gis_humg.Dto.PhuongDTO;
import org.example.yte_gis_humg.Dto.ThongKeDTO;
import org.example.yte_gis_humg.Service.CoSoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phuong")
public class PhuongController {

    private final CoSoService service;

    public PhuongController(CoSoService service) {
        this.service = service;
    }

    @GetMapping
    public List<PhuongDTO> theoQuan(@RequestParam String quan) {
        return service.phuongTheoQuan(quan);
    }

    @GetMapping("/thong-ke")
    public List<ThongKeDTO> thongKe(@RequestParam String quan) {
        return service.thongKeTheoQuan(quan);
    }
}