# 🗂️ FileTool — Command Line File Utility (Java)
**CYB345 – Operating Systems Project | Taibah University**

A Java command-line utility for performing essential file system operations including listing, copying, moving, deleting, and renaming files. Built using the modern `java.nio.file` API.

---

## 📋 Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Getting Started](#getting-started)
- [Commands](#commands)
  - [help](#-help)
  - [list](#-list)
  - [copy](#-copy)
  - [move](#-move)
  - [delete](#-delete)
  - [rename](#-rename)
- [Error Reference](#error-reference)
- [Team](#team)

---

## 📖 Overview

FileTool is a command-line application that allows users to manage files through terminal commands. It demonstrates secure input handling, path validation, and proper exception management using Java's NIO API. The tool does **not** run interactively — the desired operation must be passed as arguments when starting the program.

The scope is limited to basic file-level operations. It does not modify system permissions, handle recursive directory structures, or operate with elevated privileges.

---

## ✨ Features

- 📁 **List** — View all files and folders inside a directory
- 📋 **Copy** — Copy a file or all files from a directory to a destination
- 🚚 **Move** — Move a file to a different directory
- 🗑️ **Delete** — Remove a file or an empty directory
- ✏️ **Rename** — Rename a file within the same directory
- 🛡️ **Safe operations** — Confirms before overwriting, validates all inputs
- 🎨 **Colored banner** — Displayed on launch with no arguments

---

## 🚀 Getting Started

### Prerequisites

- Java **JDK 8+**
- Any terminal (Command Prompt, PowerShell, Bash)

### Compile

```bash
javac FileTool.java
```

### Run

```bash
java FileTool <command> <arguments>
```

If launched with **no arguments**, the tool displays its banner and available commands:

```bash
java FileTool
```

---

## 📟 Commands

### 🆘 help

Displays all available commands and their usage.

```bash
java FileTool help
```

No arguments required. Output example:
```
Available Commands:
help                                 - Show this help message
list "<directory_path>"              - List all files and folders in the given directory
copy "<source>" "<destination>"      - Copy a file from source path to destination path
move "<source>" "<destination>"      - Move a file from source path to destination path
delete "<path>"                      - Delete the specified file or directory
rename "<old_name>" "<new_name>"     - Rename the specified file or directory
```

---

### 📂 list

Lists all files and folders inside a given directory. Accepts both relative and absolute paths.

```bash
java FileTool list "<directory_path>"
```

**Example:**
```bash
java FileTool list "C:\Users\User\Documents"
```

**Possible outputs:**

| Situation | Output |
|-----------|--------|
| Valid directory with items | Prints each file/folder name on a new line |
| Empty directory | `ERROR: The directory is empty.` |
| Path is a file, not a folder | `ERROR: Given path isn't for a directory` |
| Directory does not exist | `ERROR: Folder doesn't exist` |
| No read permission | `ERROR: Permission denied.` |

---

### 📋 copy

Copies a file from the source path to the destination directory. The destination must already exist — it will not be created automatically.

```bash
java FileTool copy "<source>" "<destination>"
```

**Example — copy a file:**
```bash
java FileTool copy "C:\Users\User\Desktop\report.txt" "C:\Users\User\Documents\Backup\"
```
```
File copied successfully report.txt
```

**Example — copy all files from a directory:**
```bash
java FileTool copy "C:\Users\User\Desktop\MyFolder\" "C:\Users\User\Documents\Backup\"
```
```
Are you sure you want to copy all the files in the specified directory? (Yes/No)
```
> Subdirectories inside the source folder are **skipped**. Only files are copied.

**Name conflict — file already exists at destination:**
```
Another file exists with the same name as ...\report.txt.
Are you sure you want to replace it? (Yes/No)
```

**Error cases:**

| Situation | Output |
|-----------|--------|
| Source file does not exist | `ERROR: the target source path doesn't exist` |
| Destination is a file, not a folder | `ERROR: The destination path is not a directory` |
| Destination directory does not exist | `ERROR: The destination path does not exist` |
| Permission denied | `ERROR: Permission denied.` |

---

### 🚚 move

Moves a single file from the source path to a destination directory. Moving directories is **not** supported.

```bash
java FileTool move "<source>" "<destination>"
```

**Example:**
```bash
java FileTool move "C:\Users\User\Desktop\notes.txt" "C:\Users\User\Documents\"
```
```
File moved successfully notes.txt
```

**Error cases:**

| Situation | Output |
|-----------|--------|
| Source does not exist | `ERROR: the target source path doesn't exist` |
| Source is a directory | `ERROR: Moving directories is not supported` |
| Destination does not exist | `ERROR: The destination path does not exist` |
| Destination is a file | `ERROR: The destination path is not a directory` |
| Name conflict (with confirmation) | Prompts: `Are you sure you want to replace it? (Yes/No)` |

---

### 🗑️ delete

Deletes a file or an **empty** directory. Non-empty directories cannot be deleted.

```bash
java FileTool delete "<path>"
```

**Example:**
```bash
java FileTool delete "C:\Users\User\Desktop\old_file.txt"
```
```
File or directory deleted successfully.
```

**Error cases:**

| Situation | Output |
|-----------|--------|
| Empty or blank path | `Error: path cannot be empty.` |
| Invalid path format | `Error: invalid path.` |
| File or folder not found | `Error: file or directory not found` |
| Directory is not empty | `Error: directory is not empty` |
| Permission denied | `Error: you don't have permission to delete this file / directory` |
| General I/O failure | `Error: unable to delete file or directory` |

---

### ✏️ rename

Renames a file within the **same directory**. Only files can be renamed — directories are not supported.

```bash
java FileTool rename "<path>" "<new_name>"
```

**Example:**
```bash
java FileTool rename "C:\Users\User\Desktop\draft.txt" "final_report.txt"
```
```
File renamed successfully.
```

> The new name must **not** contain path separators (`/` or `\`) and must not be empty.

**Error cases:**

| Situation | Output |
|-----------|--------|
| Invalid path format | `Error: invalid path.` |
| File not found | `Error: file not found` |
| Source is a directory | `Error: cannot rename a directory` |
| New name is empty/blank | `Error: the new name cannot be empty.` |
| New name has illegal characters | `Error: invalid characters in the new file name.` |
| New name contains path separators | `Error: the new name must not contain path separators.` |
| A file with the new name already exists | `Error: a file with the new name already exists.` |
| Permission denied | `Error: you do not have permission to rename this file` |

---

## 🛠️ Error Reference

| Error | Meaning |
|-------|---------|
| `Error: invalid path.` | Path string has invalid characters or structure |
| `ERROR: Permission denied.` | Insufficient permissions for the operation |
| `ERROR: Folder doesn't exist` | The directory path does not exist |
| `ERROR: Moving directories is not supported` | Source path points to a directory |
| `Error: directory is not empty` | Cannot delete a non-empty folder |
| `Error: a file with the new name already exists.` | Name conflict during rename |

---

## 👥 Team

**Group F4 — Taibah University, Cybersecurity Department**
Supervised by: **Modhi AlSobeihy**

| Name                  | Contribution              |
|-----------------------|---------------------------|
| Lama Ali Alowaydhi    | Copy & Move commands      |
| Layan Faisal Alaboudi | Main method, Path validation, Banner |
| Shouq Faez Aljohani   | Rename & Delete commands  |
| Haneen Atiah Alharbi  | Help & List commands      |

---

> 📌 *First Semester 2025–2026 | CYB345 Operating Systems | Taibah University*
