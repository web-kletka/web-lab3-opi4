package tests;

import com.roma.beans.CheckerBean;
import com.roma.data.common.customException.ValidException;
import com.roma.data.models.MyEntityModel;
import com.roma.services.CheckerModelService;
import com.roma.services.EntityModelService;
import com.roma.services.LocalService;
import com.roma.services.ParsParamsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckerBeanTests {

    @Mock
    private EntityModelService entityModelService;

    @Mock
    private CheckerModelService checkerModelService;

    @Mock
    private ParsParamsService parsParamsService;

    @Mock
    private LocalService localService;

    @InjectMocks
    private CheckerBean checkerBean;


    @Captor
    private ArgumentCaptor<MyEntityModel> modelCaptor;

    @BeforeEach
    void setUp() throws Exception {
        checkerBean = new CheckerBean();

        checkerBean.init();

        setPrivateField(checkerBean, "entityModelService", entityModelService);
        setPrivateField(checkerBean, "checkerModelService", checkerModelService);
        setPrivateField(checkerBean, "parsParamsService", parsParamsService);
    }

    private void setPrivateField(Object target, String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void testCheckSuccess() throws ValidException {
        // Подготовка
        String x = "1.0", y = "2.0", z = "3.0", r = "4.0";
        float parsedX = 1.0F, parsedY = 2.0F, parsedZ = 3.0F, parsedR = 4.0F;
        boolean calcResult = true;

        // Настройка моков
        doNothing().when(parsParamsService).pars(x, y, z, r);
        doNothing().when(parsParamsService).validParams();
        when(parsParamsService.getX()).thenReturn(parsedX);
        when(parsParamsService.getY()).thenReturn(parsedY);
        when(parsParamsService.getZ()).thenReturn(parsedZ);
        when(parsParamsService.getR()).thenReturn(parsedR);
        when(checkerModelService.calculate(parsedX, parsedY, parsedZ, parsedR)).thenReturn(calcResult);

        // Вызов
        checkerBean.check(x, y, z, r);

        // Проверки
        verify(parsParamsService).pars(x, y, z, r);
        verify(parsParamsService).validParams();
        verify(checkerModelService).calculate(parsedX, parsedY, parsedZ, parsedR);

        verify(entityModelService).saveModel(modelCaptor.capture());
        MyEntityModel savedModel = modelCaptor.getValue();

        assertEquals(parsedX, savedModel.getX());
        assertEquals(parsedY, savedModel.getY());
        assertEquals(parsedZ, savedModel.getZ());
        assertEquals(parsedR, savedModel.getR());
        assertEquals(calcResult, savedModel.isResult());
    }

    @Test
    void testCheckValidException() throws ValidException {
        String x = "invalid", y = "2.0", z = "3.0", r = "4.0";
        String errorMessage = "Некорректные параметры";

        doThrow(new ValidException(errorMessage)).when(parsParamsService).pars(x, y, z, r);

        checkerBean.check(x, y, z, r);

        verify(parsParamsService).pars(x, y, z, r);
        verify(parsParamsService, never()).validParams();
        verify(checkerModelService, never()).calculate(anyFloat(), anyFloat(), anyFloat(), anyFloat());
        verify(entityModelService, never()).saveModel(any(MyEntityModel.class));

        assertEquals(errorMessage, checkerBean.getResult());
    }

    @Test
    void testInitialResult() {
        assertEquals(LocalService.getInstance().getMessage().getString("text.bad.result"), checkerBean.getResult());
    }
}