package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.PaymentPage;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.data.SQLHelper.cleanDatabase;


public class PaymentTest {
    PaymentPage paymentPage;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    void setUp() {paymentPage = open("http://localhost:8080", PaymentPage.class);}

    @AfterAll
    static void tearDownAll() {cleanDatabase();}

    String statusApproved = "APPROVED";
    String statusDeclined= "DECLINED";
    String msgExpiredDateField = "Истёк срок действия карты";
    String msgInvalidDateField = "Неверно указан срок действия карты";
    String msgInvalidFormField = "Неверный формат";
    String msgEmptyField = "Поле обязательно для заполнения";
    String msgNotificationError = "Ошибка\nОшибка! Банк отказал в проведении операции.";
    String msgNotificationSuccess = "Успешно\nОперация одобрена Банком.";

    @Test
    void shouldSuccessPaymentDebitApprovedCard(){
        PaymentPage.banner();
        var cardInfo = DataHelper.getValidCardInfo(statusApproved);
        var debitFormPayment = paymentPage.debitPayment(cardInfo);
        debitFormPayment.fillFormPayment(cardInfo);
        debitFormPayment.requestToBank();
        paymentPage.verifyNotification();
        assertAll(
                () -> assertEquals(msgNotificationSuccess, paymentPage.getActualNotificationMessage()),
                () -> assertEquals(statusApproved, SQLHelper.getPaymentStatus()),
                () -> assertEquals(SQLHelper.getPaymentID(), SQLHelper.getPaymentOrderID()),
                () -> assertEquals(null, SQLHelper.getCreditOrderID())
        );
    }

    @Test
    void shouldFailedPaymentDebitDeclinedCard(){
        PaymentPage.banner();
        var cardInfo = DataHelper.getValidCardInfo(statusDeclined);
        var debitFormPayment = paymentPage.debitPayment(cardInfo);
        debitFormPayment.fillFormPayment(cardInfo);
        debitFormPayment.requestToBank();
        paymentPage.verifyNotification();
        assertAll(
                () -> assertEquals(msgNotificationError, paymentPage.getActualNotificationMessage()),
                () -> assertEquals(statusDeclined, SQLHelper.getPaymentStatus()),
                () -> assertEquals(null, SQLHelper.getPaymentOrderID()),
                () -> assertEquals(null, SQLHelper.getCreditOrderID())
        );
    }

    @Test
    void shouldSuccessPaymentCreditApprovedCard(){
        PaymentPage.banner();
        var cardInfo = DataHelper.getValidCardInfo(statusApproved);
        var creditFormPayment = paymentPage.creditPayment(cardInfo);
        creditFormPayment.fillFormPayment(cardInfo);
        creditFormPayment.requestToBank();
        paymentPage.verifyNotification();
        assertAll(
                () -> assertEquals(msgNotificationSuccess, paymentPage.getActualNotificationMessage()),
                () -> assertEquals(statusApproved, SQLHelper.getCreditStatus()),
                () -> assertEquals(SQLHelper.getCreditID(), SQLHelper.getCreditOrderID()),
                () -> assertEquals(null, SQLHelper.getPaymentOrderID())

        );
    }

    @Test
    void shouldFailedPaymentCreditDeclinedCard(){
        PaymentPage.banner();
        var cardInfo = DataHelper.getValidCardInfo(statusDeclined);
        var creditFormPayment = paymentPage.creditPayment(cardInfo);
        creditFormPayment.fillFormPayment(cardInfo);
        creditFormPayment.requestToBank();
        paymentPage.verifyNotification();
        assertAll(
                () -> assertEquals(msgNotificationError, paymentPage.getActualNotificationMessage()),
                () -> assertEquals(statusDeclined, SQLHelper.getCreditStatus()),
                () -> assertEquals(null, SQLHelper.getCreditOrderID()),
                () -> assertEquals(null, SQLHelper.getPaymentOrderID())
        );
    }

    @Test
    void shouldFailedPaymentDebitWithInvalidNumberCard(){
        PaymentPage.banner();
        var cardInfo = DataHelper.getInvalidCardInfoOtherCard();
        var debitFormPayment = paymentPage.debitPayment(cardInfo);
        debitFormPayment.fillFormPayment(cardInfo);
        debitFormPayment.requestToBank();
        assertEquals(msgNotificationError, paymentPage.getActualNotificationMessage());
    }

    @Test
    void shouldNotVisibleNotificationError() {
        PaymentPage.banner();
        var cardInfo = DataHelper.getValidCardInfo(statusApproved);
        var debitFormPayment = paymentPage.debitPayment(cardInfo);
        debitFormPayment.fillFormPayment(cardInfo);
        debitFormPayment.requestToBank();
        paymentPage.verifyNotificationSuccessClose();
    }

    @Test
    void shouldNotVisibleNotificationSuccess() {
        PaymentPage.banner();
        var cardInfo = DataHelper.getInvalidCardInfoOtherCard();
        var creditFormPayment = paymentPage.creditPayment(cardInfo);
        creditFormPayment.fillFormPayment(cardInfo);
        creditFormPayment.requestToBank();
        paymentPage.verifyNotificationErrorClose();
    }

    @Test
    void shouldNotSubmitCreditFormWithInvalidSmallNumberCard(){
        PaymentPage.banner();
        var cardInfo = DataHelper.getInvalidCardInfoSmallNumberCard();
        var creditFormPayment = paymentPage.creditPayment(cardInfo);
        creditFormPayment.fillFormPayment(cardInfo);
        creditFormPayment.verifyErrorCardNumber();
        assertEquals(msgInvalidFormField, creditFormPayment.getNumberCardError().getText());
    }

    @Test
    void shouldNotSubmitCreditFormWithInvalidLLetterNumberCard(){
        PaymentPage.banner();
        var cardInfo = DataHelper.getInvalidCardInfoWithLetterNumberCard();
        var creditFormPayment = paymentPage.creditPayment(cardInfo);
        creditFormPayment.fillFormPayment(cardInfo);
        creditFormPayment.verifyErrorCardNumber();
        assertEquals(msgInvalidFormField, creditFormPayment.getNumberCardError().getText());
    }

    @Test
    void shouldNotSubmitDebitFormWithInvalidMonthCard(){
        PaymentPage.banner();
        var cardInfo = DataHelper.getInvalidCardInfoInvalidMonthCard(statusApproved);
        var debitFormPayment = paymentPage.debitPayment(cardInfo);
        debitFormPayment.fillFormPayment(cardInfo);
        debitFormPayment.verifyErrorMonth();
        assertEquals(msgInvalidDateField, debitFormPayment.getMonthError().getText());
    }

    @Test
    void shouldNotSubmitCreditFormWithInvalidZeroMonthCard(){
        PaymentPage.banner();
        var cardInfo = DataHelper.getInvalidCardInfoZeroMonthCard(statusApproved);
        var creditFormPayment = paymentPage.creditPayment(cardInfo);
        creditFormPayment.fillFormPayment(cardInfo);
        creditFormPayment.verifyErrorMonth();
        assertEquals(msgInvalidDateField, creditFormPayment.getMonthError().getText());
    }

    @Test
    void shouldNotSubmitDebitFormWithInvalidOneFigureMonthCard(){
        PaymentPage.banner();
        var cardInfo = DataHelper.getInvalidCardInfoOneFigureMonthCard(statusApproved);
        var debitFormPayment = paymentPage.debitPayment(cardInfo);
        debitFormPayment.fillFormPayment(cardInfo);
        debitFormPayment.verifyErrorMonth();
        assertEquals(msgInvalidFormField, debitFormPayment.getMonthError().getText());
    }

    @Test
    void shouldNotSubmitCreditFormWithInvalidLastMonthCard(){
        PaymentPage.banner();
        var cardInfo = DataHelper.getInvalidCardInfoLastMonthCard(statusApproved);
        var creditFormPayment = paymentPage.creditPayment(cardInfo);
        creditFormPayment.fillFormPayment(cardInfo);
        creditFormPayment.verifyErrorMonth();
        assertEquals(msgInvalidDateField, creditFormPayment.getMonthError().getText());
    }

    @Test
    void shouldNotSubmitDebitFormWithInvalidLastYearCard(){
        PaymentPage.banner();
        var cardInfo = DataHelper.getInvalidCardInfoLastYearCard(statusApproved);
        var debitFormPayment = paymentPage.debitPayment(cardInfo);
        debitFormPayment.fillFormPayment(cardInfo);
        debitFormPayment.verifyErrorYear();
        assertEquals(msgExpiredDateField, debitFormPayment.getYearError().getText());
    }

    @Test
    void shouldNotSubmitCreditFormWithInvalidBigFutureYearCard(){
        PaymentPage.banner();
        var cardInfo = DataHelper.getInvalidCardInfoBigFutureYearCard(statusApproved);
        var creditFormPayment = paymentPage.creditPayment(cardInfo);
        creditFormPayment.fillFormPayment(cardInfo);
        creditFormPayment.verifyErrorYear();
        assertEquals(msgInvalidDateField, creditFormPayment.getYearError().getText());
    }

    @Test
    void shouldNotSubmitCreditFormWithInvalidOneFigureYearCard(){
        PaymentPage.banner();
        var cardInfo = DataHelper.getInvalidCardInfoOneFigureYearCard(statusApproved);
        var creditFormPayment = paymentPage.creditPayment(cardInfo);
        creditFormPayment.fillFormPayment(cardInfo);
        creditFormPayment.verifyErrorYear();
        assertEquals(msgInvalidFormField, creditFormPayment.getYearError().getText());
    }

    @Test
    void shouldNotSubmitDebitFormWithInvalidNumberHolderCard(){
        PaymentPage.banner();
        var cardInfo = DataHelper.getInvalidCardInfoNumberHolderCard(statusApproved);
        var debitFormPayment = paymentPage.debitPayment(cardInfo);
        debitFormPayment.fillFormPayment(cardInfo);
        debitFormPayment.verifyErrorHolder();
        assertEquals(msgInvalidFormField, debitFormPayment.getHolderError().getText());
    }

    @Test
    void shouldNotSubmitDebitFormWithInvalidRuHolderCard(){
        PaymentPage.banner();
        var cardInfo = DataHelper.getInvalidCardInfoRuHolderCard(statusApproved);
        var debitFormPayment = paymentPage.debitPayment(cardInfo);
        debitFormPayment.fillFormPayment(cardInfo);
        debitFormPayment.verifyErrorHolder();
        assertEquals(msgInvalidFormField, debitFormPayment.getHolderError().getText());
    }

    @Test
    void shouldNotSubmitCreditFormWithInvalidWithSymbolHolderCard(){
        PaymentPage.banner();
        var cardInfo = DataHelper.getInvalidCardInfoWithSymbolHolderCard(statusApproved);
        var creditFormPayment = paymentPage.creditPayment(cardInfo);
        creditFormPayment.fillFormPayment(cardInfo);
        creditFormPayment.verifyErrorHolder();
        assertEquals(msgInvalidFormField, creditFormPayment.getHolderError().getText());
    }

    @Test
    void shouldNotSubmitCreditFormWithInvalidSmallCVCCard(){
        PaymentPage.banner();
        var cardInfo = DataHelper.getInvalidCardSmallCVCCard(statusApproved);
        var creditFormPayment = paymentPage.creditPayment(cardInfo);
        creditFormPayment.fillFormPayment(cardInfo);
        creditFormPayment.verifyErrorCVC();
        assertEquals(msgInvalidFormField, creditFormPayment.getCVCError().getText());
    }

    @Test
    void shouldNotSubmitCreditFormWithInvalidSymbolCVCCard(){
        PaymentPage.banner();
        var cardInfo = DataHelper.getInvalidCardWithSymbolCVCCard(statusApproved);
        var creditFormPayment = paymentPage.creditPayment(cardInfo);
        creditFormPayment.fillFormPayment(cardInfo);
        creditFormPayment.verifyErrorCVC();
        assertEquals(msgInvalidFormField, creditFormPayment.getCVCError().getText());
    }

    @Test
    void shouldNotSubmitDebitFormWithEmptyFieldCard(){
        PaymentPage.banner();
        var cardInfo = DataHelper.getInvalidCardWithEmptyFieldCard();
        var debitFormPayment = paymentPage.debitPayment(cardInfo);
        debitFormPayment.fillFormPayment(cardInfo);
        debitFormPayment.verifyErrorCardNumber();
        debitFormPayment.verifyErrorMonth();
        debitFormPayment.verifyErrorYear();
        debitFormPayment.verifyErrorHolder();
        debitFormPayment.verifyErrorCVC();
        assertAll(
                () -> assertEquals(msgEmptyField, debitFormPayment.getNumberCardError().getText()),
                () -> assertEquals(msgEmptyField, debitFormPayment.getMonthError().getText()),
                () -> assertEquals(msgEmptyField, debitFormPayment.getYearError().getText()),
                () -> assertEquals(msgEmptyField, debitFormPayment.getHolderError().getText()),
                () -> assertEquals(msgEmptyField, debitFormPayment.getCVCError().getText())
        );
    }
}
