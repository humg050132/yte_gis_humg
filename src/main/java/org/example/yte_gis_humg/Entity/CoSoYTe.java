package org.example.yte_gis_humg.Entity;

import jakarta.persistence.*;
import lombok.*;

import org.locationtech.jts.geom.Point;
@Entity
@Table(name = "co_so_y_te")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoSoYTe {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "name")
    private String name;
    @Column(name = "amenity")
    private String amenity;
    @Column(name = "healthcare")
    private String healthcare;

    @Column(name = "loai",insertable = false,updatable = false)
    private String loai;

    @Column(name = "phuong_id")
    private Long phuongId;
    @Column(name = "geom")
    private Point geom;
}
