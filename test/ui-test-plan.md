# EV UI Test Plan

Text-UI regression tests for EV. Each test case starts a fresh `EV` process, feeds it the
listed input lines, and compares the console output against the expected output.

## How to run

```powershell
.\test\run-ui-tests.ps1
```

Options:

- `.\test\run-ui-tests.ps1 -Filter TC-13` runs a single case (prefix match, so `-Filter TC-1` runs TC-10 to TC-19).
- `.\test\run-ui-tests.ps1 -Quiet` prints only the pass/fail summary instead of the full session transcript.

The runner compiles `src\main\java\*.java` into `out\` before running, so there is no need to
compile by hand first. It stops at the first failing case and prints the expected and actual
output side by side.

## Conventions

Every session starts with the banner and greeting, and ends with the farewell:

```text
 _______     __
|   ____|   /  \
|  |__     |    |
|   __|    |    |
|  |____    \  /
|_______|    \/

____________________________________________________________
Hello! I'm EV.
What can I do for you?
____________________________________________________________
...
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

These three fixed parts are checked by the runner on every case, so the **Expected output**
block of each case below covers only the replies in between. This keeps the test cases short
and means a change to the greeting does not require editing all of them.

Every input block ends with `bye` so the session terminates.

## Test cases

### TC-01 Greet and exit

**Aim:** A session with no commands produces exactly the banner, greeting and farewell, and
nothing else.

**Input**

```text
bye
```

**Expected output**

```text
```

### TC-02 Add a todo

**Aim:** `todo` creates a task shown with the `[T]` type icon and an unticked status box, and
the running count is reported in the singular.

**Input**

```text
todo borrow book
bye
```

**Expected output**

```text
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
```

### TC-03 Add a deadline

**Aim:** `deadline ... /by ...` creates a `[D]` task and echoes the due time in the
`(by: ...)` suffix.

**Input**

```text
deadline return book /by Sunday
bye
```

**Expected output**

```text
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 1 task in the list.
____________________________________________________________
```

### TC-04 Add an event

**Aim:** `event ... /from ... /to ...` creates an `[E]` task and echoes both times in the
`(from: ... to: ...)` suffix.

**Input**

```text
event project meeting /from Mon 2pm /to 4pm
bye
```

**Expected output**

```text
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 1 task in the list.
____________________________________________________________
```

### TC-05 Date and time are free text

**Aim:** At this stage dates are stored verbatim, so a `/by` value that is not a date at all is
still accepted. Guards against a future date parser silently rejecting existing input.

**Input**

```text
deadline do homework /by no idea :-p
bye
```

**Expected output**

```text
____________________________________________________________
Got it. I've added this task:
  [D][ ] do homework (by: no idea :-p)
Now you have 1 task in the list.
____________________________________________________________
```

### TC-06 List an empty list

**Aim:** `list` on a fresh session reports that the list is empty rather than printing an empty
frame.

**Input**

```text
list
bye
```

**Expected output**

```text
____________________________________________________________
There is nothing in your list yet.
____________________________________________________________
```

### TC-07 List all three task types

**Aim:** `list` numbers tasks from 1 in insertion order and renders each subclass with its own
type icon and suffix.

**Input**

```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
list
bye
```

**Expected output**

```text
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: June 6th)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: June 6th)
3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
____________________________________________________________
```

### TC-08 Mark a task as done

**Aim:** `mark 2` ticks the second task only, and the change is visible in a later `list`.
Guards against off-by-one errors in the 1-based to 0-based index conversion.

**Input**

```text
todo read book
todo return book
mark 2
list
bye
```

**Expected output**

```text
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] return book
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] return book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[T][X] return book
____________________________________________________________
```

### TC-09 Unmark a task

**Aim:** `unmark` reverses `mark` and restores the empty status box.

**Input**

```text
todo read book
mark 1
unmark 1
list
bye
```

**Expected output**

```text
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] read book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
____________________________________________________________
```

### TC-10 Marking twice is harmless

**Aim:** Marking an already-done task leaves it done instead of toggling it back off.

**Input**

```text
todo read book
mark 1
mark 1
list
bye
```

**Expected output**

```text
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
____________________________________________________________
```

### TC-11 Unknown command

**Aim:** An unrecognised keyword is rejected with the offending word quoted and the list of
supported commands.

**Input**

```text
blah
bye
```

**Expected output**

```text
____________________________________________________________
I don't know what "blah" means.
I understand: todo, deadline, event, list, mark, unmark, delete, bye.
____________________________________________________________
```

### TC-12 Todo without a description

**Aim:** `todo` with no argument is rejected with an example of correct usage.

**Input**

```text
todo
bye
```

**Expected output**

```text
____________________________________________________________
A todo needs a description.
Try something like: todo borrow book
____________________________________________________________
```

### TC-13 Malformed deadline

**Aim:** The three ways a `deadline` can be incomplete each produce their own specific message
rather than one generic error: no `/by` at all, nothing before `/by`, nothing after `/by`.

**Input**

```text
deadline
deadline /by Sunday
deadline return book /by
bye
```

**Expected output**

```text
____________________________________________________________
A deadline needs a /by to say when it is due.
Try something like: deadline return book /by Sunday
____________________________________________________________
____________________________________________________________
A deadline needs a description before /by.
Try something like: deadline return book /by Sunday
____________________________________________________________
____________________________________________________________
A deadline needs a due time after /by.
Try something like: deadline return book /by Sunday
____________________________________________________________
```

### TC-14 Malformed event

**Aim:** Each way an `event` can be malformed produces its own message: no `/from`, no `/to`,
`/to` written before `/from`, and no description.

**Input**

```text
event
event meeting /from Mon
event meeting /to 4pm /from Mon 2pm
event /from Mon /to 4pm
bye
```

**Expected output**

```text
____________________________________________________________
An event needs a /from to say when it starts.
Try something like: event project meeting /from Mon 2pm /to 4pm
____________________________________________________________
____________________________________________________________
An event needs a /to to say when it ends.
Try something like: event project meeting /from Mon 2pm /to 4pm
____________________________________________________________
____________________________________________________________
Please put /from before /to.
Try something like: event project meeting /from Mon 2pm /to 4pm
____________________________________________________________
____________________________________________________________
An event needs a description before /from.
Try something like: event project meeting /from Mon 2pm /to 4pm
____________________________________________________________
```

### TC-15 Invalid task numbers for mark

**Aim:** Marking is rejected when the list is empty, when no number is given, when the argument
is not a number, and when the number is out of range. The out-of-range message states the valid
range.

**Input**

```text
mark 1
todo read book
mark
mark abc
mark 5
bye
```

**Expected output**

```text
____________________________________________________________
Your list is empty, so there is no task to update yet.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Please tell me which task number.
Try something like: mark 2
____________________________________________________________
____________________________________________________________
"abc" is not a task number.
Try something like: mark 2
____________________________________________________________
____________________________________________________________
There is no task 5 in your list.
You currently have 1 task, so please pick a number between 1 and 1.
____________________________________________________________
```

### TC-16 Invalid task numbers for unmark

**Aim:** `unmark` validates its argument the same way `mark` does, including rejecting task
number 0 (the list is 1-based).

**Input**

```text
todo read book
unmark abc
unmark 0
unmark 1
bye
```

**Expected output**

```text
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
"abc" is not a task number.
Try something like: mark 2
____________________________________________________________
____________________________________________________________
There is no task 0 in your list.
You currently have 1 task, so please pick a number between 1 and 1.
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] read book
____________________________________________________________
```

### TC-17 Failed commands do not corrupt the list

**Aim:** Interleave rejected commands with successful ones. The closing `list` must contain
exactly the two tasks that were added successfully, numbered 1 and 2, proving that a rejected
command left no half-built task behind and did not advance the task counter.

**Input**

```text
todo read book
blah
deadline oops
mark 9
deadline return book /by Sunday
todo
list
bye
```

**Expected output**

```text
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
I don't know what "blah" means.
I understand: todo, deadline, event, list, mark, unmark, delete, bye.
____________________________________________________________
____________________________________________________________
A deadline needs a /by to say when it is due.
Try something like: deadline return book /by Sunday
____________________________________________________________
____________________________________________________________
There is no task 9 in your list.
You currently have 1 task, so please pick a number between 1 and 1.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
A todo needs a description.
Try something like: todo borrow book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Sunday)
____________________________________________________________
```

### TC-18 Blank input lines are ignored

**Aim:** Pressing Enter on an empty line produces no reply and does not create an empty task.

**Input**

```text

todo read book


list
bye
```

**Expected output**

```text
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
____________________________________________________________
```

### TC-19 Descriptions that look like commands

**Aim:** Only the first word of a line is treated as the command keyword, so a description that
happens to be or start with a command word is stored as ordinary text.

**Input**

```text
todo list
todo unmark the thing
list
bye
```

**Expected output**

```text
____________________________________________________________
Got it. I've added this task:
  [T][ ] list
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] unmark the thing
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] list
2.[T][ ] unmark the thing
____________________________________________________________
```

### TC-20 Surrounding and repeated whitespace is trimmed

**Aim:** Leading, trailing and repeated spaces around the command and its argument are removed
before the task is stored.

**Input**

```text
   todo    borrow book   
list
bye
```

**Expected output**

```text
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
____________________________________________________________
```

### TC-21 Delete a task from the middle

**Aim:** `delete 3` removes the third task, reports it, and the remaining tasks close the gap so
that `list` numbers them 1 to 3 with no hole.

**Input**

```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
todo join sports club
delete 3
list
bye
```

**Expected output**

```text
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: June 6th)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] join sports club
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: June 6th)
3.[T][ ] join sports club
____________________________________________________________
```

### TC-22 Invalid task numbers for delete

**Aim:** `delete` validates its argument the same way `mark` and `unmark` do: empty list, no
number, non-numeric argument, and out of range.

**Input**

```text
delete 1
todo read book
delete
delete abc
delete 2
bye
```

**Expected output**

```text
____________________________________________________________
Your list is empty, so there is no task to update yet.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Please tell me which task number.
Try something like: mark 2
____________________________________________________________
____________________________________________________________
"abc" is not a task number.
Try something like: mark 2
____________________________________________________________
____________________________________________________________
There is no task 2 in your list.
You currently have 1 task, so please pick a number between 1 and 1.
____________________________________________________________
```

### TC-23 Deleting keeps the remaining tasks intact

**Aim:** The most dangerous part of delete is shifting the surviving tasks. Mark the last task,
delete the first, then check that (a) the marked task is still marked after moving position,
(b) `mark 2` now targets the shifted task rather than the old occupant of that slot, and (c) a
task added afterwards lands at the end instead of overwriting a survivor.

**Input**

```text
todo a
todo b
todo c
mark 3
delete 1
list
mark 2
list
todo d
list
bye
```

**Expected output**

```text
____________________________________________________________
Got it. I've added this task:
  [T][ ] a
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] b
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] c
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] c
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [T][ ] a
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] b
2.[T][X] c
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] c
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] b
2.[T][X] c
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] d
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] b
2.[T][X] c
3.[T][ ] d
____________________________________________________________
```

### TC-24 Delete the only task

**Aim:** Deleting the last remaining task reports a count of zero and leaves the list genuinely
empty, not holding a stale reference to the removed task.

**Input**

```text
todo only task
delete 1
list
bye
```

**Expected output**

```text
____________________________________________________________
Got it. I've added this task:
  [T][ ] only task
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [T][ ] only task
Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
There is nothing in your list yet.
____________________________________________________________
```

## Not covered

- The 100-task limit. Reaching it needs 100 setup commands, which would dominate the plan for a
  branch that is one comparison. Worth adding if the storage strategy changes.
- Interactive behaviour such as Ctrl+C, and terminal-specific rendering of the banner.
