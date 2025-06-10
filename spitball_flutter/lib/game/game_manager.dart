import 'dart:ui';

import 'package:flutter/material.dart';

import 'ball.dart';
import 'ball_green.dart';
import 'ball_pink.dart';
import 'tile.dart';
import 'ball_type.dart';
import 'ai_algorithm.dart';

class GameManager {
  static const int width = 10;
  static const int height = 6;

  final List<List<Tile>> tiles =
      List.generate(height, (_) => List.generate(width, (_) => Tile()));

  int playerTurn = 0;
  int clicks = 0;
  int? initialX;
  int? initialY;
  bool gameOver = false;

  GameManager() {
    _initialize();
  }

  void _initialize() {
    tiles[1][3].ball = BallGreen(20);
    tiles[2][2].ball = BallGreen(20);
    tiles[3][3].ball = BallGreen(20);
    tiles[1][5].ball = BallPink(20);
    tiles[2][6].ball = BallPink(20);
    tiles[3][5].ball = BallPink(20);
  }

  void render(Canvas canvas, Size size) {
    final cellWidth = size.width / width;
    final cellHeight = size.height / height;
    final paint = Paint()..color = Colors.black;

    for (var y = 0; y < height; y++) {
      for (var x = 0; x < width; x++) {
        final rect = Rect.fromLTWH(
          x * cellWidth,
          y * cellHeight,
          cellWidth,
          cellHeight,
        );
        canvas.drawRect(rect, paint..style = PaintingStyle.stroke);
        final ball = tiles[y][x].ball;
        if (ball.size > 0) {
          final color = ball is BallGreen ? Colors.green : Colors.pink;
          final radius = (ball.size.toDouble() / 40) *
              (cellWidth < cellHeight ? cellWidth / 2 : cellHeight / 2);
          canvas.drawCircle(rect.center, radius, Paint()..color = color);
        }
      }
    }
  }

  void handleTap(Offset pos) {
    final c = _posToCoord(pos);
    if (c == null) return;
    _processTap(c[1], c[0]);
  }

  void startDrag(Offset pos) {
    final c = _posToCoord(pos);
    if (c != null) {
      _processTap(c[1], c[0]);
    }
  }

  void updateDrag(Offset pos) {}

  void endDrag() {
    clicks = 0;
  }

  List<int>? _posToCoord(Offset pos) {
    final cellWidth = gameSize!.width / width;
    final cellHeight = gameSize!.height / height;
    final x = (pos.dx / cellWidth).floor();
    final y = (pos.dy / cellHeight).floor();
    if (x >= 0 && x < width && y >= 0 && y < height) {
      return [y, x];
    }
    return null;
  }

  Size? gameSize;

  void move(int fromY, int fromX, int toY, int toX) {
    final moved = tiles[toY][toX].battle(tiles[fromY][fromX].ball, false);
    if (moved) {
      tiles[fromY][fromX].ball = Ball(0);
      playerTurn = (playerTurn + 1) % 2;
    }
  }

  void split(int fromY, int fromX, int toY, int toX) {
    final ball = tiles[fromY][fromX].ball;
    if (ball.size < 10) return;
    final splittedSize = ball.size ~/ 3;
    Ball splitted;
    if (ball is BallPink) {
      splitted = BallPink((splittedSize * 1.2).floor());
    } else {
      splitted = BallGreen((splittedSize * 1.2).floor());
    }
    ball.size -= splittedSize;
    tiles[toY][toX].battle(splitted, false);
    playerTurn = (playerTurn + 1) % 2;
  }

  void _processTap(int x, int y) {
    if (clicks == 0) {
      initialX = x;
      initialY = y;
      clicks = 1;
    } else if (clicks == 1) {
      if (initialX == x && initialY == y) {
        clicks = 0;
      } else if ((initialX! - x).abs() <= 1 && (initialY! - y).abs() <= 1) {
        move(initialY!, initialX!, y, x);
        clicks = 0;
      } else if ((initialX! - x).abs() == 2 && initialY == y ||
          (initialY! - y).abs() == 2 && initialX == x) {
        split(initialY!, initialX!, y, x);
        clicks = 0;
      } else {
        clicks = 0;
      }
    }
  }
}
