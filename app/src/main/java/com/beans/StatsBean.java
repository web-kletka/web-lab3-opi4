package beans;

import data.models.MyEntityModel;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import services.EntityModelService;

import javax.management.*;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.util.List;

@Named("StatsBean")
@ApplicationScoped
public class StatsBean implements Serializable, StatsBeanMBean {

    private int totalNumberPoints = 0;
    private int numberGotItPoints = 0;
    private int numberMissedPoints = 0;

    @Inject
    private AverageClickBean averageClickBean;

    @Inject
    private EntityModelService entityModelService;

    @PostConstruct
    private void init(){
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("com.beans:type=MyStatsBean");

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

        List<MyEntityModel> points = entityModelService.findAllUsers();
        for (MyEntityModel point : points) {
            updateStats(point.isResult());
        }
    }

    @Override
    public void updateStats(boolean hit) {
        totalNumberPoints++;
        if (hit) {
            numberGotItPoints++;
        } else {
            numberMissedPoints++;
        }
    }

    @Override
    public void reset() {
        totalNumberPoints = 0;
        numberGotItPoints = 0;
        numberMissedPoints = 0;
    }

    @Override
    public double getAverageClickPoints() {
        return averageClickBean.getAverageClickTime();
    }

    @Override
    public int getTotalNumberPoints() {
        return totalNumberPoints;
    }

    @Override
    public int getNumberGotItPoints() {
        return numberGotItPoints;
    }

    @Override
    public int getNumberMissedPoints() {
        return numberMissedPoints;
    }
}
