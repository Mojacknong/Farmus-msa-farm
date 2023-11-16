package com.example.farmusfarm.domain.veggie.entity;

import com.example.farmusfarm.common.BaseEntity;
import com.example.farmusfarm.domain.challenge.entity.Registration;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@Table(indexes = {@Index(name = "veggie_user_id_idx", columnList = "user_id")})
@Entity(name = "veggie")
public class Veggie extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "veggie_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String veggieInfoId;

    @Column(nullable = false)
    private String veggieNickname;

    @Column(nullable = false)
    private String veggieImage;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private LocalDate birth;

    @OneToMany(mappedBy = "veggie")
    @Builder.Default
    private List<Routine> routines = new ArrayList<>();

    @OneToOne(mappedBy = "veggie")
    private Registration registration;

    @OneToMany(mappedBy = "veggie")
    @Builder.Default
    private List<Diary> diaries = new ArrayList<>();

    // 채소 별명, 채소 정보 id, 채소 생일
    public static Veggie createVeggie(Long userId, String veggieInfoId, String veggieNickname, String veggieImage, String birth, String color) {
        return Veggie.builder()
                .userId(userId)
                .veggieInfoId(veggieInfoId)
                .veggieNickname(veggieNickname)
                .veggieImage(veggieImage)
                .birth(LocalDate.parse(birth))
                .color(color)
                .build();
    }

    // 채소 수정
    public void updateVeggie(String nickname, String birth) {
        this.veggieNickname = nickname;
        this.birth = LocalDate.parse(birth);
    }

    // 챌린지 등록
    public void register(Registration registration) {
        this.registration = registration;
    }

    // 챌린지 삭제
    public void unregister() {
        this.registration = null;
    }

    public void addRoutine(Routine routine) {
        routines.add(routine);
    }

    public void addDiary(Diary diary) {
        diaries.add(diary);
    }
}
