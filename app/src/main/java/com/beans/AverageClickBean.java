package beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import javax.management.*;
import java.io.Serializable;
import java.lang.management.ManagementFactory;

@Named("AverageClickBean")
@ApplicationScoped
public class AverageClickBean implements Serializable, AverageClickBeanMBean {

    private int numberClicks = 0;
    private long totalTime = 0;
    private long lastTimeClick = 0;
    private double averageClickTime = 0.0;

    @PostConstruct
    private void init()  {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("com.beans:type=MyAverageClickBean");

            if (!mbs.isRegistered(name)) {
                mbs.registerMBean(this, name);
                System.out.println("MBean registered successfully: " + name);
            } else {
                System.out.println("MBean already registered: " + name);
            }
        } catch (Exception e) {
            System.err.println("Failed to register MBean: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public double getAverageClickTime() {
        return averageClickTime;
    }

    @Override
    public void click() {
        long currentTime = System.currentTimeMillis();

        if (lastTimeClick != 0) {
            long timeDiff = currentTime - lastTimeClick;
            totalTime += timeDiff;
        }

        numberClicks++;
        lastTimeClick = currentTime;

        if (numberClicks > 1) {
            averageClickTime = (double) totalTime / (numberClicks - 1);
        } else {
            averageClickTime = 0.0;
        }
    }

}