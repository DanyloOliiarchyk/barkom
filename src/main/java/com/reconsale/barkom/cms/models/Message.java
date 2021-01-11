package com.reconsale.barkom.cms.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    @NotNull
    @Column(length = 500)
    private String title;

    @NotEmpty
    @NotNull
    @Column(length = 1000)
    private String text;

    @NotEmpty
    @NotNull
    private LocalDate startDate;

    @NotEmpty
    @NotNull
    private LocalDate endDate;

    public enum Status {
        Редагується, Опубліковано
    }

    @NotEmpty
    @NotNull
    private Message.Status status;

    @NotEmpty
    @NotNull
    private String category;
}
