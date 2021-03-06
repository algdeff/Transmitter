package Transmitter.Logic.DistributedComputing;

import java.io.Serializable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class ScheduledTaskEntity implements IRemoteTaskEntity, Delayed, Serializable {

    private Object _completedTaskResult;
    private Callable<Object> _taskUnit;
    private String _taskName, _assignedClientName;
    private long _targetTime;

    public ScheduledTaskEntity() {
        this(null);
    }
    public ScheduledTaskEntity(Callable<Object> taskUnit) {
        this(taskUnit, null);
    }
    public ScheduledTaskEntity(Callable<Object> taskUnit, String taskName) {
        this(taskUnit, taskName, null);
    }
    public ScheduledTaskEntity(Callable<Object> taskUnit, String taskName, String assignedClientName) {
        _taskUnit = taskUnit;
        _taskName = taskName;
        _assignedClientName = assignedClientName;
    }

    public void setTaskName(String taskName) {
        _taskName = taskName;
    }
    public String getTaskName() {
        return _taskName;
    }

    @Override
    public String getTaskType() {
        return TASK_TYPE_SCHEDULED;
    }

    public void setAssignedClientName(String assignedClientName) {
        _assignedClientName = assignedClientName;
    }
    public String getAssignedClientName() {
        return _assignedClientName;
    }

    public void setTaskUnit(Callable<Object> taskUnit) {
        _taskUnit = taskUnit;
    }
    public Callable<Object> getTaskUnit() {
        return _taskUnit;
    }

    public void setCompletedTaskResult(Object completedTaskResult) {
        _completedTaskResult = completedTaskResult;
    }
    public Object getCompletedTaskResult() {
        return _completedTaskResult;
    }

    public void setTargetTime(long epochSecond) {
        _targetTime = epochSecond;
    }
    public void setTargetTime(LocalDateTime targetTime) {
        ZonedDateTime zonedTargetTime = targetTime.atZone(ZoneId.systemDefault());
        setTargetTime(zonedTargetTime.toEpochSecond());
    }
    public LocalDateTime getTargetTime() {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(_targetTime), ZoneId.systemDefault());
    }

    public long getTimeRemainSec() {
        long epochSecondNow = ZonedDateTime.now().toEpochSecond(); //System.currentTimeMillis() for epoch ms.
        return _targetTime - epochSecondNow;
    }

    /**
     *  Сортируем задачи в очереди(DelayQueue<ScheduledTaskEntity>()) по ближайшему времени выполнения
     */
    @Override
    public long getDelay(TimeUnit timeUnit) {
        return timeUnit.convert(getTimeRemainSec(), TimeUnit.SECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        long diff = getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS);
        return diff > 0 ? 1 : -1;
    }
}