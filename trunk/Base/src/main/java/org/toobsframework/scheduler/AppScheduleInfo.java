package org.toobsframework.scheduler;

public class AppScheduleInfo {
  private String jobClass;
  private String jobName;
  private String groupName;
  private String triggerType;
  private String jobSchedule;
  private String jobEnvCronProperty;
  
  public String getGroupName() {
    return groupName;
  }
  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }
  public String getJobClass() {
    return jobClass;
  }
  public void setJobClass(String jobClass) {
    this.jobClass = jobClass;
  }
  public String getJobEnvCronProperty() {
    return jobEnvCronProperty;
  }
  public void setJobEnvCronProperty(String jobEnvCronProperty) {
    this.jobEnvCronProperty = jobEnvCronProperty;
  }
  public String getJobName() {
    return jobName;
  }
  public void setJobName(String jobName) {
    this.jobName = jobName;
  }
  public String getJobSchedule() {
    return jobSchedule;
  }
  public void setJobSchedule(String jobSchedule) {
    this.jobSchedule = jobSchedule;
  }
  public String getTriggerType() {
    return triggerType;
  }
  public void setTriggerType(String triggerType) {
    this.triggerType = triggerType;
  }
  
}
