package de.ssp.ttr_rechner.service.caller;

public interface ServiceFinish<T> {
    public void serviceFinished(boolean success, T value, String errorMessage);
}
