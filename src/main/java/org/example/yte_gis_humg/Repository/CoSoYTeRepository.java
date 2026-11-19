package org.example.yte_gis_humg.Repository;

import org.example.yte_gis_humg.Dto.CoSoGanDayProjection;
import org.example.yte_gis_humg.Dto.PhuongProjection;
import org.example.yte_gis_humg.Dto.ThongKeProjection;
import org.example.yte_gis_humg.Entity.CoSoYTe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoSoYTeRepository extends JpaRepository<CoSoYTe, String> {
    List<CoSoYTe> findByPhuongId(Long phuongId);
    List<CoSoYTe> findByLoai(String loai);
    @Query(value = """
        SELECT id AS "id",
               COALESCE(name, '(không tên)') AS "ten",
               loai AS "loai",
               ST_Y(geom) AS "lat",
               ST_X(geom) AS "lng",
               ST_Distance(geom::geography, ST_MakePoint(:lng, :lat)::geography) AS "khoangCachM"
        FROM public.co_so_y_te
        WHERE (CAST(:loai AS text) IS NULL OR loai = CAST(:loai AS text))
          AND ST_DWithin(geom::geography, ST_MakePoint(:lng, :lat)::geography, :radius)
        ORDER BY "khoangCachM"
        LIMIT :limit
        """, nativeQuery = true)
    List<CoSoGanDayProjection> timGanDay(@Param("lat") double lat,
                                         @Param("lng") double lng,
                                         @Param("radius") double radius,
                                         @Param("loai") String loai,
                                         @Param("limit") int limit);

    @Query(value = """
    SELECT id, name, amenity, healthcare, loai, phuong_id, geom
    FROM public.co_so_y_te
    WHERE geom && ST_MakeEnvelope(:minLng, :minLat, :maxLng, :maxLat, 4326)
      AND (CAST(:loai AS text) IS NULL OR loai = CAST(:loai AS text))
    LIMIT :limit
    """, nativeQuery = true)
    List<CoSoYTe> timTrongKhung(@Param("minLng") double minLng,
                                @Param("minLat") double minLat,
                                @Param("maxLng") double maxLng,
                                @Param("maxLat") double maxLat,
                                @Param("loai") String loai,
                                @Param("limit") int limit);

    @Query(value = """
    SELECT fid AS "id", "NAME_3" AS "phuong", "NAME_2" AS "quan"
    FROM public."ranh_gioi_HN"
    WHERE "NAME_2" = :quan
    ORDER BY "NAME_3"
    """, nativeQuery = true)
    List<PhuongProjection> timPhuongTheoQuan(@Param("quan") String quan);

    @Query(value = """
    SELECT p."NAME_3" AS "phuong", c.loai AS "loai", COUNT(*) AS "soLuong"
    FROM public.co_so_y_te c
    JOIN public."ranh_gioi_HN" p ON p.fid = c.phuong_id
    WHERE p."NAME_2" = :quan
    GROUP BY p."NAME_3", c.loai
    ORDER BY p."NAME_3", "soLuong" DESC
    """, nativeQuery = true)
    List<ThongKeProjection> thongKeTheoQuan(@Param("quan") String quan);
}
