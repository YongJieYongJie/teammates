package teammates.test.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import teammates.common.util.Const;

public class AdminEmailPage extends AppPage {
    public AdminEmailPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Admin Email</h1>");
    }
    
    public void inputRecipient(String recipient) {
        WebElement recipientBox = this.getRecipientBox();
        recipientBox.sendKeys(recipient);
    }
    
    public void inputSubject(String subject) {
        WebElement subjectBox = this.getSubjectBox();
        subjectBox.sendKeys(subject);
    }
    
    public void inputContent(String content) {
        browser.driver.switchTo().frame("adminEmailBox_ifr");
        WebElement contentBox = browser.driver.findElement(By.cssSelector("body"));
        contentBox.sendKeys(content);
        browser.driver.switchTo().defaultContent();
    }
    
    public void clickSendButton() {
        click(getSendButton());
        waitForPageToLoad();
    }
    
    public void clickSaveButton() {
        click(getSaveButton());
        waitForPageToLoad();
    }
    
    public void clearSubjectBox() {
        WebElement subjectBox = this.getSubjectBox();
        subjectBox.clear();
    }
    
    public void clickSentTab() {
        click(getSentTab());
        waitForPageToLoad();
    }

    public void clickDraftTab() {
        click(getDraftTab());
        waitForPageToLoad();
    }
    
    public void clickTrashTab() {
        click(getTrashTab());
        waitForPageToLoad();
    }
    
    private WebElement getRecipientBox() {
        return browser.driver.findElement(By.id("addressReceiverEmails"));
    }
    
    private WebElement getSubjectBox() {
        return browser.driver.findElement(By.name("emailsubject"));
    }
    
    private WebElement getSendButton() {
        return browser.driver.findElement(By.id("composeSubmitButton"));
    }
    
    private WebElement getSaveButton() {
        return browser.driver.findElement(By.id("composeSaveButton"));
    }
    
    private WebElement getSentTab() {
        return browser.driver.findElement(By.cssSelector("a[href='" + Const.ActionURIs.ADMIN_EMAIL_SENT_PAGE + "']"));
    }
    
    private WebElement getDraftTab() {
        return browser.driver.findElement(By.cssSelector("a[href='" + Const.ActionURIs.ADMIN_EMAIL_DRAFT_PAGE + "']"));
    }
    
    private WebElement getTrashTab() {
        return browser.driver.findElement(By.cssSelector("a[href='" + Const.ActionURIs.ADMIN_EMAIL_TRASH_PAGE + "']"));
    }
}
