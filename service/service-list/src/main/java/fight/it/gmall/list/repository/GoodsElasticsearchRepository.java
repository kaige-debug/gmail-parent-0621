package fight.it.gmall.list.repository;

import fight.it.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GoodsElasticsearchRepository extends ElasticsearchRepository<Goods,Long>{

}
