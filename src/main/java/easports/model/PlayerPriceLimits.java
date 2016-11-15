package easports.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class PlayerPriceLimits {

    private Long id;
    private PlatformPriceLimits priceLimits;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlatformPriceLimits getPriceLimits() {
        return priceLimits;
    }

    public void setPriceLimits(PlatformPriceLimits priceLimits) {
        this.priceLimits = priceLimits;
    }
}
