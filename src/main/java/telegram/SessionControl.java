package telegram;

import java.util.concurrent.atomic.AtomicBoolean;

/**
  Разделяемое состояние сессии: reader выставляет timedOut при таймауте,
  подавляет прощальное сообщение BookBot, если оно пришло после таймаута.
 */
public class SessionControl {
    private final AtomicBoolean timedOut = new AtomicBoolean(false);

    public void markTimedOut() {
        timedOut.set(true);
    }

    /** Возвращает true, если был таймаут, и сбрасывает флаг. */
    public boolean consumeTimedOut() {
        return timedOut.getAndSet(false);
    }
}