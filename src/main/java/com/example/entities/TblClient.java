package com.example.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "tbl_client")
@Data
public class TblClient {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String status;
	@Column(name = "date_time")
	private Date dateTime;
	@Column(name = "template_id")
	private Long templateId;
	@Column(name = "tps")
	private Integer tps;
}
