import 'ball.dart';
import 'ball_green.dart';
import 'ball_pink.dart';

class Tile {
  Ball ball = Ball(0);

  bool battle(Ball immigrant, bool limited) {
    if ((immigrant is BallGreen && ball is BallGreen && limited) ||
        (immigrant is BallPink && ball is BallPink && limited)) {
      return false;
    }

    if (immigrant.size >= ball.size) {
      if (immigrant is BallGreen) {
        ball = BallGreen(ball.size + immigrant.size);
      } else {
        ball = BallPink(ball.size + immigrant.size);
      }
    } else {
      ball.size += immigrant.size;
    }
    return true;
  }
}
