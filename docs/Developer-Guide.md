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
Taskie is made up of five components which are User Interface (UI), Logic, Parser, Storage and Utility; each of them contains some sub components. UI, Logic, Parser and Storage are the core components of Taskie and Utility is the component facilities core components’ functionality. The component for user to interact with Taskie is UI. It takes user’s command and passes it to Logic. Logic component is the center of Taskie which takes user’s command, interacts with Parser and Storage and decides which action should be taken. Parser component interacts with Logic. It identifies the user’s command and facilities Logic taking action. Storage is to store users’ task lists data. It provides Logic component with a series of application programming interfaces (APIs) to modify the internal data. As for Utility component, it is a collection of data structures used by core components. Figure 3.2.1 below shows the architecture of Taskie. 

# TaskieParser Component
Taskie is made up of five components which are User Interface (UI), Logic, Parser, Storage and Utility; each of them contains some sub components. UI, Logic, Parser and Storage are the core components of Taskie and Utility is the component facilities core components’ functionality. The component for user to interact with Taskie is UI. It takes user’s command and passes it to Logic. Logic component is the center of Taskie which takes user’s command, interacts with Parser and Storage and decides which action should be taken. Parser component interacts with Logic. It identifies the user’s command and facilities Logic taking action. Storage is to store users’ task lists data. It provides Logic component with a series of application programming interfaces (APIs) to modify the internal data. As for Utility component, it is a collection of data structures used by core components. Figure 3.2.1 below shows the architecture of Taskie. 

# TaskieStorage Component 
Storage component is the component to store user data and interact with the actual data file. Storage receive the message from Logic component and proceed add, delete, search, update and so on in the task list data. Every time after an action, Storage will write the data to the file. Storage component make use of four classes in Utility component which are TaskieTask, TaskieEnum, TaskComparator, IndexTaskPair and CalendarPair.
# TaskieUtil Component

# Future Development
 ## Improving on UI
Currently we have a simple and neat list view of task list. In the future development, a calendar view of task list could be implemented to improve user experience.
 ## Improving on Parser
One of the features of Taskie is flexible command format. The future development could improve the natural language processing ability on parser.
