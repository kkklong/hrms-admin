package com.hrms.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hrms.entity.FileData;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author System
 * @since 2024-06-08
 */
public interface FileDataRepository extends BaseMapper<FileData> {
    @Select("SELECT * FROM file_data WHERE case_id = #{caseId} and table_name = #{tableName}")
    List<FileData> selectByCaseIdAndTableName(int caseId, String tableName);
}
