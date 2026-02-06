package org.KaiFlo.SolarCell.Helpers;

public class BlockHelper {

    public static void executeForCubeAround(int x, int y, int z, int size, Callback callback) {
        for (int x1 = x - size / 2; x1 < x + size / 2; x1++) {
            for (int y1 = y - size / 2; y1 < y + size / 2; y1++) {
                for (int z1 = z - size / 2; z1 < z + size / 2; z1++) {
                    callback.accept(x1, y1, z1);
                }
            }
        }
    }
    public interface Callback {
        void accept(int x, int y, int z);
    }
}


