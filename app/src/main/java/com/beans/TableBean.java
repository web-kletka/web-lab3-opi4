package beans;

import data.models.MyEntityModel;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Startup;
import services.EntityModelService;
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
import java.util.List;

@Named("TableBean")
@SessionScoped
@Getter
@Setter
@Startup
public class TableBean implements Serializable {

    @Inject
    private EntityModelService entityModelService;


    public void clearTable(){
        entityModelService.clearTable();
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<MyEntityModel> getRequestHistory() {
        return entityModelService.findAllUsers();
    }
}
