package bundles;

import java.util.ListResourceBundle;

public class Language_en_CA extends ListResourceBundle {
    private static final Object[][] contents = {
            //Main.java
            {"cantStartErrorForLog", "Unable to launch the app as IP and port are not specified!"},
            {"cantStartAlertHeader", "App launch error"},
            {"cantStartAlertContent", "Unable to launch the app as IP (or hostname) and port are not specified!\n(They must be specified via arguments of the command line. IP format: xxx.xxx.xxx.xxx; hostname format: not empty string; port format: a number from 1 to 65535.)"},
            {"wrongPortErrorForLog", "Unable to launch the app because of invalid port format: "},
            {"wrongPortAlertContent", "Unable to launch the app because of invalid port format!\n(a number from 1 to 65535 must be specified via second argument of the command line)"},

            //start.fxml
            {"startWindowTitle", "Sign into app"},
            {"authorisation", "Authorize"},
            {"registration", "Register"},
            {"login", "Login"},
            {"password", "Password"},
            {"proceedSignIn", "SIGN IN"},
            {"proceedSignUp", "SIGN UP"},
            {"emptyLoginError", "Login can't be empty!"},
            {"longLoginError", "Login can't be longer than\n20 characters!"},
            {"emptyPasswordError", "Password can't be empty!"},
            {"mainWindowTitle", "Collection management"},
            {"wrongPassword", "Wrong password!"},
            {"userNotExists", "That user doesn't exist!"},
            {"serverCantConnectToDB", "Server couldn't connect to the DB!\nTry again."},
            {"userAlreadyExists", "That user is already\nregistered!"},

            //main.fxml
            {"executeCommand", "Execute command"},
            {"applyFilter", "Apply a filter"},
            {"proceed", "PROCEED"},
            {"mapWindowTitle", "Object map"},
            {"toMap", "To object map"},
            {"chooseCommand", "Choose command"},
            {"chooseField", "Choose field"},
            {"filterAlertHeader", "Filter configuration error"},
            {"filterConditionAlertContent", "Invalid condition format!\nExamples of valid conditions (numbers):\n>5\n<5\n=5\n>=5\n<=5\n!=5\nExamples of valid conditions (strings):\n=text\n!=text"},
            {"collectionLoadAlertHeader", "Collection loading error"},
            {"collectionEmptyAlertHeader", "Collection is empty!"},
            {"collectionEmptyAlertContent", "Zero elements loaded."},
            {"filterComparisonAlertContent", "Unable to compare a number and a string!"},
            {"condition", "Condition"},
            {"update", "Update"},
            {"remove", "Remove"},
            {"sendErrorAlertHeader", "Command send/receive error"},
            {"prepareErrorAlertHeader", "Command's argument check error"},
            {"command", "Command "},
            {"executedSuccessfully", " is executed successfully!"},
            {"executeFailure", "Command execution error"},
            {"checkArgumentsAlertHeader", "Command's argument check errors occurred"},
            {"returnedResult", " has returned following result:"},
            {"idError", "Invalid id input! Appropriate format: whole positive number.\n"},
            {"nameError", "Invalid product name input! Can't be an empty string.\n"},
            {"xError", "Invalid x coordinate input! Appropriate format: fractional number.\n"},
            {"yError", "Invalid y coordinate input! Appropriate format: whole number.\n"},
            {"priceError", "Invalid product price input! Appropriate format: fractional positive number.\n"},
            {"partNumError", "Invalid part number input! Appropriate format: #xxxxxx, where x - digits.\n"},
            {"manCostError", "Invalid manufacture cost input! Appropriate format: fractional positive number.\n"},
            {"uomError", "Invalid input of unit of measure! Available input options: "},
            {"manNameError", "Invalid manufacturer name input! Can't be an empty string.\n"},
            {"turnoverError", "Invalid input of manufacturer's annual turnover! Appropriate format: empty string or whole positive number.\n"},
            {"empCountError", "Invalid input of manufacturer's employee count! Appropriate format: empty string or whole positive number.\n"},
            {"typeError", "Invalid input of manufacturer's type! Available input options: empty string, "},
            {"stopSessionAlertHeader", "Session is terminated"},
            {"stopSessionAlertContent", "Due to numerous connection errors you are signed out. Try signing in later."},
            {"back", "Back"},
            {"signOut", "Sign out"},
            {"updateTooltip","Click twice to update the element"},

            //other
            {"changeSceneError", "Scene change error"},
            {"jsonError", "JSON-syntax error! "},
            {"productCreationError", "Command is not executed - unable to create product!"},

            //Add.java
            {"addInteractiveError", "Command add isn't supposed to have any arguments!"},
            {"addNotInteractiveError", "Command add is supposed to have 1 argument: JSON-string!"},
            {"addSqlError", "SQL error occurred when adding element!"},
            {"addDescription", "Adds new element to collection."},
            {"addSyntax", " Syntax: add \n(In scripts - add {element})"},

            //AddIfMax.java
            {"addIfMaxInteractiveError", "Command add_if_max isn't supposed to have any arguments!"},
            {"addIfMaxNotInteractiveError", "Command add_if_max is supposed to have 1 argument: JSON-string!"},
            {"addIfMaxSqlError", "Even though the element's price is the biggest, it was not added due to an SQL error!"},
            {"addIfMaxError1", "Element wasn't added as its price isn't the biggest."},
            {"addIfMaxSqlError2", "Even though the element's price is the biggest(as collection is empty), it was not added due to an SQL error!"},
            {"addIfMaxDesc", "Adds new element to collection if its price is the biggest."},
            {"addIfMaxSyntax", " Syntax: add_if_max \n(In scripts - add_if_max {element})"},

            //Exit.java
            {"exitError", "Command exit isn't supposed to have any arguments!"},
            {"exitDesc", "Stops the app."},
            {"exitSyntax", " Syntax: exit"},

            //Clear.java
            {"clearError", "Command clear isn't supposed to have any arguments!"},
            {"clearError1", "Not all elements were deleted due to SQL errors an the fact that you don't own some elements!"},
            {"clearError2", "Not all elements were deleted due to SQL errors!"},
            {"clearError3", "Not all elements were deleted as you don't own some elements!"},
            {"clearError4", "You don't own any element in collection, nothing to clear!"},
            {"clearError5", "Collection is empty, nothing to clear!"},
            {"clearSuccess", "Collection is cleared!"},
            {"clearDesc", "Clears the collection."},
            {"clearSyntax", " Syntax: clear"},

            //History.java
            {"historyError", "Command history isn't supposed to have any arguments!"},
            {"historySuccess", "Command history executed, last 7 commands:"},
            {"historyEmpty", "History is empty!"},
            {"historyDesc", "Prints last 7 executed commands."},
            {"historySyntax", " Syntax: history"},

            //Help.java
            {"helpError", "Command help isn't supposed to have any arguments!"},
            {"helpSuccess", "Available commands:"},
            {"helpDesc", "Prints info about available commands."},
            {"helpSyntax", " Syntax: help"},

            //Show.java
            {"showError", "Command show isn't supposed to have any arguments!"},
            {"showDesc", "Prints the collection."},
            {"showSyntax", " Syntax: show"},

            //RemoveById.java
            {"removeError", "Command remove_by_id is supposed to have 1 argument - whole positive number!"},
            {"removeError1", "When deleting an element with id "},
            {"removeError2", " an SQL-error occurred!"},
            {"removeError3", "You don't own element with id "},
            {"removeError4", ", so you can't remove it!"},
            {"removeError5", "Removal is impossible: no element with id "},
            {"removeSuccess1", "Element with id "},
            {"removeSuccess2", " was successfully removed!"},
            {"removeDesc", "Removes element with the specified id from collection."},
            {"removeSyntax", " Syntax: remove_by_id id, where id - whole positive number."},

            //RemoveByUOM.java
            {"removeUOMError", "Command remove_any_by_unit_of_measure is supposed to have 1 argument out of these: "},
            {"removeUOMError1", "When removing an element with unitOfMeasure "},
            {"removeUOMError2", " an SQL-error occurred"},
            {"removeUOMError3", "We found "},
            {"removeUOMError4", " elements with unitOfMeasure "},
            {"removeUOMError5", ", but you don't own any of them, so you can't remove them!"},
            {"removeUOMError6", "Nothing to remove - there are no elements with unitOfMeasure "},
            {"removeUOMSuccess1", "One of elements with unitOfMeasure "},
            {"removeUOMSuccess2", " was successfully removed!"},
            {"removeUOMDesc", "Removes random element that has specified unitOfMeasure."},
            {"removeUOMSyntax", " Syntax: remove_any_by_unit_of_measure unitOfMeasure, where unitOfMeasure: "},

            //RemoveGreater.java
            {"rgInteractiveError", "Command remove_greater isn't supposed to have any arguments!"},
            {"rgNotInteractiveError", "Command remove_greater is supposed to have 1 argument: JSON-string!"},
            {"rgError", "There are no elements that have bigger prices than the specified element's - collection is empty!"},
            {"rgError1", "Not all elements that have bigger prices than the specified element's were deleted!"},
            {"rgError2", "Nothing deleted, as there are no elements that have bigger prices than the specified element or errors occurred!"},
            {"rgDesc", "Removes all elements that have bigger prices than the specified element from collection."},
            {"rgSyntax", " Syntax: remove_greater \n(In scripts - remove_greater {element}"},

            //Update.java
            {"updateInteractiveError", "Command update is supposed to have 1 argument - whole positive number!"},
            {"updateNotInteractiveError", "Command update is supposed to have 2 arguments: whole positive number and a JSON-string!"},
            {"updateError1", "Nothing to update: there is no element with id "},
            {"updateError2", " in collection!"},
            {"updateError3", "When updating an element with id "},
            {"updateError4", ", which is why you can't edit it!"},
            {"updateDesc", "Updates the value of element with the specified id."},
            {"updateSyntax", " Syntax: update id, where id - whole positive number. \n(In scripts - update id {element})"},

            //ExecuteScript.java
            {"execError", " doesn't exist!"},
            {"execMsg", "Script named "},
            {"execSuccess", " is successfully executed!"},
            {"updateError1", " wasn't executed, because executable file can't be a directory."},
            {"updateError2", " wasn't executed, because executable file can't be a special file."},
            {"updateError3", " wasn't executed, because the executable file is closed for reading."},
            {"updateError4", " wasn't executed, because it calls itself!"},
            {"execDesc", "Executes a script."},
            {"execSyntax", " Syntax: execute_script file_name, where file_name - full name of file."},

    };

    protected Object[][] getContents() {
        return contents;
    }
}
