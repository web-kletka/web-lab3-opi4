package tests;

import com.roma.beans.TableBean;
import com.roma.data.models.MyEntityModel;
import com.roma.services.EntityModelService;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TableBeanTests {
    @Mock
    private EntityModelService entityModelService;

    @Mock
    private FacesContext facesContext;

    @Mock
    private ExternalContext externalContext;

    @InjectMocks
    private TableBean tableBean;

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
    void testClearTable_SuccessfulRedirect() throws IOException {
        when(facesContext.getExternalContext()).thenReturn(externalContext);
        tableBean.clearTable();

        verify(entityModelService, times(1)).clearTable();
        verify(externalContext, times(1)).redirect("index.xhtml");
    }

    @Test
    void testClearTable_IOExceptionHandling() throws IOException {
        when(facesContext.getExternalContext()).thenReturn(externalContext);
        doThrow(new IOException("Test IO Exception")).when(externalContext).redirect("index.xhtml");

        tableBean.clearTable();

        verify(entityModelService, times(1)).clearTable();
        verify(externalContext, times(1)).redirect("index.xhtml");
    }

    @Test
    void testGetRequestHistory_ReturnsList() {
        List<MyEntityModel> expectedList = Arrays.asList(new MyEntityModel(), new MyEntityModel());
        when(entityModelService.findAllUsers()).thenReturn(expectedList);

        List<MyEntityModel> result = tableBean.getRequestHistory();

        assertEquals(expectedList, result);
        verify(entityModelService, times(1)).findAllUsers();
    }
}