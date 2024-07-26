package com.deepu.os.cpuscheduling.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProcessObjects {
    private Character[] jobs;
    private Integer[] arrivalTimes;
    private Integer[] burstTimes;
    private Integer[] finishTimes;
    private Integer[] turnAroundTimes;
    private Integer[] waitingTimes;
    private Integer[] priorities;
    private Integer timeQuantum;
    private Double averageWaitingTime;
    private Double averageTurnAroundTime;
}
