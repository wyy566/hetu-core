/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.prestosql.plugin.geospatial.aggregation;

import com.esri.core.geometry.ogc.OGCGeometry;
import io.airlift.slice.Slice;
import io.prestosql.geospatial.serde.GeometrySerde;
import io.prestosql.spi.block.BlockBuilder;
import io.prestosql.spi.function.AggregationFunction;
import io.prestosql.spi.function.AggregationState;
import io.prestosql.spi.function.CombineFunction;
import io.prestosql.spi.function.Description;
import io.prestosql.spi.function.InputFunction;
import io.prestosql.spi.function.OutputFunction;
import io.prestosql.spi.function.SqlType;

import static io.prestosql.plugin.geospatial.GeometryType.GEOMETRY;
import static io.prestosql.plugin.geospatial.GeometryType.GEOMETRY_TYPE_NAME;

/**
 * Aggregate form of ST_Union which takes a set of geometries and unions them into a single geometry using an iterative approach,
 * resulting in no intersecting regions.  The output may be a multi-geometry, a single geometry or a geometry collection.
 */
@Description("Returns a geometry that represents the point set union of the input geometries.")
@AggregationFunction("geometry_union_agg")
public class GeometryUnionAgg
{
    private GeometryUnionAgg() {}

    @InputFunction
    public static void input(@AggregationState GeometryState state, @SqlType(GEOMETRY_TYPE_NAME) Slice input)
    {
        OGCGeometry geometry = GeometrySerde.deserialize(input);
        if (state.getGeometry() == null) {
            state.setGeometry(geometry);
        }
        else if (!geometry.isEmpty()) {
            state.setGeometry(state.getGeometry().union(geometry));
        }
    }

    @CombineFunction
    public static void combine(@AggregationState GeometryState state, @AggregationState GeometryState otherState)
    {
        if (state.getGeometry() == null) {
            state.setGeometry(otherState.getGeometry());
        }
        else if (otherState.getGeometry() != null && !otherState.getGeometry().isEmpty()) {
            state.setGeometry(state.getGeometry().union(otherState.getGeometry()));
        }
    }

    @OutputFunction(GEOMETRY_TYPE_NAME)
    public static void output(@AggregationState GeometryState state, BlockBuilder out)
    {
        if (state.getGeometry() == null) {
            out.appendNull();
        }
        else {
            GEOMETRY.writeSlice(out, GeometrySerde.serialize(state.getGeometry()));
        }
    }
}
