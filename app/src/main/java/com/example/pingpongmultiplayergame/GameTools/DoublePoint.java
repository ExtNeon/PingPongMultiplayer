package com.example.pingpongmultiplayergame.GameTools;

/**
 * Примитивный класс - контейнер, позволяющий представить точку на двумерном пространстве с дробным значением координат
 */
public class DoublePoint {
    public double x, y;

    public DoublePoint(double x, double y) {
        this.set(x, y);
    }

    public DoublePoint(DoublePoint copyFrom) {
        set(copyFrom);
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void set(DoublePoint copyFrom) {
        set(copyFrom.x, copyFrom.y);
    }
}
