package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分类管理
 */
@Service
public class SpecificationService {

    @Resource
    private SpecGroupMapper specGroupMapper;

    @Resource
    private SpecParamMapper specParamMapper;

    /**
     * 根据分类cid查询规格组
     *
     * @param cid
     * @return
     */
    public List<SpecGroup> queryGroupByCid(Long cid) {
        //组装数据
        SpecGroup group = new SpecGroup();
        group.setCid(cid);
        System.out.println("组装的SpecGroup数据："+group.toString());
        //根据cid查询数据库
        List<SpecGroup> select = specGroupMapper.select(group);
        //遍历规格数据
        for (SpecGroup specGroup : select) {
            System.out.println(specGroup);
        }
        return select;
    }


    public List<SpecParam> querySpecParams(Long gid, Long cid, Boolean searching, Boolean generic) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setSearching(searching);
        specParam.setGeneric(generic);
        List<SpecParam> list = specParamMapper.select(specParam);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        return list;
    }

    public List<SpecGroup> queryListByCid(Long cid) {
        // 查询规格组
        List<SpecGroup> specGroups = queryGroupByCid(cid);
        // 查询当前分类下的参数
        List<SpecParam> specParams = querySpecParams(null, cid, null, null);
        Map<Long, List<SpecParam>> map = new HashMap<>();
        for (SpecParam param : specParams) {
            if (!map.containsKey(param.getGroupId())) {
                map.put(param.getGroupId(), new ArrayList<>());
            }
            map.get(param.getGroupId()).add(param);

        }
        for (SpecGroup specGroup : specGroups) {
            specGroup.setParams(map.get(specGroup.getCid()));
        }

        return specGroups;
    }

    public List<SpecGroup> querySpecsByCid(Long cid) {
        // 查询规格组
        List<SpecGroup> groups = this.queryGroupByCid(cid);
        if (CollectionUtils.isEmpty(groups)) {
            throw new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
        }
        SpecParam param = new SpecParam();
        groups.forEach(g -> {
            // 查询组内参数
            g.setParams(this.querySpecParams(g.getId(), null, null, null));
        });
        return groups;
    }

    /**
     * 查询所有规格组数据
     */
    public List<SpecGroup> querySpecsAll() {
        return specGroupMapper.selectAll();
    }
}
