package com.hrms.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hrms.entity.Menu;
import com.hrms.repository.MenuRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服務實現類
 * </p>
 *
 * @author System
 * @since 2024-06-08
 */
@Slf4j
@Service
@Transactional
public class MenuService extends ServiceImpl<MenuRepository, Menu> {

}
