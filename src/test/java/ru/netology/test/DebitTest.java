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


public class DebitTest {
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
        paymentPage.checkBanner();
        var cardInfo = DataHelper.getValidCardInfo(statusApproved);
        var debitFormPayment = paymentPage.debitPayment(cardInfo);
        debitFormPayment.fillFormPayment(cardInfo);
        debitFormPayment.requestToBank();
        paymentPage.verifyNotification();
        assertAll(
                () -> assertEquals(msgNotificationSuccess, paymentPage.getActualNotificationMessage()),
                () -> assertEquals(statusApproved, SQLHelper.getPaymentEntityTableInfo().getStatus()),
                () -> assertEquals(SQLHelper.getPaymentEntityTableInfo().getTransaction_id(), SQLHelper.getOrderEntityTableInfo().getPayment_id()),
                () -> assertNull(SQLHelper.getOrderEntityTableInfo().getCredit_id())
        );
    }

    @Test
    void shouldFailedPaymentDebitDeclinedCard(){
        paymentPage.checkBanner();
        var cardInfo = DataHelper.getValidCardInfo(statusDeclined);
        var debitFormPayment = paymentPage.debitPayment(cardInfo);
        debitFormPayment.fillFormPayment(cardInfo);
        debitFormPayment.requestToBank();
        paymentPage.verifyNotification();
        assertAll(
                () -> assertEquals(msgNotificationError, paymentPage.getActualNotificationMessage()),
                () -> assertEquals(statusDeclined, SQLHelper.getPaymentEntityTableInfo().getStatus()),
                () -> assertNull(SQLHelper.getOrderEntityTableInfo().getPayment_id()),
                () -> assertNull(SQLHelper.getOrderEntityTableInfo().getCredit_id())
        );
    }

    @Test
    void shouldFailedPaymentDebitWithInvalidNumberCard(){
        paymentPage.checkBanner();
        var cardInfo = DataHelper.getInvalidCardInfoOtherCard();
        var debitFormPayment = paymentPage.debitPayment(cardInfo);
        debitFormPayment.fillFormPayment(cardInfo);
        debitFormPayment.requestToBank();
        assertEquals(msgNotificationError, paymentPage.getActualNotificationMessage());
    }

    @Test
    void shouldNotVisibleNotificationError() {
        paymentPage.checkBanner();
        var cardInfo = DataHelper.getValidCardInfo(statusApproved);
        var debitFormPayment = paymentPage.debitPayment(cardInfo);
        debitFormPayment.fillFormPayment(cardInfo);
        debitFormPayment.requestToBank();
        paymentPage.verifyNotificationSuccessClose();
    }

    @Test
    void shouldNotSubmitDebitFormWithInvalidMonthCard(){
        paymentPage.checkBanner();
        var cardInfo = DataHelper.getInvalidCardInfoInvalidMonthCard(statusApproved);
        var debitFormPayment = paymentPage.debitPayment(cardInfo);
        debitFormPayment.fillFormPayment(cardInfo);
        debitFormPayment.verifyErrorMonth();
        assertEquals(msgInvalidDateField, debitFormPayment.getMonthError().getText());
    }

    @Test
    void shouldNotSubmitDebitFormWithInvalidOneFigureMonthCard(){
        paymentPage.checkBanner();
        var cardInfo = DataHelper.getInvalidCardInfoOneFigureMonthCard(statusApproved);
        var debitFormPayment = paymentPage.debitPayment(cardInfo);
        debitFormPayment.fillFormPayment(cardInfo);
        debitFormPayment.verifyErrorMonth();
        assertEquals(msgInvalidFormField, debitFormPayment.getMonthError().getText());
    }

    @Test
    void shouldNotSubmitDebitFormWithInvalidLastYearCard(){
        paymentPage.checkBanner();
        var cardInfo = DataHelper.getInvalidCardInfoLastYearCard(statusApproved);
        var debitFormPayment = paymentPage.debitPayment(cardInfo);
        debitFormPayment.fillFormPayment(cardInfo);
        debitFormPayment.verifyErrorYear();
        assertEquals(msgExpiredDateField, debitFormPayment.getYearError().getText());
    }

    @Test
    void shouldNotSubmitDebitFormWithInvalidNumberHolderCard(){
        paymentPage.checkBanner();
        var cardInfo = DataHelper.getInvalidCardInfoNumberHolderCard(statusApproved);
        var debitFormPayment = paymentPage.debitPayment(cardInfo);
        debitFormPayment.fillFormPayment(cardInfo);
        debitFormPayment.verifyErrorHolder();
        assertEquals(msgInvalidFormField, debitFormPayment.getHolderError().getText());
    }

    @Test
    void shouldNotSubmitDebitFormWithInvalidRuHolderCard(){
        paymentPage.checkBanner();
        var cardInfo = DataHelper.getInvalidCardInfoRuHolderCard(statusApproved);
        var debitFormPayment = paymentPage.debitPayment(cardInfo);
        debitFormPayment.fillFormPayment(cardInfo);
        debitFormPayment.verifyErrorHolder();
        assertEquals(msgInvalidFormField, debitFormPayment.getHolderError().getText());
    }

    @Test
    void shouldNotSubmitDebitFormWithEmptyFieldCard(){
        paymentPage.checkBanner();
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
