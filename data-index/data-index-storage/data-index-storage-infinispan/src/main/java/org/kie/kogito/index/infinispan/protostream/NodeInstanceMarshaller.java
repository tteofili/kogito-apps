/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.index.infinispan.protostream;

import java.io.IOException;

import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.index.model.NodeInstance;
import org.kie.kogito.persistence.infinispan.protostream.AbstractMarshaller;

import com.fasterxml.jackson.databind.ObjectMapper;

public class NodeInstanceMarshaller extends AbstractMarshaller implements MessageMarshaller<NodeInstance> {

    public NodeInstanceMarshaller(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    public NodeInstance readFrom(ProtoStreamReader reader) throws IOException {
        NodeInstance node = new NodeInstance();
        node.setId(reader.readString("id"));
        node.setName(reader.readString("name"));
        node.setType(reader.readString("type"));
        node.setEnter(dateToZonedDateTime(reader.readDate("enter")));
        node.setExit(dateToZonedDateTime(reader.readDate("exit")));
        node.setDefinitionId(reader.readString("definitionId"));
        node.setNodeId(reader.readString("nodeId"));
        return node;
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, NodeInstance node) throws IOException {
        writer.writeString("id", node.getId());
        writer.writeString("name", node.getName());
        writer.writeString("type", node.getType());
        writer.writeDate("enter", zonedDateTimeToDate(node.getEnter()));
        writer.writeDate("exit", zonedDateTimeToDate(node.getExit()));
        writer.writeString("definitionId", node.getDefinitionId());
        writer.writeString("nodeId", node.getNodeId());
    }

    @Override
    public Class<? extends NodeInstance> getJavaClass() {
        return NodeInstance.class;
    }

    @Override
    public String getTypeName() {
        return getJavaClass().getName();
    }
}
