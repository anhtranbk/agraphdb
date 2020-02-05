package com.agraph.storage.mlae;

import com.agraph.AGraph;
import com.agraph.common.util.StreamSupports;
import com.agraph.core.AGraphProperty;
import com.agraph.core.AGraphVertexProperty;
import com.agraph.core.InternalEdge;
import com.agraph.core.InternalVertex;
import com.agraph.core.serialize.Serializer;
import com.agraph.core.type.DataType;
import com.agraph.storage.Mutation;
import com.agraph.storage.MutationBuilder;
import com.agraph.storage.TableEntry;
import com.agraph.storage.rdbms.schema.Argument;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.agraph.storage.mlae.Constants.EDGE_PROPS_TABLE;
import static com.agraph.storage.mlae.Constants.EDGE_TABLE;
import static com.agraph.storage.mlae.Constants.HIDDEN_ID_PROPERTY;
import static com.agraph.storage.mlae.Constants.ID_COL;
import static com.agraph.storage.mlae.Constants.KEY_COL;
import static com.agraph.storage.mlae.Constants.LABEL_COL;
import static com.agraph.storage.mlae.Constants.REF_ID_COL;
import static com.agraph.storage.mlae.Constants.VALUE_COL;
import static com.agraph.storage.mlae.Constants.VERTEX_DST_ID_COL;
import static com.agraph.storage.mlae.Constants.VERTEX_DST_LABEL_COL;
import static com.agraph.storage.mlae.Constants.VERTEX_SRC_ID_COL;
import static com.agraph.storage.mlae.Constants.VERTEX_SRC_LABEL_COL;
import static com.agraph.storage.mlae.Constants.VERTEX_TABLE;

public class Mutations implements MutationBuilder {

    private final Serializer serializer;

    public Mutations(AGraph graph) {
        this.serializer = graph.serializer();
    }

    @Override
    public Collection<Mutation> fromModifiedVertices(Iterable<InternalVertex> vertices) {
        return StreamSupports.stream(vertices)
                .flatMap((Function<InternalVertex, Stream<Mutation>>) vertex
                        -> this.modifiedVertexToMutations(vertex).stream())
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Mutation> fromRemovedVertices(Iterable<InternalVertex> vertices) {
        Collection<TableEntry> entries = StreamSupports.stream(vertices)
                .filter(Objects::nonNull)
                .map(vertex -> toEntryBuilder(vertex).build())
                .collect(Collectors.toList());

        return Collections.singletonList(
                new Mutation(VERTEX_TABLE, Mutation.Action.REMOVE, entries)
        );
    }

    @Override
    public Collection<Mutation> fromModifiedEdges(Iterable<InternalEdge> edges) {
        return StreamSupports.stream(edges)
                .flatMap((Function<InternalEdge, Stream<Mutation>>) edge
                        -> this.modifiedEdgeToMutations(edge).stream())
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Mutation> fromRemovedEdges(Iterable<InternalEdge> edges) {
        return StreamSupports.stream(edges)
                .flatMap((Function<InternalEdge, Stream<Mutation>>) edge
                        -> this.removedEdgeToMutation(edge).stream())
                .collect(Collectors.toList());
    }

    private Collection<Mutation> modifiedVertexToMutations(InternalVertex vertex) {
        Collection<AGraphProperty<?>> updateProps = vertex.isNew()
                ? vertex.asPropertiesMap().values()
                : vertex.modifiedProps();

        if (vertex.isNew() && vertex.asPropertiesMap().isEmpty()) {
            AGraphVertexProperty<?> hiddenProp = new AGraphVertexProperty<>(
                    vertex, HIDDEN_ID_PROPERTY, serializer.write(vertex.id()));
            updateProps.add(hiddenProp);
        }

        Collection<TableEntry> modifiedEntries = updateProps.stream()
                .map(p -> this.toEntryBuilder(vertex)
                        .addKey(KEY_COL, Argument.of(p.key(), false))
                        .addValue(VALUE_COL, Argument.of(serializer.write(DataType.RAW, p.value())))
                        .build()
                )
                .collect(Collectors.toList());

        if (vertex.removedProps().isEmpty()) {
            return Collections.singletonList(
                    new Mutation(VERTEX_TABLE, Mutation.Action.UPSERT, modifiedEntries)
            );
        }

        Collection<TableEntry> removedEntries = vertex.removedProps().stream()
                .map(p -> this.toEntryBuilder(vertex)
                        .addKey(KEY_COL, Argument.of(p.key(), false))
                        .addValue(VALUE_COL, Argument.of(serializer.write(DataType.RAW, p.value())))
                        .build()
                )
                .collect(Collectors.toList());

        return Arrays.asList(
                new Mutation(VERTEX_TABLE, Mutation.Action.UPSERT, modifiedEntries),
                new Mutation(VERTEX_TABLE, Mutation.Action.REMOVE, removedEntries)
        );
    }

    private Collection<Mutation> modifiedEdgeToMutations(InternalEdge edge) {
        Collection<AGraphProperty<?>> updateProps = edge.isNew()
                ? edge.asPropertiesMap().values()
                : edge.modifiedProps();

        Collection<TableEntry> modifiedEntries = updateProps.stream()
                .map(p -> TableEntry.builder()
                        .addKey(REF_ID_COL, Argument.of(edge.internalId()))
                        .addKey(KEY_COL, Argument.of(p.key(), false))
                        .addValue(VALUE_COL, Argument.of(serializer.write(DataType.RAW, p.value())))
                        .build()
                )
                .collect(Collectors.toList());

        if (edge.isNew()) {
            TableEntry addEntry = this.toEntryBuilder(edge)
                    .addValue(REF_ID_COL, Argument.of(edge.internalId()))
                    .build();

            return Arrays.asList(
                    new Mutation(EDGE_PROPS_TABLE, Mutation.Action.UPSERT, modifiedEntries),
                    new Mutation(EDGE_TABLE, Mutation.Action.UPSERT, addEntry)
            );
        }

        if (edge.removedProps().isEmpty()) {
            return Collections.singletonList(
                    new Mutation(EDGE_PROPS_TABLE, Mutation.Action.UPSERT, modifiedEntries)
            );
        }

        Collection<TableEntry> removedEntries = edge.removedProps().stream()
                .map(p -> TableEntry.builder()
                        .addKey(REF_ID_COL, Argument.of(edge.internalId()))
                        .addKey(KEY_COL, Argument.of(p.key(), false))
                        .addValue(VALUE_COL, Argument.of(serializer.write(DataType.RAW, p.value())))
                        .build()
                )
                .collect(Collectors.toList());

        return Arrays.asList(
                new Mutation(EDGE_PROPS_TABLE, Mutation.Action.UPSERT, modifiedEntries),
                new Mutation(EDGE_PROPS_TABLE, Mutation.Action.REMOVE, removedEntries)
        );
    }

    private Collection<Mutation> removedEdgeToMutation(InternalEdge edge) {
        TableEntry edgeEntry = this.toEntryBuilder(edge).build();
        TableEntry propsEntry = TableEntry.builder()
                .addKey(REF_ID_COL, Argument.of(edge.internalId())).build();

        return Arrays.asList(
                new Mutation(EDGE_TABLE, Mutation.Action.REMOVE, edgeEntry),
                new Mutation(EDGE_PROPS_TABLE, Mutation.Action.REMOVE, propsEntry)
        );
    }

    private TableEntry.Builder toEntryBuilder(InternalVertex vertex) {
        return TableEntry.builder()
                .addKey(ID_COL, Argument.of(serializer.write(vertex.id())))
                .addKey(LABEL_COL, Argument.of(vertex.label(), false));
    }

    private TableEntry.Builder toEntryBuilder(InternalEdge edge) {
        final InternalVertex outV = edge.outVertex();
        final InternalVertex inV = edge.inVertex();
        return TableEntry.builder()
                .addKey(VERTEX_SRC_ID_COL, Argument.of(serializer.write(outV.id()), false))
                .addKey(VERTEX_DST_ID_COL, Argument.of(serializer.write(inV.id()), false))
                .addKey(VERTEX_SRC_LABEL_COL, Argument.of(outV.label()))
                .addKey(VERTEX_DST_LABEL_COL, Argument.of(inV.label()))
                .addKey(LABEL_COL, Argument.of(edge.label()));
    }
}
