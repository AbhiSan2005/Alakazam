# Shazam-Clone
S.Y. OOPS Mini Project
```
AudioRecognitionSystem/
│
├── src/
│   └── com/project/
│
│       ├── core/
│       │   ├── Fingerprint.java
│       │   ├── MatchResult.java
│
│       ├── media/
│       │   ├── Media.java
│       │   ├── Audio.java
│       │   ├── Video.java
│
│       ├── fingerprint/
│       │   ├── Fingerprintable.java
│       │   ├── FingerprintStrategy.java
│       │   ├── SimpleAmplitudeStrategy.java
│       │   ├── FFTStrategy.java
│
│       ├── matching/
│       │   ├── Matcher.java
│       │   ├── MatchStrategy.java
│       │   ├── SimilarityMatchStrategy.java
│
│       ├── database/
│       │   ├── MediaDatabase.java
│
│       ├── exceptions/
│       │   ├── ProcessingException.java
│       │   ├── MatchNotFoundException.java
│
│       ├── utils/
│       │   ├── AudioReader.java
│       │   ├── FFTUtil.java
│
│       └── Main.java
│
├── resources/
│   ├── audio_samples/
│
├── diagrams/
│   ├── class_diagram.drawio

```
│
├── README.md


# Updates to make

1) Fix tables -- Remove chunk_id, add movie descriptions
2) Exception Handling
3) UML diagram