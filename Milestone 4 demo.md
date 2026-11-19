# Milestone 4 demo

Show create, read, update, and delete for Review in the running web app. Read is the new row in the table after you press Create. There is no separate Read button.

Extra Explanation If Needed:
The green status line in the screenshots is `${messages.success}`. The page prints it in `ReviewList.jsp`, 
`ReviewUpdate.jsp`, 
`ReviewDelete.jsp`, 
The servlet writes that text with `messages.put("success", ...)` in the matching Java file: `ReviewList.java` for create, `ReviewUpdate.java` for update, `ReviewDelete.java` for delete.

## Before class

1. Start MySQL. It must be on `localhost` port `3306`, database `NextQuest`.
2. In Eclipse, import `NextQuestApp` if it is not already in the workspace. Use File, Import, Existing Projects into Workspace, and choose the `NextQuestApp` folder inside Project 1.
3. Right-click `NextQuestApp`, choose Run As, then Run on Server, and pick Apache Tomcat v9.0. If that Tomcat is already running, add `NextQuestApp` to it. Do not start a second Tomcat on port 8080.
4. Open `http://localhost:8080/NextQuestApp/gamedetail`.

The app logs in as MySQL user `root` with a blank password. A 500 page whose log says `Access denied for user 'root'@'localhost'` means this MySQL server does not allow a blank password. Set the password in `ConnectionManager.java` to the password this machine uses, then run the app again. Do not commit that password if the team's other machines use a blank one.

These rows are already loaded:

| Use this | Value |
|---|---|
| User | `user_id` 1, username `demo` |
| Game | `app_id` 10, Counter-Strike |
| Second game | `app_id` 20, Team Fortress Classic |

Review start empty. A review needs a real `app_id` and a real `user_id`. A survey needs a real `user_id`. One user can review a given game only once.

## Review

1. On Game Detail, click Manage Reviews. The address is `http://localhost:8080/NextQuestApp/reviews`.
2. Create. Enter app_id `10`, user_id `1`, Recommended Yes, hours `5`, and today's date. Press Create. A green line says `Created review id=` followed by a number. The new row in the table is the read.
3. Update. Click Update on that row. Change hours to `9`. Press Update. A green line says `Updated review id=` and the same number.
4. Delete. Go back to Reviews, click Delete on that row, and press Delete. A green line says `Deleted review id=` and the same number. The row is gone from the list.

A second Create for app_id `10` and user_id `1` stays on the form and says `This user already reviewed this game.` Use app_id `20` for another review, or delete the first one. A bad id says `Game not found.` or `User not found.`
