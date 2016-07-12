package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.template.CourseEditInstructorPanel;
import teammates.ui.template.ElementTag;

public class InstructorCourseEditPageData extends PageData {
    private int instructorToShowIndex;
    private ElementTag editCourseButton;
    private ElementTag deleteCourseButton;
    private CourseAttributes course;
    private List<CourseEditInstructorPanel> instructorPanelList;
    private CourseEditInstructorPanel addInstructorPanel;
    private ElementTag addInstructorButton;
    
    public InstructorCourseEditPageData(AccountAttributes account, CourseAttributes course,
                                        List<InstructorAttributes> instructorList,
                                        InstructorAttributes currentInstructor, int instructorToShowIndex,
                                        List<String> sectionNames, List<String> feedbackNames) {
        super(account);
        this.course = course;
        this.instructorToShowIndex = instructorToShowIndex;
        
        createButtons(currentInstructor);
        boolean isShowingAllInstructors = instructorToShowIndex == -1;
        if (isShowingAllInstructors) {
            instructorPanelList = createInstructorPanelList(currentInstructor, instructorList, sectionNames,
                                                            feedbackNames);
        } else {
            instructorPanelList = createInstructorPanelForSingleInstructor(
                                            currentInstructor, instructorList.get(0), instructorToShowIndex,
                                            sectionNames, feedbackNames);
        }
        addInstructorPanel = createInstructorPanel(currentInstructor, instructorPanelList.size() + 1, null,
                                                   sectionNames, feedbackNames);
    }

    private List<CourseEditInstructorPanel> createInstructorPanelList(InstructorAttributes currentInstructor,
                                           List<InstructorAttributes> instructorList,
                                           List<String> sectionNames, List<String> feedbackNames) {
        List<CourseEditInstructorPanel> panelList = new ArrayList<CourseEditInstructorPanel>();
        int instructorIndex = 0;
        for (InstructorAttributes instructor : instructorList) {
            instructorIndex++;
            CourseEditInstructorPanel instructorPanel = createInstructorPanel(currentInstructor,
                                                                              instructorIndex, instructor,
                                                                              sectionNames, feedbackNames);
            panelList.add(instructorPanel);
        }
        return panelList;
    }
    
    private List<CourseEditInstructorPanel> createInstructorPanelForSingleInstructor(InstructorAttributes currentInstructor,
                                    InstructorAttributes instructorForPanel, int instructorIndex,
                                    List<String> sectionNames, List<String> feedbackNames) {
        List<CourseEditInstructorPanel> panelList = new ArrayList<CourseEditInstructorPanel>();
        CourseEditInstructorPanel instructorPanel = createInstructorPanel(
                                                            currentInstructor,
                                                            instructorIndex, instructorForPanel,
                                                            sectionNames, feedbackNames);
        panelList.add(instructorPanel);
     
        return panelList;
    }

    private void createButtons(InstructorAttributes currentInstructor) {
        boolean isDisabled = !currentInstructor.isAllowedForPrivilege(
                                                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE);
        
        String editCourseButtonContent = "<span class=\"glyphicon glyphicon-pencil\"></span> Edit";
        editCourseButton = createButton(editCourseButtonContent, "btn btn-primary btn-xs", "courseEditLink",
                                        "javascript:;", Const.Tooltips.COURSE_INFO_EDIT, null, null, null, null, isDisabled);
        
        String content = "<span class=\"glyphicon glyphicon-trash\"></span>Delete";
        String onClick = "return toggleDeleteCourseConfirmation('" + course.getId() + "');";
        deleteCourseButton = createButton(content, "btn btn-primary btn-xs", "courseDeleteLink",
                                          getInstructorCourseDeleteLink(course.getId(), false),
                                          Const.Tooltips.COURSE_DELETE, onClick, null, null, null, isDisabled);
        
        isDisabled = !currentInstructor.isAllowedForPrivilege(
                                            Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);
        addInstructorButton = createButton(null, "btn btn-primary", "btnShowNewInstructorForm", null, null,
                                           "showNewInstructorForm()", null, null, null, isDisabled);
    }
    
    private CourseEditInstructorPanel createInstructorPanel(InstructorAttributes currentInstructor,
                                                            int instructorIndex,
                                                            InstructorAttributes instructor,
                                                            List<String> sectionNames,
                                                            List<String> feedbackNames) {
        CourseEditInstructorPanel instructorPanel = new CourseEditInstructorPanel(instructorToShowIndex,
                                                                          instructorIndex, instructor,
                                                                          sectionNames, feedbackNames);
        
        if (instructor != null) {
            String buttonContent = "<span class=\"glyphicon glyphicon-envelope\"></span> Resend Invite";
            boolean isDisabled = !currentInstructor.isAllowedForPrivilege(
                                                            Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);
            String href;
            String onClick;
            if (instructor.googleId == null) {
                href = getInstructorCourseRemindInstructorLink(instructor.courseId, instructor.email);
                instructorPanel.setResendInviteButton(createButton(buttonContent, "btn btn-primary btn-xs",
                                                                   "instrRemindLink" + instructorPanel.getIndex(),
                                                                   href, Const.Tooltips.COURSE_INSTRUCTOR_REMIND,
                                                                   null, instructor.getCourseId(), instructor.getName(),
                                                                   null, isDisabled));
            }
            
            buttonContent = "<span class=\"glyphicon glyphicon-pencil\"></span> Edit";
            instructorPanel.setEditButton(createButton(buttonContent, "btn btn-primary btn-xs",
                                                       "instrEditLink" + instructorPanel.getIndex(),
                                                       "javascript:;", Const.Tooltips.COURSE_INSTRUCTOR_EDIT,
                                                       null, null, null, null, isDisabled));
            
            buttonContent = "<span class=\"glyphicon glyphicon-remove\"></span> Cancel";
            instructorPanel.setCancelButton(createButton(buttonContent, "btn btn-primary btn-xs",
                                                         "instrCancelLink" + instructorPanel.getIndex(),
                                                         "javascript:;", Const.Tooltips.COURSE_INSTRUCTOR_CANCEL_EDIT,
                                                         null, null, null, null, isDisabled));
            
            buttonContent = "<span class=\"glyphicon glyphicon-trash\"></span> Delete";
            href = getInstructorCourseInstructorDeleteLink(instructor.courseId, instructor.email);
            boolean isDeleteSelf = instructor.email.equals(this.account.email);
            instructorPanel.setDeleteButton(createButton(buttonContent, "btn btn-primary btn-xs",
                                                         "instrDeleteLink" + instructorPanel.getIndex(),
                                                         href, Const.Tooltips.COURSE_INSTRUCTOR_DELETE,
                                                         null, instructor.getCourseId(), instructor.getName(),
                                                         String.valueOf(isDeleteSelf), isDisabled));
        }
        
        return instructorPanel;
    }
    
    public ElementTag getEditCourseButton() {
        return editCourseButton;
    }
    
    public ElementTag getDeleteCourseButton() {
        return deleteCourseButton;
    }
    
    public ElementTag getAddInstructorButton() {
        return addInstructorButton;
    }
    
    public CourseEditInstructorPanel getAddInstructorPanel() {
        return addInstructorPanel;
    }
    
    public CourseAttributes getCourse() {
        return course;
    }
    
    public List<CourseEditInstructorPanel> getInstructorPanelList() {
        return instructorPanelList;
    }
    
    public int getInstructorToShowIndex() {
        return instructorToShowIndex;
    }
    
    private ElementTag createButton(String content, String buttonClass, String id, String href,
                                    String title, String onClick, String dataCourseId, String dataInstructorName,
                                    String dataIsDeleteSelf, boolean isDisabled) {
        ElementTag button = new ElementTag(content);
        
        button.setAttribute("type", "button");
        
        if (buttonClass != null) {
            button.setAttribute("class", buttonClass);
        }
        
        if (id != null) {
            button.setAttribute("id", id);
        }
        
        if (href != null) {
            button.setAttribute("href", href);
        }
        
        if (title != null) {
            button.setAttribute("title", title);
            button.setAttribute("data-toggle", "tooltip");
            button.setAttribute("data-placement", "top");
        }
        
        if (onClick != null) {
            button.setAttribute("onclick", onClick);
        }
        
        if (dataCourseId != null) {
            button.setAttribute("data-course-id", dataCourseId);
        }
        
        if (dataInstructorName != null) {
            button.setAttribute("data-instructor-name", dataInstructorName);
        }
        
        if (dataIsDeleteSelf != null) {
            button.setAttribute("data-is-delete-self", dataIsDeleteSelf);
        }
        
        if (isDisabled) {
            button.setAttribute("disabled", null);
        }
        return button;
    }
}

