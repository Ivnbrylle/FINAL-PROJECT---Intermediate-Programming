# BioByte — Real People, Real Stories

A modern Java desktop application built with **JavaFX 24** that lets you securely
record and browse a personal biography. User accounts, an experiences timeline,
achievements, challenges, and a personal profile — all wrapped in a dark, glass-
morphism UI inspired by contemporary product design.

> Internal package name is `lifetrack` (the project's original codename); the
> shipping product brand is **BioByte**.

## Features

- **Landing page** with hero gradient, animated-looking purple particle field,
  feature highlights, and "Get Started" CTA.
- **Authentication**
  - Sign up + sign in (passwords hashed with PBKDF2-HmacSHA256 + per-user salt)
  - Show/hide password toggle on every password field
  - Clear validation messages
  - Logout from the dashboard sidebar
- **Dashboard shell** with a left sidebar (Dashboard / Experiences / Achievements
  / Challenges / Profile), breadcrumb header, primary content area, and a right
  rail with two **functional** cards:
  - **Journey Reflection** — surfaces a random quote pulled from your own
    saved challenges (lessons + solutions) and achievements
  - **Keep Growing** — context-aware progress hints based on what sections you
    have/haven't populated yet
- **Experiences** — a vertical purple timeline with date column, rail with dot,
  and a content card per entry (icon, title, organization, description, tags,
  edit/delete menu).
- **Achievements** — responsive card grid with category color-coded headers
  (awards/honors/certifications/projects/other).
- **Challenges** — list cards with separate "How I handled it" and "Lesson
  learned" blocks.
- **Profile** — Background information form with circular profile-picture
  upload, gender dropdown, education tiers, family background, and notes.

## Tech stack

- **Java 17+** (built/tested against OpenJDK 24)
- **JavaFX 24.0.1** (bundled in `lib/javafx-sdk-24.0.1/`)
- **SQLite** via `sqlite-jdbc 3.46.0.0`
- **SLF4J** (no-op binding) to silence the JDBC logging warning
- No external build system — plain `javac` invoked from `compile.bat`

## Project layout

```
.
├── compile.bat                     Compile script (javac + module path)
├── run.bat                         Launch script
├── lib/
│   ├── javafx-sdk-24.0.1/          Bundled OpenJFX SDK
│   ├── sqlite-jdbc-3.46.0.0.jar
│   ├── slf4j-api-2.0.13.jar
│   └── slf4j-nop-2.0.13.jar
├── data/                           SQLite DB lives here at runtime
├── src/lifetrack/
│   ├── Main.java                   JavaFX Application entry point
│   ├── app/
│   │   ├── Router.java             Scene/view switching
│   │   └── Session.java            Current logged-in user
│   ├── auth/                       AuthService, PasswordUtil
│   ├── db/                         Database connection + schema bootstrapping
│   ├── model/                      User, Biography, Experience, Achievement, Challenge
│   ├── dao/                        SQL access objects
│   ├── ui/
│   │   ├── styles.css              The JavaFX stylesheet — colors, gradients, cards
│   │   ├── LandingView.java        Home page (image 4 in spec)
│   │   ├── LoginView.java          Welcome Back screen
│   │   ├── RegisterView.java       Create Your Account screen
│   │   ├── DashboardView.java      Sidebar shell + right rail
│   │   ├── component/              BrandPanel, FeatureCard reusable pieces
│   │   └── section/                Home/Experiences/Achievements/Challenges/Profile
│   └── util/                       FxIcons (SVG paths), FxUtil (dialogs)
└── build/                          Compiled .class output (created by compile.bat)
```

## Requirements

- **Java 17 or newer** (text blocks + pattern-matching `instanceof` are used).
- Windows for the `.bat` scripts. macOS/Linux equivalents below.
- ~80 MB on disk for the JavaFX SDK after extraction.

## How to run

In a terminal opened at the project root:

```
compile.bat
run.bat
```

In **PowerShell** specifically you need the `.\` prefix:

```
.\compile.bat
.\run.bat
```

On first run the database file is created at `data\lifetrack.db`. Click
**Get Started** on the landing page, register a username/password, then sign in.

## Mac/Linux

```bash
FX=lib/javafx-sdk-24.0.1/lib
CP="build:lib/sqlite-jdbc-3.46.0.0.jar:lib/slf4j-api-2.0.13.jar:lib/slf4j-nop-2.0.13.jar"
mkdir -p build/lifetrack/ui
cp src/lifetrack/ui/styles.css build/lifetrack/ui/
javac --module-path "$FX" --add-modules javafx.controls,javafx.graphics \
      -d build -cp "lib/sqlite-jdbc-3.46.0.0.jar:lib/slf4j-api-2.0.13.jar:lib/slf4j-nop-2.0.13.jar" \
      $(find src -name "*.java")
java  --module-path "$FX" --add-modules javafx.controls,javafx.graphics \
      -cp "$CP" lifetrack.Main
```

Replace the `javafx-sdk-24.0.1` directory with the macOS or Linux build of OpenJFX
(downloadable from https://openjfx.io/ — only the `lib/` folder is needed).

## Security notes

- Passwords are never stored in plaintext. PBKDF2-HmacSHA256 with 120 000
  iterations and a per-user 16-byte salt — see `src/lifetrack/auth/PasswordUtil.java`.
- SQL access uses `PreparedStatement` parameters throughout — no string
  concatenation, no SQL injection surface.
- Foreign keys are enforced (`PRAGMA foreign_keys = ON`). Deleting a user
  cascades through all of their data.

## Rubric coverage

| Rubric item | Where it's implemented |
|---|---|
| Login validation | `AuthService.login`, `LoginView.doLogin` |
| Input validation (empty fields) | `LoginView`, `RegisterView`, `ProfileSection.doSave` |
| Password masking / show-hide | `LoginView` & `RegisterView` "SHOW" toggles |
| Clear / reset | "Reset form" button in `ProfileSection`; per-section clear |
| Logout | `DashboardView.doLogout` (kebab menu in sidebar chip) |
| Personalize | Profile picture upload (`ProfileSection.pickImage`) |
| Background information form | `ProfileSection` |
| File chooser for image | `ProfileSection.pickImage` (uses `FileChooser`) |
| Experiences section (multi-entry) | `ExperiencesSection` (timeline) |
| Achievements section | `AchievementsSection` (card grid) |
| Challenges section | `ChallengesSection` (handled/lesson blocks) |
| Tabs / organized layout | Sidebar nav in `DashboardView` |
| Save & edit & reset | Each section has Add/Edit/Delete dialogs |
| Summary view | `HomeSection` dashboard stats + `QuoteCard` + `GrowthCard` |
| Database storage | SQLite via `Database` + DAOs |
| Validation & error handling | Required-field checks, date regex, dialog-styled `Alert`s |
| Password masking | `PasswordField` everywhere (with toggle to reveal) |

## What I couldn't verify

- I built and compiled the app, then launched it for a 5-second smoke run
  (process started cleanly, no stderr, no startup exception). Visual
  confirmation of layout, gradients, hover states, animations, etc. requires
  you to run `run.bat` and click through.
- The Google sign-in button on the auth screens shows an info dialog
  explaining it isn't wired up — implementing OAuth is out of scope.
