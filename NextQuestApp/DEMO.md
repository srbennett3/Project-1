# NextQuestApp — Demo Instructions (M4)

Web app for the **NextQuest** schema (JDBC + JSP + Servlets, no framework).
This module covers the **`Genre`** and **`GameVideo`** tables (full CRUD), the **game detail page**, and the
advanced feature: **in-page HLS trailer playback** with `hls.js`.

Demo writeup with screenshots: [`demo/PM4_NextQuestApp_v4.pdf`](demo/PM4_NextQuestApp_v4.pdf)<br>
Raw screenshots: [`demo/screenshots/`](demo/screenshots/)

---

## 1. Prerequisites

| Item | Version used |
|---|---|
| JDK | 21 (`JavaSE-21`) |
| Server | Tomcat **9.0** (Servlet 4.0 — do **not** use Tomcat 10+, it is `jakarta.*`) |
| MySQL | 8.x (local) |
| IDE | Eclipse IDE for Enterprise Java Developers |

Internet access is needed while demoing: `GameDetail.jsp` loads `hls.js` from the jsDelivr CDN.

---

## 2. Load the database

From the **repository root** (not this folder):

```bash
mysql -u root -p < create-tables.sql
mysql -u root -p < load-data.sql
```

`create-tables.sql` creates the `NextQuest` schema and tables; `load-data.sql` loads the CSVs
(including `GameVideo` with the `video_url` HLS column). Verify:

```sql
USE NextQuest;
SELECT COUNT(*) FROM Genre;
SELECT COUNT(*) FROM GameVideo;
```

---

## 3. Configure the connection

Edit `src/main/java/nextquest/dal/ConnectionManager.java` if your MySQL differs from the defaults:

```java
private final String user     = "root";
private final String password = "";        // set your root password if you have one
private final String hostName = "localhost";
private final int    port     = 3306;
private final String schema   = "NextQuest";
```

---

## 4. Import and run

1. **Import**: `File > Import > General > Existing Projects into Workspace` → select the
   `NextQuestApp` folder inside your `Project-1` clone. It is already an Eclipse Dynamic Web Project
   (`jst.web` 4.0, JavaSE-21, Tomcat v9.0).
2. **Build path**: the three jars are committed in `src/main/webapp/WEB-INF/lib/`. If Eclipse does not
   pick them up automatically, right-click each jar → **Build Path → Add to Build Path**:
   - `mysql-connector-java-8.0.16.jar`
   - `taglibs-standard-impl-1.2.5.jar`
   - `taglibs-standard-spec-1.2.5.jar`
3. **Server**: right-click the project → `Run As > Run on Server` → **Tomcat v9.0**.
4. Open <http://localhost:8080/NextQuestApp/genres>

---

## 5. Demo walkthrough

The home page (<http://localhost:8080/NextQuestApp/>) has a **Demo shortcuts** section with one-click links to
the CS2 detail page, the CS2 trailer, and the "Add a game video for CS2" form.

### 5.1 `Genre` CRUD

| Step | URL | What to show |
|---|---|---|
| **Create** | `/genres` | Type `PM4 Demo Genre` in the name box → **Create**. Green line: **`Created genre id=32`** |
| **Read** | `/genres` | The **All Genres** list now contains row `id=32` `PM4 Demo Genre`, with Update / Delete links |
| **Update (load)** | `/genreupdate?genreId=32` | Form is pre-filled with the current name |
| **Update (submit)** | same | Rename to `PM4 Demo Genre Updated` → **Update**. Green line: **`Updated genre id=32`** |
| **Delete (confirm)** | `/genredelete?genreId=32` | Confirmation page shows the row to be deleted |
| **Delete (submit)** | same | **Delete**. Green line: **`Deleted genre id=32`**, and the row is gone from the list |

### 5.2 `GameVideo` CRUD

| Step | URL | What to show |
|---|---|---|
| **Create** | `/videocreate?appId=730` | Form fields: `appId` (pre-filled `730`), `Name`, `url`, `videoUrl`, `video_type` (trailer / gameplay / screenshot) → **Create**. Green line: **`Created video id=409599`** |
| **Read** | `/gamedetail?appId=730&videoId=409599` | Game detail page lists the videos for the game; the selected one (name `PM4 Demo Video`) is highlighted |
| **Update (load)** | `/videoupdate?videoId=409599` | Form pre-filled |
| **Update (submit)** | same | Rename to `PM4 Demo Video Updated` → **Update**. Green line: **`Updated video id=409599`** |
| **Delete (confirm)** | `/videodelete?videoId=409599` | Confirmation page |
| **Delete (submit)** | same | **Delete**. Green line: **`Deleted video id=409599`** |

`/videocreate`, `/videoupdate` and `/videodelete` also accept a `videoId` / `appId` query parameter so a
single screenshot can show the form and its result. The Game Detail page links to the create form with the
`appId` already filled in, and every GameVideo page links back to the detail page of the game it belongs to.

### 5.3 Advanced feature — trailer plays inside the app

Open <http://localhost:8080/NextQuestApp/gamedetail?appId=730&videoId=1>

`GameDetail.jsp` renders a `<video>` element and a small `hls.js` script block:

```js
var hls = new Hls();
hls.loadSource(videoSrc);   // GameVideo.video_url  (application/vnd.apple.mpegurl)
hls.attachMedia(video);
```

The **Launch Trailer** for *Counter-Strike 2* plays at `0:00 / 0:55` directly on the page — the user never
leaves the app to watch the trailer. Native HLS (`video.canPlayType('application/vnd.apple.mpegurl')`) is
used as a fallback where supported (Safari).

---

## 6. Screenshot index

All screenshots are 1280×900 browser captures of the running app.

| # | File | Covers | Expected on-screen text |
|---|---|---|---|
| 1 | `genre_create_result.png` | Genre — Create | `Created genre id=32` |
| 2 | `genre_list.png` | Genre — Read | All Genres list with row `id=32` |
| 3 | `genre_update_form.png` | Genre — Update (form) | name pre-filled `PM4 Demo Genre` |
| 4 | `genre_update_result.png` | Genre — Update (result) | `Updated genre id=32` |
| 5 | `genre_delete_form.png` | Genre — Delete (confirm) | delete confirmation for `id=32` |
| 6 | `genre_delete_result.png` | Genre — Delete (result) | `Deleted genre id=32` |
| 7 | `video_create_form.png` | GameVideo — Create (form) | appId / Name / url / videoUrl / video_type |
| 8 | `video_create_result.png` | GameVideo — Create (result) | `Created video id=409599` |
| 9 | `video_update_form.png` | GameVideo — Update (form) | name pre-filled `PM4 Demo Video` |
| 10 | `video_update_result.png` | GameVideo — Update (result) | `Updated video id=409599` |
| 11 | `video_delete_form.png` | GameVideo — Delete (confirm) | delete confirmation for `id=409599` |
| 12 | `video_delete_result.png` | GameVideo — Delete (result) | `Deleted video id=409599` |
| 13 | `hls_player_final.png` | Advanced feature | trailer playing in-page, `0:00 / 0:55` |

---

## 7. Troubleshooting

| Symptom | Cause / fix |
|---|---|
| `404` on every URL | Context path is not `NextQuestApp`, or the app was deployed to Tomcat 10+ (`jakarta` vs `javax`) |
| `No suitable driver` / `NullPointerException` in DAL | Connector/J not on the build path (see step 2) |
| `Access denied for user 'root'@'localhost'` | Set `password` in `ConnectionManager` |
| `Unknown database 'NextQuest'` | `create-tables.sql` / `load-data.sql` not run |
| Trailer area is blank, `hls.js` error in console | No internet (CDN blocked), or the row's `video_url` is not an `.m3u8` URL |
| `JSTL` tags printed as literal text | `taglibs-standard-impl` / `-spec` jars missing from the deployed `WEB-INF/lib` |

---

## 8. Optional: `Inserter` test runner

`src/main/java/nextquest/tools/Inserter.java` has a `main()` that walks the CRUD paths through the DAOs and
cleans up its own test rows — useful to confirm the DAL works without going through the browser:

```
Run As > Java Application (Inserter)
```
