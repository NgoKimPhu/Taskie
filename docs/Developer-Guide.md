# Instruction
The aim of this developer guide is to provide you, the incoming developer, with an overview of the project’s progress thus far. It is also intended to help you begin working on this project by introducing to you the current design of the product and possible implementations of new components.

#Table of Content
- [Architecture](#architecture)
- [TaskieUI Component](#taskieui-component)
- [TaskieLogic Component](#taskielogic-component)
- [TaskieParser Component](#taskieparser-component)
    - [`Author` Class](#author-class)
    - [`CodeSnippet` Class](#codesnippet-class)
    - [`SourceFile` Class](#sourcefile-class)
- [TaskieStorage Component](#taskiestorage-component)
    - [`Author` Class](#author-class)
    - [`CodeSnippet` Class](#codesnippet-class)
    - [`SourceFile` Class](#sourcefile-class)
- [TaskieUtil Component](#taskieutil-component)
    - [`MainApp` Class](#mainapp-class)
    - [`CommandBarController` Class](#commandbarcontroller-class)
    - [`SummaryController` Class](#summarycontroller-class)
    - [`AuthorBean` Class](#authorbean-class)
    - [`FileStatsController` Class](#filestatscontroller-class)
    - [`FileStatsItem` Class](#filestatsitem-class)
- [Future Development](#future-development)

# Architecture
Taskie is made up of five components which are User Interface (UI), Logic, Parser, Storage and Utility; each of them contains some sub components. UI, Logic, Parser and Storage are the core components of Taskie and Utility is the component facilities core components’ functionality. The component for user to interact with Taskie is UI. It takes user’s command and passes it to Logic. Logic component is the center of Taskie which takes user’s command, interacts with Parser and Storage and decides which action should be taken. Parser component interacts with Logic. It identifies the user’s command and facilities Logic taking action. Storage is to store users’ task lists data. It provides Logic component with a series of application programming interfaces (APIs) to modify the internal data. As for Utility component, it is a collection of data structures used by core components. Figure 3.2.1 below shows the architecture of Taskie. 

# TaskieUI Component
TaskieUI useses javafx package.
# TaskieLogic Componment 
The Logic component acts as the “middle man” in our software system. It glues all parts together by being a common accessing point of the other components. As shown in our architecture diagram, Logic takes in the user input String from UI, then passes this String to Parser. Parser will interpret this user command and return to Logic an action. Logic determines the action type, and then sends the relative information to the respective methods, which will interact with Storage to complete the action. Logic also relies heavily on the three Utility Classes, namely IndexTaskPair, TaskieEnum, TaskieAction and TaskieTask. These Utility Classes will be discussed soon.
	
The Logic component is made up of one single TaskieLogic class. All fields and methods in TaskieLogic class can be found in the appendix with a brief description. This section mainly explains the structure of this class.

The methods in TaskieLogic can be categorized into three groups: 1) the Backbone Functions, 2) the Auxiliary Methods and 3) the Functional Methods. The Backbone Functions constitute the framework of Logic. The Auxiliary Methods provide necessary services to other methods, such as formatting a String. The Functional Methods are the implementation of various functions that Taskie supports, such as add, delete, etc. To understand how Logic works, we start with the Backbone Functions. 


# TaskieParser Component
TaskieParser is the component that takes user's input data in the form of a String, analyses the string, then assembles and returns a TaskieAction object with Action type and optional attributes like TaskieTask or index depending on its type. TaskieParser’s fields and methods are described below.

Components of TaskieParser can be divided into three parts, i.e. the API method, helper internal classes, and helper methods. The first two parts will be discussed in more details and information on helper methods can be found in the appendix. TaskieParser has a dependency on Utility classes TaskieEnum, TaskieAction and TaskieTask.

The API contains only one protected (package-wide) method parse(String) that is the very main method of the whole class. parse(String) will first use helper methods to get the first word from the String and determine what type of Action it is based on this word. Should this type of Action indicates that more parameters are needed (Add: task data; Delete: index of the task to be deleted; Update: both index and task data; Search: data to be searched for), parse(String) will goes on to call helper sub-methods to properly handle each specific case.
 

# TaskieStorage Component 
Storage component is the component to store user data and interact with the actual data file. Storage receive the message from Logic component and proceed add, delete, search, update and so on in the task list data. Every time after an action, Storage will write the data to the file. Storage component make use of four classes in Utility component which are TaskieTask, TaskieEnum, TaskComparator, IndexTaskPair and CalendarPair.
# TaskieUtil Component

# Future Development
 ## Improving on UI
Currently we have a simple and neat list view of task list. In the future development, a calendar view of task list could be implemented to improve user experience.
 ## Improving on Parser
One of the features of Taskie is flexible command format. The future development could improve the natural language processing ability on parser.
