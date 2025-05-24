package services;

import java.util.Locale;
import java.util.ResourceBundle;

public class LocalService {
    private static LocalService localService;

    private ResourceBundle resourceBundle;
    private final String[] SUPPORTED_LOCALES = {"en", "ru"};

    private LocalService() {
        getResourceBundle();
    }

    public static LocalService getInstance() {
        if (localService == null) {
            localService = new LocalService();
        }
        return localService;
    }

    private Locale getResourceLocale() {
        for (String lang : SUPPORTED_LOCALES) {
            try {
                ResourceBundle bundle = ResourceBundle.getBundle("messages", new Locale(lang));
                if (bundle != null) {
                    return new Locale(lang);
                }
            } catch (Exception ignored) {}
        }
        return new Locale("en");
    }

    private void getResourceBundle() {
        Locale locale = getResourceLocale();
        resourceBundle = ResourceBundle.getBundle("locale.messages", locale);
    }

    public ResourceBundle getMessage() {
        return resourceBundle;
    }
}