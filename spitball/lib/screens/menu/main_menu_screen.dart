import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../common/controllers/game_controller.dart';
import '../../common/services/networking_service.dart';
import '../game/game_screen.dart';
import '../high_scores/high_scores_screen.dart';
import '../settings/settings_screen.dart';
import '../tutorial/how_to_play_screen.dart';     // Import NetworkingService


// Provider for NetworkingService - adjust if you have a different way to provide it
final networkingServiceProvider = Provider<NetworkingService>((ref) {
  // TODO: Replace "YOUR_PHP_SERVER_BASE_URL_HERE" with the actual URL in NetworkingService
  // or provide it through environment variables / another configuration mechanism.
  return NetworkingService();
});


class MainMenuScreen extends StatelessWidget {
  const MainMenuScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('SpitBall - Main Menu'),
        actions: [
          IconButton(
            icon: const Icon(Icons.settings),
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => const SettingsScreen()),
              );
            },
          ),
        ],
      ),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: <Widget>[
              ElevatedButton(
                style: ElevatedButton.styleFrom(padding: const EdgeInsets.symmetric(vertical: 16)),
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (context) => const ChooseGameTypeScreen()),
                  );
                },
                child: const Text('Play', style: TextStyle(fontSize: 18)),
              ),
              const SizedBox(height: 20),
              ElevatedButton(
                style: ElevatedButton.styleFrom(padding: const EdgeInsets.symmetric(vertical: 16)),
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (context) => const HowToPlayScreen()),
                  );
                },
                child: const Text('How to Play', style: TextStyle(fontSize: 18)),
              ),
              const SizedBox(height: 20),
              ElevatedButton(
                style: ElevatedButton.styleFrom(padding: const EdgeInsets.symmetric(vertical: 16)),
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (context) => const HighScoresScreen()),
                  );
                },
                child: const Text('High Scores', style: TextStyle(fontSize: 18)),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class ChooseGameTypeScreen extends ConsumerWidget { // Changed to ConsumerWidget
  const ChooseGameTypeScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) { // Added WidgetRef
    return Scaffold(
      appBar: AppBar(title: const Text('Choose Game Type')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            ElevatedButton(
              onPressed: () {
                 Navigator.push(
                    context,
                    MaterialPageRoute(builder: (context) => const ChooseDifficultyScreen()),
                  );
              },
              child: const Text('Play Local (vs Computer/Friend)'),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: () async { // Made async
                print('Online Game selected - initiating...');
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Creating online game... Please wait.')),
                );
                try {
                  final networkingService = ref.read(networkingServiceProvider);
                  // TODO: Provide actual player color selection if needed, default to 0 (Green)
                  // For now, this client will try to be player 0 (Green) or player 1 (Pink) based on what server assigns.
                  final gameData = await networkingService.createOrJoinOnlineGame();

                  final gameId = gameData['GAMEID'] as int;
                  // final numPlayers = gameData['NUMPLAYERS'] as int;
                  final playerColor = gameData['TURN'] as int? ?? 0; // Server assigns turn which is effectively player color

                  print('Online game created/joined. Game ID: \$gameId, Your Color Index: \$playerColor');

                  GameController onlineGameController = GameController(
                    gameId: gameId,
                    isAgainstAI: false, // It's an online game
                    onlinePlayerColor: playerColor,
                    networkingService: networkingService,
                  );
                  Navigator.pushReplacement(
                    context,
                    MaterialPageRoute(builder: (context) => GameScreen(gameController: onlineGameController)),
                  );
                } catch (e) {
                  print('Error creating online game: \$e');
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text('Error creating online game: \$e')),
                  );
                }
              },
              child: const Text('Play Online'),
            ),
          ],
        ),
      ),
    );
  }
}

class ChooseDifficultyScreen extends ConsumerWidget { // Changed to ConsumerWidget
  const ChooseDifficultyScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) { // Added WidgetRef
    return Scaffold(
      appBar: AppBar(title: const Text('Choose AI Difficulty')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            ElevatedButton(onPressed: () => _startGame(context, ref, 0), child: const Text('Easy')),
            ElevatedButton(onPressed: () => _startGame(context, ref, 1), child: const Text('Hard (No Chaser)')),
            ElevatedButton(onPressed: () => _startGame(context, ref, 2), child: const Text('Hard (Chaser)')),
            // TODO: Add local 2-player (pass and play) option
            // This would involve creating a GameController with isAgainstAI: false and gameId: 0
          ],
        ),
      ),
    );
  }

  void _startGame(BuildContext context, WidgetRef ref, int difficulty) {
    print('Starting game with AI difficulty: \$difficulty');

    final networkingService = ref.read(networkingServiceProvider);
    GameController aiGameController = GameController(
      isAgainstAI: true,
      difficulty: difficulty,
      networkingService: networkingService, // GameController expects it
      gameId: 0, // Local game
    );

    Navigator.pushReplacement( // Use pushReplacement to prevent going back to difficulty screen
      context,
      MaterialPageRoute(builder: (context) => GameScreen(gameController: aiGameController)),
    );
  }
}
