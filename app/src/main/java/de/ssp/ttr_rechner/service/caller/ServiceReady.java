package de.ssp.ttr_rechner.service.caller;

public interface ServiceReady<T> {
    public void serviceReady(boolean success, T value, String errorMessage);
}
