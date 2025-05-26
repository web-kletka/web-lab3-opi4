package beans;

public interface StatsBeanMBean {
    double getAverageClickPoints();
    void updateStats(boolean hit);
    void reset();

    int getTotalNumberPoints();
    int getNumberGotItPoints();
    int getNumberMissedPoints();
}
