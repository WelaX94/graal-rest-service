package com.project.graalrestservice.representationModels.mappers;

import com.project.graalrestservice.domain.scriptHandler.models.Script;
import com.project.graalrestservice.representationModels.ScriptInfoForList;
import com.project.graalrestservice.representationModels.ScriptInfoForSingle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Necessary to simplify the conversion of {@link Script} to {@link ScriptInfoForSingle}
 */
@Mapper
public interface SingleScriptMapper {
  SingleScriptMapper forSingle = Mappers.getMapper(SingleScriptMapper.class);

  @Mapping(target = "status")
  ScriptInfoForSingle map(Script script);

}
