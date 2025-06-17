import 'package:flutter/material.dart';
import '../../../../core/controllers/game_controller.dart';
import '../../../../core/models/ball.dart';
import '../../../../core/models/tile.dart' as game_model_tile; // Alias to avoid conflict with Flutter's Tile widget

// TODO: Import settings provider to get bouncing ball preference

class GameBoardWidget extends StatelessWidget {
  final GameController gameController;
  final Function(int row, int col) onTileTap;

  const GameBoardWidget({
    super.key,
    required this.gameController,
    required this.onTileTap,
  });

  @override
  Widget build(BuildContext context) {
    // Access settings if needed, e.g., for bounce animation toggle
    // final settings = ref.watch(settingsProvider); // If this becomes a ConsumerWidget

    return LayoutBuilder(
      builder: (context, constraints) {
        // Calculate tile size based on available space
        // Assuming a 10x6 board (width x height from GameController constants)
        double tileWidth = constraints.maxWidth / GameController.boardWidth;
        double tileHeight = constraints.maxHeight / GameController.boardHeight;
        // To make tiles square-ish, can use min(tileWidth, tileHeight) for both,
        // but that might not fill the space. For now, let them be rectangular.

        return GridView.builder(
          physics: const NeverScrollableScrollPhysics(), // Board itself shouldn't scroll
          itemCount: GameController.boardWidth * GameController.boardHeight,
          gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: GameController.boardWidth,
            // childAspectRatio will be determined by tileWidth/tileHeight implicitly if not set
            // If tiles are forced square: childAspectRatio: 1.0, and adjust GridView size.
          ),
          itemBuilder: (context, index) {
            int row = index ~/ GameController.boardWidth;
            int col = index % GameController.boardWidth;
            game_model_tile.Tile currentTile = gameController.tiles[row][col];
            Ball? ball = currentTile.ball;

            // Determine if this tile is selected (from GameController's internal state)
            bool isSelected = gameController.clicks == 1 &&
                              gameController.initialRow == row &&
                              gameController.initialCol == col;

            return GestureDetector(
              onTap: () => onTileTap(row, col),
              child: Container(
                width: tileWidth,
                height: tileHeight,
                decoration: BoxDecoration(
                  border: Border.all(color: Colors.grey.shade300, width: 0.5),
                  color: isSelected ? Colors.yellow.withOpacity(0.3) : Colors.transparent,
                ),
                child: _buildBallWidget(ball, tileWidth, tileHeight, isSelected /*, settings.bouncingBalls*/),
              ),
            );
          },
        );
      },
    );
  }

  Widget _buildBallWidget(Ball? ball, double tileWidth, double tileHeight, bool isSelected /*, bool enableBounce*/) {
    if (ball == null || ball.size == 0) {
      return Container(); // Empty tile
    }

    // Determine ball color
    Color ballColor = (ball is BallGreen) ? Colors.green.shade600 : Colors.pink.shade500;

    // Basic representation of ball size - can be improved
    // Max ball size could be e.g. 100, min size 1.
    // Scale the visual size of the ball based on its 'size' property.
    // This is a simple linear scaling, might need adjustment.
    double maxVisualSize = tileWidth * 0.8; // Max diameter of ball visually
    double minVisualSize = tileWidth * 0.2;
    double visualSize = minVisualSize + (ball.size / 100.0) * (maxVisualSize - minVisualSize);
    visualSize = visualSize.clamp(minVisualSize, maxVisualSize);


    // TODO: Implement bouncing animation if 'enableBounce' and 'isSelected'
    // For now, just a static circle.
    Widget ballWidget = Container(
      width: visualSize,
      height: visualSize,
      decoration: BoxDecoration(
        color: ballColor,
        shape: BoxShape.circle,
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.3),
            blurRadius: 3,
            offset: const Offset(1,1),
          )
        ]
      ),
      // Display ball size as text
      child: Center(
        child: Text(
          ball.size.toString(),
          style: TextStyle(
            color: Colors.white,
            fontWeight: FontWeight.bold,
            fontSize: visualSize * 0.4, // Adjust font size based on ball size
          ),
        ),
      ),
    );

    return Center(child: ballWidget);
  }
}
