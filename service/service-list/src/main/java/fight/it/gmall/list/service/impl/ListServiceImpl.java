package fight.it.gmall.list.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import fight.it.gmall.list.repository.GoodsElasticsearchRepository;
import fight.it.gmall.list.service.ListService;
import fight.it.gmall.model.list.*;

import fight.it.gmall.model.product.BaseTrademark;
import fight.it.gmall.model.product.SkuInfo;
import fight.it.gmall.product.client.ProductFeignClient;
import io.lettuce.core.GeoArgs;
import net.bytebuddy.description.modifier.ProvisioningState;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;

import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ListServiceImpl implements ListService {
    @Autowired
    ProductFeignClient productFeignClient;
    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    GoodsElasticsearchRepository goodsElasticsearchRepository;
    @Autowired
    RestHighLevelClient restHighLevelClient;
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public List<JSONObject> getBaseCategoryList(){
        List<JSONObject> list=  productFeignClient.getBaseCategoryList();
        return list;
    }
    @Override
    public void cancelSale(Long skuId) {

        goodsElasticsearchRepository.deleteById(skuId);
    }

    @Override
    public void hotScore(Long skuId) {
        Integer hotScoreRedis = (Integer)redisTemplate.opsForValue().get("hotScore:" + skuId);
        if (null!=hotScoreRedis){
            hotScoreRedis++;
            redisTemplate.opsForValue().set("hotScore:"+skuId,hotScoreRedis);
        }else{
            redisTemplate.opsForValue().set("hotScore:" + skuId,1);
        }

        if(hotScoreRedis%10==0){
            Goods goods = goodsElasticsearchRepository.findById(skuId).get();
            goods.setHotScore(Long.parseLong(hotScoreRedis+""));
            goodsElasticsearchRepository.save(goods);

        }

    }

    @Override
    public void onSale(Long skuId) {
        Goods goods = new Goods();
        //获取商品信息
        SkuInfo skuInfo= productFeignClient.getSkuInfoById(skuId);
        //获取属性数据
        List<SearchAttr> searchAttrs = productFeignClient.getSearchAttrList(skuId);
        //商标数据
        BaseTrademark baseTrademark = productFeignClient.getTrademarkById(skuInfo.getTmId());

        goods.setTitle(skuInfo.getSkuName());
        goods.setHotScore(0l);
        goods.setCategory3Id(skuInfo.getCategory3Id());
        goods.setCreateTime(new Date());
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setPrice(skuInfo.getPrice().doubleValue());
        goods.setTmLogoUrl(baseTrademark.getLogoUrl());
        goods.setTmName(baseTrademark.getTmName());
        goods.setTmId(baseTrademark.getId());
        goods.setAttrs(searchAttrs);
        goods.setId(skuId);

        goodsElasticsearchRepository.save(goods);
    }
    @Override
    public void createGoodsIndex() {
        elasticsearchRestTemplate.createIndex(Goods.class);
        elasticsearchRestTemplate.putMapping(Goods.class);
    }

    @Override
    public SearchResponseVo list(SearchParam searchParam)  {
        // 需要解析的返回结果
        SearchResponse searchResponse = null;

        //封装请求
        SearchRequest  searchRequest = getSearchRequest(searchParam);
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        }catch (IOException e){
            e.printStackTrace();
        }
        //解析返回结果

        SearchResponseVo searchResponseVo = parseSearchResponse(searchResponse);

        return searchResponseVo;
    }



    //封装请求
    private SearchRequest getSearchRequest(SearchParam searchParam) {

        SearchRequest searchRequest = new SearchRequest();// 请求
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();//dsl
        searchRequest.indices("goods");
        searchRequest.types("info");

        // 参数
        Long category3Id = searchParam.getCategory3Id();
        String keyword = searchParam.getKeyword();
        String[] props = searchParam.getProps();// 属性id:属性值名称:属性名称
        String trademark = searchParam.getTrademark();// 商标id:商标名称
        String order = searchParam.getOrder();

        //
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        //商标
        if(!StringUtils.isEmpty(trademark)){
            TermQueryBuilder termQueryBuilder= new TermQueryBuilder("tmId",trademark.split(":")[0]);
            boolQueryBuilder.filter(termQueryBuilder);
        }
        // 关键字
        if(!StringUtils.isEmpty(keyword)){
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("title",keyword);
            boolQueryBuilder.must(matchQueryBuilder);
        }

        //属性
        if(null!=props&&props.length>0){
            for (String prop : props) {
                String[] split = prop.split(":");
                long attrId = Long.parseLong(split[0]);
                String attrValueName = split[1];
                String attrName = split[2];

                BoolQueryBuilder boolQueryBuilderNested = new BoolQueryBuilder();
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("attrs.attrId",attrId);
                MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("attrs.attrValue",attrValueName);
                boolQueryBuilderNested.filter(termQueryBuilder);
                boolQueryBuilderNested.must(matchQueryBuilder);

                NestedQueryBuilder nestedQueryBuilder = new NestedQueryBuilder("attrs",boolQueryBuilderNested, ScoreMode.None);

                boolQueryBuilder.filter(nestedQueryBuilder);


            }
        }

        // 三级分类
        if (null != category3Id && category3Id > 0) {
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("category3Id", category3Id);
            boolQueryBuilder.filter(termQueryBuilder);
        }
        searchSourceBuilder.query(boolQueryBuilder);

        // 商标聚合
        TermsAggregationBuilder termsTmAggregationBuilder = AggregationBuilders.terms("tmIdAgg").field("tmId")
                .subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName"))
                .subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl"));
        searchSourceBuilder.aggregation(termsTmAggregationBuilder);

        //属性聚合
        NestedAggregationBuilder nestedAggregationBuilder = AggregationBuilders.nested("attrsAgg", "attrs")
                .subAggregation(AggregationBuilders.terms("attrIdAgg").field("attrs.attrId")
                .subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName"))
                .subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue")));
        searchSourceBuilder.aggregation(nestedAggregationBuilder);

        //页面
        searchSourceBuilder.size(20);
        searchSourceBuilder.from(0);

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span style='color:red;font-weight:bolder'>");
        highlightBuilder.postTags("</span>");

        highlightBuilder.field("title");
        searchSourceBuilder.highlighter(highlightBuilder);

        //排序
        if(!StringUtils.isEmpty(order)){
            String key = order.split(":")[0];
            String sort = order.split(":")[1];
            String sortName = "hotScore";

            if(key.equals("2")){
               sortName = "price";
            }
         searchSourceBuilder.sort(sortName,sort.equals("asc")?SortOrder.ASC:SortOrder.DESC);
        }


        System.out.println(searchSourceBuilder.toString());
        searchRequest.source(searchSourceBuilder);
        System.out.println(searchRequest.toString());
        return searchRequest;
    }
    //需要解析的返回结果
    private SearchResponseVo parseSearchResponse(SearchResponse searchResponse) {
//        SearchResponseVo searchResponseVo = new SearchResponseVo();
//
//        SearchHits hits = searchResponse.getHits();// 概览
//        SearchHit[] hitsResult = hits.getHits();//结果
//        if(null!=hitsResult&&hitsResult.length>0){
//            List<Goods> list = new ArrayList<>();
//            for (SearchHit document: hitsResult) {
//                String json = document.getSourceAsString();
//                Goods  goods = JSONObject.parseObject(json, Goods.class);//解析json
//
//                list.add(goods);
//            }
//            searchResponseVo.setGoodsList(list);//商品集合
//
//            ParsedLongTerms tmIdAgg = (ParsedLongTerms)searchResponse.getAggregations().get("tmIdAgg");
//            List<SearchResponseTmVo> searchResponseTmVos = tmIdAgg.getBuckets().stream().map(tmAggBucket->{
//                SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
//
//                //id
//                long tmIdKey   = tmAggBucket.getKeyAsNumber().longValue();
//                //name
//                ParsedStringTerms tmNameAggKey =  tmAggBucket.getAggregations().get("tmNameAgg");
//                List<? extends Terms.Bucket> tmNameAggBuckets = tmNameAggKey.getBuckets();
//                String tmName = tmNameAggBuckets.get(0).getKeyAsString();
//                //logUrl
//                ParsedStringTerms tmLogUrlKey =  tmAggBucket.getAggregations().get("tmLogoUrlAgg");
//                List<? extends Terms.Bucket> tmLogUrlAggBuckets = tmLogUrlKey.getBuckets();
//                String tmLogUrl = tmLogUrlAggBuckets.get(0).getKeyAsString();
//
//                searchResponseTmVo.setTmId(tmIdKey);
//                searchResponseTmVo.setTmName(tmName);
//                searchResponseTmVo.setTmLogoUrl(tmLogUrl);
//
//                return searchResponseTmVo;
//            }).collect(Collectors.toList());
//            searchResponseVo.setTrademarkList(searchResponseTmVos);
//        }
//
//
//        return searchResponseVo;
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        SearchHits hits = searchResponse.getHits();// 概览
        SearchHit[] hitsResult = hits.getHits();// 结果
        if (null != hitsResult && hitsResult.length > 0) {
            List<Goods> list = new ArrayList<>();
            for (SearchHit document : hitsResult) {
                String json = document.getSourceAsString();

                Goods goods = JSON.parseObject(json, Goods.class);

                Map<String, HighlightField> highlightFields = document.getHighlightFields();
               if(null!=highlightFields){
                   HighlightField highlightField = highlightFields.get("title");
                   if(null!=highlightField){
                       Text text = highlightField.fragments()[0];
                       String title = text.string();
                       goods.setTitle(title);
                   }
               }
                list.add(goods);
            }
            searchResponseVo.setGoodsList(list);//商品集合

            //解析商标聚合函数
            ParsedLongTerms tmIdAgg = (ParsedLongTerms) searchResponse.getAggregations().get("tmIdAgg");
            List<SearchResponseTmVo> searchResponseTmVos = tmIdAgg.getBuckets().stream().map(tmIdAggBucket->{
                SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();

                // id
                long tmIdKey = tmIdAggBucket.getKeyAsNumber().longValue();
                // name
                ParsedStringTerms tmNameAgg = (ParsedStringTerms)tmIdAggBucket.getAggregations().get("tmNameAgg");
                List<? extends Terms.Bucket> tmNameAggBuckets = tmNameAgg.getBuckets();
                String tmNameKey = tmNameAggBuckets.get(0).getKeyAsString();
                // url
                ParsedStringTerms tmLogoUrlAgg = (ParsedStringTerms)tmIdAggBucket.getAggregations().get("tmLogoUrlAgg");
                List<? extends Terms.Bucket> tmLogoUrlAggBuckets = tmLogoUrlAgg.getBuckets();
                String tmLogoUrlKey = tmLogoUrlAggBuckets.get(0).getKeyAsString();

                searchResponseTmVo.setTmId(tmIdKey);
                searchResponseTmVo.setTmName(tmNameKey);
                searchResponseTmVo.setTmLogoUrl(tmLogoUrlKey);
                return searchResponseTmVo;
            }).collect(Collectors.toList());

            searchResponseVo.setTrademarkList(searchResponseTmVos);// 品牌集合
            System.out.println(searchResponseVo.getTrademarkList());

            //解析属性聚合函数
//            ParsedNested attrsAgg = searchResponse.getAggregations().get("attrsAgg");
//            ParsedLongTerms attrIdAgg = attrsAgg.getAggregations().get("attrIdAgg");
//            List<SearchResponseAttrVo> searchResponseAttrVos = attrIdAgg.getBuckets().stream().map(attrIdBucket -> {
//                SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
//                //id
//                long attrIdKey = attrIdBucket.getKeyAsNumber().longValue();
//                //attrName
//                ParsedStringTerms attrNameAgg =  attrIdBucket.getAggregations().get("attrNameAgg");
//                String attrNameKey = attrNameAgg.getBuckets().get(0).getKeyAsString();
//                //ValueList
//                ParsedStringTerms attrValueAgg = attrIdBucket.getAggregations().get("attrValueAgg");
//                List<String> attrValueList = attrValueAgg.getBuckets().stream().map(attrValueBucket->{
//                    String attrValueKey =  attrValueBucket.getKeyAsString();
//                    return attrValueKey;
//                }).collect(Collectors.toList());
//                searchResponseAttrVo.setAttrId(attrIdKey);
//                searchResponseAttrVo.setAttrName(attrNameKey);
//                searchResponseAttrVo.setAttrValueList(attrValueList);
//
//                return searchResponseAttrVo;
//            }).collect(Collectors.toList());
//           searchResponseVo.setAttrsList(searchResponseAttrVos);// 属性集合
            //解析属性聚合函数
            ParsedNested attrsAgg = (ParsedNested)searchResponse.getAggregations().get("attrsAgg");
            ParsedLongTerms attrIdAgg = (ParsedLongTerms)attrsAgg.getAggregations().get("attrIdAgg");
            List<SearchResponseAttrVo> searchResponseAttrVos = attrIdAgg.getBuckets().stream().map(attrIdBucket->{
                SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
                // id
                long attrIdKey = attrIdBucket.getKeyAsNumber().longValue();
                // name
                ParsedStringTerms attrNameAgg = (ParsedStringTerms)attrIdBucket.getAggregations().get("attrNameAgg");
                String attrNameKey = attrNameAgg.getBuckets().get(0).getKeyAsString();
                // ValueList
                ParsedStringTerms attrValueAgg = (ParsedStringTerms)attrIdBucket.getAggregations().get("attrValueAgg");
                List<String> attrValueList = attrValueAgg.getBuckets().stream().map(attrValueBucket->{
                    String attrValueKey = attrValueBucket.getKeyAsString();
                    return attrValueKey;
                }).collect(Collectors.toList());

                searchResponseAttrVo.setAttrId(attrIdKey);
                searchResponseAttrVo.setAttrName(attrNameKey);
                searchResponseAttrVo.setAttrValueList(attrValueList);
                return searchResponseAttrVo;
            }).collect(Collectors.toList());

            searchResponseVo.setAttrsList(searchResponseAttrVos);// 属性集合
        }
        return searchResponseVo;
    }
}
