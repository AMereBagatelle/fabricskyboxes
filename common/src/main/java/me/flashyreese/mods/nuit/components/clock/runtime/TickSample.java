package me.flashyreese.mods.nuit.components.clock.runtime;

import java.util.Objects;

public class TickSample extends InterpolatedSample {
    private boolean initialized;
    private boolean available;
    private long tickId;
    private Object identity;

    void update(long tickId, long ticks, boolean available, Object identity) {
        if (!this.initialized || this.available != available || !Objects.equals(this.identity, identity)) {
            this.initialized = true;
            this.available = available;
            this.tickId = tickId;
            this.previousTicks = ticks;
            this.currentTicks = ticks;
            this.identity = identity;
            return;
        }

        if (this.tickId != tickId) {
            this.tickId = tickId;
            this.previousTicks = this.currentTicks;
        }

        this.currentTicks = ticks;
    }
}
