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
