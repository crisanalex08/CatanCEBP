package Classes;

import Enums.ResourceType;

public class Trade {
    private String InitiatorPlayerId;
    private String tradePlayerId;
    private Resources resourcesGiven;
    private Resources resourcesReceived;

    public Trade(String playerId, Resources resourcesGiven, Resources resourcesReceived) {
        this.InitiatorPlayerId = playerId;
        this.resourcesGiven = resourcesGiven;
        this.resourcesReceived = resourcesReceived;
    }

    public String getPlayerId() {
        return InitiatorPlayerId;
    }

    public String getTradePlayerId() {
        return tradePlayerId;
    }


}
