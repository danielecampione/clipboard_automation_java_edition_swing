# Clipboard Automation Swing HiDPI

Versione Java 1.8 Swing con supporto HiDPI esplicito.

## Compilazione e avvio

Eseguire manualmente:

```bat
mkdir bin
javac -source 1.8 -target 1.8 -encoding UTF-8 -d bin src\*.java
java -Dsun.java2d.dpiaware=true -Dsun.java2d.uiScale.enabled=true -cp bin Main
```
