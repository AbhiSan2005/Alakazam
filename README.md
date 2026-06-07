# Alakazam — Audio Recognition System

> A Shazam-inspired audio fingerprinting and recognition engine built in Java.  
> **S.Y. Object-Oriented Programming Mini Project**

---

## Overview

Alakazam is a Java-based audio recognition system that identifies songs or audio clips by generating and matching acoustic fingerprints — similar to how Shazam works. It demonstrates core OOP principles through a clean, layered architecture covering audio I/O, signal processing (FFT), fingerprinting strategies, and similarity-based matching.

---

## Features

- **Audio & Video media abstraction** via an inheritance hierarchy (`Media` → `Audio` / `Video`)
- **Multiple fingerprinting strategies** — Simple Amplitude and FFT-based analysis
- **In-memory media database** for storing and querying registered tracks
- **Similarity-based matching** to find the closest audio match
- **Custom exception handling** (`ProcessingException`, `MatchNotFoundException`)
- **Strategy and Interface patterns** throughout (`FingerprintStrategy`, `MatchStrategy`)

---

## Project Structure

```
src/main/java/com/project/
│
├── core/
│   ├── Fingerprint.java          # Fingerprint data model
│   └── MatchResult.java          # Encapsulates a match result
│
├── media/
│   ├── Media.java                # Abstract base class
│   ├── Audio.java                # Audio media type
│   └── Video.java                # Video media type
│
├── fingerprint/
│   ├── Fingerprintable.java           # Interface for fingerprintable media
│   ├── FingerprintStrategy.java       # Strategy interface
│   ├── SimpleAmplitudeStrategy.java   # Amplitude-based fingerprinting
│   └── FFTStrategy.java               # FFT-based fingerprinting
│
├── matching/
│   ├── Matcher.java                   # Core matching engine
│   ├── MatchStrategy.java             # Strategy interface for matching
│   └── SimilarityMatchStrategy.java   # Similarity scoring implementation
│
├── database/
│   └── MediaDatabase.java            # Stores and retrieves registered tracks
│
├── exceptions/
│   ├── ProcessingException.java      # Thrown on audio processing errors
│   └── MatchNotFoundException.java   # Thrown when no match is found
│
├── utils/
│   ├── AudioReader.java              # Reads audio input from files
│   └── FFTUtil.java                  # FFT computation utilities
│
└── Server.java                         # Entry point
```

---

## Tech Stack

| Technology        | Details                           |
| ----------------- | --------------------------------- |
| Language          | Java 15                           |
| Build Tool        | Maven                             |
| Signal Processing | JavaCV (`javacv-platform 1.5.10`) |

---

## Getting Started

### Prerequisites

- Java 15+
- Maven 3.6+

### Clone & Build

```bash
git clone https://github.com/AbhiSan2005/Alakazam.git
cd Alakazam
mvn clean install
```

### Run
1. Open Docker Desktop
Go to root directory ./Alakazam

2. Run this command
```bash
docker compose up -d
```
3. Go to Server.java in Backend folder 
Run the server.java file

4. If you want to reset the database
```bash
docker compose down -v
```

5. Then again run 
```bash
docker compose up -d
```

---

## OOP Concepts Demonstrated

| Concept                | Where Used                                                           |
| ---------------------- | -------------------------------------------------------------------- |
| **Inheritance**        | `Media` → `Audio`, `Video`                                           |
| **Abstraction**        | `Fingerprintable`, `FingerprintStrategy`, `MatchStrategy` interfaces |
| **Polymorphism**       | Swappable fingerprinting & matching strategies                       |
| **Encapsulation**      | `Fingerprint`, `MatchResult`, `MediaDatabase`                        |
| **Exception Handling** | Custom exceptions in the `exceptions` package                        |

---

## Contributors

- [@XennaSkywalker](https://github.com/XennaSkywalker) — Aditya Lampuse
- [@Abdurrahman-shaikh02](https://github.com/Abdurrahman-shaikh02) - Abdurrahman Shaikh
- [@Karan-30506](https://github.com/Karan-30506) - Karan Ahuja
- [@AbhiSan2005](https://github.com/AbhiSan2005) — Abhiraj Sankpal
