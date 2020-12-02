package fight.it.gmall.product.service;


import fight.it.gmall.model.product.BaseCategory1;
import fight.it.gmall.model.product.BaseCategory2;
import fight.it.gmall.model.product.BaseCategory3;

import java.util.List;

public interface CategoryService  {
    List<BaseCategory1> getCategory1();

    List<BaseCategory2> getCategory2(String category1Id);

    List<BaseCategory3> getCategory3(String category2Id);
}
