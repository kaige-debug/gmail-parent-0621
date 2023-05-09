package singleton;
/**
 *懒汉式单例模式（延迟加载）
 */
public class SingletonTest2 {
    /**
     * 构造器私有化
     */
    private SingletonTest2(){

    }
    /**
     * 初始化实例值为null
     */
    private static SingletonTest2 singleton = null;
    /**
     * jingt工厂方法
     */
    public static  SingletonTest2 getInstance(){
        if(singleton==null){
           singleton= new SingletonTest2();
        }
        return singleton;
    }
    public static void main(String[] args){

        SingletonTest2 instance = SingletonTest2.getInstance();
    }
}
