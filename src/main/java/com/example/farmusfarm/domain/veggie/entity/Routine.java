package com.example.farmusfarm.domain.veggie.entity;

import com.example.farmusfarm.common.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Date;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@Entity(name = "routine")
public class Routine extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "routine_id")
    private Long id;

    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private String content;

    @Column
    private int period = 0;

    @Column(nullable = false)
    private boolean isDone = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veggie_id")
    private Veggie veggie;

    public static Routine createRoutine(Date date, String content, int period, Veggie veggie) {
        Routine routine = Routine.builder()
                .date(date)
                .content(content)
                .period(period)
                .veggie(veggie)
                .build();

        veggie.getRoutines().add(routine);
        return routine;
    }

    public void updateDone() {
        this.isDone = true;
    }


}
