package com.deepu.os.cpuscheduling.request;

import com.deepu.os.cpuscheduling.constant.Constant;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class CommonRequest {
    @NotNull(message = Constant.ARRIVAL_TIME_NOT_PROVIDED)
    private List<Integer> arrivalTimes;
    @NotNull(message = Constant.BURST_TIME_NOT_PROVIDED)
    private List<Integer> burstTimes;
    private List<Integer> priorities;
    private int timeQuantum;
}
