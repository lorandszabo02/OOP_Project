import controllers.ShowCountryController;
import views.ShowCountryForm;

public class Main {
    public static void main(String[] args) {
        ShowCountryForm showCountryForm = new ShowCountryForm();
        ShowCountryController showCountryController = new ShowCountryController(showCountryForm);
    }
}
