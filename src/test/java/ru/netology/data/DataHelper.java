package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import lombok.Value;
import net.gcardone.junidecode.Junidecode;
import org.checkerframework.checker.units.qual.C;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static java.lang.String.*;


public class DataHelper {
    private DataHelper() {}

    private static final Faker faker = new Faker(new Locale("en"));

    @Value
    public static class CardInfo{
        private String cardNumber;
        private String cardMonth;
        private String cardYear;
        private String cardHolder;
        private String cardCVC;
    }

    @SneakyThrows
    private static String getCardNumberByStatus(String cardStatus) {
        JSONArray dataCards = (JSONArray) new JSONParser().parse(new FileReader("gate-simulator/data.json"));

        for (Object cardObj : dataCards) {
            JSONObject card = (JSONObject) cardObj;
            String status = ((String) card.get("status"));

            if (status.equalsIgnoreCase(cardStatus)) {
                return (String) card.get("number");
            }
        }
        return null;
    }

    private static String generateMonth(int addMonth) {
        return LocalDate.now().plusMonths(addMonth).format(DateTimeFormatter.ofPattern("MM"));
    }

    private static String generateYear(int addYear) {
        return LocalDate.now().plusYears(addYear).format(DateTimeFormatter.ofPattern("yy"));
    }

    private static String generateValidHolder(String locale) {
        var faker = new Faker(new Locale(locale));
        String holderRu = faker.name().lastName() + " " + faker.name().firstName();
        String holderTransliterate = Junidecode.unidecode(holderRu).replace(valueOf('\''), "");
        return holderTransliterate;
    }

    private static String generateCVC(int number) {
        return Integer.toString((int) faker.number().randomNumber(number, true));
    }

    public static CardInfo getValidCardInfo(String cardStatus) {
        return new CardInfo (
            getCardNumberByStatus(cardStatus),
            generateMonth(1),
            generateYear(1),
            generateValidHolder("ru"),
            generateCVC(3)
        );
    }

    public static CardInfo getInvalidCardInfoOtherCard() {
        return new CardInfo(
                "4444444444444444",
                generateMonth(2),
                generateYear(2),
                generateValidHolder("ru"),
                generateCVC(3)
        );
    }

    public static CardInfo getInvalidCardInfoSmallNumberCard() {
        return new CardInfo(
                "5555555555",
                generateMonth(5),
                generateYear(2),
                generateValidHolder("ru"),
                generateCVC(3)
        );
    }

    public static CardInfo getInvalidCardInfoWithLetterNumberCard() {
        return new CardInfo(
                "1картакартакар",
                generateMonth(5),
                generateYear(2),
                generateValidHolder("ru"),
                generateCVC(3)
        );
    }

    public static CardInfo getInvalidCardInfoInvalidMonthCard(String cardStatus) {
        return new CardInfo(
                getCardNumberByStatus(cardStatus),
                "13",
                generateYear(2),
                generateValidHolder("ru"),
                generateCVC(3)
        );
    }

    public static CardInfo getInvalidCardInfoZeroMonthCard(String cardStatus) {
        return new CardInfo(
                getCardNumberByStatus(cardStatus),
                "00",
                generateYear(2),
                generateValidHolder("ru"),
                generateCVC(3)
        );
    }

    public static CardInfo getInvalidCardInfoOneFigureMonthCard(String cardStatus) {
        return new CardInfo(
                getCardNumberByStatus(cardStatus),
                "1",
                generateYear(2),
                generateValidHolder("ru"),
                generateCVC(3)
        );
    }

    public static CardInfo getInvalidCardInfoLastMonthCard(String cardStatus) {
        return new CardInfo(
                getCardNumberByStatus(cardStatus),
                generateMonth(-1),
                generateYear(0),
                generateValidHolder("ru"),
                generateCVC(3)
        );
    }

    public static CardInfo getInvalidCardInfoLastYearCard(String cardStatus) {
        return new CardInfo(
                getCardNumberByStatus(cardStatus),
                generateMonth(0),
                generateYear(-1),
                generateValidHolder("ru"),
                generateCVC(3)
        );
    }

    public static CardInfo getInvalidCardInfoBigFutureYearCard(String cardStatus) {
        return new CardInfo(
                getCardNumberByStatus(cardStatus),
                generateMonth(0),
                generateYear(10),
                generateValidHolder("ru"),
                generateCVC(3)
        );
    }

    public static CardInfo getInvalidCardInfoOneFigureYearCard(String cardStatus) {
        return new CardInfo(
                getCardNumberByStatus(cardStatus),
                generateMonth(0),
                "1",
                generateValidHolder("ru"),
                generateCVC(3)
        );
    }

    public static CardInfo getInvalidCardInfoNumberHolderCard(String cardStatus) {
        return new CardInfo(
                getCardNumberByStatus(cardStatus),
                generateMonth(0),
                generateYear(0),
                "123456789 21",
                generateCVC(3)
        );
    }

    public static CardInfo getInvalidCardInfoRuHolderCard(String cardStatus) {
        return new CardInfo(
                getCardNumberByStatus(cardStatus),
                generateMonth(0),
                generateYear(0),
                "Иван Иванов",
                generateCVC(3)
        );
    }

    public static CardInfo getInvalidCardInfoWithSymbolHolderCard(String cardStatus) {
        return new CardInfo(
                getCardNumberByStatus(cardStatus),
                generateMonth(0),
                generateYear(0),
                generateValidHolder("ru") + "!",
                generateCVC(3)
        );
    }

    public static CardInfo getInvalidCardSmallCVCCard(String cardStatus) {
        return new CardInfo(
                getCardNumberByStatus(cardStatus),
                generateMonth(0),
                generateYear(0),
                generateValidHolder("ru"),
                generateCVC(2)
        );
    }

    public static CardInfo getInvalidCardWithSymbolCVCCard(String cardStatus) {
        return new CardInfo(
                getCardNumberByStatus(cardStatus),
                generateMonth(0),
                generateYear(0),
                generateValidHolder("ru"),
                "12!"
        );
    }

    public static CardInfo getInvalidCardWithEmptyFieldCard() {
        return new CardInfo(
                "",
                "",
                "",
                "",
                ""
        );
    }
}
