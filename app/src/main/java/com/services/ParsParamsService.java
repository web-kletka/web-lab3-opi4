package roma.services;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import roma.data.common.customException.ValidException;

import java.util.HashMap;

@ApplicationScoped
@Getter
public class ParsParamsService {
    private Float x = null;
    private Float y = null;
    private Float z = null;

    private Float r = null;

    public ParsParamsService(){
    }

    public void pars(String x, String y, String z, String r) throws ValidException  {
        pars(new HashMap<>() {{
            put("x", x);
            put("y", y);
            put("z", z);
            put("r", r);
        }});
    }


    private void pars(HashMap<String, String> map){

        for (String key : map.keySet()) {
            switch (key) {
                case "x" -> x = Float.parseFloat(map.get(key));
                case "y" -> y = Float.parseFloat(map.get(key));
                case "z" -> z = Float.parseFloat(map.get(key));
                case "r" -> r = Float.parseFloat(map.get(key));
            }
        }
    }

    public void validParams() throws ValidException{
        if (x == null) throw new ValidException("x is empty");
        if (y == null) throw new ValidException("y is empty");
        if (z == null) throw new ValidException("y is empty");
        if (r == null) throw new ValidException("r is empty");
        if (-4.0 > x || x > 4.0) throw new ValidException("x must be between -4 and 4");
        if (-3.0 > y || y > 3.0) throw new ValidException("y must be between -3 and 3");
        if (-3.0 > z || z > 3.0) throw new ValidException("z must be between -3 and 3");
        if (1 > r || r > 5) throw new ValidException("r must be between 1 and 5");
    }

}
