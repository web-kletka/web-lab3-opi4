package beans;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Startup;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import javax.management.*;
import java.io.IOException;
import java.io.Serializable;
import java.lang.management.ManagementFactory;

@Named("FormBean")
@SessionScoped
@Getter
@Setter
@Startup
public class FormBean implements Serializable {
    private String x;
    private String y;
    private String z;
    private String r;

    private boolean checkbox1; //1r
    private boolean checkbox2; //2r
    private boolean checkbox3; //3r
    private boolean checkbox4; //4r
    private boolean checkbox5; //dynamic

    @Inject
    private CheckerBean checkerBean;


    public void processRequest() {
        checkerBean.check(x,y,z,r);

        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
