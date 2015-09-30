# Table of Contents
<!-- MarkdownTOC -->

- [Quick Start](#quick-start)
- [Feature Details](#feature-details)
- [Command Format](#command-format)
  - [Basic Commands](#basic-commands)
  - [Advanced Commands](#advanced-commands)
- [Cheatsheet](#cheatsheet)

<!-- /MarkdownTOC -->

# Quick Start

1. **Install JDK 8u40 or later**: Earlier version may not work.
2. **Download Taskie**: Download the Taskie.jar file. 
3. **Create/access your own account and specify data storage path**: Follow the instruction provide by Taskie to create or access the task list.

# Feature Details
1. **Support three task types**: Event(task with start time and end time), deadline task(task with only deadline), floating task(task without start time or end time).
2. **CRUD fuctions**: Create tasks, read tasks, update tasks, delete tasks. 
3. **Undo function**: Undo the most recent action.
4. **Command line input UI**: Edit the task list by tying in command line.
5. **Output GUI**: Taskie will have split views for user. The left part will response to users' commands and the right part will display all the tasks in task list.  
6. **Free-slot work recommendation**: If user want to add time specification to a floating task, Taskie is able to suggest free-slot to user.
7. **Mark tasks**: User is able to make a task as completed.
8. **Quick activation**: Could be activated by pressing a keyboard shortcut.
9. **Flexible commands**: Taskie supports natural variations of the command format.
10. **Decide what to do next**: When the user do not to know what to do, Taskie could provide user some suggestions about what to do next.
11. **Tentative timing**:“Block” multiple slots when the exact timing of a task is uncertain, and release the blocked slots when the time is finalized, e.g., Block Mon 2-3pm and Tue 2-3pm for a meeting with boss → the meeting is confirmed for Tue → automatically release Mon slot.
12. **Task tracking**: Keep track of which items are done and which are yet to be done.
13. **Power search**: Search/filter the tasks based on content/time/priority.
14. **Postponing task**: User could postpond the time or date of a task.
15. **Offline work**: Taskie can work independently from Internet connection. However, when there is Internet connection, it can proceed data synchronization.
16. **Customizable data storage path**: User can specify a specific folder as the data storage location. For example, user can choose to store the data file in a local folder controlled by a cloud syncing service (e.g. dropbox), allowing him to access task data from multiple computers.
17. **Human readable and editable data file**: Taskie allows advanced users to manipulate the task list by editing the local data file.

# Command Format
## Basic Commands

1. **add [task] [type] [-op:start time] [-op:end time] [-op:priority]/ + [task] [type] [-op:start time] [-op:end time] [-op:priority]**: 
Add a task into task list. [type] should be one of three choices: event, deadline, float. 
If the task type is "event", user is required to type in start time and end time. If the task type is "deadline",    user is required to type in only end time. If the task type is "float", user do not need to type in time specification
2. **display** :
  1. **display** : Display all the tasks in the list.
  2. **display {-op: value/values}** : Display tasks filered by some 
  3. **display [kewords]** : Display tasks which contain the key words.
3. **delete [index] / - [index]** : 
  1. **delete [index] / - [index]**: Delete an event/ deadline task with certain index.
  2. **delete f [index] / - f [index]**: Delete a floating task.
4. **delete [keywords] / - [keywords]** : Will first display all the tasks contains the keywords with indeces to user, then user need to type in command [index].
5. **done [index]** : 
  1. **done [index]**: Mark an event/ deadline task as completed.
  2. **done f [index]**: Mark a floating task as completed
6. **done [kewords]** : Will first display all the tasks contains the keywords with indeces to user, then user need to type in command [index].
7. **update [index] [value] <new value>** :
  1. **update [index] [value] <new value>**: Update the information of an event/ deadline task.
  2. **update f [index] [value] <new value>**: Update the information of a floating task.
8. **undo** : Undo the most recent action.
9. **help**: Ask for help.
10. **exit**: Exit Taskie.

## Advanced Commands
1. **free slot / fs**: Find all the free slot for user
2. **what to do**: Randomly find a task for user.
3. **change path <new path>** : Move the data file to a new path.


# Cheatsheet
Command | Description
--------| ------------
`add [task] [type] [-op:start time] [-op:end time] [-op:priority]` </br> `+ [task] [type] [-op:start time] [-op:end time] [-op:priority]` | Add a task into task list
`display` </br>`display {-op: value/values}`</br>`display [kewords]`| Display the tasks
`delete [index] / - [index]**`</br>`delete f [index] / - f [index]**`</br>`delete [keywords] / - [keywords]` | Delete a task
`done [index]`</br>`done f [index]`</br>`done [kewords]` | Mark a task as done
`update [index] [value] <new value>`</br>`update f [index] [value] <new value>` | Update the information of a task.
`undo` | Undo the most recent action
`help` | Ask for help
`exit` | Exit Taskie
`free slot / fs` | Find free slot
`what to do` | Recommand user what to do next
`change path <new path>` | Move the data file to a new path
