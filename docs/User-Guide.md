# Table of Contents
<!-- MarkdownTOC -->

- [Quick Start](#quick-start)
- [Feature Details](#feature-details)
- [Command Format](#command-format)
  - [Basic Commands](#basic-commands)
  - [Advanced Commands](#advances-commands)
- [Cheatsheet](#cheatsheet)

<!-- /MarkdownTOC -->

# Quick Start

1. **Install JDK 8u60 or later**: Earlier version may not work.
2. **Download Taskie**: 
3. **Create your own account and specify data storage path**:

# Feature Details
1. **Support three task types**: event(task with start time and end time), deadline task(task with only deadline), floating task(task without start time or end time).
2. **CRUD fuctions**: create tasks, read tasks, update tasks, delete tasks. 
3. **Undo function**: undo the most recent action.
4. **Command line input UI**: edit the task list by tying in command line.
5. **Output GUI**: both calendar view and list view of task list.
6. **Free-slot work recommendation**: if user want to add time specification to a floating task, Taskie is able to suggest free-slot to user.
7. **Mark tasks**: user is able to make a task as completed.
8. **Quick activation**: could be activated by pressing a keyboard shortcut.
9. **Flexible commands**: Taskie supports natural variations of the command format.
10. **Decide what to do next**: when the user do not to know what to do, Taskie could provide user some suggestions about what to do next.
11. **Tentative timing**:“block” multiple slots when the exact timing of a task is uncertain, and release the blocked slots when the time is finalized, e.g., Block Mon 2-3pm and Tue 2-3pm for a meeting with boss → the meeting is confirmed for Tue → automatically release Mon slot.
12. **Task tracking**: keep track of which items are done and which are yet to be done.
13. **Power search**: search/filter the tasks based on content/time/priority.
14. **Postponing task**: user could postpond the time or date of a task.
15. **Offline work**: Taskie can work independently from Internet connection. However, when there is Internet connection, it can proceed data synchronization.
16. **Customizable data storage path**: user can specify a specific folder as the data storage location. For example, user can choose to store the data file in a local folder controlled by a cloud syncing service (e.g. dropbox), allowing him to access task data from multiple computers.
17. **Human readable and editable**: Taskie allows advanced users to manipulate the task list by editing the local data file.

# Command Format
## Basic Commands

1. **add [task] [type] [-op:start time] [-op:end time] [-op:priority]/ + [task] [type] [-op:start time] [-op:end time] [-op:priority]**: 
2. **display** :
  1. **display** :
  2. **display {-op: value/values}** :
  3. **display [kewords]** :
3. **delete [index] / - [index]** :
4. **delete [keywords] / - [keywords]** :
5. **done [index]** :
6. **done [kewords]** :
7. **update [index] [value] <new value>** :
8. **undo** : 


## Advanced Commands
1. **free slot / fs**:
2. **what to do**: randomly find a task for user.
3. **change path <new path>** :
4. ****


# Cheatsheet
