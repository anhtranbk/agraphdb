package com.agraph.storage.rdbms.mlae;

import com.agraph.AGraph;
import com.agraph.core.AGraphProperty;
import com.agraph.core.AGraphVertexProperty;
import com.agraph.core.InternalEdge;
import com.agraph.core.InternalVertex;
import com.agraph.core.serialize.Serializer;
import com.agraph.core.type.DataType;
import com.agraph.storage.Mutation;
import com.agraph.storage.MutationBuilder;
import com.agraph.storage.RowEntry;
import com.agraph.storage.rdbms.schema.Argument;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static com.agraph.storage.rdbms.mlae.Constants.EDGE_PROPS_TABLE;
import static com.agraph.storage.rdbms.mlae.Constants.EDGE_TABLE;
import static com.agraph.storage.rdbms.mlae.Constants.HIDDEN_ID_PROPERTY;
import static com.agraph.storage.rdbms.mlae.Constants.ID_COL;
import static com.agraph.storage.rdbms.mlae.Constants.KEY_COL;
import static com.agraph.storage.rdbms.mlae.Constants.LABEL_COL;
import static com.agraph.storage.rdbms.mlae.Constants.REF_ID_COL;
import static com.agraph.storage.rdbms.mlae.Constants.VALUE_COL;
import static com.agraph.storage.rdbms.mlae.Constants.VERTEX_DST_ID_COL;
import static com.agraph.storage.rdbms.mlae.Constants.VERTEX_DST_LABEL_COL;
import static com.agraph.storage.rdbms.mlae.Constants.VERTEX_SRC_ID_COL;
import static com.agraph.storage.rdbms.mlae.Constants.VERTEX_SRC_LABEL_COL;
import static com.agraph.storage.rdbms.mlae.Constants.VERTEX_TABLE;

public class Mutations implements MutationBuilder {

    private final Serializer serializer;

    public Mutations(AGraph graph) {
        this.serializer = graph.serializer();
    }

    @Override
    public Collection<Mutation> fromModifiedVertices(Iterable<InternalVertex> vertices) {
        return Observable.fromIterable(vertices)
                .flatMap((Function<InternalVertex, ObservableSource<Mutation>>) vertex
                        -> Observable.fromIterable(this.modifiedVertexToMutations(vertex)))
                .toList().blockingGet();
    }

    @Override
    public Collection<Mutation> fromRemovedVertices(Iterable<InternalVertex> vertices) {
        Iterable<RowEntry> entries = Observable.fromIterable(vertices)
                .map(vertex -> vertexTableEntryBuilder(vertex).build())
                .blockingIterable();

        return Collections.singletonList(
                new Mutation(VERTEX_TABLE, Mutation.Action.REMOVE, entries)
        );
    }

    @Override
    public Collection<Mutation> fromModifiedEdges(Iterable<InternalEdge> edges) {
        return Observable.fromIterable(edges)
                .flatMap((Function<InternalEdge, ObservableSource<Mutation>>) edge
                        -> Observable.fromIterable(this.modifiedEdgeToMutations(edge)))
                .toList().blockingGet();
    }

    @Override
    public Collection<Mutation> fromRemovedEdges(Iterable<InternalEdge> edges) {
        return Observable.fromIterable(edges)
                .flatMap((Function<InternalEdge, ObservableSource<Mutation>>) edge
                        -> Observable.fromIterable(this.removedEdgeToMutation(edge)))
                .toList().blockingGet();
    }

    private Iterable<Mutation> modifiedVertexToMutations(InternalVertex vertex) {
        Collection<AGraphProperty<?>> updateProps = vertex.isNew()
                ? vertex.asPropertiesMap().values() : vertex.modifiedProps();

        if (vertex.isNew() && vertex.asPropertiesMap().isEmpty()) {
            AGraphVertexProperty<?> hiddenProp = new AGraphVertexProperty<>(
                    vertex, HIDDEN_ID_PROPERTY, serializer.write(vertex.id()));
            updateProps.add(hiddenProp);
        }

        Iterable<RowEntry> modifiedEntries = Observable.fromIterable(updateProps)
                .map(p -> this.vertexTableEntryBuilder(vertex)
                        .addKey(KEY_COL, Argument.of(p.key(), false))
                        .addValue(VALUE_COL, Argument.of(serializer.write(DataType.RAW, p.value())))
                        .build()
                )
                .toList().blockingGet();

        if (vertex.removedProps().isEmpty()) {
            return Collections.singletonList(
                    new Mutation(VERTEX_TABLE, Mutation.Action.ADD_OR_UPDATE, modifiedEntries)
            );
        }

        Iterable<RowEntry> removedEntries = Observable.fromIterable(vertex.removedProps())
                .map(p -> this.vertexTableEntryBuilder(vertex)
                        .addKey(KEY_COL, Argument.of(p.key(), false))
                        .addValue(VALUE_COL, Argument.of(serializer.write(DataType.RAW, p.value())))
                        .build()
                )
                .blockingIterable();

        return Arrays.asList(
                new Mutation(VERTEX_TABLE, Mutation.Action.ADD_OR_UPDATE, modifiedEntries),
                new Mutation(VERTEX_TABLE, Mutation.Action.REMOVE, removedEntries)
        );
    }

    private Iterable<Mutation> modifiedEdgeToMutations(InternalEdge edge) {
        Iterable<AGraphProperty<?>> updateProps = edge.isNew()
                ? edge.asPropertiesMap().values() : edge.modifiedProps();

        Iterable<RowEntry> modifiedEntries = Observable.fromIterable(updateProps)
                .map(p -> RowEntry.builder()
                        .addKey(REF_ID_COL, Argument.of(edge.internalId()))
                        .addKey(KEY_COL, Argument.of(p.key(), false))
                        .addValue(VALUE_COL, Argument.of(serializer.write(DataType.RAW, p.value())))
                        .build()
                )
                .blockingIterable();

        if (edge.isNew()) {
            RowEntry addEntry = this.edgeTableEntryBuilder(edge)
                    .addValue(REF_ID_COL, Argument.of(edge.internalId()))
                    .build();

            return Arrays.asList(
                    new Mutation(EDGE_PROPS_TABLE, Mutation.Action.ADD_OR_UPDATE, modifiedEntries),
                    new Mutation(EDGE_TABLE, Mutation.Action.ADD_OR_UPDATE, addEntry)
            );
        }

        if (edge.removedProps().isEmpty()) {
            return Collections.singletonList(
                    new Mutation(EDGE_PROPS_TABLE, Mutation.Action.ADD_OR_UPDATE, modifiedEntries)
            );
        }

        Iterable<RowEntry> removedEntries = Observable.fromIterable(edge.removedProps())
                .map(p -> RowEntry.builder()
                        .addKey(REF_ID_COL, Argument.of(edge.internalId()))
                        .addKey(KEY_COL, Argument.of(p.key(), false))
                        .addValue(VALUE_COL, Argument.of(serializer.write(DataType.RAW, p.value())))
                        .build()
                )
                .blockingIterable();

        return Arrays.asList(
                new Mutation(EDGE_PROPS_TABLE, Mutation.Action.ADD_OR_UPDATE, modifiedEntries),
                new Mutation(EDGE_PROPS_TABLE, Mutation.Action.REMOVE, removedEntries)
        );
    }

    private Iterable<Mutation> removedEdgeToMutation(InternalEdge edge) {
        RowEntry edgeEntry = this.edgeTableEntryBuilder(edge).build();
        RowEntry propsEntry = RowEntry.builder()
                .addKey(REF_ID_COL, Argument.of(edge.internalId())).build();

        return Arrays.asList(
                new Mutation(EDGE_TABLE, Mutation.Action.REMOVE, edgeEntry),
                new Mutation(EDGE_PROPS_TABLE, Mutation.Action.REMOVE, propsEntry)
        );
    }

    private RowEntry.Builder vertexTableEntryBuilder(InternalVertex vertex) {
        return RowEntry.builder()
                .addKey(ID_COL, Argument.of(serializer.write(vertex.id())))
                .addKey(LABEL_COL, Argument.of(vertex.label(), false));
    }

    private RowEntry.Builder edgeTableEntryBuilder(InternalEdge edge) {
        final InternalVertex outV = edge.outVertex();
        final InternalVertex inV = edge.inVertex();
        return RowEntry.builder()
                .addKey(VERTEX_SRC_ID_COL, Argument.of(serializer.write(outV.id()), false))
                .addKey(VERTEX_DST_ID_COL, Argument.of(serializer.write(inV.id()), false))
                .addKey(VERTEX_SRC_LABEL_COL, Argument.of(outV.label()))
                .addKey(VERTEX_DST_LABEL_COL, Argument.of(inV.label()))
                .addKey(LABEL_COL, Argument.of(edge.label()));
    }
}
