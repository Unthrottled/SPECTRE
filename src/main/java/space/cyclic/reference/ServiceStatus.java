package space.cyclic.reference;

public class ServiceStatus {
    enum ServiceState {ALIVE, DEAD}

    private ServiceState currentServiceState = ServiceState.ALIVE;

    public boolean isServiceAlive() {
        return ServiceState.ALIVE == currentServiceState;
    }

    public void serviceDied() {
        currentServiceState = ServiceState.DEAD;
    }

    public void serviceReanimated() {
        currentServiceState = ServiceState.ALIVE;
    }
}
