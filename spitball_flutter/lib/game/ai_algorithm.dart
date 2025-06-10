import 'dart:math';

import 'ball_green.dart';
import 'ball_pink.dart';
import 'game_manager.dart';
import 'tile.dart';

class AIAlgorithm {
  static final _rand = Random();

  static List<int> randomMove(List<List<Tile>> tiles) {
    final coords = _randomBall(tiles);
    int y = coords[0];
    int x = coords[1];

    int dy = _rand.nextBool() ? 1 : -1;
    int dx = _rand.nextBool() ? 1 : -1;

    int newY = y + dy;
    int newX = x + dx;

    if (newY >= 0 && newY < GameManager.height && newX >= 0 && newX < GameManager.width) {
      return [y, x, newY, newX, 0];
    } else {
      return randomMove(tiles);
    }
  }

  static List<int> _randomBall(List<List<Tile>> tiles) {
    final positions = <List<int>>[];
    for (var i = 0; i < GameManager.height; i++) {
      for (var j = 0; j < GameManager.width; j++) {
        if (tiles[i][j].ball is BallPink) {
          positions.add([i, j]);
        }
      }
    }
    return positions[_rand.nextInt(positions.length)];
  }
}
