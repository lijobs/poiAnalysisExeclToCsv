package com.poianalysisexecltocsv.demo.dao;


import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author FH Q313596790
 * 修改时间：2015、12、11
 */
@Repository("daoSupport")
public class DaoSupport implements DAO {

    @Resource(name = "sqlSessionTemplate")
    private SqlSessionTemplate sqlSessionTemplate;

    /**
     * 保存对象
     *
     * @param str
     * @param obj
     * @return
     * @throws Exception
     */
    public Object save(String str, Object obj) throws Exception {
        return sqlSessionTemplate.insert(str, obj);
    }

    /**
     * 批量更新
     *
     * @param str
     * @param obj
     * @return
     * @throws Exception
     */
    public Object batchSave(String str, List objs) throws Exception {
        return sqlSessionTemplate.insert(str, objs);
    }


    /**
     * @param @param  str
     * @param @param  objs
     * @param @return
     * @param @throws Exception    参数
     * @return Object    返回类型
     * @throws
     * @Title: batchSave
     * @Description: TODO(mybiats foreach 插入方式)
     */

    public Object batchSave(String str, Map objs) throws Exception {
        return sqlSessionTemplate.insert(str, objs);
    }

    /**
     * 自定义批量更新
     *
     * @param str
     * @param obj
     * @return
     * @throws Exception
     */
    public boolean userDefinedBatchSave(String str, Map objs) throws Exception {
        SqlSessionFactory sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
        boolean res = false;
        //批量执行器
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        try {
            List list = (List) objs.get("columnValue");
            if (list != null) {
                for (int i = 0, size = list.size(); i < size; i++) {
                    Map insertMap = new HashMap();
                    insertMap.put("tableName", objs.get("tableName"));
                    insertMap.put("columnName", objs.get("columnName"));
                    insertMap.put("columnValue", list.get(i));
                    sqlSession.insert(str, insertMap);
                    sqlSession.flushStatements();
                }
                sqlSession.commit();
                sqlSession.clearCache();
                res = true;
            }
        } finally {
            sqlSession.close();
        }
        return res;
    }

    /**
     * 修改对象
     *
     * @param str
     * @param obj
     * @return
     * @throws Exception
     */
    public Object update(String str, Object obj) throws Exception {
        return sqlSessionTemplate.update(str, obj);
    }

    /**
     * 批量更新
     *
     * @param str
     * @param obj
     * @return
     * @throws Exception
     */
    public void batchUpdate(String str, List objs) {
        SqlSessionFactory sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
        //批量执行器
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        try {
            if (objs != null) {
                for (int i = 0, size = objs.size(); i < size; i++) {
                    sqlSession.update(str, objs.get(i));
                }
                sqlSession.flushStatements();
                sqlSession.commit();
                sqlSession.clearCache();
            }
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 批量更新
     *
     * @param str
     * @param obj
     * @return
     * @throws Exception
     */
    public Object batchDelete(String str, List objs) {
        return sqlSessionTemplate.delete(str, objs);
    }

    /**
     * 删除对象
     *
     * @param str
     * @param obj
     * @return
     * @throws Exception
     */
    public Object delete(String str, Object obj) {
        return sqlSessionTemplate.delete(str, obj);
    }

    /**
     * 查找对象
     *
     * @param str
     * @param obj
     * @return
     * @throws Exception
     */
    public Object findForObject(String str, Object obj) {
        return sqlSessionTemplate.selectOne(str, obj);
    }

    /**
     * 查找对象
     *
     * @param str
     * @param obj
     * @return
     * @throws Exception
     */
    public Object findForList(String str, Object obj) {
        return sqlSessionTemplate.selectList(str, obj);
    }

    public Object findForMap(String str, Object obj, String key, String value) throws Exception {
        return sqlSessionTemplate.selectMap(str, obj, key);
    }

    @Override
    public Object findObjectForMap(String sql, Object object, String key) throws Exception {
        return sqlSessionTemplate.selectMap(sql, object, key);
    }


}


