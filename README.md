# Experiment 8 - Android Menus & WebView Application

**Owner & Author:** joylynprincita

This repository contains the complete Android project for **Experiment 8**, which demonstrates the implementation of **3 distinct types of Android Menus** (Options Menu, Context Menu, and Popup Menu) along with an interactive **WebView** component.

---

## 📌 Features Overview

### 1. Options Menu (Toolbar Menu)
- **Inflation**: Defined in `res/menu/menu_options.xml` and attached to the Action Bar / Toolbar in `MainActivity.kt`.
- **Menu Items**:
  - 🏠 **Home**: Displays a Toast message indicating selection.
  - 🌐 **Web**: Triggers the embedded WebView to display web content (`https://example.com/`).
  - ℹ️ **About**: Shows project and experiment information.

### 2. Context Menu (Long-Press Menu)
- **Inflation**: Defined in `res/menu/menu_context.xml` and registered on the target TextView (`txtContext`).
- **Trigger**: Activated by performing a **long-press** on the interactive TextView.
- **Menu Items**:
  - ✏️ **Edit**: Contextual edit action with feedback.
  - 📋 **Copy**: Contextual copy action.
  - 🗑️ **Delete**: Contextual delete action.

### 3. Popup Menu (Button Anchored Menu)
- **Inflation**: Defined in `res/menu/menu_popup.xml`.
- **Trigger**: Dynamically anchored to the "Show Popup Menu" button (`btnPopup`) upon a click event.
- **Menu Items**:
  - 👤 **Profile**: Displays profile selection feedback.
  - ⚙️ **Settings**: Opens settings placeholder action.
  - ❓ **Help**: Displays help option response.

---

## 🌐 WebView Integration

- **Trigger**: Clicking the "Open Web View" button (`btnWebView`) or selecting **Web** from the Options Menu.
- **Configuration**:
  - JavaScript Enabled (`javaScriptEnabled = true`)
  - DOM Storage Enabled (`domStorageEnabled = true`)
  - `WebViewClient` configured to keep browsing inline within the app.

---

## 🛠️ Project Structure

```text
menu_exp8/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/menu_exp8/
│   │       │   └── MainActivity.kt
│   │       └── res/
│   │           ├── layout/
│   │           │   └── activity_main.xml
│   │           └── menu/
│   │               ├── menu_options.xml
│   │               ├── menu_context.xml
│   │               └── menu_popup.xml
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── README.md
```

---

## 🚀 How to Run

1. Open the project folder in **Android Studio**.
2. Sync Project with Gradle Files.
3. Connect an Android Emulator or a physical device with USB Debugging enabled.
4. Click **Run 'app'** (`Shift + F10`).

---

**Author & Sole Maintainer**: joylynprincita  
