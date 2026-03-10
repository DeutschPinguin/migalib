package go.sinzchr.migalib.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import go.sinzchr.migalib.MigaLib;
import go.sinzchr.migalib.persistent.PointsPersistentState;
import go.sinzchr.migalib.point.Point;
import go.sinzchr.migalib.point.PointsContainer;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.Vec2ArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;


public final class PointsEditorCommand
{
        
        private PointsEditorCommand () {}
        
        
        private static final Map<UUID, Identifier> CONTAINER_SELECTED = new HashMap<>();
        
        
        public static @Nullable Identifier getSelectedContainer (@NotNull UUID player)
        {
                return CONTAINER_SELECTED.get(player);
        }
        
        
        public static void selectContainer (@NotNull UUID player, @NotNull Identifier id)
        {
                CONTAINER_SELECTED.put(player, id);
        }
        
        
        public static void unselectContainer (@NotNull UUID player)
        {
                CONTAINER_SELECTED.remove(player);
        }
        
        
        public static void unselectContainersForAllPlayers ()
        {
                CONTAINER_SELECTED.clear();
        }
        
        
        private static @NotNull PointsPersistentState state (@NotNull CommandContext<ServerCommandSource> ctx)
        {
                return PointsPersistentState.load(Objects.requireNonNull(
                        ctx.getSource().getServer().getWorld(World.OVERWORLD)
                ));
        }
        
        
        private static @NotNull MutableText errorNotFoundContainer (@NotNull Identifier container)
        {
                return Text.literal("Container ")
                        .append(Text.literal(String.format("[%s]", container)).formatted(Formatting.AQUA))
                        .append(" not found");
        }
        
        
        private static @NotNull MutableText errorEmptyContainer (@NotNull Identifier container)
        {
                return Text.literal("Container ")
                        .append(Text.literal(String.format("[%s]", container)).formatted(Formatting.AQUA))
                        .append(" is empty");
        }
        
        
        private static @NotNull MutableText errorNotFoundPoint (@NotNull Identifier container, @NotNull Identifier point)
        {
                return Text.literal("Container ")
                        .append(Text.literal(String.format("[%s]", container)).formatted(Formatting.AQUA))
                        .append(" does not contain ")
                        .append(Text.literal(String.format("[%s}", point)).formatted(Formatting.GREEN));
        }
        
        
        private static @NotNull MutableText errorAlreadyContainsPoint (@NotNull Identifier container, @NotNull Identifier point)
        {
                return Text.literal("Container ")
                        .append(Text.literal(String.format("[%s]", container)).formatted(Formatting.AQUA))
                        .append(" already contains ")
                        .append(Text.literal(String.format("[%s}", point)).formatted(Formatting.GREEN));
        }
        
        
        private static @NotNull MutableText textFor (@NotNull PointsContainer container)
        {
                return Text.empty()
                        .append(Text.literal("Container ").formatted(Formatting.WHITE))
                        .append(Text.literal(String.format("[%s]", container.id())).formatted(Formatting.AQUA));
        }
        
        
        private static @NotNull MutableText textWithDataFrom (@NotNull Point point)
        {
                return Text.empty()
                        .append(Text.literal(String.format("[%s]", point.world())).formatted(Formatting.GREEN))
                        .append(" ")
                        .append(Text.literal(String.format("(%f %f %f)", point.x(), point.y(), point.z())).formatted(Formatting.WHITE))
                        .append(" ")
                        .append(Text.literal(String.format("(pitch %f, yaw %f)", point.pitch(), point.yaw())).formatted(Formatting.WHITE));
        }
        
        
        private static @NotNull MutableText textWithDataFrom (@NotNull Point point, @NotNull PointsContainer container)
        {
                return Text.empty()
                        .append(textWithDataFrom(point))
                        .append(Text.literal("\n Contained in ").formatted(Formatting.GRAY))
                        .append(Text.literal(String.format("[%s", container.id())).formatted(Formatting.AQUA));
        }
        
        
        private static @NotNull Style styleFor (@NotNull Point point)
        {
                var cmd = String.format("/execute in %s run tp @s %f %f %f %f %f", point.world(), point.x(), point.y(), point.z(), point.yaw(), point.pitch());
                return Style.EMPTY
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, textWithDataFrom(point)))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmd));
        }
        
        
        private static @NotNull Style styleFor (@NotNull Point point, @NotNull PointsContainer container)
        {
                var cmd = String.format("/execute in %s run tp @s %f %f %f %f %f", point.world(), point.x(), point.y(), point.z(), point.yaw(), point.pitch());
                return Style.EMPTY
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, textWithDataFrom(point, container)))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmd));
        }
        
        
        private static @NotNull MutableText textFor (@NotNull Point point)
        {
                return Text.empty()
                        .append(Text.literal("Point ").formatted(Formatting.WHITE))
                        .append(Text.literal(String.format("[%s]", point.id())).formatted(Formatting.GREEN));
        }
        
        
        private static @NotNull MutableText styledTextFor (@NotNull Point point)
        {
                return textFor(point).fillStyle(styleFor(point));
        }
        
        
        private static @NotNull MutableText styledTextFor (@NotNull Point point, @NotNull PointsContainer container)
        {
                return textFor(point).fillStyle(styleFor(point, container));
        }
        
        
        private static int addPoint (
                @NotNull CommandContext<ServerCommandSource> ctx,
                @NotNull Identifier containerIdentifier,
                @NotNull Identifier pointIdentifier,
                @NotNull Vec3d position,
                @NotNull Vec2f rotation
        )
        {
                var source = ctx.getSource();
                var state = state(ctx);
                var container = state.get(containerIdentifier);
                
                if (container == null)
                {
                        source.sendError(errorNotFoundContainer(containerIdentifier));
                        return -1;
                }
                
                if (container.has(pointIdentifier))
                {
                        source.sendError(errorAlreadyContainsPoint(containerIdentifier, pointIdentifier));
                        return -2;
                }
                
                var world = source.getWorld().getRegistryKey().getValue();
                
                var point = new Point(pointIdentifier, world, position.x, position.y, position.z, rotation.y, rotation.x);
                container.add(point);
                
                source.sendFeedback(
                        () -> Text.literal("Added ").append(styledTextFor(point, container)),
                        true
                );
                
                state.markDirty();
                return 1;
        }
        
        
        private static int movePoint (
                @NotNull CommandContext<ServerCommandSource> ctx,
                @NotNull Identifier containerIdentifier,
                @NotNull Identifier pointIdentifier,
                @NotNull Vec3d position,
                @NotNull Vec2f rotation
        )
        {
                var source = ctx.getSource();
                var state = state(ctx);
                var container = state.get(containerIdentifier);
                
                if (container == null)
                {
                        source.sendError(errorNotFoundContainer(containerIdentifier));
                        return -1;
                }
                
                var point = container.get(pointIdentifier);
                
                if (point == null)
                {
                        source.sendError(errorNotFoundPoint(containerIdentifier, pointIdentifier));
                        return -2;
                }
                
                var world = source.getWorld().getRegistryKey().getValue();
                
                var mut = point.toMutablePoint();
                mut.world(world);
                mut.position(position);
                mut.rotation(rotation);
                container.set(mut.toPoint());
                
                source.sendFeedback(
                        () -> Text.literal("Moved ").append(styledTextFor(mut, container)),
                        true
                );
                
                state.markDirty();
                return 1;
        }
        
        
        private static int deletePoint (
                @NotNull CommandContext<ServerCommandSource> ctx,
                @NotNull Identifier containerIdentifier,
                @NotNull Identifier pointIdentifier
        )
        {
                var source = ctx.getSource();
                var state = state(ctx);
                var container = state.get(containerIdentifier);
                
                if (container == null)
                {
                        source.sendError(errorNotFoundContainer(containerIdentifier));
                        return -1;
                }
                
                var point = container.get(pointIdentifier);
                
                if (point == null)
                {
                        source.sendError(errorNotFoundPoint(containerIdentifier, pointIdentifier));
                        return -2;
                }
                
                container.remove(point);
                
                source.sendFeedback(
                        () -> Text.literal("Deleted ").append(styledTextFor(point, container)),
                        true
                );
                
                state.markDirty();
                return 1;
        }
        
        
        private static int showPoint (
                @NotNull CommandContext<ServerCommandSource> ctx,
                @NotNull Identifier containerIdentifier,
                @NotNull Identifier pointIdentifier
        )
        {
                var source = ctx.getSource();
                var state = state(ctx);
                var container = state.get(containerIdentifier);
                
                if (container == null)
                {
                        source.sendError(errorNotFoundContainer(containerIdentifier));
                        return -1;
                }
                
                var point = container.get(pointIdentifier);
                
                if (point == null)
                {
                        source.sendError(errorNotFoundPoint(containerIdentifier, pointIdentifier));
                        return -2;
                }
                
                source.sendMessage(styledTextFor(point, container));
                
                return 1;
        }
        
        
        private static int listPoints (
                @NotNull CommandContext<ServerCommandSource> ctx,
                @NotNull Identifier containerIdentifier,
                int page
        )
        {
                var source = ctx.getSource();
                
                if (page < 1)
                {
                        source.sendError(Text.literal("Page ordinal must be positive"));
                        return -1;
                }
                
                var state = state(ctx);
                var container = state.get(containerIdentifier);
                
                if (container == null)
                {
                        source.sendError(errorNotFoundContainer(containerIdentifier));
                        return -2;
                }
                
                var collection = container.points().values();
                var size = collection.size();
                
                if (size < 1)
                {
                        source.sendError(errorEmptyContainer(containerIdentifier));
                        return -3;
                }
                
                int lastPage = (size / 7) + 1;
                if (page > lastPage) page = lastPage;
                
                int start = page * 7, end = Math.min(start + 7, size);
                
                var list = new ArrayList<>(collection);
                MutableText text = textFor(container).append(" contains:");
                
                for (int i = start; i < end; i++)
                {
                        var point = list.get(i);
                        text = text.append("\n - ").append(styledTextFor(point, container));
                }
                
                source.sendMessage(text);
                
                return size;
        }
        
        
        
        
        
        private static final Function<CommandContext<ServerCommandSource>, PointsPersistentState>
                loader = ctx ->
        {
                var world = ctx.getSource().getServer().getWorld(World.OVERWORLD);
                return PointsPersistentState.load(Objects.requireNonNull(world));
        };
        
        
        public static @NotNull CompletableFuture<Suggestions> containersSuggestions (
                @NotNull CommandContext<ServerCommandSource> ctx,
                @NotNull SuggestionsBuilder builder
        )
        {
                var state = loader.apply(ctx);
                state.containers().keySet().forEach(id -> builder.suggest(id.toString()));
                return builder.buildFuture();
        }
        
        
        public static @NotNull CompletableFuture<Suggestions> pointsSuggestions (
                @NotNull CommandContext<ServerCommandSource> ctx,
                @NotNull SuggestionsBuilder builder
        )
        {
                var containerIdentifier = IdentifierArgumentType.getIdentifier(ctx, "container");
                var container = loader.apply(ctx).get(containerIdentifier);
                if (container == null) return builder.buildFuture();
                container.points().keySet().forEach(id -> builder.suggest(id.toString()));
                return builder.buildFuture();
        }
        
        
        private static int edit (
                @NotNull CommandContext<ServerCommandSource> ctx,
                @NotNull Function<@NotNull PointsContainer, Integer> function
        )
        {
                var containerIdentifier = IdentifierArgumentType.getIdentifier(ctx, "container");
                var state = loader.apply(ctx);
                var source = ctx.getSource();
                
                var container = state.get(containerIdentifier);
                if (container == null)
                {
                        container = new PointsContainer(containerIdentifier);
                        state.add(container);
                        var txt = String.format("Created new container [%s]", containerIdentifier);
                        source.sendFeedback(() -> Text.literal(txt), true);
                }
                
                var result = function.apply(container);
                state.markDirty();
                
                return result;
        }
        
        
        private static int addPoint (
                @NotNull CommandContext<ServerCommandSource> ctx,
                @NotNull Vec3d pos,
                @NotNull Vec2f rot
        )
        {
                return edit(ctx, container -> {
                        var pointIdentifier = IdentifierArgumentType.getIdentifier(ctx, "point");
                        var source = ctx.getSource();
                        var world = source.getWorld();
                        
                        if (container.has(pointIdentifier))
                        {
                                var txt = String.format("Container [%s] already has point [%s]", container.id(), pointIdentifier);
                                source.sendError(Text.literal(txt));
                                return 0;
                        }
                        
                        var worldIdentifier = world.getRegistryKey().getValue();
                        var point = new Point(pointIdentifier, worldIdentifier, pos.x, pos.y, pos.z, rot.y, rot.x);
                        container.add(point);
                        
                        source.sendFeedback(
                                () -> {
                                        var p = Text.literal("Added point ")
                                                .append(Text.literal(String.format("[%s]", point.id())).formatted(Formatting.GREEN))
                                                .setStyle(Style.EMPTY.withHoverEvent(
                                                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, textInfo(point, container.id())))
                                                );
                                        return Text.literal("")
                                                .append(p)
                                                .append(" to container ").append(Text.literal(String.format("[%s]", container.id())).formatted(Formatting.AQUA));
                                },
                                true
                        );
                        
                        return 1;
                });
        }
        
        
        public static @NotNull MutableText textTeleport (@NotNull Point point)
        {
                double x = point.x(), y = point.y(), z = point.z();
                float rp = point.pitch(), ry = point.yaw();
                var world = point.world();
                
                var cmd = String.format("/execute in %s run tp @s %f %f %f %f %f", world, x, y, z, rp, ry);
                
                return Text.literal("<TELEPORT>")
                        .setStyle(Style.EMPTY
                                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmd))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(cmd).formatted(Formatting.GRAY)))
                        )
                        .formatted(Formatting.AQUA, Formatting.UNDERLINE);
        }
        
        
        public static @NotNull MutableText textInfo (@NotNull Point point, @Nullable Identifier container)
        {
                var id = Text.literal(String.format("[%s]", point.id())).formatted(Formatting.GREEN);
                
                var cont = container == null ? Text.empty()
                        : Text.literal(String.format("[%s]", container)).formatted(Formatting.AQUA);
                
                var world = Text.literal(String.format("[%s]", point.world())).formatted(Formatting.WHITE);
                var pos = Text.literal(String.format("(%f, %f, %f)", point.x(), point.y(), point.z())).formatted(Formatting.WHITE);
                var rot = Text.literal(String.format("(%f, %f)", point.pitch(), point.yaw())).formatted(Formatting.WHITE);
                
                return Text.empty()
                        .append(Text.literal("Point ").append(id).formatted(Formatting.WHITE))
                        .append(Text.literal("\n - container ").formatted(Formatting.GRAY).append(cont))
                        .append(Text.literal("\n - dimension ").formatted(Formatting.GRAY).append(world))
                        .append(Text.literal("\n - position ").formatted(Formatting.GRAY).append(pos))
                        .append(Text.literal("\n - rotation ").formatted(Formatting.GRAY).append(rot));
        }
        
        
        public static @NotNull LiteralArgumentBuilder<ServerCommandSource> create (@NotNull String name)
        {
                var showSubTree = CommandManager.literal("show").then(CommandManager.argument("point", IdentifierArgumentType.identifier())
                        .suggests(PointsEditorCommand::pointsSuggestions)
                        .executes(ctx -> edit(ctx, container -> {
                                var pointIdentifier = IdentifierArgumentType.getIdentifier(ctx, "point");
                                var point = container.get(pointIdentifier);
                                
                                if (point == null)
                                {
                                        var txt = String.format("Container [%s] doesn't has point [%s]", container.id(), pointIdentifier);
                                        ctx.getSource().sendError(Text.literal(txt));
                                        return 0;
                                }
                                
                                ctx.getSource().sendFeedback(
                                        () -> textInfo(point, container.id()).append(Text.empty()).append("\n > ").formatted(Formatting.GRAY).append(textTeleport(point)),
                                        false
                                );
                                
                                return 1;
                        }))
                );
                
                
                var listSubTree = CommandManager.literal("list")
                        .executes(ctx -> {
                                MigaLib.LOGGER.info("listing");
                                return 0;
                        })
                        
                        .then(CommandManager.argument("page", IntegerArgumentType.integer(1))
                                .executes(ctx -> {
                                        MigaLib.LOGGER.info("listing page");
                                        return 0;
                                })
                        );
                
                
                var moveSubTree = CommandManager.literal("move").then(CommandManager.argument("point", IdentifierArgumentType.identifier())
                        .suggests(PointsEditorCommand::pointsSuggestions)
                        .then(CommandManager.argument("pos", Vec3ArgumentType.vec3())
                                .executes(ctx -> {
                                        MigaLib.LOGGER.info("moving point");
                                        return 0;
                                })
                        )
                );
                
                
                var addSubTree = CommandManager.literal("add").then(CommandManager.argument("point", IdentifierArgumentType.identifier())
                        .executes(ctx -> addPoint(
                                ctx,
                                ctx.getSource().getPosition(),
                                ctx.getSource().getRotation()
                        ))
                        
                        .then(CommandManager.argument("pos", Vec3ArgumentType.vec3())
                                .executes(ctx -> addPoint(
                                        ctx,
                                        Vec3ArgumentType.getVec3(ctx, "pos"),
                                        ctx.getSource().getRotation()
                                ))
                                
                                .then(CommandManager.argument("rot", Vec2ArgumentType.vec2())
                                        .executes(ctx -> addPoint(
                                                ctx,
                                                Vec3ArgumentType.getVec3(ctx, "pos"),
                                                Vec2ArgumentType.getVec2(ctx, "rot")
                                        ))
                                )
                        )
                );
                
                
                var removeSubTree = CommandManager.literal("remove").then(CommandManager.argument("point", IdentifierArgumentType.identifier())
                        .suggests(PointsEditorCommand::pointsSuggestions)
                        .executes(ctx -> edit(ctx, container -> {
                                var pointIdentifier = IdentifierArgumentType.getIdentifier(ctx, "point");
                                var point = container.get(pointIdentifier);
                                
                                if (point == null)
                                {
                                        var txt = String.format("Container [%s] doesn't has point [%s]", container.id(), pointIdentifier);
                                        ctx.getSource().sendError(Text.literal(txt));
                                        return 0;
                                }
                                
                                container.remove(point);
                                
                                ctx.getSource().sendFeedback(
                                        () -> {
                                                var p = Text.literal("Removed point ")
                                                        .append(Text.literal(String.format("[%s]", point.id())).formatted(Formatting.GREEN))
                                                        .setStyle(Style.EMPTY.withHoverEvent(
                                                                new HoverEvent(HoverEvent.Action.SHOW_TEXT, textInfo(point, container.id())))
                                                        );
                                                return Text.literal("")
                                                        .append(p)
                                                        .append(" from container ").append(Text.literal(String.format("[%s]", container.id())).formatted(Formatting.AQUA));
                                        },
                                        true
                                );
                                
                                return 1;
                        }))
                );
                
                
                return CommandManager.literal(name).then(CommandManager.argument("container", IdentifierArgumentType.identifier())
                                .suggests(PointsEditorCommand::containersSuggestions)
                                .then(showSubTree)
                                .then(listSubTree)
                                .then(moveSubTree)
                                .then(addSubTree)
                                .then(removeSubTree)
                );
        }
        
}
