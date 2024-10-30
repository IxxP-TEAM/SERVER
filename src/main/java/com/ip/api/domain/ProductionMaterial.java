package com.ip.api.domain;

import com.ip.api.domain.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionMaterial extends BaseEntity {
	
	@ManyToOne
	@JoinColumn(name = "production_id", referencedColumnName = "productionId")
	private Production production;
	@ManyToOne
	@JoinColumn(name = "product_id", referencedColumnName = "productId")
	private Product product;
	@Column(nullable = false)
	private int consumedQuantity;
}
