package com.krs.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Entity
@Table(name = "tasks")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralTaskInfo {

    @Id
    private UUID id;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "threads", nullable = false)
    private int threads;

    @Column(name = "task_type", nullable = false)
    private String taskType;
}