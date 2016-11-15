package easports.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class PlatformPriceLimits {

    private Limits pc;
    private Limits xboxone;
    private Limits ps4;

    public Limits getPc() {
        return pc;
    }

    public void setPc(Limits pc) {
        this.pc = pc;
    }

    public Limits getXboxone() {
        return xboxone;
    }

    public void setXboxone(Limits xboxone) {
        this.xboxone = xboxone;
    }

    public Limits getPs4() {
        return ps4;
    }

    public void setPs4(Limits ps4) {
        this.ps4 = ps4;
    }
}
