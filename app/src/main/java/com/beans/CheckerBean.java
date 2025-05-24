package roma.beans;

import roma.services.CheckerModelService;
import roma.services.EntityModelService;
import roma.services.LocalService;
import roma.services.ParsParamsService;
import roma.data.common.customException.ValidException;
import roma.data.models.MyEntityModel;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Named("CheckerBean")
@SessionScoped
public class CheckerBean implements Serializable {

    @Getter
    @Setter
    private String result;

    @Inject
    private EntityModelService entityModelService;
    @Inject
    private CheckerModelService checkerModelService;
    @Inject
    private ParsParamsService parsParamsService ;

    @PostConstruct
    public void init() {
        result = LocalService.getInstance().getMessage().getString("text.bad.result");
    }

    public void check(String x, String y, String z, String r) {
        long startTime = System.currentTimeMillis();
        try {
            parsParamsService.pars(x, y, z, r);
            parsParamsService.validParams();
            boolean resultOfCalc = checkerModelService.calculate(parsParamsService.getX(), parsParamsService.getY(), parsParamsService.getZ(), parsParamsService.getR());
            MyEntityModel myEntityModel = new MyEntityModel(0, parsParamsService.getX(), parsParamsService.getY(), parsParamsService.getZ(), parsParamsService.getR(), resultOfCalc, System.currentTimeMillis() - startTime, new Date(), "ok");
            result = myEntityModel.toString();
            entityModelService.saveModel(myEntityModel);
        } catch (ValidException e) {
            result = e.getMessage();
        }
    }
}
