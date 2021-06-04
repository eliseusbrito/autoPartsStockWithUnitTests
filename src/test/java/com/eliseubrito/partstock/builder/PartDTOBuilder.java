package com.eliseubrito.partstock.builder;

import lombok.Builder;
import com.eliseubrito.partstock.dto.PartDTO;
import com.eliseubrito.partstock.enums.PartType;

@Builder
public class PartDTOBuilder {

    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String name = "Harmonic Balancer Assembly";

    @Builder.Default
    private String brand = "Dorman - OE Solutions";

    @Builder.Default
    private int max = 50;

    @Builder.Default
    private int quantity = 10;

    @Builder.Default
    private PartType type = PartType.ENGINE;

    public PartDTO toPartDTO() {
        return new PartDTO(id,
                name,
                brand,
                max,
                quantity,
                type);
    }
}
