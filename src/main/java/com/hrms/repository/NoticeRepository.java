package com.hrms.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hrms.entity.Notice;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author System
 * @since 2024-06-08
 */
public interface NoticeRepository extends BaseMapper<Notice> {

    @Select("""
            SELECT * FROM notice WHERE publish_date <= #{currentTime} AND status = #{status}
            """)
    List<Notice> selectPendingNotices(LocalDateTime currentTime, Byte status);

    @Update("UPDATE notice SET status = 1 WHERE id = #{id}")
    int enableStatus(@Param("id") Integer id);

    @Update("UPDATE notice SET status = 0 WHERE id = #{id}")
    int disableStatus(@Param("id") Integer id);
}
