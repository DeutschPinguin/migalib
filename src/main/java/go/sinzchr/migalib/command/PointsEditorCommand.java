package go.sinzchr.migalib.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
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
import java.util.function.Supplier;


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
        
        
        private static @NotNull MutableText errorForPlayersOnly ()
        {
                return Text.literal("This command is for players only");
        }
        
        
        private static @NotNull MutableText errorNotFoundContainer (@NotNull Identifier container)
        {
                return Text.literal("Container ")
                        .append(Text.literal(String.format("[%s]", container)).formatted(Formatting.AQUA))
                        .append(" not found");
        }
        
        
        private static @NotNull MutableText errorAlreadyExistsContainer (@NotNull Identifier container)
        {
                return Text.literal("Container ")
                        .append(Text.literal(String.format("[%s]", container)).formatted(Formatting.AQUA))
                        .append(" already exists");
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
                        .append(Text.literal(String.format("[%s]", point)).formatted(Formatting.GREEN));
        }
        
        
        private static @NotNull MutableText errorAlreadyContainsPoint (@NotNull Identifier container, @NotNull Identifier point)
        {
                return Text.literal("Container ")
                        .append(Text.literal(String.format("[%s]", container)).formatted(Formatting.AQUA))
                        .append(" already contains ")
                        .append(Text.literal(String.format("[%s]", point)).formatted(Formatting.GREEN));
        }
        
        
        private static @NotNull MutableText errorAlreadyHasTag (@NotNull Identifier point, @NotNull Identifier tag)
        {
                return Text.literal("Point ")
                        .append(Text.literal(String.format("[%s]", point)).formatted(Formatting.GREEN))
                        .append(" already has tag ")
                        .append(Text.literal(String.format("[%s]", tag)).formatted(Formatting.GREEN));
        }
        
        
        private static @NotNull MutableText errorDoesNotTaggedWith (@NotNull Identifier point, @NotNull Identifier tag)
        {
                return Text.literal("Point ")
                        .append(Text.literal(String.format("[%s]", point)).formatted(Formatting.GREEN))
                        .append(" does not tagged with ")
                        .append(Text.literal(String.format("[%s]", tag)).formatted(Formatting.GREEN));
        }
        
        
        private static @NotNull MutableText errorHasNoTags (@NotNull Identifier point, @NotNull Identifier tag)
        {
                return Text.literal("Point ")
                        .append(Text.literal(String.format("[%s]", point)).formatted(Formatting.GREEN))
                        .append(" does not has any tags ");
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
                        .append(textFor(point))
                        .append(Text.literal(String.format("\n[%s]", point.world())).formatted(Formatting.GREEN))
                        .append(Text.literal(String.format("\n(%f %f %f)", point.x(), point.y(), point.z())).formatted(Formatting.WHITE))
                        .append(Text.literal(String.format("\n(pitch %f, yaw %f)", point.pitch(), point.yaw())).formatted(Formatting.WHITE));
        }
        
        
        private static @NotNull MutableText textWithDataFrom (@NotNull Point point, @NotNull PointsContainer container)
        {
                return Text.empty()
                        .append(textWithDataFrom(point))
                        .append(Text.literal("\n Contained in ").formatted(Formatting.GRAY))
                        .append(Text.literal(String.format("[%s]", container.id())).formatted(Formatting.AQUA));
        }
        
        
        private static @NotNull Style styleFor (@NotNull Point point, @NotNull Text hoverText)
        {
                var cmd = String.format("/execute in %s run tp @s %f %f %f %f %f", point.world(), point.x(), point.y(), point.z(), point.yaw(), point.pitch());
                return Style.EMPTY
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmd));
        }
        
        
        private static @NotNull Style styleFor (@NotNull Point point)
        {
                return styleFor(point, textWithDataFrom(point));
        }
        
        
        private static @NotNull Style styleFor (@NotNull Point point, @NotNull PointsContainer container)
        {
                return styleFor(point, textWithDataFrom(point, container));
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
        
        
        private static int listContainers (@NotNull CommandContext<ServerCommandSource> ctx, int page)
        {
                var source = ctx.getSource();
                
                if (page < 1)
                {
                        source.sendError(Text.literal("Page ordinal must be positive"));
                        return -1;
                }
                
                var state = state(ctx);
                var collection = state.containers().values();
                var size = collection.size();
                
                if (size < 1)
                {
                        source.sendError(Text.literal("No containers exist in this level"));
                        return -2;
                }
                
                int lastPage = (size / 7) + 1;
                if (page > lastPage) page = lastPage;
                
                int start = (page - 1) * 7, end = Math.min(start + 7, size);
                
                var list = new ArrayList<>(collection);
                MutableText text = Text.literal("Existing containers:");
                
                for (int i = start; i < end; i++)
                {
                        var container = list.get(i);
                        text = text.append("\n - ").append(textFor(container));
                }
                
                source.sendMessage(text);
                
                return size;
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
                
                var point = new Point(pointIdentifier, world, position.x, position.y, position.z, rotation.x, rotation.y);
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
                
                int start = (page - 1) * 7, end = Math.min(start + 7, size);
                
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
        
        
        private static int tagPoint (
                @NotNull CommandContext<ServerCommandSource> ctx,
                @NotNull Identifier containerIdentifier,
                @NotNull Identifier pointIdentifier,
                @NotNull Identifier tag
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
                
                if (point.hasTag(tag))
                {
                        source.sendError(errorAlreadyHasTag(pointIdentifier, tag));
                        return -3;
                }
                
                var mut = point.toMutablePoint();
                mut.tag(tag);
                container.set(mut.toPoint());
                
                source.sendFeedback(
                        () -> Text.literal("Tagged ")
                                .append(styledTextFor(mut, container))
                                .append(" with ")
                                .append(Text.literal(String.format("<%s>", tag)).formatted(Formatting.BLUE)),
                        true
                );
                
                state.markDirty();
                return 1;
        }
        
        
        private static int untagPoint (
                @NotNull CommandContext<ServerCommandSource> ctx,
                @NotNull Identifier containerIdentifier,
                @NotNull Identifier pointIdentifier,
                @NotNull Identifier tag
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
                
                if (!point.hasTag(tag))
                {
                        source.sendError(errorDoesNotTaggedWith(pointIdentifier, tag));
                        return -3;
                }
                
                var mut = point.toMutablePoint();
                mut.untag(tag);
                container.set(mut.toPoint());
                
                source.sendFeedback(
                        () -> Text.literal("Removed tag ")
                                .append(Text.literal(String.format("<%s>", tag)).formatted(Formatting.BLUE))
                                .append(" from ")
                                .append(styledTextFor(mut, container)),
                        true
                );
                
                state.markDirty();
                return 1;
        }
        
        
        private static int checkTagPoint (
                @NotNull CommandContext<ServerCommandSource> ctx,
                @NotNull Identifier containerIdentifier,
                @NotNull Identifier pointIdentifier,
                @NotNull Identifier tag
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
                
                if (!point.hasTag(tag))
                {
                        source.sendError(errorDoesNotTaggedWith(pointIdentifier, tag));
                        return 0;
                }
                
                source.sendFeedback(
                        () -> styledTextFor(point, container)
                                .append(" is tagged with ")
                                .append(Text.literal(String.format("<%s>", tag)).formatted(Formatting.BLUE)),
                        true
                );
                
                return 1;
        }
        
        
        private static @NotNull CompletableFuture<Suggestions> getSuggestionsForContainers (
                @NotNull CommandContext<ServerCommandSource> ctx,
                @NotNull SuggestionsBuilder builder
        )
        {
                var state = state(ctx);
                state.containers().keySet().forEach(id -> builder.suggest(id.toString()));
                return builder.buildFuture();
        }
        
        
        private static @NotNull CompletableFuture<Suggestions> getSuggestionsForPoints (
                @NotNull CommandContext<ServerCommandSource> ctx,
                @NotNull SuggestionsBuilder builder
        )
        {
                Identifier containerIdentifier;
                try
                {
                        containerIdentifier = IdentifierArgumentType.getIdentifier(ctx, "container");
                }
                catch (IllegalArgumentException e)
                {
                        var player = ctx.getSource().getPlayer();
                        if (player == null) return builder.buildFuture();
                        containerIdentifier = getSelectedContainer(player.getUuid());
                        if (containerIdentifier == null) return builder.buildFuture();
                }
                
                var container = state(ctx).get(containerIdentifier);
                if (container == null) return builder.buildFuture();
                
                container.points().keySet().forEach(id -> builder.suggest(id.toString()));
                
                return builder.buildFuture();
        }
        
        
        private static @NotNull CompletableFuture<Suggestions> getSuggestionsForTags (
                @NotNull CommandContext<ServerCommandSource> ctx,
                @NotNull SuggestionsBuilder builder
        )
        {
                var containerIdentifier = IdentifierArgumentType.getIdentifier(ctx, "container");
                var container = state(ctx).get(containerIdentifier);
                if (container == null) return builder.buildFuture();
                
                var pointIdentifier = IdentifierArgumentType.getIdentifier(ctx, "point");
                var point = container.get(pointIdentifier);
                if (point == null) return builder.buildFuture();
                
                point.tags().forEach(id -> builder.suggest(id.toString()));
                
                return builder.buildFuture();
        }
        
        
        private static int containerChecker (
                @NotNull CommandContext<ServerCommandSource> ctx,
                @Nullable Identifier container,
                @NotNull Function<@NotNull Identifier, @NotNull Integer> func
        )
        {
                if (container != null) return func.apply(container);
                ctx.getSource().sendError(Text.literal("Select existing container first"));
                return 0;
        }
        
        
        public static @NotNull ArgumentBuilder<ServerCommandSource, ?> writePointsEditorTo (
                @NotNull ArgumentBuilder<ServerCommandSource, ?> root,
                @NotNull Function<@NotNull CommandContext<ServerCommandSource>, @Nullable Identifier> containerGetter
        )
        {
                return root
                        .then(CommandManager.literal("add")
                                .then(CommandManager.argument("point", IdentifierArgumentType.identifier())
                                        .executes(ctx -> containerChecker(ctx,
                                                containerGetter.apply(ctx),
                                                cont -> addPoint(
                                                        ctx, cont, IdentifierArgumentType.getIdentifier(ctx, "point"),
                                                        ctx.getSource().getPosition(),
                                                        ctx.getSource().getRotation()
                                                ))
                                        )
                                        .then(CommandManager.argument("position", Vec3ArgumentType.vec3())
                                                .executes(ctx -> containerChecker(ctx,
                                                        containerGetter.apply(ctx),
                                                        cont -> addPoint(
                                                                ctx, cont, IdentifierArgumentType.getIdentifier(ctx, "point"),
                                                                Vec3ArgumentType.getVec3(ctx, "position"),
                                                                ctx.getSource().getRotation()
                                                        ))
                                                )
                                                .then(CommandManager.argument("rotation", Vec2ArgumentType.vec2())
                                                        .executes(ctx -> containerChecker(ctx,
                                                                containerGetter.apply(ctx),
                                                                cont -> {
                                                                var rot = Vec2ArgumentType.getVec2(ctx, "rotation");
                                                                return addPoint(
                                                                        ctx, cont, IdentifierArgumentType.getIdentifier(ctx, "point"),
                                                                        Vec3ArgumentType.getVec3(ctx, "position"),
                                                                        new Vec2f(rot.y, rot.x)
                                                                );
                                                        }))
                                                )
                                        )
                                )
                        )
                        
                        .then(CommandManager.literal("move")
                                .then(CommandManager.argument("point", IdentifierArgumentType.identifier())
                                        .suggests(PointsEditorCommand::getSuggestionsForPoints)
                                        .then(CommandManager.argument("position", Vec3ArgumentType.vec3())
                                                .executes(ctx -> containerChecker(ctx,
                                                        containerGetter.apply(ctx),
                                                        cont -> movePoint(
                                                                ctx, cont, IdentifierArgumentType.getIdentifier(ctx, "point"),
                                                                Vec3ArgumentType.getVec3(ctx, "position"),
                                                                ctx.getSource().getRotation()
                                                        ))
                                                )
                                                .then(CommandManager.argument("rotation", Vec2ArgumentType.vec2())
                                                        .executes(ctx -> containerChecker(ctx,
                                                                containerGetter.apply(ctx),
                                                                cont -> {
                                                                        var rot = Vec2ArgumentType.getVec2(ctx, "rotation");
                                                                        return movePoint(
                                                                                ctx, cont, IdentifierArgumentType.getIdentifier(ctx, "point"),
                                                                                Vec3ArgumentType.getVec3(ctx, "position"),
                                                                                new Vec2f(rot.y, rot.x)
                                                                        );
                                                                }))
                                                )
                                        )
                                )
                        )
                        
                        .then(CommandManager.literal("delete")
                                .then(CommandManager.argument("point", IdentifierArgumentType.identifier())
                                        .suggests(PointsEditorCommand::getSuggestionsForPoints)
                                        .executes(ctx -> containerChecker(ctx,
                                                containerGetter.apply(ctx),
                                                cont -> deletePoint(
                                                        ctx, cont, IdentifierArgumentType.getIdentifier(ctx, "point")
                                                ))
                                        )
                                )
                        )
                        
                        .then(CommandManager.literal("show")
                                .then(CommandManager.argument("point", IdentifierArgumentType.identifier())
                                        .suggests(PointsEditorCommand::getSuggestionsForPoints)
                                        .executes(ctx -> containerChecker(ctx,
                                                containerGetter.apply(ctx),
                                                cont -> showPoint(
                                                        ctx, cont, IdentifierArgumentType.getIdentifier(ctx, "point")
                                                ))
                                        )
                                )
                        )
                        
                        .then(CommandManager.literal("list")
                                .executes(ctx -> containerChecker(ctx,
                                        containerGetter.apply(ctx),
                                        cont -> listPoints(
                                                ctx, cont, 1
                                        ))
                                )
                                .then(CommandManager.argument("page", IntegerArgumentType.integer(1))
                                        
                                        .executes(ctx -> containerChecker(ctx,
                                                containerGetter.apply(ctx),
                                                cont -> listPoints(
                                                        ctx, cont, ctx.getArgument("page", Integer.class)
                                                ))
                                        )
                                )
                        )
                        
                        .then(CommandManager.literal("tag")
                                .then(CommandManager.argument("point", IdentifierArgumentType.identifier())
                                        .suggests(PointsEditorCommand::getSuggestionsForPoints)
                                        .then(CommandManager.literal("add")
                                                .then(CommandManager.argument("tag", IdentifierArgumentType.identifier())
                                                        .executes(ctx -> containerChecker(ctx,
                                                                containerGetter.apply(ctx),
                                                                cont -> tagPoint(
                                                                        ctx, cont, IdentifierArgumentType.getIdentifier(ctx, "point"),
                                                                        IdentifierArgumentType.getIdentifier(ctx, "tag")
                                                                )
                                                        ))
                                                )
                                        )
                                        .then(CommandManager.literal("delete")
                                                .then(CommandManager.argument("tag", IdentifierArgumentType.identifier())
                                                        .suggests(PointsEditorCommand::getSuggestionsForTags)
                                                        .executes(ctx -> containerChecker(ctx,
                                                                containerGetter.apply(ctx),
                                                                cont -> untagPoint(
                                                                        ctx, cont, IdentifierArgumentType.getIdentifier(ctx, "point"),
                                                                        IdentifierArgumentType.getIdentifier(ctx, "tag")
                                                                )
                                                        ))
                                                )
                                        )
                                        .then(CommandManager.literal("check")
                                                .then(CommandManager.argument("tag", IdentifierArgumentType.identifier())
                                                        .suggests(PointsEditorCommand::getSuggestionsForTags)
                                                        .executes(ctx -> containerChecker(ctx,
                                                                containerGetter.apply(ctx),
                                                                cont -> checkTagPoint(
                                                                        ctx, cont, IdentifierArgumentType.getIdentifier(ctx, "point"),
                                                                        IdentifierArgumentType.getIdentifier(ctx, "tag")
                                                                )
                                                        ))
                                                )
                                        )
                                )
                        );
        }
        
        
        public static @NotNull LiteralArgumentBuilder<ServerCommandSource> createContainers (@NotNull String name)
        {
                return CommandManager.literal(name)
                        .then(CommandManager.literal("add")
                                .then(CommandManager.argument("container", IdentifierArgumentType.identifier())
                                        .suggests(PointsEditorCommand::getSuggestionsForContainers)
                                        .executes(ctx -> {
                                                var state = state(ctx);
                                                var containerIdentifier = IdentifierArgumentType.getIdentifier(ctx, "container");
                                                var container = state.get(containerIdentifier);
                                                
                                                if (container != null)
                                                {
                                                        ctx.getSource().sendError(errorAlreadyExistsContainer(containerIdentifier));
                                                        return -1;
                                                }
                                                
                                                container = new PointsContainer(containerIdentifier);
                                                state.add(container);
                                                
                                                ctx.getSource().sendMessage(
                                                        Text.literal("Created ")
                                                                .append(textFor(container))
                                                );
                                                
                                                state.markDirty();
                                                return 1;
                                        })
                                )
                        )
                        
                        .then(CommandManager.literal("delete")
                                .then(CommandManager.argument("container", IdentifierArgumentType.identifier())
                                        .suggests(PointsEditorCommand::getSuggestionsForContainers)
                                        .executes(ctx -> {
                                                var state = state(ctx);
                                                var containerIdentifier = IdentifierArgumentType.getIdentifier(ctx, "container");
                                                var container = state.get(containerIdentifier);
                                                
                                                if (container == null)
                                                {
                                                        ctx.getSource().sendError(errorNotFoundContainer(containerIdentifier));
                                                        return -1;
                                                }
                                                
                                                state.remove(container);
                                                
                                                ctx.getSource().sendMessage(
                                                        Text.literal("Deleted ")
                                                                .append(textFor(container))
                                                );
                                                
                                                CONTAINER_SELECTED.forEach((uuid, id) -> {
                                                        var player = ctx.getSource().getServer().getPlayerManager().getPlayer(uuid);
                                                        if (player == null) return;
                                                        
                                                        var selected = getSelectedContainer(player.getUuid());
                                                        if (selected == null || !selected.equals(containerIdentifier)) return;
                                                        
                                                        unselectContainer(player.getUuid());
                                                        
                                                        player.sendMessage(
                                                                Text.literal("Selected container was deleted")
                                                                        .formatted(Formatting.YELLOW)
                                                        );
                                                });
                                                
                                                state.markDirty();
                                                return 1;
                                        })
                                )
                        )
                        
                        .then(CommandManager.literal("checkout")
                                .then(CommandManager.argument("container", IdentifierArgumentType.identifier())
                                        .suggests(PointsEditorCommand::getSuggestionsForContainers)
                                        .executes(ctx -> {
                                                var player = ctx.getSource().getPlayer();
                                                
                                                if (player == null)
                                                {
                                                        ctx.getSource().sendError(errorForPlayersOnly());
                                                        return -1;
                                                }
                                                
                                                var state = state(ctx);
                                                var containerIdentifier = IdentifierArgumentType.getIdentifier(ctx, "container");
                                                var container = state.get(containerIdentifier);
                                                
                                                if (container == null)
                                                {
                                                        ctx.getSource().sendError(errorNotFoundContainer(containerIdentifier));
                                                        return -2;
                                                }
                                                
                                                selectContainer(player.getUuid(), containerIdentifier);
                                                
                                                ctx.getSource().sendMessage(
                                                        Text.literal("Selected ")
                                                                .append(textFor(container))
                                                                .append(" for modification (/migalib:point)")
                                                );
                                                
                                                return 1;
                                        })
                                )
                        )
                        
                        .then(CommandManager.literal("list")
                                .executes(ctx -> listContainers(ctx, 1))
                                .then(CommandManager.argument("page", IntegerArgumentType.integer(1))
                                        .executes(ctx -> listContainers(
                                                ctx, ctx.getArgument("page", Integer.class)
                                        ))
                                )
                        )
                        
                        .then(CommandManager.literal("in")
                                .then(writePointsEditorTo(
                                        CommandManager.argument("container", IdentifierArgumentType.identifier())
                                                .suggests(PointsEditorCommand::getSuggestionsForContainers),
                                        ctx -> IdentifierArgumentType.getIdentifier(ctx, "container")
                                ))
                        );
        }
        
        
        public static @NotNull LiteralArgumentBuilder<ServerCommandSource> createPoints (@NotNull String name)
        {
                return (LiteralArgumentBuilder<ServerCommandSource>) writePointsEditorTo(CommandManager.literal(name), ctx ->
                {
                        var player = ctx.getSource().getPlayer();
                        return player == null ? null : getSelectedContainer(player.getUuid());
                });
        }
        
        
        public static void registerBoth (@NotNull CommandDispatcher<ServerCommandSource> dispatcher)
        {
                dispatcher.register(createContainers("migalib:container")
                        .requires(source -> source.hasPermissionLevel(4))
                );
                
                dispatcher.register(createPoints("migalib:point")
                        .requires(source -> source.hasPermissionLevel(4)));
        }
        
}
