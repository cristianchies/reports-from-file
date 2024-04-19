package sample;

import java.math.BigDecimal;
import java.util.Objects;

public class Client {
    private final Integer id;
    private Integer saleCount;
    private BigDecimal saleValue;

    public Client(Integer id, Integer saleCount, BigDecimal saleValue) {
        if (id == null || saleCount == null || saleValue == null) {
            throw new IllegalArgumentException("Client.id, Client.saleCount and Client.saleValue are required");
        }
        this.id = id;
        this.saleCount = saleCount > 0 ? saleCount : 0;
        this.saleValue = saleValue.compareTo(BigDecimal.ZERO) > 0 ? saleValue : BigDecimal.ZERO;
    }

    public Integer getId() {
        return id;
    }

    public Integer getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(Integer saleCount) {
        if (saleCount == null) {
            return;
        }
        this.saleCount = this.saleCount + saleCount;
    }

    public BigDecimal getSaleValue() {
        return saleValue;
    }

    public void setSaleValue(BigDecimal saleValue) {
        if (saleValue == null) {
            return;
        }
        this.saleValue = this.saleValue.add(saleValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client client)) return false;
        return Objects.equals(id, client.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}