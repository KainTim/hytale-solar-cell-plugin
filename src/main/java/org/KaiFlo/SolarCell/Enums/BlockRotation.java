package org.KaiFlo.SolarCell.Enums;

public enum BlockRotation {
    NORTH,
    EAST,
    SOUTH,
    WEST;
    public static BlockRotation getEnum(int rotationIndex) {
        return switch ( rotationIndex )
        {
            case 0 -> NORTH;
            case 1 -> WEST;
            case 2 -> SOUTH;
            case 3 -> EAST;
            default -> NORTH;
        };

    }
}
