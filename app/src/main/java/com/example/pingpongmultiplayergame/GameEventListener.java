package com.example.pingpongmultiplayergame;

public interface GameEventListener {
    void collisionWithPlatformEvent(boolean collidedWithMyPlatform);

    void collisionWithBorderEvent(boolean collidedWithLeftBorder);

    void ballLeftTheGameZoneEvent(boolean enemyLose);

    void gamePausedEvent();

    void gameResumeEvent();

    void newRoundStartedEvent();
}
