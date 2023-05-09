package singleton;

/**
 * 饿汉式单例模式
 */
public class SingletonTest1 {
    /**
     * 构造器私有化
     */
    private SingletonTest1(){
    }
    /**
     *私有静态化初始化实例
     */
    private static SingletonTest1 singleton = new SingletonTest1();
    /**
     * 静态工厂方法
     * return singleton
     */
    public static SingletonTest1 getInstance(){
        return singleton;
    }
    public static void main(String[] args){
        SingletonTest1 singletons = SingletonTest1.getInstance();

    }
}
