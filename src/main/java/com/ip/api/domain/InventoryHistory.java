package com.ip.api.domain;

import java.time.LocalDate;

import com.ip.api.domain.common.BaseEntity;
import com.ip.api.domain.enums.ChangeType;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class InventoryHistory extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long historyId;
	@ManyToOne
	@JoinColumn(name = "product_id", referencedColumnName = "productId")
	private Product product;
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "userId")
	private User user;
	@Column(nullable = false)
	private ChangeType changeType;
	@Column(nullable = false)
	private int changeQuantity;
	@Column(nullable = false)
	private LocalDate expirationDate;
	@Column(nullable = false)
	private LocalDate changeDate;
	@Column(columnDefinition = "TEXT")
	private String adjustment;
}
