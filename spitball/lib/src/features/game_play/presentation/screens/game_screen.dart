import 'dart:async'; // Added for Timer

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart'; // For potential providers
import '../../../../core/controllers/game_controller.dart';
import '../widgets/game_board_widget.dart';
// TODO: Import finish_game_screen.dart

// Provider for GameController - this assumes GameController is passed to GameScreen
// or created here. For now, let's assume it's passed via constructor.
// If created here, it would need parameters like difficulty, onlineGameId etc.
// final gameControllerProvider = Provider<GameController>((ref) {
//   throw UnimplementedError('GameController must be provided to GameScreen');
// });

class GameScreen extends ConsumerStatefulWidget {
  final GameController gameController;

  const GameScreen({required this.gameController, super.key});

  @override
  ConsumerState<GameScreen> createState() => _GameScreenState();
}

class _GameScreenState extends ConsumerState<GameScreen> {
  late GameController _controller;
  Timer? _timer;

  @override
  void initState() {
    super.initState();
    _controller = widget.gameController;
    // Start a timer to periodically check for game updates and rebuild UI
    // This is similar to the refresh mechanism in the original Android GameActivity
    _timer = Timer.periodic(const Duration(milliseconds: 200), (timer) {
      if (_controller.isGameOver || !mounted) {
        timer.cancel();
        if (mounted && _controller.isGameOver) _showGameOverDialog();
        return;
      }
      // Check if any move occurred that requires a UI update
      if (_controller.anyMoveOccurred) {
        if (mounted) setState(() {});
      }
      // For online games, GameController's internal polling handles opponent moves.
      // We just need to refresh UI if state changes.
      // If GameController itself becomes a Notifier, this can be more reactive.
    });
  }

  @override
  void dispose() {
    _timer?.cancel();
    _controller.dispose(); // Dispose the controller to cancel its internal timers
    super.dispose();
  }

  void _showGameOverDialog() {
    // Ensure dialog is not shown if context is no longer valid
    if (!mounted) return;

    showDialog(
      context: context,
      barrierDismissible: false, // User must tap button!
      builder: (BuildContext context) {
        return AlertDialog(
          title: const Text('Game Over'),
          content: Text(
              'Winner: \${_controller.pinkBallCount == 0 ? "Green" : "Pink"}! \nGreen Balls: \${_controller.greenBallCount}\nPink Balls: \${_controller.pinkBallCount}'),
          actions: <Widget>[
            TextButton(
              child: const Text('Back to Menu'),
              onPressed: () {
                Navigator.of(context).popUntil((route) => route.isFirst); // Pop back to main menu
              },
            ),
            // TODO: Add "Play Again" option if desired
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    // Example of reading settings - though GameController should ideally get this if needed
    // final settings = ref.watch(settingsProvider);

    return Scaffold(
      appBar: AppBar(
        title: Text(_controller.isOnlineGame ? 'Online Game' : 'Local Game (vs AI)'),
        actions: [
          // Button to manually leave an online game
          if (_controller.isOnlineGame)
            IconButton(
              icon: const Icon(Icons.exit_to_app),
              tooltip: 'Leave Game',
              onPressed: () {
                _controller.setFinishOnlineGame(); // Signal controller to end game
                // Optionally show a confirmation dialog first
              },
            ),
        ],
      ),
      body: Column(
        children: <Widget>[
          // Top bar for scores or turn info
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: <Widget>[
                Text('Green: \${_controller.greenBallCount}', style: const TextStyle(fontSize: 18, color: Colors.green, fontWeight: FontWeight.bold)),
                Text(
                  _controller.isGameOver
                    ? 'GAME OVER'
                    : (_controller.currentPlayerTurn == 0 ? "Green's Turn" : "Pink's Turn"),
                  style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold)
                ),
                Text('Pink: \${_controller.pinkBallCount}', style: const TextStyle(fontSize: 18, color: Colors.pink, fontWeight: FontWeight.bold)),
              ],
            ),
          ),
          if (_controller.isOnlineGame && !_controller.isCurrentPlayersTurnOnline && !_controller.isGameOver)
            const Padding(
              padding: EdgeInsets.symmetric(vertical: 4.0),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  SizedBox(width: 16, height: 16, child: CircularProgressIndicator(strokeWidth: 2.0,)),
                  SizedBox(width: 8),
                  Text("Waiting for opponent..."),
                ],
              ),
            ),

          // The Game Board
          Expanded(
            child: GameBoardWidget(
              gameController: _controller,
              onTileTap: (row, col) {
                if (!_controller.isGameOver) {
                  bool success = _controller.handleTap(row, col);
                  // UI might update based on _controller.anyMoveOccurred in the timer
                  // or if handleTap directly triggers a state change via a notifier pattern later.
                  if (success || _controller.anyMoveOccurred) { // anyMoveOccurred also handles deselection feedback
                     if (mounted) setState(() {}); // Refresh UI after tap handling
                  }
                }
              },
            ),
          ),
          // TODO: Add controls or status messages at the bottom if needed
        ],
      ),
    );
  }
}
