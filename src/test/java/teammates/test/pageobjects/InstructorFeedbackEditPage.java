package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;

import com.google.appengine.api.datastore.Text;

public class InstructorFeedbackEditPage extends AppPage {
    
    @FindBy(id = "starttime")
    private WebElement startTimeDropdown;
    
    @FindBy(id = "startdate")
    private WebElement startDateBox;
    
    @FindBy(id = "endtime")
    private WebElement endTimeDropdown;
    
    @FindBy(id = "enddate")
    private WebElement endDateBox;
    
    @FindBy(id = "timezone")
    private WebElement timezoneDropDown;
    
    @FindBy(id = "graceperiod")
    private WebElement gracePeriodDropdown;

    @FindBy(id = "editUncommonSettingsButton")
    private WebElement uncommonSettingsButton;
    
    @FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON + "_custom")
    private WebElement customSessionVisibleTimeButton;
    
    @FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON + "_custom")
    private WebElement customResultsVisibleTimeButton;
    
    @FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON + "_atopen")
    private WebElement defaultSessionVisibleTimeButton;
    
    @FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON + "_atvisible")
    private WebElement defaultResultsVisibleTimeButton;
    
    @FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON + "_later")
    private WebElement manualResultsVisibleTimeButton;
    
    @FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON + "_never")
    private WebElement neverSessionVisibleTimeButton;
    
    @FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON + "_never")
    private WebElement neverResultsVisibleTimeButton;
    
    @FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL + "_closing")
    private WebElement closingSessionEmailReminderButton;
    
    @FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL + "_published")
    private WebElement publishedSessionEmailReminderButton;
    
    @FindBy(id = "fsEditLink")
    private WebElement fsEditLink;
    
    @FindBy(id = "fsSaveLink")
    private WebElement fsSaveLink;
    
    @FindBy(id = "fsDeleteLink")
    private WebElement fsDeleteLink;
    
    @FindBy(id = "button_openframe")
    private WebElement openNewQuestionButton;

    @FindBy(id = "button_submit_add")
    private WebElement addNewQuestionButton;
    
    @FindBy(id = "button_done_editing")
    private WebElement doneEditingButton;
    
    @FindBy(id = "button_fscopy")
    private WebElement fscopyButton;

    @FindBy(id = "button_copy")
    private WebElement copyQuestionLoadButton;
    
    @FindBy(id = "button_copy_submit")
    private WebElement copyQuestionSubmitButton;
    
    @FindBy(id = "button_preview_student")
    private WebElement previewAsStudentButton;
    
    @FindBy(id = "button_preview_instructor")
    private WebElement previewAsInstructorButton;

    private InstructorCopyFsToModal fsCopyToModal;
    
    public InstructorFeedbackEditPage(Browser browser) {
        super(browser);
        fsCopyToModal = new InstructorCopyFsToModal(browser);
    }


    //////////////////////////
    // PAGE RELATED METHODS //
    //////////////////////////

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Edit Feedback Session</h1>");
    }

    public boolean isCorrectPage(String courseId, String feedbackSessionName) {
        boolean isCorrectCourseId = this.getCourseId().equals(courseId);
        boolean isCorrectFeedbackSessionName = this.getFeedbackSessionName().equals(feedbackSessionName);
        return isCorrectCourseId && isCorrectFeedbackSessionName && containsExpectedPageContents();
    }

    /**
     * @return number of question edit forms + question add form
     */
    public int getNumberOfQuestionEditForms() {
        return browser.driver.findElements(By.className("questionTable")).size();
    }

    public WebElement getStatusMessage() {
        return statusMessage;
    }

    public InstructorFeedbacksPage clickDoneEditingLink() {
        doneEditingButton.click();
        waitForPageToLoad();
        return changePageType(InstructorFeedbacksPage.class);
    }

    public FeedbackSubmitPage clickPreviewAsStudentButton() {
        previewAsStudentButton.click();
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(FeedbackSubmitPage.class);
    }

    public FeedbackSubmitPage clickPreviewAsInstructorButton() {
        waitForPageToLoad();
        previewAsInstructorButton.click();
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(FeedbackSubmitPage.class);
    }

    /**
     * Changes the value of actionlink of the copy question button.
     * @param actionLink value to change to
     */
    public void changeActionLinkOnCopyButton(String actionLink) {
        String selector = "$('#button_copy')";
        String action = ".data('actionlink', '" + actionLink + "')";
        ((JavascriptExecutor) browser.driver).executeScript(selector + action);
    }

    public void clickCopyButton() {
        copyQuestionLoadButton.click();
    }

    public boolean isCopySubmitButtonEnabled() {
        return copyQuestionSubmitButton.isEnabled();
    }

    public void clickCopySubmitButton() {
        copyQuestionSubmitButton.click();
    }

    public void clickCopyTableAtRow(int rowIndex) {
        WebElement row = browser.driver.findElement(By.id("copyTableModal"))
                                                      .findElements(By.tagName("tr"))
                                                      .get(rowIndex + 1);
        row.click();
    }

    public void waitForCopyTableToLoad() {
        By tableRowSelector = By.cssSelector("#copyTableModal tr");
        waitForElementPresence(tableRowSelector);
        waitForElementVisibility(browser.driver.findElement(tableRowSelector));
    }

    public void waitForCopyErrorMessageToLoad() {
        By errorMessageSelector = By.cssSelector("#question-copy-modal-status.alert-danger");
        waitForElementPresence(errorMessageSelector);
        waitForElementVisibility(browser.driver.findElement(errorMessageSelector));
    }

    public String getCopyErrorMessageText() {
        return browser.driver.findElement(
                By.cssSelector("#question-copy-modal-status.alert-danger")).getText();
    }

    public void clickNewQuestionButton() {
        openNewQuestionButton.click();
    }

    public void clickAddQuestionButton() {
        addNewQuestionButton.click();
        waitForPageToLoad();
    }

    public void selectNewQuestionType(String questionType) {
        browser.driver.findElement(By.cssSelector("[data-questionType=" + questionType + "]")).click();
    }

    public boolean verifyNewEssayQuestionFormIsDisplayed() {
        return addNewQuestionButton.isDisplayed();
    }

    public boolean verifyNewMcqQuestionFormIsDisplayed() {
        WebElement mcqForm = browser.driver.findElement(By.id("mcqForm"));
        return mcqForm.isDisplayed() && addNewQuestionButton.isDisplayed();
    }

    public boolean verifyNewMsqQuestionFormIsDisplayed() {
        WebElement mcqForm = browser.driver.findElement(By.id("msqForm"));
        return mcqForm.isDisplayed() && addNewQuestionButton.isDisplayed();
    }

    public boolean verifyNewNumScaleQuestionFormIsDisplayed() {
        WebElement mcqForm = browser.driver.findElement(By.id("numScaleForm"));
        return mcqForm.isDisplayed() && addNewQuestionButton.isDisplayed();
    }

    public boolean verifyNewConstSumQuestionFormIsDisplayed() {
        WebElement constSumForm = browser.driver.findElement(By.id("constSumForm"));
        return constSumForm.isDisplayed() && addNewQuestionButton.isDisplayed();
    }

    public boolean verifyNewContributionQuestionFormIsDisplayed() {
        // No contribForm to check for.
        return addNewQuestionButton.isDisplayed();
    }

    public boolean verifyNewRubricQuestionFormIsDisplayed() {
        WebElement contribForm = browser.driver.findElement(By.id("rubricForm"));
        return contribForm.isDisplayed() && addNewQuestionButton.isDisplayed();
    }

    public boolean verifyNewRankOptionsQuestionFormIsDisplayed() {
        WebElement contribForm = browser.driver.findElement(By.id("rankOptionsForm"));
        return contribForm.isDisplayed() && addNewQuestionButton.isDisplayed();
    }

    public boolean verifyNewRankRecipientsQuestionFormIsDisplayed() {
        WebElement contribForm = browser.driver.findElement(By.id("rankRecipientsForm"));
        return contribForm.isDisplayed() && addNewQuestionButton.isDisplayed();
    }

    private String getCourseId() {
        return browser.driver.findElement(By.name("courseid")).getAttribute("value");
    }

    private String getFeedbackSessionName() {
        return browser.driver.findElement(By.name("fsname")).getAttribute("value");
    }


    /////////////////////
    // SESSION RELATED //
    /////////////////////

    public void clickEditSessionButton() {
        waitForElementVisibility(fsEditLink);
        fsEditLink.click();
    }

    public void clickSaveSessionButton() {
        fsSaveLink.click();
        waitForPageToLoad();
    }

    public InstructorFeedbacksPage deleteSession() {
        clickAndConfirm(getDeleteSessionLink());
        waitForPageToLoad();
        return changePageType(InstructorFeedbacksPage.class);
    }

    public WebElement getDeleteSessionLink() {
        return fsDeleteLink;
    }

    public InstructorCopyFsToModal getFsCopyToModal() {
        return fsCopyToModal;
    }

    public void clickEditUncommonSettingsButton() {
        uncommonSettingsButton.click();
    }

    public void clickDefaultVisibleTimeButton() {
        defaultSessionVisibleTimeButton.click();
    }

    public void clickDefaultPublishTimeButton() {
        defaultResultsVisibleTimeButton.click();
    }

    public void clickManualPublishTimeButton() {
        manualResultsVisibleTimeButton.click();
    }

    public void clickFsCopyButton() {
        waitForElementNotCovered(fscopyButton);
        fscopyButton.click();
    }

    public void editFeedbackSession(Date startTime, Date endTime, Text instructions, int gracePeriod) {
        // Select start date
        JavascriptExecutor js = (JavascriptExecutor) browser.driver;
        js.executeScript("$('#" + Const.ParamsNames.FEEDBACK_SESSION_STARTDATE + "')[0].value='"
                         + TimeHelper.formatDate(startTime) + "';");
        selectDropdownByVisibleValue(startTimeDropdown,
                                     TimeHelper.convertToDisplayValueInTimeDropDown(startTime));

        // Select deadline date
        js.executeScript("$('#" + Const.ParamsNames.FEEDBACK_SESSION_ENDDATE + "')[0].value='"
                         + TimeHelper.formatDate(endTime) + "';");
        selectDropdownByVisibleValue(endTimeDropdown,
                                     TimeHelper.convertToDisplayValueInTimeDropDown(endTime));

        // Fill in instructions
        fillRichTextEditor("instructions", instructions.getValue());

        // Select grace period
        selectDropdownByVisibleValue(gracePeriodDropdown, Integer.toString(gracePeriod) + " mins");

        fsSaveLink.click();
        waitForElementVisibility(statusMessage);
    }

    /**
     *
     * @return {@code True} if all elements expected to be enabled
     * in the edit session frame are enabled after edit link is clicked.
     * {@code False} if not.
     */
    public boolean verifyEditSessionBoxIsEnabled() {
        boolean isEditSessionEnabled = fsSaveLink.isDisplayed() && timezoneDropDown.isEnabled()
                                       // && "Session visible from" radio buttons
                                       && neverSessionVisibleTimeButton.isEnabled()
                                       && defaultSessionVisibleTimeButton.isEnabled()
                                       && customSessionVisibleTimeButton.isEnabled()
                                       // && "Send emails for" checkboxes
                                       && closingSessionEmailReminderButton.isEnabled()
                                       && publishedSessionEmailReminderButton.isEnabled();

        if (isEditSessionEnabled && !neverSessionVisibleTimeButton.isSelected()) {
            isEditSessionEnabled = gracePeriodDropdown.isEnabled() // && Submission times inputs
                                   && startDateBox.isEnabled() && startTimeDropdown.isEnabled()
                                   && endDateBox.isEnabled() && endTimeDropdown.isEnabled()
                                   // && "Responses visible from" radio buttons
                                   && defaultResultsVisibleTimeButton.isEnabled()
                                   && customResultsVisibleTimeButton.isEnabled()
                                   && manualResultsVisibleTimeButton.isEnabled()
                                   && neverResultsVisibleTimeButton.isEnabled();
        }

        return isEditSessionEnabled;
    }

    public boolean areDatesOfPreviousCurrentAndNextMonthEnabled() throws ParseException {
        return areDatesOfPreviousCurrentAndNextMonthEnabled(startDateBox)
               && areDatesOfPreviousCurrentAndNextMonthEnabled(endDateBox);
    }

    /**
     * @param dateBox is a {@link WebElement} that triggers a datepicker
     * @return true if the dates of previous, current and next month are
     *         enabled, otherwise false
     * @throws ParseException if the string in {@code dateBox} cannot be parsed
     */
    private boolean areDatesOfPreviousCurrentAndNextMonthEnabled(WebElement dateBox) throws ParseException {

        Calendar previousMonth = Calendar.getInstance();
        previousMonth.add(Calendar.MONTH, -1);

        // Navigate to the previous month
        if (!navigate(dateBox, previousMonth)) {
            return false;
        }

        // Check if the dates of previous, current and next month are enabled
        for (int i = 0; i < 3; i++) {

            List<WebElement> dates =
                    browser.driver.findElements(By.xpath("//div[@id='ui-datepicker-div']/table/tbody/tr/td"));

            for (WebElement date : dates) {

                boolean isDisabled = date.getAttribute("class").contains("ui-datepicker-unselectable ui-state-disabled");
                boolean isFromOtherMonth = date.getAttribute("class").contains("ui-datepicker-other-month");

                if (isDisabled && !isFromOtherMonth) {
                    return false;
                }
            }

            // Navigate to the next month
            browser.driver.findElement(By.className("ui-datepicker-next")).click();
        }

        return true;
    }

    /**
     * Navigate the datepicker associated with {@code dateBox} to the specified {@code date}
     *
     * @param dateBox is a {@link WebElement} that triggers a datepicker
     * @param date is a {@link Calendar} that specifies the date that needs to be navigated to
     * @return true if navigated to the {@code date} successfully, otherwise
     *         false
     * @throws ParseException if the string in {@code dateBox} cannot be parsed
     */
    private boolean navigate(WebElement dateBox, Calendar date) throws ParseException {

        dateBox.click();

        Calendar selectedDate = Calendar.getInstance();

        String month = date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        String year = Integer.toString(date.get(Calendar.YEAR));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        selectedDate.setTime(dateFormat.parse(dateBox.getAttribute("value")));

        if (selectedDate.after(date)) {

            while (!getDatepickerMonth().equals(month) || !getDatepickerYear().equals(year)) {

                WebElement previousButton = browser.driver.findElement(By.className("ui-datepicker-prev"));
                if (previousButton.getAttribute("class").contains("ui-state-disabled")) {
                    return false;
                }
                previousButton.click();
            }

        } else {

            while (!getDatepickerMonth().equals(month) || !getDatepickerYear().equals(year)) {

                WebElement nextButton = browser.driver.findElement(By.className("ui-datepicker-next"));
                if (nextButton.getAttribute("class").contains("ui-state-disabled")) {
                    return false;
                }
                nextButton.click();
            }
        }

        return true;
    }

    private String getDatepickerYear() {
        By by = By.className("ui-datepicker-year");
        waitForElementPresence(by);
        return browser.driver.findElement(by).getText();
    }

    private String getDatepickerMonth() {
        By by = By.className("ui-datepicker-month");
        waitForElementPresence(by);
        return browser.driver.findElement(by).getText();
    }


    //////////////////////////////
    // GENERAL QUESTION RELATED //
    //////////////////////////////

    //methods that apply to all question types in general, e.g., enabling disabling, filling question text, etc.

    public void clickEditQuestionButton(int qnNumber) {
        WebElement qnEditLink = browser.driver.findElement(By.id("questionedittext-" + qnNumber));
        qnEditLink.click();
    }

    public void fillEditQuestionBox(String qnText, int qnNumber) {
        WebElement questionEditTextBox = browser.driver.findElement(By.id("questiontext-" + qnNumber));
        fillTextBox(questionEditTextBox, qnText);
    }

    public void clickSaveExistingQuestionButton(int qnNumber) {
        WebElement qnSaveLink = browser.driver.findElement(By.id("button_question_submit-" + qnNumber));
        qnSaveLink.click();
        waitForPageToLoad();
    }

    public void selectQuestionNumber(int qnNumber, int newQnIndex) {
        WebElement qnNumSelect = browser.driver.findElement(By.id("questionnum-" + qnNumber));
        selectDropdownByVisibleValue(qnNumSelect, String.valueOf(newQnIndex));
    }

    public String getQuestionBoxText(int qnNumber) {
        WebElement questionEditTextBox = browser.driver.findElement(By.id("questiontext-" + qnNumber));
        return getTextBoxValue(questionEditTextBox);
    }

    public WebElement getDeleteQuestionLink(int qnNumber) {
        return browser.driver.findElement(By.xpath("//a[@onclick='deleteQuestion(" + qnNumber + ")']"));
    }

    public WebElement getDiscardChangesLink(int qnNumber) {
        return browser.driver.findElement(By.xpath("//a[@onclick='discardChanges(" + qnNumber + ")']"));
    }

    public void changeQuestionTypeInForm(int qnNumber, String newQuestionType) {
        String selector = "$('#form_editquestion-" + qnNumber + "').find('[name=\"questiontype\"]')";
        String action = ".val('" + newQuestionType + "')";
        ((JavascriptExecutor) browser.driver).executeScript(selector + action);
    }

    public boolean isQuestionEnabled(int qnNumber) {
        WebElement questionTextArea = browser.driver.findElement(By.id("questiontext-" + qnNumber));
        return questionTextArea.isEnabled();
    }

    public boolean isDiscardChangesButtonVisible(int qnNumber) {
        WebElement discardChangesButton =
                browser.driver.findElement(By.xpath("//a[@onclick='discardChanges(" + qnNumber + ")']"));

        return discardChangesButton.isDisplayed();
    }

    private String getIdSuffix(int qnNumber) {
        int newQnNumber = -1;
        boolean isValid = qnNumber > 0 || qnNumber == newQnNumber;
        return isValid ? "-" + qnNumber : "";
    }


    ////////////////////////////////////
    // SPECIFIC QUESTION TYPE RELATED //
    ////////////////////////////////////

    public void fillMinNumScaleBox(int minScale, int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);

        WebElement minScaleBox = browser.driver.findElement(By.id("minScaleBox" + idSuffix));
        fillTextBox(minScaleBox, Integer.toString(minScale));

        JavascriptExecutor jsExecutor = (JavascriptExecutor) browser.driver;
        jsExecutor.executeScript("$(arguments[0]).change();", minScaleBox);
    }

    public void fillMaxNumScaleBox(int maxScale, int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);

        WebElement maxScaleBox = browser.driver.findElement(By.id("maxScaleBox" + idSuffix));
        fillTextBox(maxScaleBox, Integer.toString(maxScale));

        JavascriptExecutor jsExecutor = (JavascriptExecutor) browser.driver;
        jsExecutor.executeScript("$(arguments[0]).change();", maxScaleBox);
    }

    public void fillMinNumScaleBox(String minScale, int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);

        WebElement minScaleBox = browser.driver.findElement(By.id("minScaleBox" + idSuffix));
        fillTextBox(minScaleBox, minScale);

        JavascriptExecutor jsExecutor = (JavascriptExecutor) browser.driver;
        jsExecutor.executeScript("$(arguments[0]).change();", minScaleBox);
    }

    public void fillMaxNumScaleBox(String maxScale, int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);

        WebElement maxScaleBox = browser.driver.findElement(By.id("maxScaleBox" + idSuffix));
        fillTextBox(maxScaleBox, maxScale);

        JavascriptExecutor jsExecutor = (JavascriptExecutor) browser.driver;
        jsExecutor.executeScript("$(arguments[0]).change();", maxScaleBox);
    }

    public String getMaxNumScaleBox(int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);
        WebElement maxScaleBox = browser.driver.findElement(By.id("maxScaleBox" + idSuffix));
        return maxScaleBox.getAttribute("value");
    }

    public void fillStepNumScaleBox(double step, int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);

        WebElement stepBox = browser.driver.findElement(By.id("stepBox" + idSuffix));
        fillTextBox(stepBox, StringHelper.toDecimalFormatString(step));

        JavascriptExecutor jsExecutor = (JavascriptExecutor) browser.driver;
        jsExecutor.executeScript("$(arguments[0]).change();", stepBox);
    }

    public void fillStepNumScaleBox(String step, int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);

        WebElement stepBox = browser.driver.findElement(By.id("stepBox" + idSuffix));
        fillTextBox(stepBox, step);

        JavascriptExecutor jsExecutor = (JavascriptExecutor) browser.driver;
        jsExecutor.executeScript("$(arguments[0]).change();", stepBox);
    }

    public String getNumScalePossibleValuesString(int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);
        WebElement possibleValuesSpan = browser.driver.findElement(By.id("numScalePossibleValues" + idSuffix));
        return possibleValuesSpan.getText();
    }

    public void fillConstSumPointsBox(String points, int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);

        WebElement pointsBox = browser.driver.findElement(By.id("constSumPoints" + idSuffix));
        fillTextBox(pointsBox, Keys.BACK_SPACE + points); //backspace to clear the extra 1 when box is cleared.

        JavascriptExecutor jsExecutor = (JavascriptExecutor) browser.driver;
        jsExecutor.executeScript("$(arguments[0]).change();", pointsBox);
    }

    public String getConstSumPointsBox(int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);
        WebElement constSumPointsBox = browser.driver.findElement(By.id("constSumPoints" + idSuffix));
        return constSumPointsBox.getAttribute("value");
    }

    public void fillRubricSubQuestionBox(String subQuestion, int qnNumber, int subQnIndex) {
        String idSuffix = getIdSuffix(qnNumber);

        String elemId = Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + idSuffix + "-" + subQnIndex;

        WebElement subQnBox = browser.driver.findElement(By.id(elemId));
        fillTextBox(subQnBox, subQuestion);
    }

    public void fillRubricChoiceBox(String choice, int qnNumber, int choiceIndex) {
        String idSuffix = getIdSuffix(qnNumber);

        String elemId = Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + idSuffix + "-" + choiceIndex;

        WebElement subQnBox = browser.driver.findElement(By.id(elemId));
        fillTextBox(subQnBox, choice);
    }

    public void fillRubricWeightBox(String weight, int qnNumber, int choiceIndex) {
        String idSuffix = getIdSuffix(qnNumber);

        String elemid = Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + idSuffix + "-" + choiceIndex;

        WebElement weightBox = browser.driver.findElement(By.id(elemid));
        fillTextBox(weightBox, weight);
    }

    public void fillRubricDescriptionBox(String description, int qnNumber, int subQnIndex, int choiceIndex) {
        String idSuffix = getIdSuffix(qnNumber);

        String elemId = Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_DESCRIPTION
                        + idSuffix + "-" + subQnIndex + "-" + choiceIndex;

        WebElement subQnBox = browser.driver.findElement(By.id(elemId));
        fillTextBox(subQnBox, description);
    }

    public void clickAddMcqOtherOptionCheckbox(int qnNumber) {
        browser.driver.findElement(By.id("mcqOtherOptionFlag-" + qnNumber)).click();
    }

    public void clickAddMsqOtherOptionCheckbox(int qnNumber) {
        browser.driver.findElement(By.id("msqOtherOptionFlag-" + qnNumber)).click();
    }

    public void selectMcqGenerateOptionsFor(String generateFor, int qnNumber) {
        selectDropdownByVisibleValue(
                browser.driver.findElement(By.id("mcqGenerateForSelect-" + qnNumber)),
                generateFor);
    }

    public void selectMsqGenerateOptionsFor(String generateFor, int qnNumber) {
        selectDropdownByVisibleValue(
                browser.driver.findElement(By.id("msqGenerateForSelect-" + qnNumber)),
                generateFor);
    }

    public void selectConstSumPointsOptions(String pointsOption, int qnNumber) {
        markRadioButtonAsChecked(
                browser.driver.findElement(By.id("constSumPoints" + pointsOption + "-" + qnNumber)));
    }

    public void fillMcqOption(int optionIndex, String optionText, int qnNumber) {
        WebElement optionBox = browser.driver.findElement(By.id("mcqOption-" + optionIndex + "-" + qnNumber));
        fillTextBox(optionBox, optionText);
    }

    public void clickAddMoreMcqOptionLink(int qnNumber) {
        WebElement addMoreOptionLink = browser.driver.findElement(By.id("mcqAddOptionLink-" + qnNumber));
        addMoreOptionLink.click();
    }

    public void clickRemoveMcqOptionLink(int optionIndex, int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);

        WebElement mcqOptionRow = browser.driver.findElement(By.id("mcqOptionRow-" + optionIndex + idSuffix));
        WebElement removeOptionLink = mcqOptionRow.findElement(By.id("mcqRemoveOptionLink"));
        removeOptionLink.click();
    }

    public void clickGenerateOptionsCheckbox(int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);

        WebElement generateOptionsCheckbox = browser.driver.findElement(By.id("generateOptionsCheckbox" + idSuffix));
        generateOptionsCheckbox.click();
    }

    public void fillMsqOption(int optionIndex, String optionText, int qnNumber) {
        WebElement optionBox = browser.driver.findElement(By.id("msqOption-" + optionIndex + "-" + qnNumber));
        fillTextBox(optionBox, optionText);
    }

    public void clickAddMoreMsqOptionLink(int qnNumber) {
        WebElement addMoreOptionLink = browser.driver.findElement(By.id("msqAddOptionLink-" + qnNumber));
        addMoreOptionLink.click();
    }

    public void clickRemoveMsqOptionLink(int optionIndex, int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);

        WebElement msqOptionRow = browser.driver.findElement(By.id("msqOptionRow-" + optionIndex + idSuffix));
        WebElement removeOptionLink = msqOptionRow.findElement(By.id("msqRemoveOptionLink"));
        removeOptionLink.click();
    }

    public void fillConstSumOption(int optionIndex, String optionText, int qnNumber) {
        WebElement optionBox = browser.driver.findElement(By.id("constSumOption-" + optionIndex + "-" + qnNumber));
        fillTextBox(optionBox, optionText);
    }

    public void clickAddMoreConstSumOptionLink(int qnNumber) {
        WebElement addMoreOptionLink = browser.driver.findElement(By.id("constSumAddOptionLink-" + qnNumber));
        addMoreOptionLink.click();
    }

    public void clickRemoveConstSumOptionLink(int optionIndex, int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);

        WebElement msqOptionRow = browser.driver.findElement(By.id("constSumOptionRow-" + optionIndex + idSuffix));
        WebElement removeOptionLink = msqOptionRow.findElement(By.id("constSumRemoveOptionLink"));
        removeOptionLink.click();
    }

    public void clickAssignWeightsCheckbox(int qnNumber) {
        By by = By.id(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHTS_ASSIGNED + getIdSuffix(qnNumber));
        WebElement assignWeightsCheckbox = browser.driver.findElement(by);
        assignWeightsCheckbox.click();
    }

    public void clickAddRubricRowLink(int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);
        WebElement addRubricRowLink = browser.driver.findElement(By.id("rubricAddSubQuestionLink" + idSuffix));
        addRubricRowLink.click();
    }

    public void clickAddRubricColLink(int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);
        WebElement addRubricColLink = browser.driver.findElement(By.id("rubricAddChoiceLink" + idSuffix));
        addRubricColLink.click();
    }

    public void clickRemoveRubricRowLinkAndConfirm(int qnNumber, int row) {
        String idSuffix = getIdSuffix(qnNumber);
        WebElement removeRubricRowLink =
                browser.driver.findElement(By.id("rubricRemoveSubQuestionLink" + idSuffix + "-" + row));
        //addRubricRowLink.click();
        clickAndConfirm(removeRubricRowLink);
    }

    public void clickRemoveRubricColLinkAndConfirm(int qnNumber, int col) {
        String idSuffix = getIdSuffix(qnNumber);
        WebElement removeRubricColLink =
                browser.driver.findElement(By.id("rubricRemoveChoiceLink" + idSuffix + "-" + col));
        clickAndConfirm(removeRubricColLink);
    }

    public void verifyRankOptionIsHidden(int qnNumber, int optionIndex) {
        WebElement optionBox = browser.driver.findElement(By.id("rankOption-" + optionIndex + "-" + qnNumber));
        assertFalse(optionBox.isDisplayed());
    }

    public void fillRankOption(int qnNumber, int optionIndex, String optionText) {
        WebElement optionBox = browser.driver.findElement(By.id("rankOption-" + optionIndex + "-" + qnNumber));
        fillTextBox(optionBox, optionText);
    }

    public void tickDuplicatesAllowedCheckbox(int qnNumber) {
        WebElement checkBox = toggleDuplicatesAllowedCheckBox(qnNumber);
        assertTrue(checkBox.isSelected());
    }

    public void untickDuplicatesAllowedCheckbox(int qnNumber) {
        WebElement checkBox = toggleDuplicatesAllowedCheckBox(qnNumber);
        assertFalse(checkBox.isSelected());
    }

    private WebElement toggleDuplicatesAllowedCheckBox(int qnNumber) {
        WebElement checkBox = browser.driver.findElement(By.id("rankAreDuplicatesAllowed-" + qnNumber));
        checkBox.click();
        return checkBox;
    }

    public boolean isRankDuplicatesAllowedChecked(int qnNumber) {
        WebElement checkBox = browser.driver.findElement(By.id("rankAreDuplicatesAllowed-" + qnNumber));
        return checkBox.isSelected();
    }

    public void clickAddMoreRankOptionLink(int qnNumber) {
        WebElement addMoreOptionLink = browser.driver.findElement(By.id("rankAddOptionLink-" + qnNumber));
        addMoreOptionLink.click();
    }

    public void clickRemoveRankOptionLink(int qnNumber, int optionIndex) {
        String idSuffix = getIdSuffix(qnNumber);

        WebElement msqOptionRow = browser.driver.findElement(By.id("rankOptionRow-" + optionIndex + idSuffix));
        WebElement removeOptionLink = msqOptionRow.findElement(By.id("rankRemoveOptionLink"));
        removeOptionLink.click();
    }

    public int getNumOfOptionsInRankOptionsQuestion(int qnNumber) {
        WebElement rankOptionsTable = browser.driver.findElement(By.id("rankOptionTable-" + qnNumber));
        List<WebElement> optionInputFields = rankOptionsTable
                                                .findElements(
                                                     By.cssSelector("input[id^='rankOption-']"));
        return optionInputFields.size();
    }

    public void toggleNotSureCheck(int qnNumber) {
        browser.driver.findElement(By.id(Const.ParamsNames.FEEDBACK_QUESTION_CONTRIBISNOTSUREALLOWED
                                         + "-" + qnNumber))
                      .click();
    }


    //////////////////////////////////////////
    // FEEDBACK PATH AND VISIBILITY RELATED //
    //////////////////////////////////////////

    public void clickVisibilityPreview(int qnNumber) {
        getPreviewLabel(qnNumber).click();
    }

    public void clickVisibilityOptions(int qnNumber) {
        getEditLabel(qnNumber).click();
    }

    public void clickResponseVisiblityCheckBox(String checkBoxValue, int qnNumber) {
        WebElement questionForm = browser.driver.findElement(By.id("form_editquestion-" + qnNumber));
        By responseVisibilitycheckBox = By.cssSelector("input[value='" + checkBoxValue
                                                       + "'].answerCheckbox");
        WebElement checkbox = questionForm.findElement(responseVisibilitycheckBox);
        waitForElementVisibility(checkbox);
        checkbox.click();
    }

    public void selectGiverToBe(FeedbackParticipantType giverType, int qnNumber) {
        WebElement giverDropdown = browser.driver.findElement(By.id("givertype-" + qnNumber));
        selectDropdownByActualValue(giverDropdown, giverType.toString());
    }

    public void selectRecipientToBe(FeedbackParticipantType recipientType, int qnNumber) {
        WebElement giverDropdown = browser.driver.findElement(By.id("recipienttype-" + qnNumber));
        selectDropdownByActualValue(giverDropdown, recipientType.toString());
    }

    public void clickMaxNumberOfRecipientsButton(int qnNumber) {
        WebElement questionForm = browser.driver.findElement(By.id("form_editquestion-" + qnNumber));
        questionForm.findElement(By.xpath("//input[@name='numofrecipientstype' and @value='max']")).click();
    }

    public void clickCustomNumberOfRecipientsButton(int qnNumber) {
        WebElement questionForm = browser.driver.findElement(By.id("form_editquestion-" + qnNumber));
        questionForm.findElement(By.xpath("//input[@name='numofrecipientstype' and @value='custom']")).click();
    }

    public void fillNumOfEntitiesToGiveFeedbackToBox(String num, int qnNumber) {
        WebElement questionForm = browser.driver.findElement(By.id("form_editquestion-" + qnNumber));
        WebElement numOfEntitiesBox = questionForm.findElement(By.className("numberOfEntitiesBox"));
        fillTextBox(numOfEntitiesBox, num);
    }

    public boolean isOptionForSelectingNumberOfEntitiesVisible(int qnNumber) {
        return isElementVisible(By.className("numberOfEntitiesElements" + qnNumber));
    }

    public boolean isAllFeedbackPathOptionsEnabled() {
        List<WebElement> options = browser.driver.findElements(By.cssSelector("#givertype option"));
        options.addAll(browser.driver.findElements(By.cssSelector("#recipienttype option")));
        for (WebElement option : options) {
            if (!option.isEnabled()) {
                return false;
            }
        }
        return true;
    }

    public boolean verifyPreviewLabelIsActive(int qnNumber) {
        return getPreviewLabel(qnNumber).getAttribute("class").contains("active");
    }

    public boolean verifyEditLabelIsActive(int qnNumber) {
        return getEditLabel(qnNumber).getAttribute("class").contains("active");
    }

    public boolean verifyVisibilityMessageIsDisplayed(int qnNumber) {
        return getVisibilityMessage(qnNumber).isDisplayed();
    }

    public boolean verifyVisibilityOptionsIsDisplayed(int qnNumber) {
        return getVisibilityOptions(qnNumber).isDisplayed();
    }

    public WebElement getVisibilityOptionTableRow(int qnNumber, int optionRowNumber) {
        return getVisibilityOptions(qnNumber).findElement(
                By.xpath("(table/tbody/tr|table/tbody/hide)[" + optionRowNumber + "]"));
    }

    public void waitForAjaxErrorOnVisibilityMessageButton(int qnNumber) {
        String errorMessage = "Visibility preview failed to load.";
        By buttonSelector = By.id("visibilityMessageButton-" + qnNumber);
        waitForTextContainedInElementPresence(buttonSelector, errorMessage);
    }

    private WebElement getPreviewLabel(int qnNumber) {
        WebElement questionForm = browser.driver.findElement(By.id("form_editquestion-" + qnNumber));
        return questionForm.findElement(By.className("visibilityMessageButton"));
    }

    private WebElement getEditLabel(int qnNumber) {
        WebElement questionForm = browser.driver.findElement(By.id("form_editquestion-" + qnNumber));
        return questionForm.findElement(By.className("visibilityOptionsLabel"));
    }

    private WebElement getVisibilityMessage(int qnNumber) {
        return browser.driver.findElement(By.id("visibilityMessage-" + qnNumber));
    }

    private WebElement getVisibilityOptions(int qnNumber) {
        return browser.driver.findElement(By.id("visibilityOptions-" + qnNumber));
    }
}
