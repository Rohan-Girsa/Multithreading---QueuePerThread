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
@Table(name = "tbl_base_test")
@Data
public class TblBase {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String msisdn;
	private String context;
	private String dnis;
	@Column(name = "server_id")
	private Integer serverId;
	private String status;
	@Column(name = "date_time")
	private Date dateTime;
	private Integer priority;
	@Column(name = "queue_code")
	private String queueCode;
	@Column(name = "template_id")
	private Long templateId;
	@Column(name = "account_id")
	private Long accountId;
	@Column(name = "user_id")
	private Integer userId;
	@Column(name = "voice_log_id")
	private Long voiceLogId;
}