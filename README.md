# 🏋️‍♂️ Fitness Manager

A simple desktop app I wrote in Java to create custom workout plans. I keep a library of exercises, bundle them into named training sessions (with set counts), and save/load everything to JSON. The UI is clean, fast, and simple to use.

---

## ✨ Features

- **Exercise Library**
  - Add exercises with: **name**, **target muscle**, **weight (lbs)**, **reps**.
  - Edit an exercise anytime.
  - Delete an exercise (with confirmation). Deleting also removes it from all sessions.

- **Training Sessions**
  - Create named sessions (e.g., *Arms*, *Push Day*).
  - Add exercises to a session with a **sets** count.
  - From the session page:
    - **+ Add Exercise** (header)
    - **✖ Remove Exercise** (header) — choose which one to remove
    - Per-row **Edit Exercise** (updates the library entry)

- **Persistence**
  - Save the whole program to `./data/fitness_manager.json`
  - Load it back with one click
  - “Programs Saved” metric increments each time I save

- **Activity & Metrics**
  - Recent Activity feed logs actions like create/edit/add/remove/save/load
  - Dashboard counters: **Total Exercises**, **Training Sessions**, **Programs Saved**

---

## 🖥️ UI tour

- **Dashboard (Home)**
  - Left card: **Exercises** (live list)
  - Right card: **Training Sessions** (live list)
  - Bottom: **Recent Activity** and three counters
  - Clicking any row opens its detail page

- **Top Bar**
  - Click the title to return Home
  - **💾 Save Program** / **📂 Load Program**

- **Sidebar**
  - **Exercise Manager**: Create, Edit, Add to Session  
  - **Session Manager**: Create, Clear, View  
  - **Save/Load & Quit**

- **Exercise Detail**
  - Shows the full definition
  - **✏️ Edit Exercise** and **✖ Delete Exercise** (deletes everywhere)

- **Session Detail**
  - Header: **Session — {name}** + **+ Add Exercise** + **✖ Remove Exercise**
  - Body: rows like  
    `Dips — Triceps | 45 lbs | 20 reps | 2 sets`
  - Each row has an **Edit Exercise** shortcut

---

## 🚀 How I use it

1. **Create exercises** → Sidebar → *Create Exercise* → fill in → OK  
2. **Build a session** → Sidebar → *Create Training Session* → name it → add exercises with sets  
3. **Remove an exercise** → Open session → **✖ Remove Exercise** → pick exercise  
4. **Edit/Delete an exercise** → Open exercise detail → Edit or Delete  
5. **Save/Load** → Top bar or sidebar buttons  

---

## 🧱 Data model

- **Exercise**
  - `name`, `targetMuscle`, `weight`, `reps`
- **TrainingSession**
  - `name`
  - `exerciseSets`: `Exercise → sets`
- **FitnessManager**
  - Holds master lists
  - Creates/edits/deletes
  - Add/remove within sessions
- **Persistence**
  - `JsonWriter` / `JsonReader`
- **Events**
  - `Event`/`EventLog` trace actions (printed on quit)

---

## 💾 JSON format

```json
{
  "exercises": [
    {"name":"Curls","targetMuscle":"Biceps","weight":50,"reps":8}
  ],
  "sessions": [
    {
      "name": "Arms",
      "exerciseSets": [
        {"name":"Curls","targetMuscle":"Biceps","weight":50,"reps":8,"sets":3}
      ]
    }
  ]
}
```
File saved at: `./data/fitness_manager.json`

---

## ⚙️ Getting Started

### Requirements
- Java 17+ (Java 11 may also work)  
- [`org.json`](https://stleary.github.io/JSON-java/) dependency on classpath  

### Run from an IDE
1. Import the project  
2. Add `org.json` dependency  
3. Run `ui.FitnessManagerAppGUINew`

### Run from Command Line

**Windows (PowerShell):**
```powershell
javac -cp "lib\json-20231013.jar;src" -d out (Get-ChildItem -Recurse -Filter *.java | % {$_.FullName})
java -cp "out;lib\json-20231013.jar" ui.FitnessManagerAppGUINew
```

**Linux/macOS:**
```
find src -name "*.java" > sources.txt
javac -cp "lib/json-20231013.jar:src" -d out @sources.txt
java -cp "out:lib/json-20231013.jar" ui.FitnessManagerAppGUINew
```

---

## 📂 Project Structure
```
src/
  main/
    model/
      Exercise.java
      TrainingSession.java
      FitnessManager.java
      Event.java
      EventLog.java
    persistence/
      JsonReader.java
      JsonWriter.java
      Writable.java
    ui/
      FitnessManagerAppGUINew.java
data/
  fitness_manager.json   # created after first save
```

---

## 🔧 Tips

Compact UI → tweak COMPACT_ROW_HEIGHT or BTN_INSETS if buttons look too squished

Quick Home → click the app title or bicep icons in the top bar

Event Log → printed to console on quit

---

## 🛠️ Troubleshooting

- Nothing saves → make sure data/ folder exists

- Load fails → check JSON file isn’t corrupted

- Squished buttons → increase COMPACT_ROW_HEIGHT

- Delete doesn’t work → open exercise detail and delete from there

---

## 📌 Roadmap

- Track individual sets (weights/reps per set)

- Add rest timers

- Search/filter in lists

- Export sessions to CSV

---

## 📜 License

- Personal project — feel free to learn from or fork. If you use this, a note of credit is appreciated.

## 🙏 Credits

- Java Swing for the UI

- org.json library for JSON handling

