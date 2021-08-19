package com.project.graalrestservice.representationModels.mappers;

import com.project.graalrestservice.domain.scriptHandler.models.Script;
import com.project.graalrestservice.representationModels.ScriptInfoForList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ListScriptMapper {
    ListScriptMapper forList = Mappers.getMapper(ListScriptMapper.class);

    @Mapping(target = "status")
    ScriptInfoForList map(Script script);

}
