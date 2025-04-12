package com.hrms.config;

//@Configuration
//public class ShiroConfig extends AbstractShiroWebConfiguration {
//
//    @Bean
//    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, ObjectMapper objectMapper) {
//        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
//        shiroFilterFactoryBean.setSecurityManager(securityManager);
//
//        Map<String, Filter> filters = new LinkedHashMap<>();
//        filters.put("authc", new DefaultShiroFilter(objectMapper));
//        shiroFilterFactoryBean.setFilters(filters);
//
//        shiroFilterFactoryBean.setFilterChainDefinitionMap(shiroFilterChainDefinition().getFilterChainMap());
//        return shiroFilterFactoryBean;
//    }
//
//    @Bean
//    public SessionDAO sessionDAO(RedisTemplate<Serializable, Session> sessionRedisTemplate) {
//        return new RedisSessionDao(sessionRedisTemplate);
//    }
//
//    @Bean
//    public SessionManager sessionManager(RedisTemplate<Serializable, Session> sessionRedisTemplate) {
//        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
//        sessionManager.setSessionDAO(sessionDAO(sessionRedisTemplate));
//        sessionManager.setCacheManager(cacheManager);
//        return sessionManager;
//    }
//
//    @Bean
//    public SecurityManager securityManager(RedisTemplate<Serializable, Session> sessionRedisTemplate) {
//        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
//        securityManager.setRealm(realm());
//        securityManager.setSessionManager(sessionManager(sessionRedisTemplate));
//        return securityManager;
//    }
//
//    @Bean
//    public Realm realm() {
//        return new DefaultRealm();
//    }
//
//    @Bean
//    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
//        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
//        advisor.setSecurityManager(securityManager);
//        return advisor;
//    }
//
//    @Bean
//    protected ShiroFilterChainDefinition shiroFilterChainDefinition() {
//        return new ShiroFilterChainDefinition() {
//            @Override
//            public Map<String, String> getFilterChainMap() {
//                Map<String, String> filterChainMap = new LinkedHashMap<>();
//                filterChainMap.put("/account/login", "anon");
//                filterChainMap.put("/account/logout", "anon");
//                filterChainMap.put("/swagger-ui/**", "anon");
//                filterChainMap.put("/v3/api-docs/**", "anon");
//                filterChainMap.put("/api/test/**", "anon");
//                filterChainMap.put("/configuration/ui", "anon");
//                filterChainMap.put("/swagger-resources/**", "anon");
//                filterChainMap.put("/swagger-ui.html", "anon");
//                filterChainMap.put("/webjars/**", "anon");
//                filterChainMap.put("/files/**", "anon");
//                filterChainMap.put("/favicon.ico", "anon");
//                filterChainMap.put("/sample/**", "anon");
//                filterChainMap.put("/**", "authc");
//                return filterChainMap;
//            }
//        };
//    }
//}
