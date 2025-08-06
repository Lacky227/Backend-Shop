package com.fullstackfamily.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Schema(description = "Запит на оновлення товару")
public class UpdateProductRequest {

    @Schema(description = "Унікальний артикул товару", example = "SKU12345")
    private String sku;

    @Schema(description = "Назва товару", example = "Футболка з логотипом")
    private String name;

    @Schema(description = "Бренд товару", example = "Lucky")
    private Long brandId;

    @Schema(description = "Гендерна категорія", example = "man")
    private String gender;

    @Schema(description = "Категорія товару", example = "dresses")
    private Long categoryId;

    @Schema(description = "Ціна товару", example = "799.99")
    private BigDecimal price;

    @Schema(description = "Стара ціна товару", example = "999.99")
    private BigDecimal oldPrice;

    @Schema(description = "Чи має знижку", example = "true")
    private Boolean hasdiscount;

    @Schema(description = "Чи з нової колекції", example = "false")
    private Boolean newCollection;

    @Schema(description = "Чи в топ продажів", example = "true")
    private Boolean topSales;

    @Schema(description = "Розміри та їх кількість (наприклад: M - 5 шт.)", example = "{\"M\": 5, \"L\": 3}")
    private Map<String, Integer> sizes;

    @Schema(description = "Колір товару", example = "чорний")
    private Long colorId;

    @Schema(description = "Сезонність", example = "spring_summer")
    private String season;

    @Schema(description = "Опис товару", example = " Універсальна жіноча сукня комфортного крою, яка поєднує в собі стиль, зручність та натуральність.")
    private String description;

    @Schema(description = "Матеріал товару", example = "Льон")
    private String material;
}
