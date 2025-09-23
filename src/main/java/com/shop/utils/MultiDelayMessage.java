package com.shop.utils;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MultiDelayMessage<T> {
    private T data;
    private List<Long> delayTimes;

    public void setDelayMessage(T data, List<Long> delayTimes) {
        this.data = data;
        this.delayTimes = new ArrayList<>(delayTimes);
    }

    public Long removeNextDelay() {
        return delayTimes.remove(0);

    }

    public boolean HasNextDelayTime() {
        return !delayTimes.isEmpty();
    }
}
