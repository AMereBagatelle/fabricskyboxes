package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.fabricskyboxes.util.Utils;

import java.util.Collection;
import java.util.List;

public class Loop {
    public static final Loop DEFAULT = new Loop(7, ImmutableList.of());
    public static final Codec<Loop> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Utils.getClampedDouble(1, Double.MAX_VALUE).optionalFieldOf("days", 7.0d).forGetter(Loop::getDays),
            MinMaxEntry.CODEC.listOf().optionalFieldOf("ranges", ImmutableList.of()).forGetter(Loop::getRanges)
    ).apply(instance, Loop::new));
    private final double days;
    private final List<MinMaxEntry> ranges;

    public Loop(double days, List<MinMaxEntry> ranges) {
        this.days = days;
        this.ranges = ranges;
    }

    public double getDays() {
        return days;
    }

    public List<MinMaxEntry> getRanges() {
        return ranges;
    }

    public static class Builder {
        private final List<MinMaxEntry> ranges = Lists.newArrayList();
        private double days = 1;

        public Builder days(double days) {
            this.days = days;
            return this;
        }

        public Builder ranges(Collection<MinMaxEntry> worldIds) {
            this.ranges.addAll(worldIds);
            return this;
        }

        public Loop build() {
            return new Loop(this.days, this.ranges);
        }
    }
}
