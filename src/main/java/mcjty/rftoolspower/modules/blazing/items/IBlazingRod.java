package mcjty.rftoolspower.modules.blazing.items;

public interface IBlazingRod {

    boolean isValid();

    int getInfusionStepsLeft();
    void setInfusionStepsLeft(int steps);

    float getAgitationTimeLeft();
    void setAgitationTimeLeft(float time);

    // Quality is expressed in RF per 1000 ticks
    float getPowerQuality();
    void setPowerQuality(float quality);

    // Duration is expressed in ticks
    float getPowerDuration();
    void setPowerDuration(float duration);
}
