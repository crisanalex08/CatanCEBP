package Classes;

import Enums.ResourceType;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Trade {
    private Map<ResourceType, Integer> offering;
    private Map<ResourceType, Integer> requesting;
    private boolean accepted;
    private final CountDownLatch responseLatch;

    public Trade(Map<ResourceType, Integer> offering, Map<ResourceType, Integer> requesting) {
        this.offering = offering;
        this.requesting = requesting;
        this.accepted = false;
        this.responseLatch = new CountDownLatch(1);
    }

    public Map<ResourceType, Integer> getOffering() {
        return offering;
    }

    public Map<ResourceType, Integer> getRequesting() {
        return requesting;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted() {
        this.accepted = true;
        responseLatch.countDown();
    }
    public void setRejected() {
        this.accepted = false;
        responseLatch.countDown();
    }

    public boolean waitForResponse(long timeout) throws InterruptedException {
        return responseLatch.await(timeout, TimeUnit.MILLISECONDS);
    }




}
