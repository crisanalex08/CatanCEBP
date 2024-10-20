package Classes;

import Enums.PortType;

public class Port {
    private int position;
    private PortType type;

    public Port(int position, PortType type) {
        this.position = position;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Port{" +
                "position=" + position +
                ", type=" + type +
                '}';
    }
}
