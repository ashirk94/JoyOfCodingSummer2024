package intermediate;

import com.sandwich.koan.Koan;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.sandwich.koan.constant.KoanConstants.__;
import static com.sandwich.util.Assert.assertEquals;

public class AboutLocale {

    @Koan
    public void localizedOutputOfDates() {
        Calendar cal = Calendar.getInstance();
        cal.set(2011, 3, 3); // Note: Months are 0-based, so 3 means April.
        Date date = cal.getTime();
        Locale localeBR = new Locale("pt", "BR"); // Portuguese, Brazil
        DateFormat dateformatBR = DateFormat.getDateInstance(DateFormat.FULL, localeBR);
        assertEquals(dateformatBR.format(date), "domingo, 3 de abril de 2011");

        Locale localeDE = new Locale("de"); // German
        DateFormat dateformatDE = DateFormat.getDateInstance(DateFormat.FULL, localeDE);
        assertEquals(dateformatDE.format(date), "Sonntag, 3. April 2011");
    }

    @Koan
    public void getCountryInformation() {
        Locale locBR = new Locale("pt", "BR");
        assertEquals(locBR.getDisplayCountry(), "Brazil");
        assertEquals(locBR.getDisplayCountry(locBR), "Brasil");

        Locale locCH = new Locale("it", "CH");
        assertEquals(locCH.getDisplayCountry(), "Switzerland");
        assertEquals(locCH.getDisplayCountry(locCH), "Svizzera");
        assertEquals(locCH.getDisplayCountry(new Locale("de", "CH")), "Schweiz");
    }
}
