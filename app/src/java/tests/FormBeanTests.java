package tests;

import com.roma.beans.CheckerBean;
import com.roma.beans.FormBean;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FormBeanTests {


    @Mock
    private CheckerBean checkerBean;
    @Mock
    private FacesContext facesContext;
    @Mock
    private ExternalContext externalContext;

    @InjectMocks
    private FormBean formBean;

    private MockedStatic<FacesContext> mockedFacesContext;

    @BeforeEach
    void setUp() {
        mockedFacesContext = mockStatic(FacesContext.class);
        mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
    }

    @AfterEach
    void tearDown() {
        mockedFacesContext.close();
    }

    @Test
    void testProcessRequestSuccess() throws IOException {
        when(facesContext.getExternalContext()).thenReturn(externalContext);

        formBean.setX("1.0");
        formBean.setY("2.0");
        formBean.setZ("3.0");
        formBean.setR("4.0");

        formBean.processRequest();

        verify(checkerBean).check("1.0", "2.0", "3.0", "4.0");
        verify(externalContext).redirect("index.xhtml");
    }


    @Test
    void testProcessRequestWithIOException() throws IOException {

        when(facesContext.getExternalContext()).thenReturn(externalContext);

        // Устанавливаем тестовые данные
        formBean.setX("1.0");
        formBean.setY("2.0");
        formBean.setZ("3.0");
        formBean.setR("4.0");

        doThrow(new IOException("Test exception")).when(externalContext).redirect("index.xhtml");

        assertDoesNotThrow(() -> formBean.processRequest());

        verify(checkerBean).check("1.0", "2.0", "3.0", "4.0");
    }

    @Test
    void testCheckboxGettersAndSetters() {
        formBean.setCheckbox1(true);
        formBean.setCheckbox2(false);
        formBean.setCheckbox3(true);
        formBean.setCheckbox4(false);
        formBean.setCheckbox5(true);

        assertTrue(formBean.isCheckbox1());
        assertFalse(formBean.isCheckbox2());
        assertTrue(formBean.isCheckbox3());
        assertFalse(formBean.isCheckbox4());
        assertTrue(formBean.isCheckbox5());
    }

    @Test
    void testParameterGettersAndSetters() {
        formBean.setX("1.5");
        formBean.setY("2.5");
        formBean.setZ("3.5");
        formBean.setR("4.5");

        assertEquals("1.5", formBean.getX());
        assertEquals("2.5", formBean.getY());
        assertEquals("3.5", formBean.getZ());
        assertEquals("4.5", formBean.getR());
    }
}