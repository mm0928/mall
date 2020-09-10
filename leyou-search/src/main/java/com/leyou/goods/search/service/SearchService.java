package com.leyou.goods.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.common.pojo.PageResult;
import com.leyou.goods.search.client.BrandClient;
import com.leyou.goods.search.client.CategoryClient;
import com.leyou.goods.search.client.GoodsClient;
import com.leyou.goods.search.client.SpecificationClient;
import com.leyou.goods.search.pojo.Goods;
import com.leyou.goods.search.pojo.SearchRequest;
import com.leyou.goods.search.pojo.SearchResult;
import com.leyou.goods.search.repository.GoodsRepository;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.swing.*;
import java.io.IOException;
import java.util.*;

/**
 * 搜索服务
 */
@Service
public class SearchService {

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private CategoryMapper categoryMapper;

    private ObjectMapper mapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    /**
     * 搜索功能
     *
     * @param request
     * @return
     */
    public PageResult<Goods> search(SearchRequest request) {

        String key = request.getKey();

        // 判断搜索关键字，如果为空，直接返回
        if (StringUtils.isBlank(key)) {
            // 返回默认值
            return null;
        }
        // 1构建自定义搜索构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 1.1设置查询条件
        MatchQueryBuilder basicQuery = QueryBuilders.matchQuery("all", key).operator(Operator.AND);
        addFilterQuery(queryBuilder, basicQuery, request.getFilter());
        // queryBuilder.withQuery(basicQuery);
        // 1.2结果集过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "subTitle", "skus"}, null));

        // 1.3分页
        Integer page = request.getPage();
        Integer size = request.getSize();
        queryBuilder.withPageable(PageRequest.of(page, size));

        //1.4排序
        String sortBy = request.getSortBy();
        Boolean desc = request.getDescending();
        if (StringUtils.isNotBlank(sortBy)) {
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(desc ? SortOrder.DESC : SortOrder.ASC));
        }

        // 1.2.分页排序
        //searchWithPageAndSort(queryBuilder,request);

        // 1.3、聚合
        String categoryAggName = "category"; // 商品分类聚合名称
        String brandAggName = "brand"; // 品牌聚合名称
        // 对商品分类进行聚合
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        // 对品牌进行聚合
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        // 2、查询，获取结果
        AggregatedPage<Goods> pageInfo = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());

        // 3、解析查询结果
        // 3.1、分页信息
        Long total = pageInfo.getTotalElements();
        int totalPage = (total.intValue() + request.getSize() - 1) / request.getSize();
        // 3.2、商品分类的聚合结果
        List<Map<String, Object>> categories =
                getCategoryAggResult(pageInfo.getAggregation(categoryAggName));
        // 3.3、品牌的聚合结果
        List<Brand> brands = getBrandAggResult(pageInfo.getAggregation(brandAggName));
        // 2执行搜索获取结果集
        // Page<Goods> goodsPage = this.goodsRepository.search(queryBuilder.build());

       /* System.out.println("pageInfo.getTotalElements() = "+pageInfo.getTotalElements());
        System.out.println("pageInfo.getTotalPages() = "+pageInfo.getTotalPages());
        System.out.println("pageInfo.getContent() = "+pageInfo.getContent());*/
        //判断商品分类是否需要聚合
        List<Map<String, Object>> specs = new ArrayList<>();
        if (categories.size() == 1) {
            specs = getSpec((Long) categories.get(0).get("id"), basicQuery);
        }
        // 封装结果并返回
        return new SearchResult<Goods>(pageInfo.getTotalElements(), pageInfo.getTotalPages(), pageInfo.getContent(), categories, brands, specs);
        // return new PageResult<Goods>(goodsPage.getTotalElements(), goodsPage.getTotalPages(), goodsPage.getContent());
    }

    /**
     * @param queryBuilder
     * @param basicQuery
     * @param filter
     */
    private void addFilterQuery(NativeSearchQueryBuilder queryBuilder, MatchQueryBuilder basicQuery, Map<String, Object> filter) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 添加基础查询条件
        boolQueryBuilder.must(basicQuery);

        BoolQueryBuilder filterQueryBuilder = QueryBuilders.boolQuery();
        for (Map.Entry<String, Object> entry : filter.entrySet()) {
            String key = entry.getKey();
            if (StringUtils.equals("品牌", key)) {
                key = "brandId";
            } else if (StringUtils.equals("分类", key)) {
                key = "cid3";
            } else {
                key = "specs." + entry.getKey() + ".keyword";
            }
            filterQueryBuilder.must(QueryBuilders.termQuery(key, entry.getValue()));
        }
        boolQueryBuilder.filter(filterQueryBuilder);
        queryBuilder.withQuery(boolQueryBuilder);
    }

    /**
     * @param cid
     * @param query
     * @return
     */
    private List<Map<String, Object>> getSpec(Long cid, QueryBuilder query) {
        try {
            // 不管是全局参数还是sku参数，只要是搜索参数，都根据分类id查询出来
            List<SpecParam> params = this.specificationClient.querySpecParams(null, cid, true, null);
            List<Map<String, Object>> specs = new ArrayList<>();

            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
            queryBuilder.withQuery(query);

            // 聚合规格参数
            params.forEach(p -> {
                String key = p.getName();
                queryBuilder.addAggregation(AggregationBuilders.terms(key).field("specs." + key + ".keyword"));

            });

            // 查询
            Map<String, Aggregation> aggs = this.elasticsearchTemplate.query(queryBuilder.build(),
                    SearchResponse::getAggregations).asMap();

            // 解析聚合结果
            params.forEach(param -> {
                Map<String, Object> spec = new HashMap<>();
                String key = param.getName();
                spec.put("k", key);
                StringTerms terms = (StringTerms) aggs.get(key);
                spec.put("options", terms.getBuckets().stream().map(StringTerms.Bucket::getKeyAsString));
                specs.add(spec);
            });

            return specs;
        } catch (
                Exception e) {
            logger.error("规格聚合出现异常：", e);
            return null;
        }
    }

    /**
     * 构建基本查询条件
     *
     * @param queryBuilder
     * @param request
     */
    private void searchWithPageAndSort(NativeSearchQueryBuilder queryBuilder, SearchRequest request) {
        // 准备分页参数
        int page = request.getPage();
        int size = request.getSize();

        // 1、分页
        queryBuilder.withPageable(PageRequest.of(page - 1, size));
        // 2、排序
        String sortBy = request.getSortBy();
        Boolean desc = request.getDescending();
        if (StringUtils.isNotBlank(sortBy)) {
            // 如果不为空，则进行排序
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(desc ? SortOrder.DESC : SortOrder.ASC));
        }
    }

    /**
     * 解析品牌聚合结果
     *
     * @param aggregation
     * @return
     */
    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        try {
            LongTerms brandAgg = (LongTerms) aggregation;
            List<Long> bids = new ArrayList<>();
            for (LongTerms.Bucket bucket : brandAgg.getBuckets()) {
                bids.add(bucket.getKeyAsNumber().longValue());
            }
            // 根据id查询品牌
            return this.brandClient.queryBrandByIds(bids);
        } catch (Exception e) {
            logger.error("品牌聚合出现异常：", e);
            return null;
        }
    }

    /**
     * 解析商品分类聚合结果
     *
     * @param aggregation
     * @return
     */
    private List<Map<String, Object>> getCategoryAggResult(Aggregation aggregation) {
        try {
            List<Map<String, Object>> categories = new ArrayList<>();
            LongTerms categoryAgg = (LongTerms) aggregation;
            List<Long> cids = new ArrayList<>();
            for (LongTerms.Bucket bucket : categoryAgg.getBuckets()) {
                cids.add(bucket.getKeyAsNumber().longValue());
            }
            // 根据id查询分类名称
            List<String> names = this.categoryClient.queryNameByIds(cids);

            for (int i = 0; i < names.size(); i++) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", cids.get(i));
                map.put("name", names.get(i));
                categories.add(map);
            }
            return categories;
        } catch (Exception e) {
            logger.error("分类聚合出现异常：", e);
            return null;
        }
    }

    /**
     * 导入数据其实就是查询数据，然后把查询到的Spu转变为Goods来保存，因此我们先编写一个SearchService，然后在里面定义一个方法， 把Spu转为Goods
     *
     * @param spu
     * @return
     * @throws IOException
     */
    public Goods buildGoods(Spu spu) throws IOException {
        Goods goods = new Goods();
        //查询商品分类名称
        List<String> names = this.categoryClient.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //查询sku
        List<Sku> skus = this.goodsClient.querySkuBySpuId(spu.getId());
        //查询详情
        SpuDetail spuDetail = this.goodsClient.querySpuDetailById(spu.getId());
        //查询规格参数
        List<SpecParam> params = this.specificationClient.querySpecParams(null, spu.getCid3(), true, null);
        //处理sku,仅封装id、价格、标题、图片，并获得价格集合
        List<Long> prices = new ArrayList<>();
        List<Map<String, Object>> skuList = new ArrayList<>();
        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("id", sku.getId());
            skuMap.put("title", sku.getTitle());
            skuMap.put("price", sku.getPrice());
            skuMap.put("image", StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            skuList.add(skuMap);
        });
        //处理规格参数
        Map<String, Object> genericSpecs = mapper.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<String, Object>>() {
        });
        Map<String, List<Object>> specialSpecs = mapper.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<String, Object>>() {
        });
        //获取可搜索的规格参数
        Map<Spring, Object> searchSpec = new HashMap<>();
        //过滤规格模板，把所有可搜索的信息保存到Map中
        Map<String, Object> specMap = new HashMap<>();
        params.forEach(p -> {
            if (p.getSearching()) {
                if (p.getGeneric()) {
                    String value = genericSpecs.get(p.getId().toString()).toString();
                    if (p.getNumeric()) {
                        value = chooseSegment(value, p);
                    }
                    specMap.put(p.getName(), StringUtils.isBlank(value) ? "其他" : value);
                } else {
                    specMap.put(p.getName(), specialSpecs.get(p.getId().toString()));
                }
            }
        });
        goods.setId(spu.getId());
        goods.setSubTitle(spu.getSubTitle());
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setAll(spu.getTitle() + " " + StringUtils.join(names, " "));
        goods.setPrice(prices);
        goods.setSkus(mapper.writeValueAsString(skuList));
        goods.setSpecs(specMap);
        return goods;
    }

    /**
     * 过滤参数中有一类比较特殊，就是数值区间,存入时要进行处理
     *
     * @param value
     * @param p
     * @return
     */
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    /**
     * @param id
     * @throws IOException
     */
    public void createIndex(Long id) throws IOException {
        Spu spu = this.goodsClient.querySpuById(id);
        //构建商品
        Goods goods = this.buildGoods(spu);
        //保存数据到索引库
        this.goodsRepository.save(goods);
    }

    /**
     * @param id
     */
    public void deleteIndex(Long id) {
        this.goodsRepository.deleteById(id);
    }

    /**
     * @param id
     * @return
     */
    public List<Category> queryAllByCid3(Long id) {
        Category c3 = this.categoryMapper.selectByPrimaryKey(id);
        Category c2 = this.categoryMapper.selectByPrimaryKey(c3.getParentId());
        Category c1 = this.categoryMapper.selectByPrimaryKey(c2.getParentId());
        return Arrays.asList(c1, c2, c3);
    }
}
