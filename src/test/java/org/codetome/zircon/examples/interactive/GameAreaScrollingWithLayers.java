package org.codetome.zircon.examples.interactive;

import org.codetome.zircon.api.Position;
import org.codetome.zircon.api.Size;
import org.codetome.zircon.api.Symbols;
import org.codetome.zircon.api.TextCharacter;
import org.codetome.zircon.api.beta.component.*;
import org.codetome.zircon.api.builder.*;
import org.codetome.zircon.api.color.ANSITextColor;
import org.codetome.zircon.api.color.TextColor;
import org.codetome.zircon.api.color.TextColorFactory;
import org.codetome.zircon.api.component.Button;
import org.codetome.zircon.api.component.Panel;
import org.codetome.zircon.api.component.builder.ButtonBuilder;
import org.codetome.zircon.api.component.builder.PanelBuilder;
import org.codetome.zircon.api.font.Font;
import org.codetome.zircon.api.graphics.Layer;
import org.codetome.zircon.api.graphics.TextImage;
import org.codetome.zircon.api.input.InputType;
import org.codetome.zircon.api.resource.CP437TilesetResource;
import org.codetome.zircon.api.resource.ColorThemeResource;
import org.codetome.zircon.api.screen.Screen;
import org.codetome.zircon.api.terminal.Terminal;
import org.codetome.zircon.internal.graphics.BoxType;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GameAreaScrollingWithLayers {

    private static final List<InputType> EXIT_CONDITIONS = new ArrayList<>();
    private static final int TERMINAL_WIDTH = 60;
    private static final int TERMINAL_HEIGHT = 30;
    private static final Size SIZE = Size.of(TERMINAL_WIDTH, TERMINAL_HEIGHT);
    private static final Font FONT = CP437TilesetResource.ROGUE_YUN_16X16.toFont();
    private static boolean headless = false;

    static {
        EXIT_CONDITIONS.add(InputType.Escape);
        EXIT_CONDITIONS.add(InputType.EOF);
    }

    @Test
    public void checkSetup() {
        main(new String[]{"test"});
    }

    public static void main(String[] args) {
        // for this example we only need a default terminal (no extra config)
        final Terminal terminal = TerminalBuilder.newBuilder()
                .font(FONT)
                .initialTerminalSize(SIZE)
                .buildTerminal(args.length > 0);
        if (args.length > 0) {
            headless = true;
        }
        final Screen screen = TerminalBuilder.createScreenFor(terminal);
        Size size = screen.getBoundableSize();
        screen.setCursorVisibility(false); // we don't want the cursor right now

        Panel actions = PanelBuilder.newBuilder()
                .size(screen.getBoundableSize().withColumns(20))
                .wrapWithBox()
                .title("Actions")
                .boxType(BoxType.TOP_BOTTOM_DOUBLE)
                .build();
        Button wait = ButtonBuilder.newBuilder()
                .text("Wait")
                .build();
        Button sleep = ButtonBuilder.newBuilder()
                .text("Sleep")
                .position(Position.DEFAULT_POSITION.withRelativeRow(1))
                .build();
        actions.addComponent(wait);
        actions.addComponent(sleep);
        screen.addComponent(actions);


        final Panel gamePanel = PanelBuilder.newBuilder()
                .size(screen.getBoundableSize().withColumns(40))
                .position(Position.DEFAULT_POSITION.relativeToRightOf(actions))
                .title("Game area")
                .wrapWithBox()
                .boxType(BoxType.TOP_BOTTOM_DOUBLE)
                .build();

        final Size3D visibleGameAreaSize = Size3D.from2DSize(gamePanel.getBoundableSize()
                .minus(Size.of(2, 2)), 5);
        final Size virtualGameAreaSize = Size.of(90, 90);


        final Map<Integer, List<TextImage>> levels = new HashMap<>();
        final int totalLevels = 10;
        for(int i = 0; i < totalLevels; i++) {
            levels.put(i, Collections.singletonList(TextImageBuilder.newBuilder()
                    .size(virtualGameAreaSize)
                    .build()));
        }

        final GameArea gameArea =
                new TextImageGameArea(Size3D.from2DSize(virtualGameAreaSize, totalLevels), levels);

        final GameComponent gameComponent = new GameComponent(
                gameArea,
                visibleGameAreaSize,
                CP437TilesetResource.PHOEBUS_16X16.toFont(),
                Position.DEFAULT_POSITION,
                ComponentStylesBuilder.DEFAULT);
        screen.addComponent(gamePanel);
        gamePanel.addComponent(gameComponent);

        final Position centerPos = Position.of(
                visibleGameAreaSize.getWidth() / 2 + gameComponent.getPosition().getColumn(),
                visibleGameAreaSize.getDepth() / 2 + gameComponent.getPosition().getRow());

        final Layer player = LayerBuilder.newBuilder()
                .offset(centerPos)
                .filler(TextCharacterBuilder.newBuilder()
                        .character('@')
                        .build())
                .build();

        screen.pushLayer(player);

        enableMovement(screen, gameComponent);
        generatePyramid(5, Position3D.of(8, 8, 5), gameArea);
        screen.applyColorTheme(ColorThemeResource.SOLARIZED_DARK_CYAN.getTheme());
        screen.display();
    }

    private static void generatePyramid(int height, Position3D startPos, GameArea gameArea) {
        TextCharacter wall = TextCharacterBuilder.newBuilder()
                .character('#')
                .build();
        AtomicInteger currLevel = new AtomicInteger(startPos.getZ());
        int currSize = 1;
        for (int i = currSize; i <= height; i++) {
            if (i == 1) {
                gameArea.setCharactersAt(startPos, Collections.singletonList(wall));
            } else {
                Position levelOffset = startPos.to2DPosition()
                        .withRelativeColumn(-i)
                        .withRelativeRow(-i);
                Size levelSize = Size.of(1 + i * 2, 1 + i * 2);
                levelSize.fetchPositions().forEach(position -> {
                    gameArea.setCharactersAt(
                            Position3D.from2DPosition((position.plus(levelOffset)), currLevel.get()),
                            Collections.singletonList(wall));
                });
            }
            currLevel.decrementAndGet();
        }
    }

    private static void enableMovement(final Screen screen, final GameComponent gameComponent) {
        screen.onInput((input) -> {
            if (EXIT_CONDITIONS.contains(input.getInputType()) && !headless) {
                System.exit(0);
            } else {
                if (InputType.ArrowUp == input.getInputType()) {
                    gameComponent.scrollOneBackward();
                }
                if (InputType.ArrowDown == input.getInputType()) {
                    gameComponent.scrollOneForward();
                }
                if (InputType.ArrowLeft == input.getInputType()) {
                    gameComponent.scrollOneLeft();
                }
                if (InputType.ArrowRight == input.getInputType()) {
                    gameComponent.scrollOneRight();
                }
                if (InputType.PageUp == input.getInputType()) {
                    gameComponent.scrollOneUp();
                }
                if (InputType.PageDown == input.getInputType()) {
                    gameComponent.scrollOneDown();
                }
                screen.refresh();
            }
        });
    }
}