# SpitBall Flutter

This directory contains a simple Flutter/Flame implementation of the
original SpitBall game logic.  It uses `GameWidget` from the `flame`
package to render a board and handle user input.

The implementation mirrors the Java engine with Dart classes for
`GameManager`, `Tile`, and the various `Ball` types. Only a subset of
the original features is provided as a starting point for a full
migration.

To run the game you need Flutter installed:

```bash
flutter run
```

This is only a skeleton intended to demonstrate how the Android game
could be migrated to Flutter.
