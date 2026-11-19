package org.example.yte_gis_humg.Controller;

import org.example.yte_gis_humg.Service.CoSoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CoSoController.class)
class CoSoControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    CoSoService service;

    @Test
    void khungNhin_thieuThamSo_tra400() throws Exception {
        mvc.perform(get("/api/co-so/khung-nhin").param("minLng", "105.8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void theoId_khongTonTai_tra404() throws Exception {
        when(service.layTheoId("khong_ton_tai"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy"));

        mvc.perform(get("/api/co-so").param("id", "khong_ton_tai"))
                .andExpect(status().isNotFound());
    }
}