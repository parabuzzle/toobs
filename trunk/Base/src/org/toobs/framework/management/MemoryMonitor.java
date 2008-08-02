package org.toobs.framework.management;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryNotificationInfo;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.Map;

import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MemoryMonitor {

  private static Log log = LogFactory.getLog(MemoryMonitor.class);

  private static final Map<String, MemoryPoolMXBean> memPools = new HashMap<String, MemoryPoolMXBean>();
  private static MemoryMonitor instance;
  
  private MemoryMonitor() {
    init();
  }
  
  public static MemoryMonitor getInstance() {
    if (instance == null) {
      instance = new MemoryMonitor();
    }
    return instance;
  }
  
  private static final NotificationListener listener = new NotificationListener() {
    public void handleNotification(Notification notification, Object handback) {
        if (MemoryNotificationInfo.MEMORY_THRESHOLD_EXCEEDED.equals(notification.getType())) {
            CompositeData cd = (CompositeData)notification.getUserData();
            MemoryNotificationInfo info = MemoryNotificationInfo.from(cd);
            MemoryUsage memUsage = info.getUsage();
            String poolName = info.getPoolName();
            System.out.printf("Notification: %s, count=%d, usage=[%s]%n",
                              poolName,
                              info.getCount(),
                              memUsage);
            MemoryPoolMXBean memPool = memPools.get(poolName);
            setUsageThreshold(memPool, 0.85);
        }
    }
  };

  public static void setUsageThreshold(MemoryPoolMXBean memPool, double percentage) {
    MemoryUsage memUsage = memPool.getUsage();
    long max = memUsage.getMax();
    memPool.setUsageThreshold((long)(max * percentage));
  }
  
  private void init() {
    MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
    ((NotificationBroadcaster)mem).addNotificationListener(listener, null, null);

    for (MemoryPoolMXBean memPool : ManagementFactory.getMemoryPoolMXBeans()) {
      if (memPool.isUsageThresholdSupported()) {
        memPools.put(memPool.getName(), memPool);
        log.info("Putting Pool " + memPool.getName());
        setUsageThreshold(memPool, 0.7);
      }
    }
  }
}
