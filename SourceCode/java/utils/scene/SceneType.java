package utils.scene;

public enum SceneType {

    CREATE_GROUP("/fxml/CreateGroupForm.fxml", "Gruppe erstellen"),
    CREATE_STUDENT("/fxml/CreateStudentForm.fxml", "Studenten erstellen"),
    CREATE_SEMESTER("/fxml/CreateSemesterForm.fxml", "Semester erstellen"),
    CREATE_GROUPAGE("/fxml/CreateGroupageForm.fxml", "Klasse erstellen"),
    EDIT_GROUP("/fxml/EditGroupForm.fxml", "Gruppe bearbeiten"),
    EDIT_STUDENT("/fxml/EditStudentForm.fxml", "Studenten bearbeiten"),
    EDIT_SEMESTER("/fxml/EditSemesterForm.fxml", "Semester bearbeiten"),
    EDIT_GROUPAGE("/fxml/EditGroupageForm.fxml", "Klasse bearbeiten"),
    HOME("/fxml/HomeScreenView.fxml", "SemestervErwaltungsPlan"),
    LOGIN("/fxml/LoginForm.fxml", "SemestervErwaltungsPlan"),
    REGISTER("/fxml/RegisterForm.fxml", "Registrierung"),
    CHAT_APP("/fxml/ChatTabContent.fxml", "ChatApp"),
    CHAT_WINDOW("/fxml/ChatWindowTabPane.fxml", "ChatWindow"),
    CREATE_NOTEPAD_WINDOW("/fxml/CreateNotepadForm.fxml", "CreateNotepadWindow"),
    EDIT_NOTEPAD_WINDOW("/fxml/EditNotepadForm.fxml", "EditNotepadWindow"),
    NOTESTAB_WINDOW("/fxml/NotesTabForm.fxml", "NotesTabWindow"),
    NOTE_WINDOW("/fxml/NoteWindowForm.fxml", "NoteWindow"),
    USER_ADMIN("/fxml/UserAdministrationForm.fxml", "User Administration"),
    EDIT_USER ("/fxml/EditUserForm.fxml","User bearbeiten"),
    CREATE_TIMETABLE("/fxml/CreateTimetable.fxml","Kalender"),
    TIMETABLE_WINDOW("/fxml/TimetableWindow.fxml","Stundenplan"),
    INVITE_CODE("/fxml/InviteCode.fxml", "Einwahlcodes"),
    PASSWORD_RESET("/fxml/PasswordResetForm.fxml","Passwort ändern"),
    CREATE_TERM_ORGANISATION("/fxml/CreateTermOrganisation.fxml","Semesterplan erstellen"),
    EDIT_AND_CREATE_SEMESTERPLAN("/fxml/Semesterplan.fxml","Semesterplan"),
    TARDY_VIEW("/fxml/TardyView.fxml","Anwesenheitsplan");



    private String path, title;

    SceneType(String path, String title) {
        this.path = path;
        this.title = title;
    }

    public String getPath() {
        return this.path;
    }

    public String getTitle() { return this.title; }

}
