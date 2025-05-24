package data.models;

import services.LocalService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Entity
@Table(name = "PointsResults")
@NoArgsConstructor
@AllArgsConstructor
public class MyEntityModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "x", nullable = false)
    private Float x;

    @Column(name = "y", nullable = false)
    private Float y;

    @Column(name = "z", nullable = false)
    private Float z;

    @Column(name = "r", nullable = false)
    private Float r;

    @Column(name = "result", nullable = false)
    private boolean result;

    @Column(name = "time", nullable = false)
    private long time;

    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Transient
    private String info;

    public MyEntityModel(String info) {
        this.info = info;
    }

    public String getXyzr(){
        return x + "," + y + "," + z +"," + r ;
    }

    @Override
    public String toString() {
        if (result) return LocalService.getInstance().getMessage().getString("text.success.result.got.it");
        return LocalService.getInstance().getMessage().getString("text.success.result.missed");
    }
}

