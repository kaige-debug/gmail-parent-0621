package singleton;

/**
 * 双重锁检查锁
 */
public class SingletonTest3 {

    /**
     * 构造器私有化
     */
     private SingletonTest3(){

     }
    /**
     * 初始实例值为null,使用代码块 获取实例 枷锁
     */
    private static  SingletonTest3 singleton  = null;
   public static SingletonTest3 getInstance(){
       if(singleton == null){
           synchronized (SingletonTest3.class){
               if(singleton==null){
                  singleton = new SingletonTest3();
               }
           }
       }
       return singleton;
   }
   public static void main(String[] args){
       SingletonTest3 instance = SingletonTest3.getInstance();
   }
}
