import 'dart:ui';

import 'package:flame/components.dart';
import 'package:flame/events.dart';
import 'package:flame/game.dart';
import 'package:flutter/material.dart';

import 'game_manager.dart';

class SpitBallGame extends FlameGame with TapDetector, PanDetector {
  const SpitBallGame();

  final GameManager _manager = GameManager();

  @override
  Future<void> onLoad() async {
    camera.viewport = FixedResolutionViewport(Vector2(800, 480));
  }

  @override
  void render(Canvas canvas) {
    super.render(canvas);
    _manager.render(canvas, size);
  }

  @override
  void onTapUp(TapUpInfo info) {
    _manager.handleTap(info.eventPosition.game);
  }

  @override
  void onPanEnd(DragEndInfo info) {
    _manager.endDrag();
  }

  @override
  void onPanStart(DragStartInfo info) {
    _manager.startDrag(info.eventPosition.game);
  }

  @override
  void onPanUpdate(DragUpdateInfo info) {
    _manager.updateDrag(info.eventPosition.game);
  }
}
