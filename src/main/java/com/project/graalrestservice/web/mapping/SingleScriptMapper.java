package com.project.graalrestservice.web.mapping;

import com.project.graalrestservice.domain.script.model.Script;
import com.project.graalrestservice.web.dto.ScriptInfoForSingle;
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
