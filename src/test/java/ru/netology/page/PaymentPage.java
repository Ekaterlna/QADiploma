package ru.netology.page;

import com.codeborne.selenide.SelenideElement;

import lombok.SneakyThrows;
import lombok.Value;
import org.openqa.selenium.By;

import ru.netology.data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PaymentPage {
    private static final SelenideElement debitButton = $$("button").find(exactText("Купить"));
    private static final SelenideElement creditButton = $$("button").find(exactText("Купить в кредит"));
    private final SelenideElement notificationOk = $(".notification_status_ok");
    private final SelenideElement notificationError = $(".notification_status_error");
    private final SelenideElement notification = $(".notification");
    private final SelenideElement closeNotificationSuccessButton = $(".notification_status_ok button ");
    private final SelenideElement closeNotificationErrorButton = $(".notification_status_error button ");
    private final SelenideElement notificationActual= $(".notification_visible");


    @SneakyThrows
    public void verifyNotificationSuccessClose() {
        notificationOk.shouldBe(visible, Duration.ofSeconds(15));
        closeNotificationSuccessButton.click();
        notificationError.shouldBe(hidden);
    }

    @SneakyThrows
    public void verifyNotificationErrorClose() {
        notificationError.shouldBe(visible, Duration.ofSeconds(15));
        closeNotificationErrorButton.click();
        notificationOk.shouldBe(hidden);
    }

    @SneakyThrows
    public void verifyNotification() {
        notification.shouldBe(visible, Duration.ofSeconds(15));
    }

    @SneakyThrows
    public String getActualNotificationMessage() {
        return notificationActual.getText();
    }

    public static class Banner {
        protected static final SelenideElement headerBanner = $("h2.heading");
        protected static final SelenideElement imageBanner = $(".Order_cardImage__Q69ii");
        protected static final SelenideElement nameTravelBanner = $(".Order_cardHeading__2PyrG");
        protected static final SelenideElement infoTravelBanner = $(".App_appContainer__3jRx1 .list");

        public Banner bannerMarrakesh() {
            headerBanner.shouldHave(exactText("Путешествие дня")).shouldBe(visible); //
            imageBanner.shouldHave(attribute("src", "http://localhost:8080/static/media/marrakech.869b2a02.jpg")).shouldBe(visible); //
            nameTravelBanner.shouldHave(exactText("Марракэш")).shouldBe(visible); //
            infoTravelBanner.shouldHave(exactText("Сказочный Восток\n33 360 миль на карту\nДо 7% на остаток по счёту\nВсего 45 000 руб.!\n")).shouldBe(visible); //
            return new Banner();
        }
    }

    public static Banner banner() {
        return new Banner().bannerMarrakesh();
    }

    @Value
    public static class FormPayment {
        protected final String headerDebitPayment = "Оплата по карте";
        protected final String headerCreditPayment = "Кредит по данным карты";

        protected final SelenideElement headerPayment = $(By.xpath("//*[@id=\"root\"]/div/h3"));
        protected final SelenideElement numberCardField = $$("input").find(attribute("placeholder", "0000 0000 0000 0000"));
        protected final SelenideElement monthCardField = $$(".input__top").find(exactText("Месяц")).parent().$("input");
        protected final SelenideElement yearCardField = $$(".input__top").find(exactText("Год")).parent().$("input");
        protected final SelenideElement holderCardField = $$(".input__top").find(exactText("Владелец")).parent().$("input");
        protected final SelenideElement cvcCardField = $$(".input__top").find(exactText("CVC/CVV")).parent().$("input");
        protected final SelenideElement continueButton = $$("button").find(exactText("Продолжить"));
        protected final SelenideElement loadingText = $$("button").find(exactText("Отправляем запрос в Банк..."));
        protected final SelenideElement numberCardError = $(By.xpath("//*[@id=\"root\"]/div/form/fieldset/div[1]/span/span/span[3]"));
        protected final SelenideElement monthError = $(By.xpath("//*[@id=\"root\"]/div/form/fieldset/div[2]/span/span[1]/span/span/span[3]"));
        protected final SelenideElement yearError = $(By.xpath("//*[@id=\"root\"]/div/form/fieldset/div[2]/span/span[2]/span/span/span[3]"));
        protected final SelenideElement holderError = $(By.xpath("//*[@id=\"root\"]/div/form/fieldset/div[3]/span/span[1]/span/span/span[3]"));
        protected final SelenideElement CVCError = $(By.xpath("//*[@id=\"root\"]/div/form/fieldset/div[3]/span/span[2]/span/span/span[3]"));

        public FormPayment debitPage() {
            headerPayment.shouldHave(exactText(headerDebitPayment)).shouldBe(visible);
            return new FormPayment();
        }

        public FormPayment creditPage() {
            headerPayment.shouldHave(exactText(headerCreditPayment)).shouldBe(visible);
            return new FormPayment();
        }

        public void fillFormPayment(DataHelper.CardInfo info) {
            numberCardField.setValue(info.getCardNumber());
            monthCardField.setValue(info.getCardMonth());
            yearCardField.setValue(info.getCardYear());
            holderCardField.setValue(info.getCardHolder());
            cvcCardField.setValue(info.getCardCVC());
            continueButton.click();
        }

        public void requestToBank() {
            loadingText.shouldBe(visible, Duration.ofSeconds(15));
        }

        public void verifyErrorCardNumber() {
            numberCardError.shouldBe(visible);
        }

        public void verifyErrorMonth() {
            monthError.shouldBe(visible);
        }

        public void verifyErrorYear() {
            yearError.shouldBe(visible);
        }

        public void verifyErrorHolder() {
            holderError.shouldBe(visible);
        }

        public void verifyErrorCVC() {
            CVCError.shouldBe(visible);
        }
    }

    public static FormPayment debitPayment(DataHelper.CardInfo info) {
        debitButton.click();
        return new FormPayment().debitPage();
    }

    public static FormPayment creditPayment(DataHelper.CardInfo info) {
        creditButton.click();
        return new FormPayment().creditPage();
    }
}