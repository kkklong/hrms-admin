package com.hrms.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hrms.entity.ShiftAdjustmentRequest;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author System
 * @since 2024-09-26
 */
public interface ShiftAdjustmentRequestRepository extends BaseMapper<ShiftAdjustmentRequest> {
    @Select("SELECT * FROM shift_adjustment_request WHERE id = #{id} FOR UPDATE")
    ShiftAdjustmentRequest selectByIdForUpdate(@Param("id") Long id);

    @Update("""
        UPDATE shift_adjustment_request
           SET status = #{status},
               approved_by = #{approvedBy},
               approved_at = NOW()
         WHERE id = #{id}
    """)
    void updateStatus(@Param("id") Long id,
                      @Param("status") int status,
                      @Param("approvedBy") Integer approvedBy);

    @Update("""
        UPDATE shift_adjustment_request
           SET reason = #{reason},
               updated_at = NOW()
         WHERE id = #{id}
    """)
    void updateReason(@Param("id") Long id, @Param("reason") String reason);

    @Update("""
        UPDATE shift_adjustment_request
           SET approval_context = #{context},
               updated_at = NOW()
         WHERE id = #{id}
    """)
    void updateApprovalContext(@Param("id") Long id, @Param("context") String context);

    @Select("""
        SELECT *
        FROM shift_adjustment_request
        WHERE status = #{status}
    """)
    List<ShiftAdjustmentRequest> selectByStatus(@Param("status") Byte status);
}