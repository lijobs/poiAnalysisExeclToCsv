package com.poianalysisexecltocsv.demo.dao;

/**
 * @author lizehua 2018-07-24 13:46 pm
 * mybatisde的通用接口，包括保存数据，删除，编辑，查找，批量等操作的数据
 */
public interface DAO {

    /**
     * 保存对象
     *
     * @param str
     * @param obj
     * @return
     * @throws Exception
     */
    public Object save(String str, Object obj) throws Exception;

    /**
     * 修改对象
     *
     * @param str
     * @param obj
     * @return
     * @throws Exception
     */
    public Object update(String str, Object obj) throws Exception;

    /**
     * 删除对象
     *
     * @param str
     * @param obj
     * @return
     * @throws Exception
     */
    public Object delete(String str, Object obj) throws Exception;

    /**
     * 查找对象
     *
     * @param str
     * @param obj
     * @return
     * @throws Exception
     */
    public Object findForObject(String str, Object obj) throws Exception;

    /**
     * 查找对象
     *
     * @param str
     * @param obj
     * @return
     * @throws Exception
     */
    public Object findForList(String str, Object obj) throws Exception;

    /**
     * 查找对象封装成Map
     *
     * @param s
     * @param obj
     * @return
     * @throws Exception
     */
    public Object findForMap(String sql, Object obj, String key, String value) throws Exception;

    /**
     * 通过执行sql语句查询对象
     *
     * @param sql
     * @param object
     * @param key
     * @return
     * @throws Exception
     */
    public Object findObjectForMap(String sql, Object object, String key) throws Exception;

}
