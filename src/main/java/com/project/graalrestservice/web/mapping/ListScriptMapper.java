package com.project.graalrestservice.web.mapping;

import com.project.graalrestservice.domain.script.model.Script;
import com.project.graalrestservice.web.dto.ScriptInfoForList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Necessary to simplify the conversion of {@link Script} to {@link ScriptInfoForList}
 */
@Mapper
public interface ListScriptMapper {
  ListScriptMapper forList = Mappers.getMapper(ListScriptMapper.class);

  @Mapping(target = "status")
  ScriptInfoForList map(Script script);

}
