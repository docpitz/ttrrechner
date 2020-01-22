package de.ssp.ttr_rechner.service.caller;

public interface ServiceCaller {
    public void callService();
    public void cancelService();
    public boolean isServiceRunning();
}
